package dev.vic17usd.spacefabric.colony;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.transport.TransportService;
import java.util.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.*;

/** A non-blocking arrival tutorial. Observes loaded equipment; never locates or generates terrain. */
public final class VenusOnboarding {
    private VenusOnboarding() { }

    public static Component next(ServerPlayer player) {
        return VenusProgress.get(player.level()).next(player.getUUID())
                .<Component>map(s -> Component.translatable("venus.guide.next",
                        Component.translatable("venus.guide." + s.key())))
                .orElseGet(() -> Component.translatable("venus.guide.complete"));
    }

    public static List<Component> pages(ServerPlayer player) {
        var pages = new ArrayList<Component>();
        pages.add(next(player));
        pages.add(Component.translatable("venus.guide.safety"));
        for (var step : VenusProgress.Step.values()) {
            if (step == VenusProgress.Step.ARRIVAL) continue;
            pages.add(Component.translatable("venus.guide.page",
                    VenusProgress.get(player.level()).reached(player.getUUID(), step) ? "✓" : "○",
                    Component.translatable("venus.guide." + step.key()),
                    Component.translatable("venus.guide." + step.key() + ".detail")));
        }
        pages.add(provisions(15, 2, 75));
        pages.add(Component.translatable("balance.space_fabric.composite"));
        return List.copyOf(pages);
    }

    public static Component provisions(int minutes, int occupants, int volume) {
        var supplies = ColonyBalance.supplies(minutes * 60, occupants, volume);
        return Component.translatable("venus.guide.supplies", minutes, occupants, volume,
                supplies.oxygen(), supplies.power(), supplies.water());
    }

    private static void show(ServerPlayer player) {
        player.sendSystemMessage(next(player).copy().withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.translatable("venus.guide.safety"));
        player.sendSystemMessage(Component.translatable("venus.guide.commands"));
    }

    private static void tick(ServerLevel level) {
        if (level.getGameTime() % 20 != 0 || PlanetDimensions.find(level).orElse(null) != Planet.VENUS) return;
        var progress = VenusProgress.get(level);
        var loaded = ThermalSystem.loaded(level);
        var served = TransportService.servingMachines(level);
        for (var player : level.players()) {
            if (!player.isAlive() || player.isSpectator()) continue;
            if (progress.observe(player.getUUID(), VenusProgress.Step.ARRIVAL)) {
                player.sendSystemMessage(Component.translatable("venus.guide.welcome").withStyle(s -> s
                        .withColor(ChatFormatting.GOLD).withClickEvent(new ClickEvent.RunCommand("/venus"))));
            }
            var observations = EnumSet.noneOf(VenusProgress.Step.class);
            if (HabitatSystem.protects(player)) observations.add(VenusProgress.Step.SHELTER);
            for (var machine : loaded) {
                if (machine.getBlockPos().distSqr(player.blockPosition()) > 256 || machine.maintenance().failed()) continue;
                if (machine.kind() == ColonyContent.Machine.GEOTHERMAL && machine.completedBatches() > 0)
                    observations.add(VenusProgress.Step.POWER);
                if (machine.kind() == ColonyContent.Machine.EXCHANGER && machine.coolingCharge() > 0)
                    observations.add(VenusProgress.Step.COOLING);
                if (served.contains(machine)) observations.add(VenusProgress.Step.AUTOMATION);
                if (machine.kind() == ColonyContent.Machine.GREENHOUSE && machine.lastCompletedRecipe().equals("hydroponic_wheat"))
                    observations.add(VenusProgress.Step.FOOD);
            }
            var ledger = ExpeditionLedger.get(level);
            if (ledger.claimed(player.getUUID(), 1) || ledger.claimed(player.getUUID(), 2) || ledger.claimed(player.getUUID(), 3))
                observations.add(VenusProgress.Step.EXPEDITION);
            for (var step : observations) progress.observe(player.getUUID(), step);
            VenusGuideNetwork.send(player,progress);
            // Reconcile on reconnect/reload as well as on first observation; awarding has no item rewards.
            for (var step : VenusProgress.Step.values()) if (progress.reached(player.getUUID(), step)) {
                var advancement = level.getServer().getAdvancements().get(SpaceFabric.id("venus_guide/" + step.key()));
                if (advancement != null) player.getAdvancements().award(advancement, "observed");
            }
        }
    }

    public static void initialize() {
        VenusGuideNetwork.initialize();
        ServerTickEvents.END_LEVEL_TICK.register(VenusOnboarding::tick);
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> dispatcher.register(
                Commands.literal("venus").executes(c -> { show(c.getSource().getPlayerOrException()); return 1; })
                        .then(Commands.literal("guia").executes(c -> {
                            var player = c.getSource().getPlayerOrException();
                            pages(player).forEach(player::sendSystemMessage); return 1;
                        }))
                        .then(Commands.literal("recursos")
                                .executes(c -> { c.getSource().sendSuccess(() -> provisions(15, 2, 75), false); return 1; })
                                .then(Commands.argument("minutos", IntegerArgumentType.integer(1, 240))
                                        .then(Commands.argument("ocupantes", IntegerArgumentType.integer(1, 16))
                                                .then(Commands.argument("volumen", IntegerArgumentType.integer(1, SealScanner.MAX_VOLUME))
                                                        .executes(c -> {
                                                            var text = provisions(IntegerArgumentType.getInteger(c, "minutos"),
                                                                    IntegerArgumentType.getInteger(c, "ocupantes"), IntegerArgumentType.getInteger(c, "volumen"));
                                                            c.getSource().sendSuccess(() -> text, false); return 1;
                                                        })))))));
    }
}

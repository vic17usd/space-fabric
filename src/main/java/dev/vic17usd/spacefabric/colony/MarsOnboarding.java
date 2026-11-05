package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.space.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.*;

public final class MarsOnboarding {
    private MarsOnboarding() { }

    /** Called at physical touchdown, before RocketEntity clears its flight phase. */
    public static void landed(RocketEntity rocket) {
        if (!(rocket.level() instanceof ServerLevel level) || !(rocket.getFirstPassenger() instanceof ServerPlayer player)
                || !player.isAlive() || player.isSpectator() || rocket.hull() <= 0 || rocket.isRemoved()) return;
        if (!MarsExpeditionChecks.landed(PlanetDimensions.find(level).orElse(null),
                TravelDestination.at(rocket.destinationIndex()).planet(), TravelDestination.at(rocket.originIndex()).planet(),
                rocket.flightState(), rocket.verticalCollision && rocket.onGround())) return;
        if (MarsProgress.get(level).land(player.getUUID())) {
            player.sendSystemMessage(Component.translatable("mars.guide.welcome").withStyle(s -> s
                    .withColor(ChatFormatting.GOLD).withClickEvent(new ClickEvent.RunCommand("/marte guia"))));
            reconcile(player);
        }
    }

    /** The existing controller interaction selects a base without consuming or changing its contents. */
    public static void selectRefuge(ServerPlayer player, ColonyMachineBlockEntity controller) {
        if (!player.isAlive() || player.isSpectator() || PlanetDimensions.find(player.level()).orElse(null) != Planet.MARS
                || controller.getLevel() != player.level() || controller.isRemoved()
                || controller.kind() != ColonyContent.Machine.HABITAT
                || player.distanceToSqr(controller.getBlockPos().getCenter()) > 64) return;
        if (MarsProgress.get(player.level()).select(player.getUUID(), controller.getBlockPos()))
            player.sendSystemMessage(Component.translatable("mars.guide.selected", controller.getBlockPos().toShortString()));
    }

    private static net.minecraft.world.level.block.entity.BlockEntity loadedEntity(ServerLevel level, BlockPos pos) {
        var chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        return chunk == null ? null : chunk.getBlockEntity(pos);
    }

    private static MarsGuideNetwork.State inspect(ServerPlayer player) {
        var level = player.level();
        var progress = MarsProgress.get(level);
        var entry = progress.entry(player.getUUID());
        if (!entry.started()) return MarsGuideNetwork.State.EMPTY;
        if (entry.complete()) return new MarsGuideNetwork.State(entry.milestones(), entry.refuge(), false, 0, 0, 0);
        var observation = MarsExpeditionChecks.Observation.ABSENT;
        if (PlanetDimensions.find(level).orElse(null) == Planet.MARS && player.isAlive() && !player.isSpectator()
                && !player.isPassenger() && entry.refuge().isPresent()) {
            var pos = entry.refuge().get();
            if (loadedEntity(level, pos) instanceof ColonyMachineBlockEntity controller
                    && !controller.isRemoved() && controller.kind() == ColonyContent.Machine.HABITAT) {
                controller.validate(level);
                var room = controller.enclosure();
                var world = HabitatSystem.world(level);
                int water = MarsExpeditionChecks.storedWater(controller, room, world, p -> loadedEntity(level, p));
                observation = MarsExpeditionChecks.evaluate(room, world, BlockPos.containing(player.getEyePosition()),
                        controller.support(), controller.operating(), water);
                if (progress.observe(player.getUUID(), pos, observation)) {
                    player.sendSystemMessage(Component.translatable("mars.guide.complete").withStyle(ChatFormatting.GOLD));
                    player.sendSystemMessage(Component.translatable("mars.guide.next"));
                }
            }
        }
        entry = progress.entry(player.getUUID());
        int flags = entry.complete() ? entry.milestones() : MarsProgress.ARRIVAL | observation.flags();
        return new MarsGuideNetwork.State(flags, entry.refuge(), observation.inside(),
                observation.oxygen(), observation.energy(), observation.water());
    }

    private static void reconcile(ServerPlayer player) {
        var entry = MarsProgress.get(player.level()).entry(player.getUUID());
        // Idempotent vanilla criteria; there are deliberately no item/XP/function rewards.
        for (var id : new String[]{"arrival", "refuge"}) {
            if (id.equals("arrival") ? !entry.started() : !entry.complete()) continue;
            var advancement = player.level().getServer().getAdvancements().get(SpaceFabric.id("mars_guide/" + id));
            if (advancement != null) player.getAdvancements().award(advancement, "observed");
        }
    }

    private static int show(ServerPlayer player, boolean guide) {
        var state = inspect(player);
        reconcile(player);
        player.sendSystemMessage(Component.translatable(state.has(MarsProgress.COMPLETE) ? "mars.guide.next"
                : state.has(MarsProgress.ARRIVAL) ? "mars.guide.title" : "mars.guide.arrival.detail").withStyle(ChatFormatting.GOLD));
        if (state.has(MarsProgress.ARRIVAL) && !state.has(MarsProgress.COMPLETE)) {
            String[] steps = {"refuge", "sealed", "supplies", "water"};
            for (int i = 0; i < steps.length; i++) player.sendSystemMessage(Component.literal(state.has(2 << i) ? "✓ " : "○ ")
                    .append(Component.translatable("mars.guide." + steps[i])));
            state.refuge().ifPresent(pos -> player.sendSystemMessage(Component.translatable("mars.guide.selected", pos.toShortString())));
            player.sendSystemMessage(Component.translatable("mars.guide.reserves", state.oxygen(), MarsExpeditionChecks.OXYGEN,
                    state.energy(), MarsExpeditionChecks.ENERGY, state.water(), MarsExpeditionChecks.WATER));
            player.sendSystemMessage(Component.translatable("mars.guide.inside"));
        }
        if (guide) for (String page : new String[]{"safety", "refuge.detail", "sealed.detail", "supplies.detail", "water.detail",
                "packing", "recipes.controller", "recipes.power", "recipes.water", "recipes.electrolyzer", "recipes.browser"})
            player.sendSystemMessage(Component.translatable("mars.guide." + page));
        else player.sendSystemMessage(Component.translatable("mars.guide.help"));
        return 1;
    }

    public static void initialize() {
        MarsGuideNetwork.initialize();
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            if (level.getGameTime() % 20 != 0 || PlanetDimensions.find(level).orElse(null) != Planet.MARS) return;
            for (var player : level.players()) {
                MarsGuideNetwork.send(player, inspect(player));
                reconcile(player);
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> dispatcher.register(
                Commands.literal("marte").executes(c -> show(c.getSource().getPlayerOrException(), false))
                        .then(Commands.literal("guia").executes(c -> {
                            var player = c.getSource().getPlayerOrException();
                            if (!MarsGuideNetwork.open(player, inspect(player))) {
                                player.sendSystemMessage(Component.translatable("mars.tutorial.update_client"));
                                return show(player, false);
                            }
                            return 1;
                        }))
                        .then(Commands.literal("texto").executes(c -> show(c.getSource().getPlayerOrException(), true)))));
    }
}

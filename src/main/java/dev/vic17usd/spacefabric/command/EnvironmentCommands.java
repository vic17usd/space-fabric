package dev.vic17usd.spacefabric.command;

import dev.vic17usd.spacefabric.environment.MeteorSystem;
import dev.vic17usd.spacefabric.environment.PlanetEnvironment;
import dev.vic17usd.spacefabric.environment.PlanetWeather;
import dev.vic17usd.spacefabric.environment.PlanetRotation;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

/** Herramientas de operador para probar el ambiente del mundo actual sin alterar el Overworld. */
public final class EnvironmentCommands {
    private EnvironmentCommands() { }
    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> dispatcher.register(
                Commands.literal("planetenvironment")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(Commands.literal("info").executes(command -> weather(command.getSource(), "info")))
                        .then(Commands.literal("storm").executes(command -> weather(command.getSource(), "storm")))
                        .then(Commands.literal("clear").executes(command -> weather(command.getSource(), "clear")))
                        .then(Commands.literal("noon").executes(command -> time(command.getSource(), 6000)))
                        .then(Commands.literal("midnight").executes(command -> time(command.getSource(), 18000)))
                        .then(Commands.literal("meteor").executes(command -> {
                            var source = command.getSource();
                            var player = source.getPlayerOrException();
                            boolean spawned = MeteorSystem.spawn(source.getLevel(), player.blockPosition().getX() + 16,
                                    player.blockPosition().getZ());
                            if (!spawned) source.sendFailure(Component.literal("Requiere Luna/Mercurio, mob_griefing y un chunk cargado con espacio."));
                            return spawned ? 1 : 0;
                        }))));
    }

    private static int time(CommandSourceStack source, long ticks) {
        Planet planet = PlanetDimensions.find(source.getLevel()).orElse(null);
        if (planet == null) { source.sendFailure(Component.literal("No es un destino planetario.")); return 0; }
        var clock = source.getServer().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.WORLD_CLOCK)
                .getOrThrow(PlanetRotation.key(planet));
        source.getServer().clockManager().setTotalTicks(clock, ticks);
        return 1;
    }

    private static int weather(CommandSourceStack source, String action) {
        Planet planet = PlanetDimensions.find(source.getLevel()).orElse(null);
        if (planet == null) { source.sendFailure(Component.literal("Esta dimensión no es un destino de Space Fabric.")); return 0; }
        var data = PlanetWeather.data(source.getLevel(), planet);
        if (!action.equals("info")) {
            if (action.equals("storm") && !PlanetWeather.hasStorms(planet)) {
                source.sendFailure(Component.literal("Este destino no tiene clima de lluvia ni polvo.")); return 0;
            }
            boolean storm = action.equals("storm");
            data.setClearWeatherTime(storm ? 0 : 2400);
            data.setRainTime(storm ? 2400 : 0);
            data.setRaining(storm);
            data.setThundering(false);
        }
        source.sendSuccess(() -> Component.literal(planet.path() + " | día solar: "
                + PlanetEnvironment.of(planet).realMinutesPerGameDay() + " min | tormenta: " + data.isRaining()
                + " | la transición visual tarda unos segundos"), false);
        return 1;
    }
}

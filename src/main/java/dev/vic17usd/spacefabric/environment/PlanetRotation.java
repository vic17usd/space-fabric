package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.gamerules.GameRules;

/** El gestor nativo conserva fracciones de tick, guarda la hora y sincroniza las tasas al cliente. */
public final class PlanetRotation {
    private static Boolean paused;
    private PlanetRotation() { }

    public static ResourceKey<WorldClock> key(Planet planet) {
        return ResourceKey.create(Registries.WORLD_CLOCK, SpaceFabric.id(planet.path()));
    }

    public static void initialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(PlanetRotation::configure);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> {
            if (success) configure(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> paused = null);
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            boolean next = !server.getGameRules().get(GameRules.ADVANCE_TIME);
            if (paused == null || paused != next) synchronizePause(server, next);
        });
    }

    private static void configure(MinecraftServer server) {
        var registry = server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK);
        for (Planet planet : Planet.values()) {
            server.clockManager().setRate(registry.getOrThrow(key(planet)), PlanetEnvironment.of(planet).clockRate());
        }
        synchronizePause(server, !server.getGameRules().get(GameRules.ADVANCE_TIME));
    }

    private static void synchronizePause(MinecraftServer server, boolean value) {
        var registry = server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK);
        for (Planet planet : Planet.values()) server.clockManager().setPaused(registry.getOrThrow(key(planet)), value);
        paused = value;
    }
}

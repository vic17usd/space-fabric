package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.equipment.SuitProtection;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class AtmosphereSystem {
    private static final int DAMAGE_INTERVAL_TICKS = 20;
    private static final float DAMAGE_PER_INTERVAL = 2.0F;

    private AtmosphereSystem() {
    }

    public static void initialize() {
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            if (level.getGameTime() % DAMAGE_INTERVAL_TICKS != 0) {
                return;
            }
            PlanetDimensions.find(level).ifPresent(planet -> {
                for (ServerPlayer player : level.players()) {
                    tick(level, player, planet);
                }
            });
        });
    }

    private static void tick(ServerLevel level, ServerPlayer player, Planet planet) {
        if (!player.isAlive()) {
            return;
        }
        boolean immune = player.isCreative() || player.isSpectator();
        Exposure.hazard(planet.atmosphere(), SuitProtection.isSealed(player), immune)
                .ifPresent(hazard -> player.hurtServer(
                        level, PlanetDamageTypes.source(level, hazard), DAMAGE_PER_INTERVAL));
    }
}

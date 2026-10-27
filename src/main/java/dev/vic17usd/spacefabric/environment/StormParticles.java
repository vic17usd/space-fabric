package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.DustParticleOptions;

public final class StormParticles {
    private StormParticles() { }
    public static void initialize() {
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            if (level.getGameTime() % 20 != 0 || level.getRainLevel(1) < 0.2F) return;
            var planet = PlanetDimensions.find(level).orElse(null);
            if (planet != Planet.MARS && planet != Planet.VENUS) return;
            var dust = new DustParticleOptions(planet == Planet.MARS ? 0xB65D30 : 0xB8BB45, 1.2F);
            for (var player : level.players()) {
                if (level.canSeeSky(player.blockPosition().above())) {
                    level.sendParticles(player, dust, false, false, player.getX(), player.getY() + 1.5,
                            player.getZ(), 8, 5, 2, 5, 0.1);
                }
            }
        });
    }
}

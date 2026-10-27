package dev.vic17usd.spacefabric.planet;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public final class PlanetDimensions {
    private static final Map<ResourceKey<Level>, Planet> PLANETS = Arrays.stream(Planet.values())
            .collect(Collectors.toUnmodifiableMap(PlanetDimensions::key, planet -> planet));

    private PlanetDimensions() {
    }

    /** Es una referencia. El JSON del datapack crea la dimensión durante el arranque del mundo. */
    public static ResourceKey<Level> key(Planet planet) {
        return ResourceKey.create(Registries.DIMENSION, SpaceFabric.id(planet.path()));
    }

    public static ResourceKey<DimensionType> typeKey(Planet planet) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, SpaceFabric.id(planet.path()));
    }

    public static ResourceKey<LevelStem> stemKey(Planet planet) {
        return ResourceKey.create(Registries.LEVEL_STEM, SpaceFabric.id(planet.path()));
    }

    public static Optional<Planet> find(Level level) {
        return Optional.ofNullable(PLANETS.get(level.dimension()));
    }

    public static void initialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (Planet planet : Planet.values()) {
                if (server.getLevel(key(planet)) == null) {
                    throw new IllegalStateException("Falta la dimensión " + SpaceFabric.id(planet.path())
                            + "; revisa los JSON de worldgen y reinicia el servidor.");
                }
            }
            SpaceFabric.LOGGER.info("Los {} destinos del sistema solar están cargados", Planet.values().length);
        });
    }
}

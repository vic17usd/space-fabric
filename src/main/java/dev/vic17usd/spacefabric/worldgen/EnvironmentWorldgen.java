package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class EnvironmentWorldgen {
    static { Registry.register(BuiltInRegistries.FEATURE, SpaceFabric.id("frozen_cave"), new FrozenCaveFeature()); }
    private EnvironmentWorldgen() { }
    public static void initialize() {
        for (var planet : new Planet[] {Planet.MARS, Planet.URANUS, Planet.NEPTUNE}) {
            String feature = planet == Planet.MARS ? "mars_crater" : planet.path() + "_frozen_cave";
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(PlanetBiomes.key(planet)),
                    GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
                    ResourceKey.create(Registries.PLACED_FEATURE, SpaceFabric.id(feature)));
        }
    }
}

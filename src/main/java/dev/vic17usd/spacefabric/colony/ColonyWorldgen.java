package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.worldgen.PlanetBiomes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class ColonyWorldgen {
    private ColonyWorldgen() { }
    public static void initialize() {
        BiomeModifications.addFeature(context->context.getBiomeKey().equals(PlanetBiomes.key(Planet.MARS)),
                GenerationStep.Decoration.UNDERGROUND_ORES,ResourceKey.create(Registries.PLACED_FEATURE,SpaceFabric.id("martian_ground_ice")));
    }
}

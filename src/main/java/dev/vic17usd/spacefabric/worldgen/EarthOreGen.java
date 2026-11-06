package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.earth.EarthMineral;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Biome injection, not chunk-tick replacement: veins are placed only during new terrain generation. */
public final class EarthOreGen {
    private static boolean initialized;
    private EarthOreGen() { }
    public static ResourceKey<PlacedFeature> key(EarthMineral mineral) {
        return ResourceKey.create(Registries.PLACED_FEATURE, SpaceFabric.id("earth_" + mineral.path));
    }
    public static void initialize() {
        if (initialized) return;
        initialized = true;
        for (var mineral : EarthMineral.values()) {
            var selector = BiomeSelectors.foundInOverworld();
            // Bauxite commonly forms through intense weathering in warm regions.
            if (mineral == EarthMineral.BAUXITE) selector = selector.and(BiomeSelectors.includeByKey(
                    Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE,
                    Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA));
            BiomeModifications.addFeature(selector, GenerationStep.Decoration.UNDERGROUND_ORES, key(mineral));
        }
    }
}

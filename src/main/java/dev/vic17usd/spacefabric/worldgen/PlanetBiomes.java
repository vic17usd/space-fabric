package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/** Los JSON del registro dinámico crean los biomas. Estas claves solo los referencian. */
public final class PlanetBiomes {
    public static final ResourceKey<Biome> VENUS_FUMAROLES=ResourceKey.create(Registries.BIOME,SpaceFabric.id("venus_fumaroles"));
    public static final ResourceKey<Biome> VENUS_BASINS=ResourceKey.create(Registries.BIOME,SpaceFabric.id("venus_obsidian_basins"));
    public static final net.minecraft.tags.TagKey<Biome> VENUS_TAG=net.minecraft.tags.TagKey.create(Registries.BIOME,SpaceFabric.id("venus_biomes"));
    public static boolean isVenus(ResourceKey<Biome> key){return key.equals(key(Planet.VENUS))||key.equals(VENUS_FUMAROLES)||key.equals(VENUS_BASINS);}
    private PlanetBiomes() { }

    public static ResourceKey<Biome> key(Planet planet) {
        if (planet == Planet.EARTH) {
            throw new IllegalArgumentException("Tierra conserva los biomas vanilla del Overworld");
        }
        return ResourceKey.create(Registries.BIOME, SpaceFabric.id(planet.path()));
    }
}

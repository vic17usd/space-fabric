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
    public static final ResourceKey<Biome> VENUS_RIFTLANDS=ResourceKey.create(Registries.BIOME,SpaceFabric.id("venus_obsidian_riftlands"));
    public static final ResourceKey<Biome> VENUS_TESSERA=ResourceKey.create(Registries.BIOME,SpaceFabric.id("venus_sulfur_shardlands"));
    public static final ResourceKey<Biome> VENUS_ASHLANDS=ResourceKey.create(Registries.BIOME,SpaceFabric.id("venus_basalt_ashlands"));
    public static final java.util.List<ResourceKey<Biome>> VENUS_KEYS=java.util.List.of(key(Planet.VENUS),VENUS_FUMAROLES,VENUS_BASINS,VENUS_RIFTLANDS,VENUS_TESSERA,VENUS_ASHLANDS);
    public static final net.minecraft.tags.TagKey<Biome> VENUS_TAG=net.minecraft.tags.TagKey.create(Registries.BIOME,SpaceFabric.id("venus_biomes"));
    public static boolean isVenus(ResourceKey<Biome> key){return VENUS_KEYS.contains(key);}
    private PlanetBiomes() { }

    public static ResourceKey<Biome> key(Planet planet) {
        if (planet == Planet.EARTH) {
            throw new IllegalArgumentException("Tierra conserva los biomas vanilla del Overworld");
        }
        return ResourceKey.create(Registries.BIOME, SpaceFabric.id(planet.path()));
    }
}

package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/** Los JSON del registro dinámico crean los biomas. Estas claves solo los referencian. */
public final class PlanetBiomes {
    private PlanetBiomes() { }

    public static ResourceKey<Biome> key(Planet planet) {
        if (planet == Planet.EARTH) {
            throw new IllegalArgumentException("Tierra conserva los biomas vanilla del Overworld");
        }
        return ResourceKey.create(Registries.BIOME, SpaceFabric.id(planet.path()));
    }
}

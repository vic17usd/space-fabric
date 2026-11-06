package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class PlanetFeatures {
    private PlanetFeatures() { }

    public static final CraterFeature CRATER = Registry.register(
            BuiltInRegistries.FEATURE, SpaceFabric.id("crater"), new CraterFeature());

    public static final SulfurClusterFeature SULFUR_CLUSTER = Registry.register(
            BuiltInRegistries.FEATURE, SpaceFabric.id("sulfur_cluster"), new SulfurClusterFeature());

    public static void initialize() { }
}

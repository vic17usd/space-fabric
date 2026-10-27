package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

/** Los biomas vanilla se comparten con el Overworld: filtra la dimensión antes de colocar bloques. */
public final class TerrestrialBasaltFeature extends OreFeature {
    public TerrestrialBasaltFeature() { super(OreConfiguration.CODEC); }

    @Override public boolean place(FeaturePlaceContext<OreConfiguration> context) {
        if (!context.level().getLevel().dimension().equals(PlanetDimensions.key(Planet.EARTH))) return false;
        return super.place(context);
    }
}

package dev.vic17usd.spacefabric;

import dev.vic17usd.spacefabric.atmosphere.AtmosphereSystem;
import dev.vic17usd.spacefabric.command.PlanetCommands;
import dev.vic17usd.spacefabric.gravity.PlanetGravity;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import dev.vic17usd.spacefabric.worldgen.PlanetFeatures;
import dev.vic17usd.spacefabric.fluid.PlanetFluids;
import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.item.ModCreativeTabs;
import dev.vic17usd.spacefabric.worldgen.OreGen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SpaceFabric implements ModInitializer {
    public static final String MOD_ID = "space_fabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        PlanetFeatures.initialize();
        PlanetFluids.initialize();
        ModItems.initialize();
        ModBlocks.initialize();
        ModCreativeTabs.initialize();
        OreGen.initialize();
        PlanetDimensions.initialize();
        PlanetGravity.initialize();
        AtmosphereSystem.initialize();
        PlanetCommands.initialize();
        LOGGER.info("Space Fabric: sistemas planetarios inicializados para Minecraft 26.1");
    }
}

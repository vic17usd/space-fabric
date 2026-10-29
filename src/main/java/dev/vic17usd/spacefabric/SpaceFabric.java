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
import dev.vic17usd.spacefabric.environment.PlanetRotation;
import dev.vic17usd.spacefabric.environment.VenusAcidSystem;
import dev.vic17usd.spacefabric.environment.MeteorSystem;
import dev.vic17usd.spacefabric.environment.StormParticles;
import dev.vic17usd.spacefabric.worldgen.EnvironmentWorldgen;
import dev.vic17usd.spacefabric.command.EnvironmentCommands;
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
        dev.vic17usd.spacefabric.maintenance.MaintenanceContent.initialize();
        ModBlocks.initialize();
        dev.vic17usd.spacefabric.venus.VenusGeology.initialize();
        dev.vic17usd.spacefabric.earth.EarthMaterials.initialize();
        dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.initialize();
        dev.vic17usd.spacefabric.entity.sanctum.ModSanctumEntities.initialize();
        dev.vic17usd.spacefabric.worldgen.structure.MarsSurfaceStructures.initialize();
        dev.vic17usd.spacefabric.worldgen.structure.VenusSurfaceStructures.initialize();
        dev.vic17usd.spacefabric.entity.ModMarsEntities.initialize();
        dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.initialize();
        dev.vic17usd.spacefabric.space.ModSpaceEntities.initialize();
        dev.vic17usd.spacefabric.machine.ModMachines.initialize();
        dev.vic17usd.spacefabric.colony.ColonyContent.initialize();
        dev.vic17usd.spacefabric.industry.IndustryContent.initialize();
        dev.vic17usd.spacefabric.recovery.RecoveryContent.initialize();
        dev.vic17usd.spacefabric.menu.ModMenus.initialize();
        ModCreativeTabs.initialize();
        dev.vic17usd.spacefabric.equipment.SuitEquipment.initialize();
        dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.initialize();
        OreGen.initialize();
        dev.vic17usd.spacefabric.worldgen.EarthOreGen.initialize();
        EnvironmentWorldgen.initialize();
        PlanetDimensions.initialize();
        PlanetGravity.initialize();
        dev.vic17usd.spacefabric.space.RocketCabin.initialize();
        AtmosphereSystem.initialize();
        PlanetRotation.initialize();
        VenusAcidSystem.initialize();
        MeteorSystem.initialize();
        StormParticles.initialize();
        PlanetCommands.initialize();
        dev.vic17usd.spacefabric.command.StructureNames.initialize();
        EnvironmentCommands.initialize();
        LOGGER.info("Space Fabric: sistemas planetarios inicializados para Minecraft 26.1");
    }
}

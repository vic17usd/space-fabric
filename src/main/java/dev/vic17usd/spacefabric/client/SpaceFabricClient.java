package dev.vic17usd.spacefabric.client;

import dev.vic17usd.spacefabric.fluid.PlanetFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

public final class SpaceFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.TRANSPORT_TANK,dev.vic17usd.spacefabric.client.menu.TransportTankScreen::new);
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.MACHINE_INTERFACE,dev.vic17usd.spacefabric.client.menu.MachineInterfaceScreen::new);
        dev.vic17usd.spacefabric.client.colony.VenusGuideHud.initialize();
        dev.vic17usd.spacefabric.client.map.AtlasClient.initialize();
        dev.vic17usd.spacefabric.client.rover.RoverView.initialize();dev.vic17usd.spacefabric.client.rover.AerostatHud.initialize();
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.rover.RoverEntity.TYPE,dev.vic17usd.spacefabric.client.rover.RoverRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.rover.RoverEntity.AEROSTAT_TYPE,dev.vic17usd.spacefabric.client.rover.RoverRenderer::new);
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.FOUNDRY,dev.vic17usd.spacefabric.client.menu.FoundryScreen::new);
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.ROVER,dev.vic17usd.spacefabric.client.menu.RoverScreen::new);
        dev.vic17usd.spacefabric.client.rocket.RocketFlightView.initialize();
        dev.vic17usd.spacefabric.client.colony.HabitatView.initialize();
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.COLONY_MACHINE,dev.vic17usd.spacefabric.client.menu.ColonyMachineScreen::new);
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.HABITAT,dev.vic17usd.spacefabric.client.menu.HabitatScreen::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.COPPER_ACIDSTINGER_DRONE,dev.vic17usd.spacefabric.client.venus.CopperAcidstingerDroneEntityRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.SULFURWING_COPPERSTINGER,dev.vic17usd.spacefabric.client.venus.SulfurwingCopperstingerEntityRenderer::new);
        dev.vic17usd.spacefabric.client.equipment.SuitEquipmentClient.initialize();
        dev.vic17usd.spacefabric.client.recipe.RecipeViewerClient.initialize();
net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.ACIDSTONE_SLIME,dev.vic17usd.spacefabric.client.venus.AcidstoneSlimeEntityRenderer::new);
net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.OBSIDIAN_LAVAFANG,dev.vic17usd.spacefabric.client.venus.ObsidianLavafangEntityRenderer::new);
net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.ASHPLUME_EMBERWALKER,dev.vic17usd.spacefabric.client.venus.AshplumeEmberwalkerEntityRenderer::new);
net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.SULFURWING_COPPER_HARBINGER,dev.vic17usd.spacefabric.client.venus.SulfurwingCopperHarbingerEntityRenderer::new);
        dev.vic17usd.spacefabric.client.rocket.RocketCockpitView.initialize();
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                dev.vic17usd.spacefabric.entity.sanctum.ModSanctumEntities.GOLEM,
                dev.vic17usd.spacefabric.client.sanctum.BasaltEmberGolemEntityRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                dev.vic17usd.spacefabric.entity.sanctum.ModSanctumEntities.GUARD,
                dev.vic17usd.spacefabric.client.sanctum.BasaltBosswardenEntityRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.CRAWLER,
                dev.vic17usd.spacefabric.client.fauna.CrystalHumCrawlerEntityRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.SANDWRAITH,
                dev.vic17usd.spacefabric.client.fauna.GoldenEyeSandwraithEntityRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.IRONMAW,
                dev.vic17usd.spacefabric.client.fauna.RedDuneIronmawEntityRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                dev.vic17usd.spacefabric.entity.ModMarsEntities.COPPER_TRADER,
                dev.vic17usd.spacefabric.client.entity.MartianCopperTraderRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                dev.vic17usd.spacefabric.space.ModSpaceEntities.ROCKET, dev.vic17usd.spacefabric.client.rocket.RocketRenderer::new);
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.ROCKET,
                dev.vic17usd.spacefabric.client.menu.RocketScreen::new);
        net.minecraft.client.gui.screens.MenuScreens.register(dev.vic17usd.spacefabric.menu.ModMenus.REFINERY,
                dev.vic17usd.spacefabric.client.menu.RefineryScreen::new);
        var model = new FluidModel.Unbaked(
                new Material(Identifier.withDefaultNamespace("block/lava_still")),
                new Material(Identifier.withDefaultNamespace("block/lava_flow")), null, null);
        FluidRenderingRegistry.register(PlanetFluids.VISCOUS_LAVA, PlanetFluids.FLOWING_VISCOUS_LAVA, model);
    }
}

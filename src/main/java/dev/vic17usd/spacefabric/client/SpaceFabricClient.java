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

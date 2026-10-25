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
        var model = new FluidModel.Unbaked(
                new Material(Identifier.withDefaultNamespace("block/lava_still")),
                new Material(Identifier.withDefaultNamespace("block/lava_flow")), null, null);
        FluidRenderingRegistry.register(PlanetFluids.VISCOUS_LAVA, PlanetFluids.FLOWING_VISCOUS_LAVA, model);
    }
}

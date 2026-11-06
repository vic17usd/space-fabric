package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.client.rocket.RocketPilotRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityRenderState.class, remap = false)
public abstract class RocketPilotStateMixin implements RocketPilotRenderState {
    @Unique private boolean spaceFabric$insideRocket;
    @Override public boolean spaceFabric$isInsideRocket() { return spaceFabric$insideRocket; }
    @Override public void spaceFabric$setInsideRocket(boolean inside) { spaceFabric$insideRocket = inside; }
}

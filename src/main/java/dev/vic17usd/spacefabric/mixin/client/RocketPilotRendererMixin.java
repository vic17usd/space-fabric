package dev.vic17usd.spacefabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.vic17usd.spacefabric.client.rocket.RocketPilotRenderState;
import dev.vic17usd.spacefabric.space.RocketEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** La cabina es cerrada: no dibuja cuerpo, armadura ni capas del piloto a través de su carcasa. */
@Mixin(value = EntityRenderDispatcher.class, remap = false)
public abstract class RocketPilotRendererMixin {
    @Inject(method = "extractEntity", at = @At("RETURN"))
    private void spaceFabric$recordPilot(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> callback) {
        // Se asigna también false: los snapshots reutilizados vuelven a verse al desmontar.
        ((RocketPilotRenderState) callback.getReturnValue()).spaceFabric$setInsideRocket(
                entity instanceof Player && entity.getVehicle() instanceof RocketEntity);
    }

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void spaceFabric$enclosedPilot(EntityRenderState state, CameraRenderState camera,
            double x, double y, double z, PoseStack poses, SubmitNodeCollector collector, CallbackInfo callback) {
        if (((RocketPilotRenderState) state).spaceFabric$isInsideRocket()) callback.cancel();
    }
}

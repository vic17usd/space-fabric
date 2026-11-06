package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.space.RocketCockpitNetwork;
import dev.vic17usd.spacefabric.space.RocketEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Intercept the use key while seated before the client sends a world interaction. */
@Mixin(Minecraft.class)
public abstract class RocketCockpitUseMixin {
    @Inject(method="startUseItem", at=@At("HEAD"), cancellable=true)
    private void spaceFabric$openRocketMenu(CallbackInfo ci) {
        var client=(Minecraft)(Object)this;
        if(client.screen!=null || client.player==null || !client.player.isAlive() || client.player.isSpectator()
                || !(client.player.getVehicle() instanceof RocketEntity rocket) || rocket.isFlying()
                || !ClientPlayNetworking.canSend(RocketCockpitNetwork.Open.TYPE)) return;
        ClientPlayNetworking.send(new RocketCockpitNetwork.Open());
        ci.cancel();
    }
}

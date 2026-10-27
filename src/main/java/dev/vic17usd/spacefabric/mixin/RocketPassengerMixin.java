package dev.vic17usd.spacefabric.mixin;

import dev.vic17usd.spacefabric.space.RocketEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Prevent an accidental Shift press from ejecting a survival pilot during the cinematic. */
@Mixin(value=Player.class,remap=false)
public abstract class RocketPassengerMixin {
    @Inject(method="wantsToStopRiding",at=@At("HEAD"),cancellable=true)
    private void spaceFabric$flightHarness(CallbackInfoReturnable<Boolean> callback) {
        var player=(Player)(Object)this;
        if(player.isAlive()&&!player.isCreative()&&!player.isSpectator()
                &&player.getVehicle() instanceof RocketEntity rocket&&rocket.isFlying())callback.setReturnValue(false);
    }
}

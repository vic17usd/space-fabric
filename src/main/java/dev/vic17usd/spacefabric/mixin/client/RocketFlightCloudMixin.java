package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.client.rocket.RocketFlightView;
import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=CloudRenderer.class,remap=false)
public abstract class RocketFlightCloudMixin {
    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private void spaceFabric$noCloudsInCruise(CallbackInfo callback){if(RocketFlightView.inSpace())callback.cancel();}
}

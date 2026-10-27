package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.client.rocket.RocketFlightView;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=WeatherEffectRenderer.class,remap=false)
public abstract class RocketFlightWeatherMixin {
    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private void spaceFabric$noRainInCruise(CallbackInfo callback){if(RocketFlightView.inSpace())callback.cancel();}
    @Inject(method="tickRainParticles",at=@At("HEAD"),cancellable=true)
    private void spaceFabric$noRainParticlesInCruise(CallbackInfo callback){if(RocketFlightView.inSpace())callback.cancel();}
}

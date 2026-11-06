package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.environment.PlanetEnvironment;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FogRenderer.class, remap = false)
public abstract class PlanetFogMixin {
    @Inject(method = "setupFog", at = @At("RETURN"))
    private void spaceFabric$storm(Camera camera, int chunks, DeltaTracker delta, float darkness,
                                  ClientLevel level, CallbackInfoReturnable<FogData> callback) {
        if(dev.vic17usd.spacefabric.client.rocket.RocketFlightView.inSpace()) {
            FogData fog=callback.getReturnValue();
            fog.environmentalStart=128;fog.environmentalEnd=256;
            fog.renderDistanceStart=128;fog.renderDistanceEnd=256;
            fog.skyEnd=512;fog.cloudEnd=512;fog.color.set(0,0,.005F,1);
            return;
        }
        if (camera.getFluidInCamera() != FogType.NONE) return;
        if(dev.vic17usd.spacefabric.client.colony.HabitatView.clearInterior(camera)) {
            dev.vic17usd.spacefabric.client.colony.HabitatFog.apply(callback.getReturnValue(),chunks);
            return;
        }
        var planet = PlanetDimensions.find(level).orElse(null);
        if (planet != Planet.MARS && planet != Planet.VENUS) return;
        if(planet==Planet.VENUS&&camera.position().y>=260){
            FogData layer=callback.getReturnValue();float end=camera.position().y>340?128:64;
            layer.environmentalStart=0;layer.environmentalEnd=end;layer.skyEnd=end;layer.cloudEnd=end;
            layer.color.set(.78F,.69F,.43F,1);return;
        }
        if (!level.canSeeSky(BlockPos.containing(camera.position()))) return;
        float intensity = level.getRainLevel(delta.getGameTimeDeltaPartialTick(false));
        if (intensity <= 0.01F) return;
        FogData fog = callback.getReturnValue();
        float end = PlanetEnvironment.stormFogEnd(planet, intensity);
        fog.environmentalStart = Math.min(fog.environmentalStart, 0);
        fog.environmentalEnd = Math.min(fog.environmentalEnd, end);
        fog.skyEnd = Math.min(fog.skyEnd, end);
        fog.cloudEnd = Math.min(fog.cloudEnd, end);
        float r = planet == Planet.MARS ? 0.58F : 0.67F;
        float g = planet == Planet.MARS ? 0.25F : 0.61F;
        float b = planet == Planet.MARS ? 0.10F : 0.15F;
        fog.color.set(fog.color.x + (r - fog.color.x) * intensity,
                fog.color.y + (g - fog.color.y) * intensity,
                fog.color.z + (b - fog.color.z) * intensity, fog.color.w);
    }
}

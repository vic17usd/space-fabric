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
        if (camera.getFluidInCamera() != FogType.NONE) return;
        var planet = PlanetDimensions.find(level).orElse(null);
        if (planet != Planet.MARS && planet != Planet.VENUS) return;
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

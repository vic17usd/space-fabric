package dev.vic17usd.spacefabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.vic17usd.spacefabric.environment.PlanetEnvironment;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkyRenderer.class, remap = false)
public abstract class PlanetSkyMixin {
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void spaceFabric$sky(ClientLevel level, float partial, Camera camera, SkyRenderState state, CallbackInfo callback) {
        PlanetDimensions.find(level).ifPresent(planet -> {
            if (PlanetEnvironment.airless(planet)) {
                state.skyColor = 0xFF000000;
                state.sunriseAndSunsetColor = 0;
                state.starBrightness = PlanetEnvironment.of(planet).stars();
                state.rainBrightness = 1;
            } else if (planet == Planet.MARS) {
                state.starBrightness *= 1 - level.getRainLevel(partial);
            }
        });
    }

    @Inject(method = "renderSun", at = @At("HEAD"), cancellable = true)
    private void spaceFabric$opaqueClouds(float alpha, PoseStack pose, CallbackInfo callback) {
        var level = Minecraft.getInstance().level;
        if (level != null && PlanetDimensions.find(level).map(PlanetEnvironment::of)
                .map(PlanetEnvironment::opaqueSky).orElse(false)) callback.cancel();
    }

    @Inject(method = "renderMoon", at = @At("HEAD"), cancellable = true)
    private void spaceFabric$noTerrestrialMoon(MoonPhase phase, float alpha, PoseStack pose, CallbackInfo callback) {
        var level = Minecraft.getInstance().level;
        if (level != null && PlanetDimensions.find(level).filter(planet -> planet != Planet.EARTH).isPresent()) callback.cancel();
    }
}

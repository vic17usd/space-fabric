package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.space.RocketEntity;
import dev.vic17usd.spacefabric.space.RocketHullGeometry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class RocketCameraMixin {
    @Shadow private Entity entity;
    @Shadow public abstract Vec3 position();
    @Shadow protected abstract void setPosition(Vec3 position);
    @Inject(method="alignWithEntity",at=@At(value="INVOKE",target="Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private void spaceFabric$centerRocket(float partial,CallbackInfo ci) {
        var client=Minecraft.getInstance();
        if(entity==null || entity!=client.player || client.options.getCameraType().isFirstPerson()
                || !(entity.getVehicle() instanceof RocketEntity rocket) || rocket.isCruising()) return;
        double center=rocket.getPosition(partial).y+RocketHullGeometry.height(rocket.variant())*.5;
        double offset=Mth.clamp(center-entity.getEyePosition(partial).y,-1.5,1.5);
        setPosition(position().add(0,offset,0));
    }
}

package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.sphere.*;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class SphereCameraMixin {
    @Shadow private Entity entity;
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f forwards;
    @Shadow @Final private Vector3f panoramicForwards;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private Vector3f left;
    @Shadow private int matrixPropertiesDirty;
    @Shadow protected abstract void setPosition(Vec3 position);
    @Inject(method="alignWithEntity",at=@At("TAIL"))
    private void spaceFabric$sphericalView(float partial,CallbackInfo ci){
        if(entity==null||!(entity.getVehicle() instanceof SphereWalkerEntity walker))return;
        var feet=walker.getPosition(partial);var normal=feet.subtract(SphereGeometry.CENTER).normalize();
        var horizontal=SphereGeometry.rotate(SphereGeometry.tangent(walker.forward(),normal),normal,-java.lang.Math.toRadians(net.minecraft.util.Mth.wrapDegrees(entity.getYRot()-walker.headingYaw())));
        var look=SphereGeometry.look(horizontal,normal,entity.getXRot());
        var cameraLeft=normal.cross(horizontal).normalize();var cameraUp=look.cross(cameraLeft).normalize();
        setPosition(feet.add(normal.scale(1.62)));
        rotation.set(SphereGeometry.camera(look,cameraUp));
        forwards.set((float)look.x,(float)look.y,(float)look.z);panoramicForwards.set(forwards);
        up.set((float)cameraUp.x,(float)cameraUp.y,(float)cameraUp.z);left.set((float)cameraLeft.x,(float)cameraLeft.y,(float)cameraLeft.z);
        matrixPropertiesDirty=3;
    }
}

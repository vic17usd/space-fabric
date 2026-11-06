package dev.vic17usd.spacefabric.client.colony;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;

/** Native PiP already reflects Z. Match the inventory's Z rotation, preserving its face winding. */
public final class MarsGuideCamera {
    private MarsGuideCamera() { }
    public static Quaternionf apply(PoseStack pose, MarsGuideScene.View view) {
        var rotation = new Quaternionf().rotationX(view.pitch()).rotateY(view.yaw());
        // Two axes form a rotation. Reflecting only Y exposes back faces of otherwise solid models.
        pose.scale(-1, -1, 1);
        pose.mulPose(rotation);
        pose.translate(-view.centerX(), -view.centerY(), -view.centerZ());
        return rotation;
    }
}

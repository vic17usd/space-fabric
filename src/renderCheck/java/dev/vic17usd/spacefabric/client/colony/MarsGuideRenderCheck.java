package dev.vic17usd.spacefabric.client.colony;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;

/** CPU contracts and framing checks; this is explicitly not a GPU screenshot test. */
public final class MarsGuideRenderCheck {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    public static void main(String[] args) throws Exception {
        check(GuiGraphicsExtractor.class.getDeclaredField("guiRenderState").getType() == GuiRenderState.class, "26.1 extractor accessor target");
        check(PictureInPictureRenderer.class.isAssignableFrom(MarsGuideScene.Renderer.class), "Native depth-buffered PiP path");
        for (var chapter : MarsGuideAnimation.Chapter.values()) for (int frame = 0; frame <= 320; frame++) {
            double p = frame / 320.0;
            var camera = MarsGuideScene.view(chapter, p);
            var rotation = new Quaternionf().rotationX(camera.pitch()).rotateY(camera.yaw());
            var nativePose = new PoseStack(); nativePose.scale(1, 1, -1); // PictureInPictureRenderer.prepare
            MarsGuideCamera.apply(nativePose, camera);
            check(nativePose.last().pose().determinant() < 0, "Preserve native PiP winding: a one-axis UI reflection exposes back faces");
            var center = new Vector3f(camera.centerX(), camera.centerY(), camera.centerZ());
            nativePose.last().pose().transformPosition(center);
            check(center.length() < .0001F, "Camera target remains centered");
            check(Float.isFinite(camera.scale(292, 86)) && camera.scale(292, 86) > 0, "Finite compact camera scale");
            if (chapter == MarsGuideAnimation.Chapter.SUPPLIES || chapter == MarsGuideAnimation.Chapter.WATER) {
                box(camera, rotation, 0, 0, 0, 1); continue;
            }
            if (chapter == MarsGuideAnimation.Chapter.AIRLOCK) {
                for (int z = 2; z <= 9; z++) for (int x = 2; x <= 4; x++) box(camera, rotation, x, 0, z, 1);
                for (int z = 2; z <= 8; z++) for (int y = 1; y <= 3; y++) box(camera, rotation, 2, y, z, 1);
                box(camera, rotation, 3.2F, 1, MarsGuideAnimation.airlockPlayerZ(p) - .3F, 1.8F);
                continue;
            }
            double cut = MarsGuideAnimation.cutaway(chapter, p);
            for (var b : MarsGuideAnimation.shell()) {
                double built = chapter == MarsGuideAnimation.Chapter.BUILD ? MarsGuideAnimation.construction(b, p) : 1;
                float size = (float)((MarsGuideAnimation.cutFace(b) ? 1 - cut : 1) * built);
                if (size < .005) continue;
                float y = (float)(b.y() + (1 - built) * 1.1 + (b.y() == 4 ? cut * 1.5 : 0));
                box(camera, rotation, b.x() + (1 - size) / 2, y, b.z() + (1 - size) / 2, size);
            }
        }
        System.out.println("OK: Mars guide native PiP/accessor contract, all camera frames, assembly/cutaway bounds and compact camera fit (CPU only).");
    }
    private static void box(MarsGuideScene.View view, Quaternionf rotation, float x, float y, float z, float size) {
        for (int a = 0; a <= 1; a++) for (int b = 0; b <= 1; b++) for (int c = 0; c <= 1; c++) {
            var point = new Vector3f(x + a * size - view.centerX(), y + b * size - view.centerY(), z + c * size - view.centerZ()).rotate(rotation);
            check(Math.abs(point.x) <= view.spanX() / 2 && Math.abs(point.y) <= view.spanY() / 2, "Model corner exceeds camera: " + point);
        }
    }
}

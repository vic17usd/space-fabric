package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.space.RocketEntity;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public final class RocketRenderer extends EntityRenderer<RocketEntity, RocketRenderer.State> {
    private static final Identifier TEXTURE = SpaceFabric.id("textures/entity/rocket.png");
    private final RocketModel model;
    private final RocketCockpitModel cockpit = new RocketCockpitModel();
    public static final class State extends EntityRenderState { float yaw; boolean cockpitView; }
    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context); shadowRadius = 0.9F;
        model = new RocketModel();
    }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(RocketEntity entity, State state, float delta) {
        super.extractRenderState(entity, state, delta); state.yaw = entity.getYRot();
        var client = Minecraft.getInstance();
        var viewer = client.getCameraEntity();
        state.cockpitView = client.options.getCameraType().isFirstPerson()
                && viewer != null && viewer.getVehicle() == entity;
    }
    @Override public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.cockpitView) {
            poses.pushPose();
            // El habitáculo acompaña la cámara durante la interpolación del vehículo.
            // Gira con el cohete, no con la mirada: se pueden observar paredes, suelo y mandos.
            poses.translate(camera.pos.x-state.x, camera.pos.y-state.y, camera.pos.z-state.z);
            poses.mulPose(Axis.YP.rotationDegrees(-state.yaw));
            int cabinLight=0x00F000B0; // Iluminación interior, también en planetas sin luz.
            collector.submitCustomGeometry(poses, RenderTypes.entityCutout(TEXTURE),
                    (pose, vertices) -> cockpit.renderGeometry(pose, vertices, cabinLight, OverlayTexture.NO_OVERLAY));
            poses.popPose();
            super.submit(state, poses, collector, camera);
            return;
        }
        poses.pushPose(); poses.mulPose(Axis.YP.rotationDegrees(180 - state.yaw)); poses.scale(1, -1, -1);
        // Las UV libres de Blockbench no siguen el despliegue UV del cubo vanilla.
        // Enviar los vértices evita que Sodium reutilice las UV del constructor Cube.
        int light = state.lightCoords;
        collector.submitCustomGeometry(poses, RenderTypes.entityCutout(TEXTURE),
                (pose, vertices) -> model.renderGeometry(pose, vertices, light, OverlayTexture.NO_OVERLAY, -1));
        poses.popPose(); super.submit(state, poses, collector, camera);
    }
}

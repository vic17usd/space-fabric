package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.space.RocketEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public final class RocketRenderer extends EntityRenderer<RocketEntity, RocketRenderer.State> {
    private static final Identifier TEXTURE = SpaceFabric.id("textures/entity/rocket.png");
    private final EntityModel<State> model;
    public static final class State extends EntityRenderState { float yaw; }
    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context); shadowRadius = 0.9F;
        model = new RocketModel();
    }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(RocketEntity entity, State state, float delta) {
        super.extractRenderState(entity, state, delta); state.yaw = entity.getYRot();
    }
    @Override public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose(); poses.mulPose(Axis.YP.rotationDegrees(180 - state.yaw)); poses.scale(1, -1, -1);
        collector.submitModel(model, state, poses, model.renderType(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null);
        poses.popPose(); super.submit(state, poses, collector, camera);
    }
}

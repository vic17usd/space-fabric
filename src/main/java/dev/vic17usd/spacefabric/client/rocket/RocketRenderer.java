package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.space.RocketEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
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
        var mesh = new MeshDefinition(); var root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -44, -8, 16, 32, 16), PartPose.ZERO);
        root.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-6, -50, -6, 12, 6, 12).addBox(-3, -56, -3, 6, 6, 6), PartPose.ZERO);
        root.addOrReplaceChild("engine", CubeListBuilder.create().texOffs(0, 48).addBox(-4, -12, -4, 8, 12, 8), PartPose.ZERO);
        root.addOrReplaceChild("fins", CubeListBuilder.create().texOffs(0, 48)
                .addBox(-12, -18, -2, 24, 18, 4).addBox(-2, -18, -12, 4, 18, 24), PartPose.ZERO);
        // UV de las aletas requieren más de 64px de ancho; el atlas original usa 128x128.
        root.addOrReplaceChild("window", CubeListBuilder.create().texOffs(80, 0).addBox(-4, -35, -8.1F, 8, 10, 1), PartPose.ZERO);
        model = new EntityModel<>(LayerDefinition.create(mesh, 128, 128).bakeRoot()) { };
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

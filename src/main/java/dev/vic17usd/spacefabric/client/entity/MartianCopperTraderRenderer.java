package dev.vic17usd.spacefabric.client.entity;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.entity.MartianCopperTraderEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

/** Modelo humano de brazos clásicos para la skin de traje de cobre; no usa UV de aldeano vanilla. */
public final class MartianCopperTraderRenderer extends HumanoidMobRenderer<MartianCopperTraderEntity,
        HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final Identifier TEXTURE = SpaceFabric.id("textures/entity/martian_copper_trader.png");
    public MartianCopperTraderRenderer(EntityRendererProvider.Context context) { super(context, createModel(), 0.5F); }
    private static HumanoidModel<HumanoidRenderState> createModel() {
        var mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
        var root = mesh.getRoot();
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48)
                .addBox(-1, -2, -2, 4, 12, 4), PartPose.offset(5, 2, 0));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48)
                .addBox(-2, 0, -2, 4, 12, 4), PartPose.offset(1.9F, 12, 0));
        return new HumanoidModel<>(LayerDefinition.create(mesh, 64, 64).bakeRoot()) {
            @Override public void setupAnim(HumanoidRenderState state) {
                super.setupAnim(state);
                hat.visible = false; // El casco ya está pintado en la capa base de la skin.
            }
        };
    }
    @Override public HumanoidRenderState createRenderState() { return new HumanoidRenderState(); }
    @Override public Identifier getTextureLocation(HumanoidRenderState state) { return TEXTURE; }
}

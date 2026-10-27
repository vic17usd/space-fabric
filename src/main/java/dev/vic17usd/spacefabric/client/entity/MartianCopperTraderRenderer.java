package dev.vic17usd.spacefabric.client.entity;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.entity.MartianCopperTraderEntity;
import net.minecraft.client.model.npc.BabyVillagerModel;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;

/** Modelo y UV del JAR aportado: aldeano vanilla con su textura original de cobre. */
public final class MartianCopperTraderRenderer extends AgeableMobRenderer<MartianCopperTraderEntity, VillagerRenderState, VillagerModel> {
    private static final Identifier TEXTURE = SpaceFabric.id("textures/entity/martian_copper_trader.png");
    public MartianCopperTraderRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)),
                new BabyVillagerModel(context.bakeLayer(ModelLayers.VILLAGER_BABY)), 0.5F);
    }
    @Override public VillagerRenderState createRenderState() { return new VillagerRenderState(); }
    @Override public Identifier getTextureLocation(VillagerRenderState state) { return TEXTURE; }
    @Override public void extractRenderState(MartianCopperTraderEntity entity, VillagerRenderState state, float delta) {
        super.extractRenderState(entity, state, delta);
        state.villagerData = net.minecraft.world.entity.npc.villager.Villager.createDefaultVillagerData();
        state.isUnhappy = entity.getUnhappyCounter() > 0;
    }
}

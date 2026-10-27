package dev.vic17usd.spacefabric.client.sanctum;

import dev.vic17usd.spacefabric.entity.sanctum.BasaltBosswardenEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class BasaltBosswardenEntityRenderer extends MobRenderer<BasaltBosswardenEntity, BasaltBosswardenRenderState, BasaltBosswardenEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("space_fabric", "textures/entity/basalt_bosswarden.png");

   public BasaltBosswardenEntityRenderer(Context context) {
      super(context, new BasaltBosswardenEntityModel(), 0.597459F);
   }

   public BasaltBosswardenRenderState createRenderState() {
      return new BasaltBosswardenRenderState();
   }

   public void extractRenderState(BasaltBosswardenEntity entity, BasaltBosswardenRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(BasaltBosswardenRenderState state) {
      return TEXTURE;
   }
}

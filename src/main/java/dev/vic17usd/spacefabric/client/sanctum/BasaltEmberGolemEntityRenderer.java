package dev.vic17usd.spacefabric.client.sanctum;

import dev.vic17usd.spacefabric.entity.sanctum.BasaltEmberGolemEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class BasaltEmberGolemEntityRenderer extends MobRenderer<BasaltEmberGolemEntity, BasaltEmberGolemRenderState, BasaltEmberGolemEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("space_fabric", "textures/entity/basalt_ember_golem.png");

   public BasaltEmberGolemEntityRenderer(Context context) {
      super(context, new BasaltEmberGolemEntityModel(), 0.412562F);
   }

   public BasaltEmberGolemRenderState createRenderState() {
      return new BasaltEmberGolemRenderState();
   }

   public void extractRenderState(BasaltEmberGolemEntity entity, BasaltEmberGolemRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(BasaltEmberGolemRenderState state) {
      return TEXTURE;
   }
}

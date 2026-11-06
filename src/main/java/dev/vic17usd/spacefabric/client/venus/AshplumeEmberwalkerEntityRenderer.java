package dev.vic17usd.spacefabric.client.venus;

import dev.vic17usd.spacefabric.entity.venus.AshplumeEmberwalkerEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class AshplumeEmberwalkerEntityRenderer extends MobRenderer<AshplumeEmberwalkerEntity, AshplumeEmberwalkerRenderState, AshplumeEmberwalkerEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "space_fabric", "textures/entity/ashplume_emberwalker.png"
   );

   public AshplumeEmberwalkerEntityRenderer(Context context) {
      super(context, new AshplumeEmberwalkerEntityModel(), 0.45F);
   }

   public AshplumeEmberwalkerRenderState createRenderState() {
      return new AshplumeEmberwalkerRenderState();
   }

   public void extractRenderState(AshplumeEmberwalkerEntity entity, AshplumeEmberwalkerRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(AshplumeEmberwalkerRenderState state) {
      return TEXTURE;
   }
}

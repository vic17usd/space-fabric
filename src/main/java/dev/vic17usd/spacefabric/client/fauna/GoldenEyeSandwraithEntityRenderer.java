package dev.vic17usd.spacefabric.client.fauna;

import dev.vic17usd.spacefabric.entity.fauna.GoldenEyeSandwraithEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class GoldenEyeSandwraithEntityRenderer extends MobRenderer<GoldenEyeSandwraithEntity, GoldenEyeSandwraithRenderState, GoldenEyeSandwraithEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "space_fabric", "textures/entity/golden_eye_sandwraith.png"
   );

   public GoldenEyeSandwraithEntityRenderer(Context context) {
      super(context, new GoldenEyeSandwraithEntityModel(), 0.95F);
   }

   public GoldenEyeSandwraithRenderState createRenderState() {
      return new GoldenEyeSandwraithRenderState();
   }

   public void extractRenderState(GoldenEyeSandwraithEntity entity, GoldenEyeSandwraithRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(GoldenEyeSandwraithRenderState state) {
      return TEXTURE;
   }
}

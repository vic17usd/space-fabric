package dev.vic17usd.spacefabric.client.venus;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import dev.vic17usd.spacefabric.entity.venus.SulfurwingCopperHarbingerEntity;

public class SulfurwingCopperHarbingerEntityRenderer
   extends MobRenderer<SulfurwingCopperHarbingerEntity, SulfurwingCopperHarbingerRenderState, SulfurwingCopperHarbingerEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "space_fabric", "textures/entity/sulfurwing_copper_harbinger.png"
   );

   public SulfurwingCopperHarbingerEntityRenderer(Context context) {
      super(context, new SulfurwingCopperHarbingerEntityModel(), 0.9F);
   }

   public SulfurwingCopperHarbingerRenderState createRenderState() {
      return new SulfurwingCopperHarbingerRenderState();
   }

   public void extractRenderState(SulfurwingCopperHarbingerEntity entity, SulfurwingCopperHarbingerRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(SulfurwingCopperHarbingerRenderState state) {
      return TEXTURE;
   }
}

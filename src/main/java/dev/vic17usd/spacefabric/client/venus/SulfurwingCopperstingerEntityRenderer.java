package dev.vic17usd.spacefabric.client.venus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import dev.vic17usd.spacefabric.entity.venus.SulfurwingCopperstingerEntity;

@Environment(EnvType.CLIENT)
public class SulfurwingCopperstingerEntityRenderer
   extends MobRenderer<SulfurwingCopperstingerEntity, SulfurwingCopperstingerRenderState, SulfurwingCopperstingerEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "space_fabric", "textures/entity/sulfurwing_copperstinger.png"
   );

   public SulfurwingCopperstingerEntityRenderer(Context context) {
      super(context, new SulfurwingCopperstingerEntityModel(), 1.5F);
   }

   public SulfurwingCopperstingerRenderState createRenderState() {
      return new SulfurwingCopperstingerRenderState();
   }

   public void extractRenderState(SulfurwingCopperstingerEntity entity, SulfurwingCopperstingerRenderState state, float tickDelta) {
      super.extractRenderState(entity, state, tickDelta);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(tickDelta);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(tickDelta);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(SulfurwingCopperstingerRenderState state) {
      return TEXTURE;
   }
}

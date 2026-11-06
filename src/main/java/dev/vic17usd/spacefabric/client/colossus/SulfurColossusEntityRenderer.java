package dev.vic17usd.spacefabric.client.colossus;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import dev.vic17usd.spacefabric.entity.colossus.SulfurColossusEntity;

public class SulfurColossusEntityRenderer extends MobRenderer<SulfurColossusEntity, SulfurColossusRenderState, SulfurColossusEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("space_fabric", "textures/entity/sulfur_colossus.png");

   public SulfurColossusEntityRenderer(Context context) {
      super(context, new SulfurColossusEntityModel(), 1.5F);
   }

   public SulfurColossusRenderState createRenderState() {
      return new SulfurColossusRenderState();
   }

   public void extractRenderState(SulfurColossusEntity entity, SulfurColossusRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.coreExposed=entity.isCoreExposed();
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(SulfurColossusRenderState state) {
      return TEXTURE;
   }
}

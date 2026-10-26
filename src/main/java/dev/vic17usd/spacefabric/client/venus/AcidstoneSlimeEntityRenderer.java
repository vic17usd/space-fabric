package dev.vic17usd.spacefabric.client.venus;

import dev.vic17usd.spacefabric.entity.venus.AcidstoneSlimeEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class AcidstoneSlimeEntityRenderer extends MobRenderer<AcidstoneSlimeEntity, AcidstoneSlimeRenderState, AcidstoneSlimeEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("space_fabric", "textures/entity/acidstone_slime.png");

   public AcidstoneSlimeEntityRenderer(Context context) {
      super(context, new AcidstoneSlimeEntityModel(), 0.7F);
   }

   public AcidstoneSlimeRenderState createRenderState() {
      return new AcidstoneSlimeRenderState();
   }

   public void extractRenderState(AcidstoneSlimeEntity entity, AcidstoneSlimeRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(AcidstoneSlimeRenderState state) {
      return TEXTURE;
   }
}

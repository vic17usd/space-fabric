package dev.vic17usd.spacefabric.client.fauna;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import dev.vic17usd.spacefabric.entity.fauna.RedDuneIronmawEntity;

public class RedDuneIronmawEntityRenderer extends MobRenderer<RedDuneIronmawEntity, RedDuneIronmawRenderState, RedDuneIronmawEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("space_fabric", "textures/entity/red_dune_ironmaw.png");

   public RedDuneIronmawEntityRenderer(Context context) {
      super(context, new RedDuneIronmawEntityModel(), 1.5F);
   }

   public RedDuneIronmawRenderState createRenderState() {
      return new RedDuneIronmawRenderState();
   }

   public void extractRenderState(RedDuneIronmawEntity entity, RedDuneIronmawRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(RedDuneIronmawRenderState state) {
      return TEXTURE;
   }
}

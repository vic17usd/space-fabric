package dev.vic17usd.spacefabric.client.venus;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import dev.vic17usd.spacefabric.entity.venus.ObsidianLavafangEntity;

public class ObsidianLavafangEntityRenderer extends MobRenderer<ObsidianLavafangEntity, ObsidianLavafangRenderState, ObsidianLavafangEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("space_fabric", "textures/entity/obsidian_lavafang.png");

   public ObsidianLavafangEntityRenderer(Context context) {
      super(context, new ObsidianLavafangEntityModel(), 0.675F);
   }

   public ObsidianLavafangRenderState createRenderState() {
      return new ObsidianLavafangRenderState();
   }

   public void extractRenderState(ObsidianLavafangEntity entity, ObsidianLavafangRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(ObsidianLavafangRenderState state) {
      return TEXTURE;
   }
}

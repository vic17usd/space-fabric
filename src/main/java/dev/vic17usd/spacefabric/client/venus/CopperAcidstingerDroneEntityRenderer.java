package dev.vic17usd.spacefabric.client.venus;

import dev.vic17usd.spacefabric.entity.venus.CopperAcidstingerDroneEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

@Environment(EnvType.CLIENT)
public class CopperAcidstingerDroneEntityRenderer
   extends MobRenderer<CopperAcidstingerDroneEntity, CopperAcidstingerDroneRenderState, CopperAcidstingerDroneEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "space_fabric", "textures/entity/copper_acidstinger_drone.png"
   );

   public CopperAcidstingerDroneEntityRenderer(Context context) {
      super(context, new CopperAcidstingerDroneEntityModel(), 0.283594F);
   }

   public CopperAcidstingerDroneRenderState createRenderState() {
      return new CopperAcidstingerDroneRenderState();
   }

   public void extractRenderState(CopperAcidstingerDroneEntity entity, CopperAcidstingerDroneRenderState state, float tickDelta) {
      super.extractRenderState(entity, state, tickDelta);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(tickDelta);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(tickDelta);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(CopperAcidstingerDroneRenderState state) {
      return TEXTURE;
   }
}

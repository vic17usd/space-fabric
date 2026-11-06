package dev.vic17usd.spacefabric.client.fauna;

import dev.vic17usd.spacefabric.entity.fauna.CrystalHumCrawlerEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class CrystalHumCrawlerEntityRenderer extends MobRenderer<CrystalHumCrawlerEntity, CrystalHumCrawlerRenderState, CrystalHumCrawlerEntityModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("space_fabric", "textures/entity/crystal_hum_crawler.png");

   public CrystalHumCrawlerEntityRenderer(Context context) {
      super(context, new CrystalHumCrawlerEntityModel(), 0.525F);
   }

   public CrystalHumCrawlerRenderState createRenderState() {
      return new CrystalHumCrawlerRenderState();
   }

   public void extractRenderState(CrystalHumCrawlerEntity entity, CrystalHumCrawlerRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.actionIndex = entity.actionIndex();
      state.actionTimeMillis = entity.actionTimeMillis(partialTick);
      state.horizontalTravelPosition = entity.horizontalTravelPosition(partialTick);
      state.horizontalTravelSpeed = entity.horizontalTravelSpeed();
   }

   public Identifier getTextureLocation(CrystalHumCrawlerRenderState state) {
      return TEXTURE;
   }
}

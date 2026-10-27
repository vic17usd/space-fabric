package dev.vic17usd.spacefabric.client.venus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public final class CopperAcidstingerDroneRenderState extends LivingEntityRenderState {
   public int actionIndex = -1;
   public long actionTimeMillis;
   public float horizontalTravelPosition;
   public float horizontalTravelSpeed;
}

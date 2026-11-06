package dev.vic17usd.spacefabric.client.colossus;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public final class SulfurColossusRenderState extends LivingEntityRenderState {
   public boolean coreExposed;
   public int actionIndex = -1;
   public long actionTimeMillis;
   public float horizontalTravelPosition;
   public float horizontalTravelSpeed;
}

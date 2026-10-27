package dev.vic17usd.spacefabric.client.colony;

import net.minecraft.client.renderer.fog.FogData;

/** Suppress planetary ground fog inside a certified room, retaining vanilla render-distance fog. */
public final class HabitatFog {
    private HabitatFog() { }
    public static void apply(FogData fog,int chunks) {
        float distance=Math.max(chunks,2)*16F;
        fog.environmentalStart=distance;
        fog.environmentalEnd=distance+16;
        fog.color.set(.64F,.70F,.74F,1);
    }
}

package dev.vic17usd.spacefabric.worldgen.structure;

import java.util.*;
import java.util.function.IntBinaryOperator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/** Mars has narrow, deep fissures. Anchor to a dominant plateau, never an isolated peak or fissure. */
public final class MarsTerrain {
    private MarsTerrain() { }

    public static OptionalInt ground(BoundingBox plot,int maxRelief,IntBinaryOperator height) {
        var samples=new ArrayList<Integer>();
        for(int[] p:new int[][]{{plot.minX(),plot.minZ()},{plot.maxX(),plot.maxZ()},
                {plot.minX(),plot.maxZ()},{plot.maxX(),plot.minZ()},{plot.getCenter().getX(),plot.getCenter().getZ()}})
            samples.add(height.applyAsInt(p[0],p[1])-1);
        for(int x=plot.minX();;x=Math.min(x+16,plot.maxX())) {
            for(int z=plot.minZ();;z=Math.min(z+16,plot.maxZ())) {
                samples.add(height.applyAsInt(x,z)-1);
                if(z==plot.maxZ())break;
            }
            if(x==plot.maxX())break;
        }
        Collections.sort(samples);
        int required=(samples.size()*3+3)/4,bestStart=0;
        int bestWidth=Integer.MAX_VALUE;
        // Tightest majority band avoids allowing a few fissure edges to drag an otherwise flat floor down.
        for(int start=0;start+required<=samples.size();start++) {
            int width=samples.get(start+required-1)-samples.get(start);
            if(width<bestWidth){bestWidth=width;bestStart=start;}
        }
        // At least 75% must be one gentle land surface. A broad canyon or hillside is unsuitable.
        if(bestWidth>maxRelief)return OptionalInt.empty();
        int bestEnd=bestStart+required-1;
        return OptionalInt.of(Math.min(samples.get((bestStart+bestEnd)/2),samples.get(bestStart)+VenusTerrain.MAX_RAISE));
    }
}

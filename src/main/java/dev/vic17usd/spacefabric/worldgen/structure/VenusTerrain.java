package dev.vic17usd.spacefabric.worldgen.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.OptionalInt;
import java.util.function.IntBinaryOperator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/** Small, read-only terrain survey shared by locate and actual structure generation. */
public final class VenusTerrain {
    public static final int MAX_RAISE = 4;
    private VenusTerrain() { }

    /** Reject steep sites early; a low reference prevents a tall retaining wall under the settlement. */
    public static OptionalInt ground(BoundingBox plot, int maxRelief, IntBinaryOperator height) {
        var samples = new ArrayList<Integer>();
        int lowest = Integer.MAX_VALUE, highest = Integer.MIN_VALUE;
        // First test opposite corners and centre; unsuitable volcano slopes cost only a few columns.
        for (int[] point : new int[][] {{plot.minX(),plot.minZ()},{plot.maxX(),plot.maxZ()},
                {plot.minX(),plot.maxZ()},{plot.maxX(),plot.minZ()},{plot.getCenter().getX(),plot.getCenter().getZ()}}) {
            int y = height.applyAsInt(point[0], point[1]) - 1;
            lowest = Math.min(lowest,y); highest = Math.max(highest,y); samples.add(y);
            if (highest-lowest > maxRelief) return OptionalInt.empty();
        }
        for (int x=plot.minX();;x=Math.min(x+16,plot.maxX())) {
            for (int z=plot.minZ();;z=Math.min(z+16,plot.maxZ())) {
                int y=height.applyAsInt(x,z)-1;
                lowest=Math.min(lowest,y); highest=Math.max(highest,y); samples.add(y);
                if(highest-lowest>maxRelief)return OptionalInt.empty();
                if(z==plot.maxZ())break;
            }
            if(x==plot.maxX())break;
        }
        Collections.sort(samples);
        return OptionalInt.of(Math.min(samples.get(samples.size()/2),lowest+MAX_RAISE));
    }

    /** A flat building interior, blending back into unmodified terrain at the outer border. */
    public static int surfaceY(BoundingBox plot,int padding,int x,int z,int floor,int natural) {
        if(padding<=0)return floor;
        int distance=Math.max(Math.max(plot.minX()+padding-x,x-(plot.maxX()-padding)),
                Math.max(plot.minZ()+padding-z,z-(plot.maxZ()-padding)));
        double influence=1-Math.clamp((double)distance/padding,0,1);
        return (int)Math.round(natural+(floor-natural)*influence);
    }
}

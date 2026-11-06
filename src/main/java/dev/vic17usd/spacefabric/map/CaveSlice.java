package dev.vic17usd.spacefabric.map;

import java.util.function.IntPredicate;

/** Bounded vertical sampling: never substitutes the surface for an unknown cave. */
public final class CaveSlice {
    private CaveSlice() { }
    public static int band(int feetY){return Math.floorDiv(feetY,8)*8+4;}
    public static int floor(int center,int min,int max,IntPredicate solid) {
        int best=Integer.MIN_VALUE,distance=Integer.MAX_VALUE;
        for(int y=Math.max(min,center-12);y<=Math.min(max-2,center+8);y++) {
            if(solid.test(y)&&!solid.test(y+1)&&!solid.test(y+2)) {
                int delta=Math.abs(y+1-center);
                if(delta<distance){distance=delta;best=y;}
            }
        }
        return best;
    }
}

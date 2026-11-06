package dev.vic17usd.spacefabric.mining;

/** Deterministic finite volume, top-down: every cell is visited at most once per survey. */
public final class BoreGeometry {
    private BoreGeometry() { }
    public static int count(int radius,int depth){return (radius*2+1)*(radius*2+1)*depth;}
    public static int dx(int index,int radius){return index%(radius*2+1)-radius;}
    public static int dz(int index,int radius){return index/(radius*2+1)%(radius*2+1)-radius;}
    public static int down(int index,int radius){return index/((radius*2+1)*(radius*2+1));}
}

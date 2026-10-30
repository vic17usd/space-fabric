package dev.vic17usd.spacefabric.map;

/** Shared raster geometry: transparent round map, brass rim and navigation symbols. */
public final class AtlasStyle {
    private static final double[] STAR_X=new double[10],STAR_Y=new double[10];
    private static final double[] ARROW_X={0,-4,0,4},ARROW_Y={7,-4,-2,-4};
    static {for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5,r=i%2==0?5.5:2.5;STAR_X[i]=Math.cos(a)*r;STAR_Y[i]=Math.sin(a)*r;}}
    private AtlasStyle() { }

    public static boolean inside(double x,double y,double radius) {
        return x*x+y*y<=radius*radius;
    }

    /** ARGB; corners stay transparent rather than covering the world with a square. */
    public static int roundPixel(int x,int y,int size,int terrain) {
        double dx=x+.5-size/2.,dy=y+.5-size/2.;
        double radius=size/2.-1,d=Math.hypot(dx,dy);
        if(d>radius)return 0;
        if(d>radius-1)return 0xFF171C20;
        if(d>radius-2)return 0xFFE1C782;
        if(d>radius-4)return dx+dy<0?0xFFAA915A:0xFF756442;
        if(d>radius-5)return 0xFF1E292C;
        // Short compass ticks inside the rim, every thirty degrees.
        double angle=Math.atan2(dy,dx),step=Math.PI/6;
        if(d>radius-8&&Math.abs(Math.IEEEremainder(angle,step))<.018)return 0xFFBAA36D;
        return terrain;
    }

    public record Point(double x,double y) { }

    /** Radial projection preserves direction at the circular edge, including diagonal targets. */
    public static Point edge(double x,double y,double radius) {
        double distance=Math.hypot(x,y);
        return distance>radius?new Point(x*radius/distance,y*radius/distance):new Point(x,y);
    }

    /** Centered 11px star for the selected destination, 7px diamond for other locations. */
    public static boolean marker(int x,int y,boolean selected) {
        if(!selected)return Math.abs(x)+Math.abs(y)<=3;
        return polygon(x,y,STAR_X,STAR_Y);
    }

    /** Minecraft yaw: zero points south/down; ninety points west/left. */
    public static boolean player(int x,int y,double yaw) {
        double sin=Math.sin(yaw),cos=Math.cos(yaw);
        double right=x*cos+y*sin,forward=-x*sin+y*cos;
        return polygon(right,forward,ARROW_X,ARROW_Y);
    }

    private static boolean polygon(double x,double y,double[] xs,double[] ys) {
        boolean result=false;
        for(int i=0,j=xs.length-1;i<xs.length;j=i++)if((ys[i]>y)!=(ys[j]>y)
                &&x<(xs[j]-xs[i])*(y-ys[i])/(ys[j]-ys[i])+xs[i])result=!result;
        return result;
    }
}

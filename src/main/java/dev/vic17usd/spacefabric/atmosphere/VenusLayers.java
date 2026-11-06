package dev.vic17usd.spacefabric.atmosphere;

/** Explicit compressed height mapping: Y160=surface, Y260≈50km, Y300≈55km. */
public final class VenusLayers {
    private static final double[] Y={160,220,260,300,340,400};
    private static final double[] PRESSURE={9200,1000,100,50,23,8};
    private static final double[] TEMPERATURE={464,200,75,27,-10,-60};
    private VenusLayers() { }
    private static double interpolate(double y,double[] values,boolean logarithmic) {
        if(!Double.isFinite(y)||y<=Y[0])return values[0];
        for(int i=1;i<Y.length;i++)if(y<=Y[i]){
            double t=(y-Y[i-1])/(Y[i]-Y[i-1]);
            return logarithmic?Math.exp(Math.log(values[i-1])*(1-t)+Math.log(values[i])*t):values[i-1]*(1-t)+values[i]*t;
        }
        return values[values.length-1];
    }
    public static double pressure(double y){return interpolate(y,PRESSURE,true);}
    public static double temperature(double y){return interpolate(y,TEMPERATURE,false);}
    public static double flightVertical(double pitch,int throttle,boolean rise,boolean powered,double falling) {
        if(!powered)return Math.max(-.6,falling-.03);
        return Math.clamp(-Math.sin(Math.toRadians(pitch))*throttle*.22+(rise?.22:0),-.25,.25);
    }
}

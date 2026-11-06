package dev.vic17usd.spacefabric.venus.regions;

/** Continuous world-coordinate patterns derived from the supplied terrain concepts. */
public final class RegionRelief {
    private RegionRelief() { }
    public static double phase(long seed){
        long value=seed+0x9E3779B97F4A7C15L;value=(value^(value>>>30))*0xBF58476D1CE4E5B9L;value=(value^(value>>>27))*0x94D049BB133111EBL;value^=value>>>31;
        return (value>>>11)*0x1.0p-53*Math.PI*2;
    }
    public static int tessera(int x,int z){
        double n=Math.sin((x+Math.sin(z*.031)*11)*.073)+Math.cos((z-Math.sin(x*.027)*9)*.067);
        return Math.clamp((int)Math.floor((n+1.55)*4),0,10);
    }
    public static double riftDistance(int x,int z,long seed){
        double p=phase(seed),axis=Math.sin(z*.008+p)*72+Math.sin(z*.027+p*1.7)*13;
        return Math.abs(x-(axis+Math.round((x-axis)/104)*104));
    }
    public static int rift(int x,int z,long seed){
        double d=riftDistance(x,z,seed),p=phase(seed),width=4.2+Math.sin(z*.19+p)*1.2;
        if(d<width)return -Math.clamp(12+(int)(Math.sin(z*.12+p)*2),10,14);
        return Math.clamp((int)((Math.sin(x*.043+p)+Math.cos(z*.057-p)+1)*2),0,6);
    }
    public static boolean fissure(int x,int z,long seed){return Math.abs(Math.sin((x+z*.64)*.115+Math.sin(z*.055+phase(seed))*.8))<.035;}
}

package dev.vic17usd.spacefabric.environment.volcanism;

import dev.vic17usd.spacefabric.equipment.SuitTier;

public final class VolcanicRules {
    public record Impact(float damage,int wear,boolean gas,boolean slow) { }
    public static final Impact NONE=new Impact(0,0,false,false);
    private VolcanicRules() { }
    public static boolean contains(VolcanicCycle.Kind kind,double dx,double feetY,double dz){
        return feetY>=.8&&feetY<=kind.height&&dx*dx+dz*dz<=kind.radius*kind.radius;
    }
    /** Brief volcanic jets exceed the continuous-temperature rating of a planetary suit. */
    public static Impact impact(VolcanicCycle.Kind kind,SuitTier suit,boolean sheltered,boolean immune){
        if(sheltered||immune)return NONE;
        boolean suited=suit.acidResistant&&suit.maxCelsius>=550;
        return kind==VolcanicCycle.Kind.GAS?(suited?new Impact(0,1,false,false):new Impact(2,0,true,true))
            :(suited?new Impact(1,2,false,false):new Impact(4,0,false,true));
    }
    /** Overlapping jets use the worst exposure, never add damage or wear per source. */
    public static Impact merge(Impact a,Impact b){return new Impact(Math.max(a.damage,b.damage),Math.max(a.wear,b.wear),a.gas||b.gas,a.slow||b.slow);}
}

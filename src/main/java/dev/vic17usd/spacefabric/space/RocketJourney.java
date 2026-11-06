package dev.vic17usd.spacefabric.space;

/** Timing and motion shared by the authoritative flight and its client presentation. */
public final class RocketJourney {
    public static final int CRUISING=3,DESCENDING=4;
    public static final int MIN_CRUISE=240,MAX_CRUISE=720;
    private RocketJourney() { }
    public static int duration(TravelDestination from,TravelDestination to) {
        return MIN_CRUISE+60*Math.abs(RocketRules.tier(from.planet())-RocketRules.tier(to.planet()));
    }
    public static int safeDuration(int ticks){return Math.clamp(ticks,MIN_CRUISE,MAX_CRUISE);}
    public static float progress(float ticks,int duration){return Math.clamp(ticks/Math.max(1,duration),0,1);}
    public static float smooth(float progress){float p=Math.clamp(progress,0,1);return p*p*(3-2*p);}
    /** Hold before the outgoing fade while the destination's asynchronous landing search completes. */
    public static int cruiseClock(int elapsed,int duration,boolean waiting){return waiting?Math.min(elapsed,duration-21):elapsed;}
    public static double descentStep(double remaining){return Math.min(Math.max(0,remaining),Math.clamp(.08+remaining*.018,.08,.55));}
    /** Retain a small downward thrust if terrain disappeared below the planned touchdown height. */
    public static double landingThrust(double y,double plannedY){return descentStep(Math.max(.08,y-plannedY));}
    public static boolean validPhase(int phase){return phase>=RocketRules.IDLE&&phase<=DESCENDING;}
    public static float blackout(int phase,float elapsed,int duration,double y,int ceiling) {
        if(phase==RocketRules.ASCENDING)return (float)Math.clamp((y-(ceiling-16))/16,0,1);
        if(phase==CRUISING)return Math.max(1-Math.clamp(elapsed/20,0,1),Math.clamp((elapsed-duration+20)/20,0,1));
        return phase==DESCENDING?1-Math.clamp(elapsed/20,0,1):0;
    }
}

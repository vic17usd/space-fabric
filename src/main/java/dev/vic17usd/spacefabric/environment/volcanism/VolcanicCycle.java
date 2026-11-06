package dev.vic17usd.spacefabric.environment.volcanism;

/** Tick-driven and local: a source always gets a full warning before becoming hazardous. */
public final class VolcanicCycle {
    public enum Kind {
        GAS(120,80,1200,2.5,5),ERUPTION(160,100,2400,4,6);
        public final int warning,active,cooldown;public final double radius,height;
        Kind(int warning,int active,int cooldown,double radius,double height){this.warning=warning;this.active=active;this.cooldown=cooldown;this.radius=radius;this.height=height;}
    }
    public enum Phase { QUIET,WARNING,ACTIVE,RECOVERY }
    private final Kind kind;
    private final int recovery;
    private Phase phase=Phase.QUIET;
    private int remaining;
    public VolcanicCycle(Kind kind,int delay,int extraCooldown){
        this.kind=kind;remaining=Math.clamp(delay,100,300);recovery=kind.cooldown+Math.clamp(extraCooldown,0,600);
    }
    public Kind kind(){return kind;}
    public Phase phase(){return phase;}
    public int remaining(){return remaining;}
    public boolean alert(){return phase==Phase.WARNING||phase==Phase.ACTIVE;}
    public void tick(boolean mayStart){
        if(remaining==1&&!alert()&&!mayStart)return;
        if(--remaining>0)return;
        switch(phase){
            case QUIET,RECOVERY->{phase=Phase.WARNING;remaining=kind.warning;}
            case WARNING->{phase=Phase.ACTIVE;remaining=kind.active;}
            case ACTIVE->{phase=Phase.RECOVERY;remaining=recovery;}
        }
    }
}

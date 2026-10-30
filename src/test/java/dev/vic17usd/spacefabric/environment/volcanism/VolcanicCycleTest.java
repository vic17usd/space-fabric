package dev.vic17usd.spacefabric.environment.volcanism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VolcanicCycleTest {
    @Test void gasHasAFullWarningBeforeItsFiniteBurstAndThenCools(){check(VolcanicCycle.Kind.GAS);}
    @Test void eruptionHasAFullWarningBeforeItsFiniteBurstAndThenCools(){check(VolcanicCycle.Kind.ERUPTION);}
    private void check(VolcanicCycle.Kind kind){
        var c=new VolcanicCycle(kind,100,0);
        for(int i=0;i<99;i++){c.tick(true);assertEquals(VolcanicCycle.Phase.QUIET,c.phase());}
        c.tick(true);assertEquals(VolcanicCycle.Phase.WARNING,c.phase());assertEquals(kind.warning,c.remaining());
        for(int i=0;i<kind.warning-1;i++){c.tick(false);assertEquals(VolcanicCycle.Phase.WARNING,c.phase());}
        c.tick(false);assertEquals(VolcanicCycle.Phase.ACTIVE,c.phase());assertEquals(kind.active,c.remaining());
        for(int i=0;i<kind.active;i++)c.tick(false);
        assertEquals(VolcanicCycle.Phase.RECOVERY,c.phase());assertEquals(kind.cooldown,c.remaining());
        for(int i=0;i<kind.cooldown;i++)c.tick(true);
        assertEquals(VolcanicCycle.Phase.WARNING,c.phase());assertEquals(kind.warning,c.remaining());
    }
    @Test void contentionWaitsBeforeWarningRatherThanStartingAnUntelegraphedBurst(){
        var c=new VolcanicCycle(VolcanicCycle.Kind.GAS,100,0);
        for(int i=0;i<10000;i++)c.tick(false);assertEquals(1,c.remaining());assertEquals(VolcanicCycle.Phase.QUIET,c.phase());
        c.tick(true);assertEquals(VolcanicCycle.Phase.WARNING,c.phase());assertEquals(120,c.remaining());
    }
    @Test void timerBoundsPreventZeroDelayAndCounterOverflow(){
        var c=new VolcanicCycle(VolcanicCycle.Kind.ERUPTION,Integer.MIN_VALUE,Integer.MAX_VALUE);assertEquals(100,c.remaining());
        for(int i=0;i<360;i++)c.tick(true);assertEquals(VolcanicCycle.Phase.RECOVERY,c.phase());assertEquals(3000,c.remaining());
    }
    @Test void constructingAReplacementAfterUnloadStartsQuietWithoutElapsedTimeCatchup(){
        var previous=new VolcanicCycle(VolcanicCycle.Kind.GAS,100,0);for(int i=0;i<250;i++)previous.tick(true);assertEquals(VolcanicCycle.Phase.ACTIVE,previous.phase());
        var reloaded=new VolcanicCycle(VolcanicCycle.Kind.GAS,100,0);assertEquals(VolcanicCycle.Phase.QUIET,reloaded.phase());assertEquals(100,reloaded.remaining());
    }
}

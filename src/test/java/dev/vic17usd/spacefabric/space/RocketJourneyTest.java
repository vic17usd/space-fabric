package dev.vic17usd.spacefabric.space;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

class RocketJourneyTest {
    @BeforeAll static void bootstrap(){net.minecraft.SharedConstants.tryDetectVersion();net.minecraft.server.Bootstrap.bootStrap();}
    @Test void everyRouteHasABoundedCruiseTimeAndLongerRoutesTakeLonger() {
        for(var from:TravelDestination.ALL)for(var to:TravelDestination.ALL) {
            int duration=RocketJourney.duration(from,to);
            assertTrue(duration>=240&&duration<=720);assertEquals(duration,RocketJourney.duration(to,from));
        }
        var moon=TravelDestination.ALL.stream().filter(d->d.id().equals("moon")).findFirst().orElseThrow();
        var neptune=TravelDestination.ALL.stream().filter(d->d.id().equals("neptune")).findFirst().orElseThrow();
        assertTrue(RocketJourney.duration(TravelDestination.at(0),moon)<RocketJourney.duration(TravelDestination.at(0),neptune));
    }
    @Test void descentIsMonotonicStopsOnThePadAndSlowsDownBeforeTouchdown() {
        for(double height:new double[]{.00001,.1,1,5,48,250}) {
            double remaining=height;int ticks=0;
            while(remaining>.001&&ticks++<5000){double step=RocketJourney.descentStep(remaining);assertTrue(step>0&&step<=remaining);remaining-=step;assertTrue(remaining>=0);}
            assertTrue(ticks<5000);assertTrue(remaining<=.001);
        }
        assertTrue(RocketJourney.descentStep(1)<RocketJourney.descentStep(48));assertEquals(0,RocketJourney.descentStep(-5));
    }
    @Test void transitionFadesHideBothEndsOfTheNativeDimensionTransfer() {
        assertEquals(0,RocketJourney.blackout(RocketRules.ASCENDING,0,240,100,312));
        assertEquals(1,RocketJourney.blackout(RocketRules.ASCENDING,0,240,312,312));
        assertEquals(1,RocketJourney.blackout(RocketJourney.CRUISING,0,240,312,312));
        assertEquals(0,RocketJourney.blackout(RocketJourney.CRUISING,40,240,312,312));
        assertEquals(1,RocketJourney.blackout(RocketJourney.CRUISING,240,240,312,312));
        assertEquals(1,RocketJourney.blackout(RocketJourney.DESCENDING,0,240,112,312));
        assertEquals(0,RocketJourney.blackout(RocketJourney.DESCENDING,20,240,100,312));
        assertEquals(0,RocketJourney.progress(-1,240));assertEquals(1,RocketJourney.progress(9999,240));
    }
    @Test void slowTerrainGenerationKeepsSpaceVisibleUntilTheFinalFadeCanReallyBegin() {
        for(int duration:new int[]{240,420,720}) {
            int held=RocketJourney.cruiseClock(duration,duration,true);
            assertEquals(0,RocketJourney.blackout(RocketJourney.CRUISING,held+.99f,duration,312,312));
            for(int tick=0;tick<1200;tick++)held=RocketJourney.cruiseClock(held+1,duration,true);
            assertTrue(held<duration);
            int resumed=RocketJourney.cruiseClock(held+1,duration,false);
            assertTrue(resumed>held);assertEquals(duration-20,resumed);
            assertEquals(duration,RocketJourney.cruiseClock(duration,duration,false));
        }
    }
}

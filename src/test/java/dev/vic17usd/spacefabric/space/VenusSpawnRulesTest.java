package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.entity.venus.VenusSpawnRules;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VenusSpawnRulesTest {
    @Test void peacefulAnimalsShareATwoPerChunkNaturalSpawnLimit() {
        for(int count=0;count<2;count++)for(int roll=0;roll<24;roll++)assertTrue(VenusSpawnRules.admits(false,6000,count,roll));
        for(int count=2;count<8;count++)for(long clock:new long[]{6000,18000})assertFalse(VenusSpawnRules.admits(false,clock,count,0));
    }
    @Test void hostileNaturalSpawnAttemptsAreTwentyFourTimesRarerByDayAndRemainCappedAtNight() {
        int day=0,night=0;
        for(int roll=0;roll<24;roll++){if(VenusSpawnRules.admits(true,6000,0,roll))day++;if(VenusSpawnRules.admits(true,18000,0,roll))night++;}
        assertEquals(1,day);assertEquals(24,night);assertFalse(VenusSpawnRules.admits(true,18000,4,0));
        assertFalse(VenusSpawnRules.night(12999));assertTrue(VenusSpawnRules.night(13000));assertTrue(VenusSpawnRules.night(22999));assertFalse(VenusSpawnRules.night(23000));
        assertTrue(VenusSpawnRules.night(24000*117L+18000));
    }
    @Test void chunkBoundariesIncludeNegativeCoordinatesWithoutCountingNeighbours() {
        assertTrue(VenusSpawnRules.inChunk(.5,15.99,0,0));assertFalse(VenusSpawnRules.inChunk(16,0,0,0));
        assertTrue(VenusSpawnRules.inChunk(-.01,-16,-1,-1));assertFalse(VenusSpawnRules.inChunk(-16.01,-16,-1,-1));
    }
}

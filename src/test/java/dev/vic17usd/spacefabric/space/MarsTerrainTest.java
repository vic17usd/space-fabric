package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.worldgen.structure.MarsTerrain;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsTerrainTest {
    private final BoundingBox plot=new BoundingBox(0,-60,0,140,319,140);
    @Test void isolatedPeakCannotLiftTheSettlementFiftyBlocks() {
        assertEquals(20,MarsTerrain.ground(plot,16,(x,z)->x==0&&z==0?71:21).orElseThrow());
    }
    @Test void narrowDeepFissuresDoNotMakeEveryMartianSettlementImpossible() {
        assertEquals(70,MarsTerrain.ground(plot,16,(x,z)->x==64?-9:71).orElseThrow());
    }
    @Test void sparseShallowFissureEdgesDoNotBuryTheBuildingBelowTheMainSurface() {
        assertEquals(70,MarsTerrain.ground(plot,16,(x,z)->x==64?56:71).orElseThrow());
    }
    @Test void broadCliffsAndContinuousSteepHillsidesAreRejected() {
        assertTrue(MarsTerrain.ground(plot,16,(x,z)->x<70?21:81).isEmpty());
        assertTrue(MarsTerrain.ground(plot,16,(x,z)->20+x).isEmpty());
    }
    @Test void skewedPlateauDoesNotRaiseItsLowGroundByMoreThanFourBlocks() {
        assertEquals(24,MarsTerrain.ground(plot,16,(x,z)->x<35?21:37).orElseThrow());
    }
    @Test void nativeSizeSurveyHasABoundedNumberOfHeightQueries() {
        var calls=new java.util.concurrent.atomic.AtomicInteger();
        assertEquals(64,MarsTerrain.ground(new BoundingBox(0,-60,0,159,319,159),16,(x,z)->{calls.incrementAndGet();return 65;}).orElseThrow());
        assertEquals(126,calls.get());
    }
}

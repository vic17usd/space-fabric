package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.worldgen.structure.VenusTerrain;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VenusTerrainTest {
    private final BoundingBox plot=new BoundingBox(0,-60,0,204,319,204);

    @Test void flatGroundUsesTheSurfaceBlockInsteadOfTheAirAboveIt() {
        assertEquals(64,VenusTerrain.ground(plot,8,(x,z)->65).orElseThrow());
    }
    @Test void aLocalHighPointDoesNotRaiseTheWholeSettlementToTheHilltop() {
        assertEquals(64,VenusTerrain.ground(plot,8,(x,z)->x==0&&z==0?73:65).orElseThrow());
    }
    @Test void aTwentyBlockCliffRejectsTheSiteBeforeGeneratingOrLoadingAnyTemplates() {
        var calls=new AtomicInteger();
        assertTrue(VenusTerrain.ground(plot,8,(x,z)->{calls.incrementAndGet();return x<100?61:81;}).isEmpty());
        assertTrue(calls.get()<=5);
    }
    @Test void theChosenFloorCannotBeMoreThanFourBlocksAboveTheLowestSample() {
        // Even a skewed distribution must not elevate the low edge on a tall foundation.
        assertEquals(64,VenusTerrain.ground(plot,8,(x,z)->x==0&&z==0?61:69).orElseThrow());
    }
    @Test void surveyHasAFixedSmallBudgetIncludingEdgesAndTheCentre() {
        var calls=new AtomicInteger();
        assertTrue(VenusTerrain.ground(plot,8,(x,z)->{calls.incrementAndGet();return 65;}).isPresent());
        assertEquals(201,calls.get());
    }
    @Test void buildingsAreFlatAndTheirOuterBorderBlendsBackIntoTheOriginalGround() {
        var square=new BoundingBox(0,-60,0,63,319,63);
        assertEquals(40,VenusTerrain.surfaceY(square,16,0,32,44,40));
        assertEquals(40,VenusTerrain.surfaceY(square,16,63,32,44,40));
        assertEquals(42,VenusTerrain.surfaceY(square,16,8,32,44,40));
        assertEquals(44,VenusTerrain.surfaceY(square,16,16,32,44,40));
        assertEquals(44,VenusTerrain.surfaceY(square,16,47,32,44,40));
        assertEquals(46,VenusTerrain.surfaceY(square,16,8,32,44,48));
        assertEquals(48,VenusTerrain.surfaceY(square,16,0,32,44,48));
    }
}

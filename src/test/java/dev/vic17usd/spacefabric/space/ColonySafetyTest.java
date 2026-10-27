package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.colony.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ColonySafetyTest {
    private static class Chamber implements SealScanner.World {
        final Map<BlockPos,SealScanner.Cell> changes=new HashMap<>();boolean rated=true;int reads;
        @Override public SealScanner.Cell cell(BlockPos p) {
            reads++;if(changes.containsKey(p))return changes.get(p);
            if(Math.abs(p.getX())<=2&&Math.abs(p.getY())<=2&&Math.abs(p.getZ())<=2) {
                if(Math.abs(p.getX())==2||Math.abs(p.getY())==2||Math.abs(p.getZ())==2)return rated?SealScanner.Cell.RATED:SealScanner.Cell.SOLID;
            }
            return SealScanner.Cell.OPEN;
        }
    }
    @Test void aRoomHasExactlyItsInteriorVolumeAndDoesNotIncludePlayersOutsideTheWall() {
        var world=new Chamber();var room=SealScanner.scan(world,BlockPos.ZERO,true);
        assertTrue(room.sealed());assertEquals(27,room.air().size());assertTrue(room.air().contains(new BlockPos(1,1,1)));
        assertFalse(room.air().contains(new BlockPos(3,0,0)));assertTrue(room.unchanged(world));
    }
    @Test void openingAHatchOrBreakingOneBlockImmediatelyInvalidatesTheCachedSeal() {
        for(var hole:List.of(new BlockPos(2,0,0),new BlockPos(0,2,0),new BlockPos(0,-2,0))) {
            var world=new Chamber();var room=SealScanner.scan(world,BlockPos.ZERO,true);
            world.changes.put(hole,SealScanner.Cell.OPEN);assertFalse(room.unchanged(world));
            assertFalse(SealScanner.scan(world,BlockPos.ZERO,true).sealed());
        }
    }
    @Test void venusRequiresCertifiedWallsWhileMarsAllowsFullStoneWalls() {
        var world=new Chamber();world.rated=false;
        assertEquals(SealScanner.Status.WEAK_WALL,SealScanner.scan(world,BlockPos.ZERO,true).status());
        assertTrue(SealScanner.scan(world,BlockPos.ZERO,false).sealed());
    }
    @Test void unloadedSpaceAndBlockedVentsFailClosed() {
        var world=new Chamber();world.changes.put(new BlockPos(2,0,0),SealScanner.Cell.UNLOADED);
        assertEquals(SealScanner.Status.UNLOADED,SealScanner.scan(world,BlockPos.ZERO,true).status());
        world.changes.put(BlockPos.ZERO,SealScanner.Cell.RATED);
        assertEquals(SealScanner.Status.BLOCKED_VENT,SealScanner.scan(world,BlockPos.ZERO,true).status());
    }
    @Test void theExteriorCannotCreateAnUnlimitedFloodFill() {
        var calls=new int[1];var room=SealScanner.scan(p->{calls[0]++;return SealScanner.Cell.OPEN;},BlockPos.ZERO,false);
        assertFalse(room.sealed());assertTrue(calls[0]<4000);assertTrue(room.air().isEmpty());
    }
    @Test void aChangedInteriorRecomputesThePressureVolume() {
        var world=new Chamber();var room=SealScanner.scan(world,BlockPos.ZERO,true);
        world.changes.put(new BlockPos(1,1,1),SealScanner.Cell.RATED);
        assertFalse(room.unchanged(world));assertEquals(26,SealScanner.scan(world,BlockPos.ZERO,true).air().size());
    }
    @Test void aNormalWorkbenchInsideARatedVenusShellDoesNotMakeTheOuterHullUnsafe() {
        var world=new Chamber();world.changes.put(new BlockPos(1,0,0),SealScanner.Cell.SOLID);
        var room=SealScanner.scan(world,BlockPos.ZERO,true);assertTrue(room.sealed());assertEquals(26,room.air().size());
        world.changes.put(new BlockPos(2,0,0),SealScanner.Cell.SOLID);
        assertFalse(room.unchanged(world));assertFalse(SealScanner.scan(world,BlockPos.ZERO,true).sealed());
    }
    @Test void oxygenAndElectricityAreBothRequiredAndStartupCostsScaleWithVolume() {
        var support=new LifeSupport();assertFalse(support.tick(125,1,false));
        assertTrue(support.fill(0));assertFalse(support.tick(125,1,false));assertTrue(support.fill(1));
        assertTrue(support.tick(125,1,false));assertEquals(2400-250-1,support.oxygen());assertEquals(2395,support.energy());
        assertTrue(support.tick(125,2,false));assertEquals(2400-250-3,support.oxygen());
    }
    @Test void venusNeedsActiveCoolingAndConsumesMorePower() {
        var support=new LifeSupport();support.fill(0);support.fill(1);
        assertFalse(support.tick(125,1,true));assertTrue(support.fill(2));assertTrue(support.tick(125,1,true));
        assertEquals(2385,support.energy());assertEquals(2395,support.coolant());
    }
    @Test void aBreachLosesPressureAndOxygenButDoesNotDuplicateStoredPower() {
        var support=new LifeSupport();support.fill(0);support.fill(1);support.fill(2);support.tick(27,1,true);
        int energy=support.energy(),coolant=support.coolant();support.vent();
        assertFalse(support.pressurized());assertEquals(0,support.oxygen());assertEquals(energy,support.energy());assertEquals(coolant,support.coolant());
        assertFalse(support.tick(27,1,true));
    }
    @Test void loadedReservesAreClampedAndCellsNeverOverflowTheTank() {
        var support=new LifeSupport();support.restore(Integer.MAX_VALUE,-999,100,10000,true);
        assertEquals(9600,support.oxygen());assertEquals(0,support.energy());assertFalse(support.fill(0));
        assertFalse(support.tick(513,0,false));assertFalse(support.pressurized());
    }
    @Test void anEmptySealedRoomDoesNotConsumeRespirationOxygenAfterPressurizing() {
        var support=new LifeSupport();support.fill(0);support.fill(1);support.tick(27,0,false);int oxygen=support.oxygen();
        for(int i=0;i<10;i++)assertTrue(support.tick(27,0,false));
        assertEquals(oxygen,support.oxygen());
    }
    @Test void supplyFailuresIdentifyTheActualMissingResource() {
        var support=new LifeSupport();assertFalse(support.tick(75,1,true));
        assertEquals(LifeSupport.Failure.OXYGEN,support.failure());
        support.fill(0);assertFalse(support.tick(75,1,true));assertEquals(LifeSupport.Failure.POWER,support.failure());
        support.fill(1);assertFalse(support.tick(75,1,true));assertEquals(LifeSupport.Failure.COOLANT,support.failure());
        support.fill(2);assertTrue(support.tick(75,1,true));assertEquals(LifeSupport.Failure.NONE,support.failure());
    }
    @Test void regulatedReadingsNeverPretendThatAFailedHabitatHasSafePressureOrTemperature() {
        var r=new HabitatReadings(HabitatReadings.Condition.PRESSURIZED,3000,3000,3000,75,true);
        assertEquals(1013,r.pressureDeciKpa());assertEquals(22,r.temperatureCelsius());assertEquals(HabitatReadings.Reserve.NONE,r.lowReserve());
        for(var condition:HabitatReadings.Condition.values()) {
            if(condition==HabitatReadings.Condition.PRESSURIZED)continue;
            var unsafe=new HabitatReadings(condition,9600,9600,9600,75,true);
            assertFalse(unsafe.regulated());assertEquals(-1,unsafe.pressureDeciKpa());assertEquals(-1,unsafe.temperatureCelsius());
        }
    }
    @Test void reserveWarningsRespectCoolingRequirementsAndNeverExceedTheirBars() {
        var mars=new HabitatReadings(HabitatReadings.Condition.PRESSURIZED,3000,3000,0,75,false);
        assertEquals(HabitatReadings.Reserve.NONE,mars.lowReserve());
        assertEquals(HabitatReadings.Reserve.COOLANT,new HabitatReadings(mars.condition(),3000,3000,10,75,true).lowReserve());
        assertEquals(HabitatReadings.Reserve.ENERGY,new HabitatReadings(mars.condition(),3000,10,3000,75,true).lowReserve());
        assertEquals(HabitatReadings.Reserve.OXYGEN,new HabitatReadings(mars.condition(),10,3000,3000,75,true).lowReserve());
        assertEquals(0,HabitatReadings.fraction(-1));assertEquals(1,HabitatReadings.fraction(Integer.MAX_VALUE));
    }

}

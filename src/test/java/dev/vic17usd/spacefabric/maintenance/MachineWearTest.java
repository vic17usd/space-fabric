package dev.vic17usd.spacefabric.maintenance;

import dev.vic17usd.spacefabric.colony.SealScanner;
import java.util.*;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MachineWearTest {
    private static void seconds(MachineWear wear,int seconds){for(int i=0;i<seconds*20;i++)wear.tick();}
    private static MachineWear exposed(){var wear=new MachineWear();wear.sample(MachineWear.Risk.ATMOSPHERE,0);return wear;}
    @Test void exposureOnlyAdvancesLoadedCorrosiveTimeAndNeverCatchesUpOnClockGaps() {
        var wear=new MachineWear();assertFalse(wear.tick());assertEquals(MachineWear.State.FRESH,wear.state());
        wear.sample(MachineWear.Risk.ATMOSPHERE,900000);seconds(wear,1);assertEquals(4,wear.state().damage());
        wear.sample(MachineWear.Risk.NONE,100000000);var before=wear.state();seconds(wear,500);assertEquals(before,wear.state());
    }
    @Test void filtersReduceWearAndCoatingAbsorbsDamageBeforeTheMachine() {
        var plain=exposed();var filtered=exposed();var coated=exposed();var both=exposed();
        filtered.apply(MachineWear.Action.FILTER);coated.apply(MachineWear.Action.COAT);both.apply(MachineWear.Action.FILTER);both.apply(MachineWear.Action.COAT);
        for(var wear:List.of(plain,filtered,coated,both))seconds(wear,100);
        assertEquals(400,plain.state().damage());assertEquals(100,filtered.state().damage());assertEquals(1100,filtered.state().filter());
        assertEquals(0,coated.state().damage());assertEquals(2000,coated.state().coating());assertEquals(2300,both.state().coating());
    }
    @Test void protectionCannotBeRefilledEarlyAndRepairOnlyChangesActualDamage() {
        var wear=exposed();assertFalse(wear.apply(MachineWear.Action.REPAIR));
        assertTrue(wear.apply(MachineWear.Action.COAT));assertTrue(wear.apply(MachineWear.Action.COAT));
        var full=wear.state();assertFalse(wear.apply(MachineWear.Action.COAT));assertEquals(full,wear.state());
        assertTrue(wear.apply(MachineWear.Action.FILTER));var filter=wear.state();assertFalse(wear.apply(MachineWear.Action.FILTER));assertEquals(filter,wear.state());
        wear.restore(new MachineWear.State(10000,300,0,true,7));assertTrue(wear.failed());assertFalse(wear.allowsWork(0));
        assertTrue(wear.apply(MachineWear.Action.REPAIR));assertEquals(7500,wear.state().damage());assertEquals(300,wear.state().coating());assertEquals(7,wear.state().phase());
        assertTrue(wear.allowsWork(0));assertFalse(wear.allowsWork(1));
    }
    @Test void saturationPersistsAndReplacementRestoresOneInstalledFilter() {
        var wear=exposed();wear.apply(MachineWear.Action.FILTER);seconds(wear,1200);
        assertEquals(0,wear.state().filter());assertTrue(wear.state().installed());assertEquals(1200,wear.state().damage());
        assertTrue(wear.apply(MachineWear.Action.FILTER));assertEquals(1200,wear.state().filter());assertTrue(wear.state().installed());
        assertFalse(wear.apply(MachineWear.Action.FILTER));
    }
    @Test void hazeAndNearbySourcesAccelerateAttackButNeverChangeOtherPlanets() {
        assertEquals(MachineWear.Risk.NONE,MachineExposure.classify(false,true,false,true));
        assertEquals(MachineWear.Risk.NONE,MachineExposure.classify(true,false,true,true));
        assertEquals(MachineWear.Risk.SOURCE,MachineExposure.classify(true,true,true,false));
        assertEquals(MachineWear.Risk.HAZE,MachineExposure.classify(true,false,false,true));
        var wear=new MachineWear();wear.sample(MachineWear.Risk.SOURCE,0);wear.apply(MachineWear.Action.FILTER);seconds(wear,100);
        assertEquals(400,wear.state().damage());assertEquals(800,wear.state().filter());
    }
    @Test void stageTransitionsArePredictableAndFailureIsReversibleWithoutResettingProtection() {
        var wear=exposed();seconds(wear,625);assertEquals(MachineWear.Stage.SERVICE,wear.stage());
        seconds(wear,625);assertEquals(MachineWear.Stage.DEGRADED,wear.stage());assertFalse(wear.allowsWork(3));
        seconds(wear,750);assertEquals(MachineWear.Stage.CRITICAL,wear.stage());
        seconds(wear,500);assertTrue(wear.failed());assertEquals(0,wear.integrity());
        wear.apply(MachineWear.Action.REPAIR);assertFalse(wear.failed());assertEquals(2500,wear.integrity());
    }
    @Test void savedFractionalSecondsSurviveRelocationAndMalformedValuesAreBounded() {
        var wear=exposed();for(int i=0;i<13;i++)wear.tick();
        var moved=new MachineWear();moved.restore(wear.state());moved.sample(MachineWear.Risk.ATMOSPHERE,0);
        for(int i=0;i<7;i++)moved.tick();assertEquals(4,moved.state().damage());assertEquals(0,moved.state().phase());
        var clamped=new MachineWear.State(-1,Integer.MAX_VALUE,-1,false,200);
        assertEquals(0,clamped.damage());assertEquals(4800,clamped.coating());assertEquals(0,clamped.filter());assertEquals(19,clamped.phase());
    }
    @Test void certifiedRoomShieldsInteriorMachinesButNotAnExposedWallFaceOrRoofOnly() {
        var machine=BlockPos.ZERO;var changes=new HashMap<BlockPos,SealScanner.Cell>();
        SealScanner.World world=p->{
            if(changes.containsKey(p))return changes.get(p);
            if(p.equals(machine))return SealScanner.Cell.RATED;
            if(Math.abs(p.getX())<=3&&Math.abs(p.getZ())<=3&&p.getY()>=-1&&p.getY()<=3)
                return Math.abs(p.getX())==3||Math.abs(p.getZ())==3||p.getY()==-1||p.getY()==3?SealScanner.Cell.RATED:SealScanner.Cell.OPEN;
            return SealScanner.Cell.OPEN;
        };
        var room=SealScanner.scan(world,machine.above(),true);assertTrue(room.sealed());
        assertTrue(MachineExposure.enclosed(room,machine,world));assertFalse(MachineExposure.enclosed(room,new BlockPos(3,0,0),world));
        changes.put(new BlockPos(3,1,0),SealScanner.Cell.OPEN);
        assertFalse(MachineExposure.enclosed(room,machine,world));
        assertFalse(MachineExposure.enclosed(SealScanner.scan(world,machine.above(),true),machine,world));
    }
}

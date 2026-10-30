package dev.vic17usd.spacefabric.rover;

import dev.vic17usd.spacefabric.planet.Planet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoverSystemsTest {
    @Test void emptyRoverNeedsEnergyOxygenAndCoolingToSealOnVenus() {
        var s=RoverSystems.State.EMPTY;assertFalse(s.protects(Planet.VENUS,64));
        s=s.supply(RoverSystems.Supply.POWER);assertFalse(s.protects(Planet.VENUS,64));
        s=s.supply(RoverSystems.Supply.OXYGEN);assertFalse(s.protects(Planet.VENUS,64));
        s=s.supply(RoverSystems.Supply.CHILLED);assertTrue(s.protects(Planet.VENUS,64));
        assertFalse(new RoverSystems.State(s.energy(),s.oxygen(),s.coolant(),0).protects(Planet.VENUS,64));
        assertTrue(new RoverSystems.State(1,0,0,100).protects(Planet.EARTH,64));
    }
    @Test void occupiedLifeSupportHasNoFreeLastTickAndIdleUnloadedTimeDoesNotAdvance() {
        var full=new RoverSystems.State(2400,4800,4800,100);
        assertEquals(full,full.tick(false,false,Planet.VENUS,200000000));
        var last=new RoverSystems.State(1,1,1,100).tick(true,true,Planet.VENUS,20);
        assertEquals(0,last.energy());assertEquals(0,last.oxygen());assertEquals(0,last.coolant());assertFalse(last.protects(Planet.VENUS,64));
        var earth=full.tick(true,false,Planet.EARTH,20);assertEquals(2399,earth.energy());assertEquals(4800,earth.oxygen());assertEquals(4800,earth.coolant());
        var mars=full.tick(true,true,Planet.MARS,21);assertEquals(2399,mars.energy());assertEquals(4799,mars.oxygen());assertEquals(4800,mars.coolant());
    }
    @Test void suppliesRefusePartialLoadsAndRepairNeverRefillsConsumables() {
        var s=new RoverSystems.State(23000,9500,9500,50);
        for(var supply:new RoverSystems.Supply[]{RoverSystems.Supply.POWER,RoverSystems.Supply.OXYGEN,RoverSystems.Supply.WATER,RoverSystems.Supply.CHILLED})assertEquals(s,s.supply(supply));
        var repaired=s.supply(RoverSystems.Supply.REPAIR);assertEquals(new RoverSystems.State(23000,9500,9500,75),repaired);
        assertEquals(4800,RoverSystems.State.EMPTY.supply(RoverSystems.Supply.CHILLED).coolant());
        assertEquals(2400,RoverSystems.State.EMPTY.supply(RoverSystems.Supply.WATER).coolant());
    }
    @Test void pressureAndThermalLimitsRejectDeepGiantsAndCryogenicWorlds() {
        var s=new RoverSystems.State(24000,9600,9600,100);
        assertTrue(s.protects(Planet.MARS,64));assertTrue(s.protects(Planet.VENUS,64));
        assertFalse(s.protects(Planet.URANUS,240));assertFalse(s.protects(Planet.NEPTUNE,240));
        assertFalse(s.protects(Planet.JUPITER,-50));assertFalse(s.protects(Planet.SATURN,-50));
    }
    @Test void accelerationAndReverseAreBoundedAndBrakesSlowTheRoverWithoutPower() {
        double speed=0;for(int i=0;i<1000;i++)speed=RoverSystems.speed(speed,1,true,false);assertEquals(.24,speed);
        for(int i=0;i<1000;i++)speed=RoverSystems.speed(speed,-1,true,false);assertEquals(-.10,speed);
        assertTrue(Math.abs(RoverSystems.speed(speed,0,false,true))<Math.abs(speed));
        assertEquals(0,RoverSystems.speed(0,1,false,false));
    }
    @Test void optimizedRoverHalvesTractionAndCoolingWhileStillConsumingOxygenAndAuxiliaryPower(){
        var original=new RoverSystems.State(24000,9600,9600,100);var improved=original.optimize();
        for(int tick=1;tick<=200;tick++){original=original.tick(true,true,Planet.VENUS,tick);improved=improved.tick(true,true,Planet.VENUS,tick);}
        assertEquals(23790,original.energy());assertEquals(23890,improved.energy());assertEquals(9400,original.oxygen());assertEquals(9400,improved.oxygen());
        assertEquals(9400,original.coolant());assertEquals(9500,improved.coolant());assertTrue(improved.optimized());
        assertEquals(improved,improved.optimize());assertEquals(improved,improved.tick(false,true,Planet.VENUS,5000000));
        assertFalse(new RoverSystems.State(0,1,1,100,true).protects(Planet.VENUS,64));
    }
    @Test void refillingAndRepairingAnOptimizedRoverPreserveTheUpgradeWithoutChangingCapacities(){
        var state=new RoverSystems.State(0,0,0,50,true);
        for(var supply:RoverSystems.Supply.values()){state=state.supply(supply);assertTrue(state.optimized());}
        assertEquals(2400,state.energy());assertEquals(4800,state.oxygen());assertEquals(7200,state.coolant());assertEquals(75,state.hull());
        var loaded=RoverSystems.State.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,RoverSystems.State.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE,state).getOrThrow()).getOrThrow();assertEquals(state,loaded);
        var old=RoverSystems.State.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,com.google.gson.JsonParser.parseString("{\"energy\":1200,\"hull\":90}")).getOrThrow();assertFalse(old.optimized());assertEquals(1200,old.energy());
    }
}

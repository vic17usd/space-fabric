package dev.vic17usd.spacefabric.environment.volcanism;

import dev.vic17usd.spacefabric.equipment.SuitTier;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VolcanicRulesTest {
    @Test void hazardsStayInsideTheirShortVerticalJet(){
        var gas=VolcanicCycle.Kind.GAS;assertTrue(VolcanicRules.contains(gas,2.5,1,0));assertFalse(VolcanicRules.contains(gas,2.51,1,0));
        assertFalse(VolcanicRules.contains(gas,0,.79,0));assertFalse(VolcanicRules.contains(gas,0,5.01,0));assertFalse(VolcanicRules.contains(gas,2,1,2));
        assertTrue(VolcanicRules.contains(VolcanicCycle.Kind.ERUPTION,4,6,0));assertFalse(VolcanicRules.contains(VolcanicCycle.Kind.ERUPTION,0,6.01,0));
    }
    @Test void sealedVehiclesHabitatsAndCreativeImmunitySuppressAllNewEffects(){
        for(var kind:VolcanicCycle.Kind.values())for(var suit:SuitTier.values()){
            assertEquals(VolcanicRules.NONE,VolcanicRules.impact(kind,suit,true,false));assertEquals(VolcanicRules.NONE,VolcanicRules.impact(kind,suit,false,true));
        }
    }
    @Test void ProperSuitsFilterGasButStillWearAndOnlyReduceEruptionHeat(){
        for(var suit:new SuitTier[]{SuitTier.VENUS,SuitTier.ABYSSAL}){
            var gas=VolcanicRules.impact(VolcanicCycle.Kind.GAS,suit,false,false);assertEquals(0,gas.damage());assertEquals(1,gas.wear());assertFalse(gas.gas());
            var lava=VolcanicRules.impact(VolcanicCycle.Kind.ERUPTION,suit,false,false);assertEquals(1,lava.damage());assertEquals(2,lava.wear());
        }
        var none=VolcanicRules.impact(VolcanicCycle.Kind.GAS,SuitTier.NONE,false,false);assertEquals(2,none.damage());assertTrue(none.gas());
        assertEquals(4,VolcanicRules.impact(VolcanicCycle.Kind.ERUPTION,SuitTier.CRYO,false,false).damage());
    }
    @Test void severalJetsNeverStackDamageOrGearWear(){
        var gas=VolcanicRules.impact(VolcanicCycle.Kind.GAS,SuitTier.NONE,false,false);var lava=VolcanicRules.impact(VolcanicCycle.Kind.ERUPTION,SuitTier.NONE,false,false);
        var worst=VolcanicRules.merge(gas,lava);assertEquals(4,worst.damage());assertTrue(worst.gas());
        for(int i=0;i<1000;i++)worst=VolcanicRules.merge(worst,lava);assertEquals(4,worst.damage());
        var suited=VolcanicRules.impact(VolcanicCycle.Kind.ERUPTION,SuitTier.VENUS,false,false);assertEquals(suited,VolcanicRules.merge(suited,suited));
    }
    @Test void configurationSupportsVisualOnlyAndRejectsAmbiguousValues(){
        assertEquals(new VolcanicConfig.Settings(true,true),VolcanicConfig.parse("{}"));
        assertEquals(new VolcanicConfig.Settings(true,false),VolcanicConfig.parse("{\"damage\":false}"));
        assertEquals(new VolcanicConfig.Settings(false,true),VolcanicConfig.parse("{\"enabled\":false}"));
        assertThrows(IllegalArgumentException.class,()->VolcanicConfig.parse("{\"enabled\":\"false\"}"));assertThrows(IllegalArgumentException.class,()->VolcanicConfig.parse("{\"damage\":1}"));
    }
}

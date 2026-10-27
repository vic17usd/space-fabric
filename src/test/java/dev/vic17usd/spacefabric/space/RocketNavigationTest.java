package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class RocketNavigationTest {
    private static int destination(Planet planet){return TravelDestination.indexOf(PlanetDimensions.key(planet));}
    private static RocketNavigation.State state(RocketVariant variant,int source,int selected,int flight,int fuel,int cost,int hull,boolean pilot,boolean occupied,boolean ready) {
        return new RocketNavigation.State(variant,source,selected,flight,fuel,cost,variant.fuelCapacity(),hull,pilot,occupied,false,ready);
    }
    @ParameterizedTest @EnumSource(RocketVariant.class)
    void everyRocketCanSelectItsOwnMissionBeforeBoardingAndLaunchAfterBoarding(RocketVariant variant) {
        int target=destination(variant.mission);int cost=RocketRules.cost(TravelDestination.at(0),TravelDestination.at(target));
        var parked=state(variant,0,target,RocketRules.IDLE,variant.fuelCapacity(),cost,100,false,false,true);
        assertEquals(RocketNavigation.Reason.READY,parked.destination(target),variant.path);
        assertEquals(RocketNavigation.Reason.READY,parked.board());assertEquals(RocketNavigation.Reason.BOARD_FIRST,parked.launch());
        assertEquals(RocketNavigation.Reason.READY,state(variant,0,target,RocketRules.IDLE,variant.fuelCapacity(),cost,100,true,true,true).launch(),variant.path);
    }
    @Test void higherTiersReachMarsButLunarRocketGivesARangeReason() {
        int mars=destination(Planet.MARS);
        assertEquals(RocketNavigation.Reason.READY,state(RocketVariant.MERCURY,0,mars,0,3000,400,100,true,true,true).destination(mars));
        assertEquals(RocketNavigation.Reason.RANGE,state(RocketVariant.MOON,0,mars,0,3000,400,100,true,true,true).destination(mars));
        assertEquals(RocketNavigation.Reason.READY,state(RocketVariant.MARS,0,mars,0,3000,400,100,true,true,true).launch());
    }
    @Test void lowTierCannotEvadeProgressionByDepartingFromAnAdvancedPlanet() {
        assertEquals(RocketNavigation.Reason.RANGE,state(RocketVariant.MOON,destination(Planet.NEPTUNE),0,0,3000,400,100,true,true,true).launch());
    }
    @Test void currentDimensionDoesNotPretendToBeAnUnsupportedPlanet() {
        int mars=destination(Planet.MARS);
        var state=state(RocketVariant.MARS,mars,mars,0,3000,0,100,true,true,true);
        assertEquals(RocketNavigation.Reason.CURRENT_WORLD,state.destination(mars));assertEquals(RocketNavigation.Reason.CURRENT_WORLD,state.launch());
        assertEquals(RocketNavigation.Reason.READY,state.destination(0));
    }
    @Test void flightOccupancyFuelAndHullGiveDistinctReasons() {
        int mars=destination(Planet.MARS);
        assertEquals(RocketNavigation.Reason.SYNC,state(RocketVariant.MARS,0,mars,0,3000,400,100,false,false,false).destination(mars));
        assertEquals(RocketNavigation.Reason.OTHER_PILOT,state(RocketVariant.MARS,0,mars,0,3000,400,100,false,true,true).destination(mars));
        assertEquals(RocketNavigation.Reason.OTHER_PILOT,state(RocketVariant.MARS,0,mars,0,3000,400,100,false,true,true).board());
        assertEquals(RocketNavigation.Reason.IN_FLIGHT,state(RocketVariant.MARS,0,mars,1,3000,400,100,true,true,true).launch());
        assertEquals(RocketNavigation.Reason.BROKEN_HULL,state(RocketVariant.MARS,0,mars,0,3000,400,0,true,true,true).launch());
        assertEquals(RocketNavigation.Reason.LOW_FUEL,state(RocketVariant.MARS,0,mars,0,399,400,100,true,true,true).launch());
        assertEquals(RocketNavigation.Reason.UNKNOWN_WORLD,state(RocketVariant.MARS,-1,mars,0,3000,0,100,true,true,true).launch());
    }
    @Test void fuelGaugeUsesSyncedCapacityAndClampsCorruptValues() {
        assertEquals(70,new RocketNavigation.State(RocketVariant.MARS,0,5,0,3000,400,6000,100,true,true,false,true).fuelBarWidth(140));
        assertEquals(140,state(RocketVariant.MARS,0,5,0,Integer.MAX_VALUE,400,100,true,true,true).fuelBarWidth(140));
        assertEquals(0,new RocketNavigation.State(RocketVariant.MARS,0,5,0,3000,400,0,100,true,true,false,false).fuelBarWidth(140));
        assertEquals(0,state(RocketVariant.MARS,0,5,0,-1,400,100,true,true,true).fuelBarWidth(140));
    }
}

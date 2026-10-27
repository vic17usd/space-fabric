package dev.vic17usd.spacefabric.space;

import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class RocketRulesTest {
    @Test void closedDestinationListIncludesHomeAndEveryCelestialBody() {
        assertEquals(10, TravelDestination.ALL.size());
        assertEquals(10, TravelDestination.ALL.stream().map(TravelDestination::world).distinct().count());
        assertEquals(0, TravelDestination.indexOf(Level.OVERWORLD));
        assertEquals(-1, TravelDestination.indexOf(Level.NETHER));
        assertThrows(IllegalArgumentException.class, () -> TravelDestination.at(-1));
        assertThrows(IllegalArgumentException.class, () -> TravelDestination.at(10));
    }
    @Test void roundTripsFitTheirMissionTankAndBudgetRespectsOriginGravity() {
        for (var origin : TravelDestination.ALL) for (var destination : TravelDestination.ALL) {
            int cost = RocketRules.cost(origin, destination);
            if (origin == destination) assertEquals(0, cost);
            else {
                assertTrue(cost >= 200);assertEquals(0,cost%100);
                int tier=Math.max(RocketRules.tier(origin.planet()),RocketRules.tier(destination.planet()));
                for(var variant:RocketVariant.values())if(variant.rank()>=tier)
                    assertTrue(cost+RocketRules.cost(destination,origin)<=variant.fuelCapacity(),origin+" -> "+destination+" : "+variant);
            }
        }
    }
    @Test void budgetsUsePhysicalReferencesAndAreAsymmetric() {
        assertEquals(11.186,RocketFuelBudget.escapeKms(dev.vic17usd.spacefabric.planet.Planet.EARTH),.001);
        assertEquals(59.5,RocketFuelBudget.escapeKms(dev.vic17usd.spacefabric.planet.Planet.JUPITER),.01);
        double[] mars=RocketFuelBudget.transferKms(dev.vic17usd.spacefabric.planet.Planet.EARTH,dev.vic17usd.spacefabric.planet.Planet.MARS);
        assertEquals(2.945,mars[0],.01);assertEquals(2.649,mars[1],.01);
        var earth=TravelDestination.at(0);var moon=TravelDestination.ALL.stream().filter(d->d.planet()==dev.vic17usd.spacefabric.planet.Planet.MOON).findFirst().orElseThrow();
        assertTrue(RocketRules.cost(earth,moon)>RocketRules.cost(moon,earth));
    }
    @Test void fartherMissionProgressionRequiresMoreFuelFromEarth() {
        int previous=0;
        for(var planet:new dev.vic17usd.spacefabric.planet.Planet[]{
                dev.vic17usd.spacefabric.planet.Planet.MOON,dev.vic17usd.spacefabric.planet.Planet.VENUS,
                dev.vic17usd.spacefabric.planet.Planet.MARS,dev.vic17usd.spacefabric.planet.Planet.MERCURY,
                dev.vic17usd.spacefabric.planet.Planet.JUPITER,dev.vic17usd.spacefabric.planet.Planet.SATURN,
                dev.vic17usd.spacefabric.planet.Planet.URANUS,dev.vic17usd.spacefabric.planet.Planet.NEPTUNE}) {
            var target=TravelDestination.ALL.stream().filter(d->d.planet()==planet).findFirst().orElseThrow();
            int cost=RocketRules.cost(TravelDestination.at(0),target);
            assertTrue(cost>previous,planet+" must have greater mission fuel budget");previous=cost;
        }
    }
    @Test void launchRejectsNonPilotsRepeatedRequestsAndInsufficientFuel() {
        assertTrue(RocketRules.canLaunch(true, RocketRules.IDLE, 500, 500, true));
        assertFalse(RocketRules.canLaunch(false, RocketRules.IDLE, 500, 500, true));
        assertFalse(RocketRules.canLaunch(true, RocketRules.ASCENDING, 500, 500, true));
        assertFalse(RocketRules.canLaunch(true, RocketRules.TRANSFERRING, 500, 500, true));
        assertFalse(RocketRules.canLaunch(true, RocketRules.IDLE, 499, 500, true));
        assertFalse(RocketRules.canLaunch(true, RocketRules.IDLE, 500, 0, true));
        assertFalse(RocketRules.canLaunch(true, RocketRules.IDLE, 500, 500, false));
    }
    @ParameterizedTest @ValueSource(ints = {-1, 2001, 3000, Integer.MAX_VALUE})
    void aFullCellCannotBePartiallyConsumedOrOverflow(int fuel) { assertFalse(RocketRules.fitsCell(fuel)); }
    @Test void ascentHasPositiveAccelerationAndAnUpperSpeedLimit() {
        assertTrue(RocketRules.ascentSpeed(1) > RocketRules.ascentSpeed(0));
        assertEquals(1.2, RocketRules.ascentSpeed(10000), 0.0001);
        assertTrue(RocketRules.ascentSpeed(-1) > 0);
    }
}

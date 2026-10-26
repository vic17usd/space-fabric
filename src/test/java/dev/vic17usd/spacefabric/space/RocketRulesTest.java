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
    @Test void allReturnTripsFitTheTankAndChargeSymmetrically() {
        for (var origin : TravelDestination.ALL) for (var destination : TravelDestination.ALL) {
            int cost = RocketRules.cost(origin, destination);
            assertEquals(cost, RocketRules.cost(destination, origin));
            if (origin == destination) assertEquals(0, cost);
            else { assertTrue(cost >= 200); assertTrue(cost * 2 <= RocketRules.TANK_CAPACITY); }
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

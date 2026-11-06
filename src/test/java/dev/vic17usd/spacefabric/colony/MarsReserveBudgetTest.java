package dev.vic17usd.spacefabric.colony;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsReserveBudgetTest {
    @Test void accountsForBreathingPowerStartupAndEmptyRoom() {
        var empty = MarsReserveBudget.requirement(100, 0, false);
        assertEquals(200, empty.oxygen()); assertEquals(3750, empty.energy()); assertEquals(2000, empty.water());
        var occupied = MarsReserveBudget.requirement(100, 2, false);
        assertEquals(1700, occupied.oxygen()); assertEquals(3750, occupied.energy());
        assertEquals(1500, MarsReserveBudget.requirement(100, 2, true).oxygen());
    }
    @Test void countOnlyInputsWithRoomForReturnedContainers() {
        var stock = MarsReserveBudget.controllerStock(100, 200, 3, 2, 2, 1, 0);
        assertEquals(2500, stock.oxygen()); assertEquals(200, stock.energy()); assertEquals(2000, stock.water());
        assertThrows(IllegalArgumentException.class, () -> MarsReserveBudget.controllerStock(0, 0, -1, 0, 0, 0, 0));
    }
    @Test void refusesInvalidVolumeAndHandlesLargeOccupancyWithoutOverflow() {
        assertThrows(IllegalArgumentException.class, () -> MarsReserveBudget.requirement(0, 1, false));
        assertThrows(IllegalArgumentException.class, () -> MarsReserveBudget.requirement(1, -1, false));
        assertTrue(MarsReserveBudget.requirement(512, Integer.MAX_VALUE, false).oxygen() > Integer.MAX_VALUE);
    }
}

package dev.vic17usd.spacefabric.colony;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ThermalBudgetTest {
    @Test void warnsBeforeTripAndRequiresCoolingToReset() {
        var budget = new ThermalBudget();
        budget.tick(7_000, 0, 0);
        assertTrue(budget.warning());
        assertFalse(budget.tripped());
        budget.tick(1_000, 1_000, 0);
        assertTrue(budget.tripped());
        budget.tick(0, 0, 3_000);
        assertTrue(budget.tripped());
        budget.tick(0, 0, 1_000);
        assertFalse(budget.tripped());
    }

    @Test void clampsCorruptValuesAndLargeInputs() {
        var budget = new ThermalBudget();
        budget.restore(-50, true);
        assertEquals(0, budget.heat());
        assertFalse(budget.tripped());
        budget.tick(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        assertEquals(ThermalBudget.CAPACITY, budget.heat());
        assertTrue(budget.tripped());
        budget.tick(-1, -1, Integer.MAX_VALUE);
        assertEquals(0, budget.heat());
    }

    @Test void savedTripRemainsUntilResetThreshold() {
        var budget = new ThermalBudget();
        budget.restore(6_000, true);
        assertTrue(budget.tripped());
        budget.restore(5_000, true);
        assertFalse(budget.tripped());
    }
}

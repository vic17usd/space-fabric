package dev.vic17usd.spacefabric.colony;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsNightTrialTest {
    @Test void uninterruptedNightCompletesOnlyAtDawn() {
        var state = MarsNightTrial.advance(MarsNightTrial.State.waiting(), new MarsNightTrial.Sample(20, 12000, true));
        for (int t = 1; t < 12000; t++) {
            state = MarsNightTrial.advance(state, new MarsNightTrial.Sample(20 + t, 12000 + t, true));
            assertEquals(MarsNightTrial.Status.RUNNING, state.status());
        }
        state = MarsNightTrial.advance(state, new MarsNightTrial.Sample(12020, 24000, true));
        assertEquals(MarsNightTrial.Status.COMPLETE, state.status());
    }
    @Test void skippedTimeUnloadedBaseAndLeaksDoNotComplete() {
        var start = MarsNightTrial.advance(MarsNightTrial.State.waiting(), new MarsNightTrial.Sample(10, 12000, true));
        assertEquals(MarsNightTrial.Status.FAILED, MarsNightTrial.advance(start, new MarsNightTrial.Sample(11, 24000, true)).status());
        assertEquals(MarsNightTrial.Status.FAILED, MarsNightTrial.advance(start, new MarsNightTrial.Sample(30, 12001, true)).status());
        assertEquals(MarsNightTrial.Status.FAILED, MarsNightTrial.advance(start, new MarsNightTrial.Sample(11, 12001, false)).status());
        assertEquals(MarsNightTrial.Status.WAITING, MarsNightTrial.advance(MarsNightTrial.State.waiting(), new MarsNightTrial.Sample(20, 12001, true)).status());
    }
    @Test void estimatedBudgetsIncludeMarginAndRespectCapacity() {
        var one = MarsNightTrial.reserves(1);
        assertEquals(750, one.oxygen()); assertEquals(3750, one.energy()); assertEquals(2000, one.water()); assertTrue(one.fitsController());
        assertFalse(MarsNightTrial.reserves(16).fitsController());
    }
}

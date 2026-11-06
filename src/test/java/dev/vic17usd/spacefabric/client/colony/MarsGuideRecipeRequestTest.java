package dev.vic17usd.spacefabric.client.colony;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsGuideRecipeRequestTest {
    @Test void replacingSelectionRejectsLateAndDuplicateReplies() {
        var gate = new MarsGuideRecipeRequest();
        gate.begin(10, 100, true); gate.begin(11, 200, true);
        assertFalse(gate.accept(10, 300)); assertTrue(gate.pending());
        assertTrue(gate.accept(11, 400)); assertFalse(gate.pending());
        assertFalse(gate.accept(11, 500));
    }
    @Test void timeoutDiscardsReplyEvenIfTickHasNotRunAndRetryGetsItsOwnDeadline() {
        var gate = new MarsGuideRecipeRequest(); gate.begin(5, 0, true);
        assertFalse(gate.accept(5, 10_001)); assertTrue(gate.expire(10_001));
        gate.begin(6, 20_000, true); assertFalse(gate.expire(20_001));
        assertFalse(gate.accept(5, 20_002)); assertTrue(gate.accept(6, 20_003));
    }
    @Test void closedScreensAndUnsupportedServersHaveNoPendingRequest() {
        var gate = new MarsGuideRecipeRequest(); assertFalse(gate.accept(1, 0));
        gate.begin(1, 0, false); assertFalse(gate.pending()); assertFalse(gate.accept(1, 10));
        gate.begin(2, 0, true); assertTrue(gate.accept(2, 10_000));
    }
}

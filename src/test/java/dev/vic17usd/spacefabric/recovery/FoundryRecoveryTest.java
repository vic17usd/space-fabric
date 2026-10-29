package dev.vic17usd.spacefabric.recovery;

import com.mojang.serialization.JsonOps;
import java.util.List;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FoundryRecoveryTest {
    @Test void rewardIsOncePerSiteAndSurvivesSavingReenrollmentAndConsoleReplacement() {
        var anchor=new BlockPos(40,66,-500);var units=List.of(anchor.east(3),anchor.east(6),anchor.east(9));var ledger=new FoundryLedger();
        ledger.enroll(anchor,units);assertTrue(ledger.activate(anchor));assertFalse(ledger.activate(anchor));
        assertTrue(ledger.enroll(anchor,units).activated());
        var restored=FoundryLedger.CODEC.parse(JsonOps.INSTANCE,FoundryLedger.CODEC.encodeStart(JsonOps.INSTANCE,ledger).getOrThrow()).getOrThrow();
        assertFalse(restored.activate(anchor));assertTrue(restored.nearby(anchor.north(5)).orElseThrow().activated());
        assertTrue(restored.nearby(anchor.north(17)).isEmpty());assertFalse(restored.activate(BlockPos.ZERO));
        var other=anchor.east(2000);restored.enroll(other,List.of(other.east(3),other.east(6),other.east(9)));assertTrue(restored.activate(other));
    }
    @Test void diagnosingAnInstallationRequiresRealRepairActiveFiltersAndProduction() {
        assertEquals(FoundryConsoleBlockEntity.Status.REPAIR,FoundryConsoleBlockEntity.classify(0,1200,1));
        assertEquals(FoundryConsoleBlockEntity.Status.REPAIR,FoundryConsoleBlockEntity.classify(6999,1200,1));
        assertEquals(FoundryConsoleBlockEntity.Status.FILTER,FoundryConsoleBlockEntity.classify(10000,0,20));
        assertEquals(FoundryConsoleBlockEntity.Status.RUN,FoundryConsoleBlockEntity.classify(10000,1200,0));
        assertEquals(FoundryConsoleBlockEntity.Status.READY,FoundryConsoleBlockEntity.classify(7000,1,1));
    }
}

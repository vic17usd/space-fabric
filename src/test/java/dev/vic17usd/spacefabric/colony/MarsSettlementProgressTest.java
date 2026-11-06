package dev.vic17usd.spacefabric.colony;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsSettlementProgressTest {
    private static MarsProgress previousSave(UUID player) {
        return MarsProgress.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(
                "{\"players\":{\"" + player + "\":{\"milestones\":63}}}"))
                .result().orElseThrow();
    }
    private static MarsProgress roundtrip(MarsProgress progress) {
        var encoded = MarsProgress.CODEC.encodeStart(JsonOps.INSTANCE, progress).result().orElseThrow();
        return MarsProgress.CODEC.parse(JsonOps.INSTANCE, encoded).result().orElseThrow();
    }
    @Test void oldSaveUnlocksAndEachMilestoneSurvivesSerialization() {
        UUID player = UUID.randomUUID(); var p = previousSave(player);
        var site = new BlockPos(16, 70, 16); var analyzer = new BlockPos(30, 70, 30); var ticket = UUID.randomUUID();
        assertTrue(p.entry(player).complete()); assertEquals(0, p.settlement(player).stage());
        assertTrue(p.visitSettlement(player, site)); p = roundtrip(p);
        assertFalse(p.visitSettlement(player, site));
        assertFalse(p.inspectSettlement(player, site.east(), site)); assertTrue(p.inspectSettlement(player, site, site));
        p = roundtrip(p); assertTrue(p.selectAnalyzer(player, analyzer, 4, ticket));
        assertFalse(p.recordAnalysis(player, analyzer, ticket, 4, true));
        assertFalse(p.recordAnalysis(player, analyzer, ticket, 5, false));
        assertFalse(p.recordAnalysis(player, analyzer.east(), ticket, 5, true));
        assertTrue(p.recordAnalysis(player, analyzer, ticket, 5, true)); p = roundtrip(p);
        assertFalse(p.completeSettlement(player, site.east())); assertTrue(p.completeSettlement(player, site));
        p = roundtrip(p); assertEquals(5, p.settlement(player).stage());
        assertFalse(p.completeSettlement(player, site));
    }
    @Test void unfinishedAndOtherPlayersCannotBorrowProgress() {
        UUID owner = UUID.randomUUID(), other = UUID.randomUUID(); var p = previousSave(owner);
        var site = new BlockPos(1, 64, 1);
        assertFalse(p.visitSettlement(other, site)); assertTrue(p.visitSettlement(owner, site));
        assertFalse(p.inspectSettlement(other, site, site)); assertEquals(0, p.settlement(other).stage());
        assertFalse(p.completeSettlement(owner, site));
    }

    @Test void cancelledAndReplacedCommissionCannotCreditTheNewMachineOrAnotherPlayer() {
        UUID player = UUID.randomUUID(); var p = previousSave(player);
        var site = new BlockPos(16, 70, 16); var a = site.east(); var b = site.west();
        p.visitSettlement(player, site); p.inspectSettlement(player, site, site);
        var old = UUID.randomUUID(); p.selectAnalyzer(player, a, 2, old);
        assertTrue(p.cancelAnalysis(player)); assertFalse(p.cancelAnalysis(player));
        var ticket = UUID.randomUUID(); assertTrue(p.selectAnalyzer(player, b, 0, ticket)); p = roundtrip(p);
        assertFalse(p.recordAnalysis(player, a, old, 3, true));
        assertFalse(p.recordAnalysis(player, b, old, 3, true));
        assertFalse(p.recordAnalysis(UUID.randomUUID(), b, ticket, 3, true));
        assertTrue(p.recordAnalysis(player, b, ticket, 1, true));
        assertFalse(p.recordAnalysis(player, b, ticket, 2, true));
    }
    @Test void destroyedSurveyPointCanBeMovedWithinTheSameVillageWithoutLosingAnalysis() {
        UUID player = UUID.randomUUID(); var p = previousSave(player); var site = new BlockPos(1, 64, 1);
        p.visitSettlement(player, site); p.inspectSettlement(player, site, site);
        assertFalse(p.relocatePoint(player, site.east(), site.east()));
        assertTrue(p.relocatePoint(player, site, site.above())); p = roundtrip(p);
        assertEquals(site.above(), p.settlement(player).point().orElseThrow());
        assertEquals(2, p.settlement(player).stage());
    }
}

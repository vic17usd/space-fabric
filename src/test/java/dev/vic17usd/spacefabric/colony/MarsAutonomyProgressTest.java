package dev.vic17usd.spacefabric.colony;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsAutonomyProgressTest {
    private MarsProgress load(UUID p, int stage) {
        return MarsProgress.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"players\":{\""+p+"\":{\"milestones\":63}},\"settlements\":{\""+p+"\":{\"stage\":"+stage+"}}}" )).getOrThrow();
    }
    @Test void oldSavesUnlockOnlyAfterSettlementAndAcceptancePersistsPerPlayer() {
        var player=UUID.randomUUID(); var other=UUID.randomUUID(); var p=load(player,4);
        assertEquals(MarsProgress.AutonomyStatus.LOCKED,p.autonomyStatus(player)); assertFalse(p.acceptAutonomy(player));
        p=load(player,5); assertEquals(MarsProgress.AutonomyStatus.AVAILABLE,p.autonomyStatus(player));
        var before=MarsProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow(); p.autonomyStatus(player);
        assertEquals(before,MarsProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow());
        assertFalse(p.acceptAutonomy(other)); assertTrue(p.acceptAutonomy(player)); assertFalse(p.acceptAutonomy(player));
        p=MarsProgress.CODEC.parse(JsonOps.INSTANCE,MarsProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow()).getOrThrow();
        assertEquals(MarsProgress.AutonomyStatus.ACCEPTED,p.autonomyStatus(player)); assertEquals(MarsProgress.AutonomyStatus.LOCKED,p.autonomyStatus(other));
    }
    @Test void missingFirstStageCannotUnlockEvenWithSettlementFlag() {
        var player=UUID.randomUUID(); var p=MarsProgress.CODEC.parse(JsonOps.INSTANCE,JsonParser.parseString("{\"settlements\":{\""+player+"\":{\"stage\":5}}}")).getOrThrow();
        assertEquals(MarsProgress.AutonomyStatus.LOCKED,p.autonomyStatus(player)); assertFalse(p.acceptAutonomy(player));
    }
}

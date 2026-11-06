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
    @Test void bindingRequiresAcceptanceIsPersonalAndSurvivesRoundTrip() {
        var a=UUID.randomUUID(); var b=UUID.randomUUID(); var id=UUID.randomUUID();
        var p=MarsProgress.CODEC.parse(JsonOps.INSTANCE,JsonParser.parseString("{\"players\":{\""+a+"\":{\"milestones\":63},\""+b+"\":{\"milestones\":63}},\"settlements\":{\""+a+"\":{\"stage\":5},\""+b+"\":{\"stage\":5}}}")).getOrThrow();
        var pos=new net.minecraft.core.BlockPos(1,70,2); var second=new net.minecraft.core.BlockPos(4,70,5);
        assertFalse(p.bindAutonomyRefuge(a,pos,id)); assertTrue(p.acceptAutonomy(a)); assertTrue(p.acceptAutonomy(b));
        assertTrue(p.bindAutonomyRefuge(a,pos,id)); assertFalse(p.bindAutonomyRefuge(a,pos,id));
        assertTrue(p.bindAutonomyRefuge(b,pos,id)); assertTrue(p.bindAutonomyRefuge(a,second,id));
        p=MarsProgress.CODEC.parse(JsonOps.INSTANCE,MarsProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow()).getOrThrow();
        assertEquals(second,p.autonomyRefuge(a).orElseThrow().pos()); assertEquals(pos,p.autonomyRefuge(b).orElseThrow().pos());
        assertEquals(id,p.autonomyRefuge(b).orElseThrow().controller());
        assertTrue(p.bindAutonomyRefuge(b,pos,UUID.randomUUID()));
    }
    @Test void cachedRoomMustStillBeSealedAndLoaded() {
        var vent=new net.minecraft.core.BlockPos(0,0,0);
        var changed=new java.util.HashMap<net.minecraft.core.BlockPos,SealScanner.Cell>();
        SealScanner.World world=pos -> changed.getOrDefault(pos, pos.equals(vent)?SealScanner.Cell.OPEN:SealScanner.Cell.SOLID);
        var room=SealScanner.scan(world,vent,false);
        assertNull(MarsAutonomyRefuge.inspect(room,world,vent)); assertEquals(1,room.air().size());
        changed.put(vent.above(),SealScanner.Cell.OPEN); assertEquals("leak",MarsAutonomyRefuge.inspect(room,world,vent));
        changed.clear(); assertNull(MarsAutonomyRefuge.inspect(room,world,vent));
        changed.put(vent,SealScanner.Cell.SOLID); assertEquals("blocked_vent",MarsAutonomyRefuge.inspect(room,world,vent));
        changed.put(vent,SealScanner.Cell.UNLOADED); assertEquals("unloaded",MarsAutonomyRefuge.inspect(room,world,vent));
        changed.clear(); changed.put(vent.above(),SealScanner.Cell.UNLOADED); assertEquals("unloaded",MarsAutonomyRefuge.inspect(room,world,vent));
        assertEquals("too_large",MarsAutonomyRefuge.inspect(new SealScanner.Room(SealScanner.Status.TOO_LARGE,java.util.Set.of(),java.util.Map.of()),world,vent));
    }
    @Test void productionRejectsOldWrongAndReplayedWorkAndPersists() {
        var player=UUID.randomUUID(); var p=load(player,5); var pos=net.minecraft.core.BlockPos.ZERO;
        var machine=UUID.randomUUID(); var ticket=UUID.randomUUID();
        assertFalse(p.commissionProduction(player,pos,machine,ticket,"agua",10));
        p.acceptAutonomy(player); p.bindAutonomyRefuge(player,pos,UUID.randomUUID());
        assertTrue(p.commissionProduction(player,pos,machine,ticket,"agua",10));
        assertFalse(p.recordProduction(player,pos,machine,ticket,"agua",10));
        assertFalse(p.recordProduction(player,pos,UUID.randomUUID(),ticket,"agua",11));
        assertFalse(p.recordProduction(player,pos,machine,UUID.randomUUID(),"agua",11));
        assertFalse(p.recordProduction(player,pos,machine,ticket,"oxigeno",11));
        p=MarsProgress.CODEC.parse(JsonOps.INSTANCE,MarsProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow()).getOrThrow();
        assertTrue(p.recordProduction(player,pos,machine,ticket,"agua",11));
        assertFalse(p.recordProduction(player,pos,machine,ticket,"agua",12)); assertEquals(1,p.production(player).flags());
        assertTrue(p.commissionProduction(player,pos,machine,UUID.randomUUID(),"solar",11));
        assertTrue(p.cancelProduction(player)); assertEquals(1,p.production(player).flags());
        assertFalse(p.recordProduction(player,pos,machine,ticket,"solar",12));
        p.bindAutonomyRefuge(player,pos,UUID.randomUUID()); assertEquals(0,p.production(player).flags());
    }
}

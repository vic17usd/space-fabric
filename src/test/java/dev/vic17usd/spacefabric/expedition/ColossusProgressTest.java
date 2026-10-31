package dev.vic17usd.spacefabric.expedition;

import com.mojang.serialization.JsonOps;
import java.util.*;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ColossusProgressTest {
    @Test void evidenceMustBeOrderedAndCannotBeReplayedOrSkipTheSurvey(){
        var p=new ColossusProgress();var id=UUID.randomUUID();
        assertFalse(p.advance(id,1));assertFalse(p.reveal(id,BlockPos.ZERO));
        for(int i=0;i<3;i++){assertTrue(p.advance(id,i));assertFalse(p.advance(id,i));}
        assertFalse(p.advance(id,3));assertTrue(p.reveal(id,new BlockPos(100,-51,200)));
        assertFalse(p.reveal(id,BlockPos.ZERO));assertTrue(p.advance(id,3));assertTrue(p.advance(id,4));assertFalse(p.advance(id,5));
    }
    @Test void playersAndDestinationsPersistIndependentlyAcrossReloads(){
        var p=new ColossusProgress();var a=UUID.randomUUID();var b=UUID.randomUUID();
        for(int i=0;i<3;i++)p.advance(a,i);p.reveal(a,new BlockPos(45,-51,-300));p.advance(b,0);
        var saved=ColossusProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow();
        var restored=ColossusProgress.CODEC.parse(JsonOps.INSTANCE,saved).getOrThrow();
        assertEquals(3,restored.entry(a).stage());assertEquals(new BlockPos(45,-51,-300),restored.entry(a).target().orElseThrow());
        assertEquals(1,restored.entry(b).stage());assertTrue(restored.entry(b).target().isEmpty());assertTrue(restored.advance(a,3));
    }
    @Test void searchVisitsUniqueNearbyRegionsFirstAndHasAFiniteBudget(){
        var offsets=ColossusExpedition.offsets();assertEquals(81,offsets.size());assertEquals(81,new HashSet<>(offsets).size());
        assertEquals(new ColossusExpedition.Offset(0,0),offsets.getFirst());int radius=0;
        for(var p:offsets){int r=Math.max(Math.abs(p.x()),Math.abs(p.z()));assertTrue(r>=radius&&r<=4);radius=r;}
    }
    @Test void invalidSavedStagesAreRejected(){
        var p=new ColossusProgress();var id=UUID.randomUUID();p.advance(id,0);
        var value=ColossusProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow();value.getAsJsonObject().getAsJsonArray("players").get(0).getAsJsonObject().addProperty("stage",6);
        assertTrue(ColossusProgress.CODEC.parse(JsonOps.INSTANCE,value).error().isPresent());
    }
}

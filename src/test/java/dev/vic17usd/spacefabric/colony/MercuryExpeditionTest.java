package dev.vic17usd.spacefabric.colony;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.space.RocketJourney;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MercuryExpeditionTest {
    @Test void requiresRealTouchdownFromAnotherDestination() {
        assertTrue(MercuryExpedition.landing(Planet.MERCURY,Planet.EARTH,Planet.MERCURY,RocketJourney.DESCENDING,true));
        assertFalse(MercuryExpedition.landing(Planet.MERCURY,Planet.EARTH,Planet.MERCURY,RocketJourney.DESCENDING,false));
        assertFalse(MercuryExpedition.landing(Planet.MERCURY,Planet.EARTH,Planet.MERCURY,RocketJourney.CRUISING,true));
        assertFalse(MercuryExpedition.landing(Planet.MERCURY,Planet.MERCURY,Planet.MERCURY,RocketJourney.DESCENDING,true));
        assertFalse(MercuryExpedition.landing(Planet.MARS,Planet.EARTH,Planet.MERCURY,RocketJourney.DESCENDING,true));
    }
    @Test void progressIsPersonalPersistsAndRequiresLiveEvidenceForBoundIdentity() {
        var p=MercuryProgress.CODEC.parse(JsonOps.INSTANCE,JsonParser.parseString("{}" )).getOrThrow();
        var a=UUID.randomUUID();var b=UUID.randomUUID();var id=UUID.randomUUID();var pos=new BlockPos(7,70,-8);
        var ref=new MercuryProgress.Refuge(pos,id);
        assertFalse(p.select(a,pos,id));assertFalse(p.observe(a,ref,true));assertTrue(p.land(a));assertFalse(p.land(a));
        assertTrue(p.select(a,pos,id));assertFalse(p.select(a,pos,id));assertFalse(p.observe(a,ref,false));
        assertFalse(p.observe(a,new MercuryProgress.Refuge(pos,UUID.randomUUID()),true));assertFalse(p.observe(b,ref,true));
        p=MercuryProgress.CODEC.parse(JsonOps.INSTANCE,MercuryProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow()).getOrThrow();
        assertTrue(p.entry(a).arrived());assertEquals(ref,p.entry(a).refuge().orElseThrow());assertFalse(p.entry(b).arrived());
        assertTrue(p.observe(a,ref,true));assertFalse(p.observe(a,ref,true));assertFalse(p.select(a,pos,UUID.randomUUID()));
        p=MercuryProgress.CODEC.parse(JsonOps.INSTANCE,MercuryProgress.CODEC.encodeStart(JsonOps.INSTANCE,p).getOrThrow()).getOrThrow();
        assertTrue(p.entry(a).complete());assertFalse(p.entry(b).complete());
        assertTrue(p.land(b)); assertTrue(p.select(b,pos,id)); assertTrue(p.observe(b,ref,true));
        assertTrue(p.entry(a).complete()); assertTrue(p.entry(b).complete());
    }
}

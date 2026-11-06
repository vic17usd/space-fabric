package dev.vic17usd.spacefabric.map;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class AtlasDataTest {
    @TempDir Path root;
    private AtlasData.Waypoint marker(String name,int x,int z){return new AtlasData.Waypoint(UUID.randomUUID(),name,x,70,z,0x65D9ED,true);}

    @Test void exploredTerrainAndWaypointsRemainIndependentBetweenPlanetsAndWorlds() throws Exception {
        var mars=new AtlasData(root,"space_fabric:mars");var point=marker("Base",12,20);
        mars.record(12,20,71,0xFFBB6633);mars.put(point);mars.flush(2);
        var venus=new AtlasData(root,"space_fabric:venus");assertEquals(0,venus.color(12,20));assertTrue(venus.waypoints().isEmpty());
        var otherWorld=new AtlasData(root.resolve("other_world"),"space_fabric:mars");assertEquals(0,otherWorld.color(12,20));
        var loaded=new AtlasData(root,"space_fabric:mars");assertEquals(0xFFBB6633,loaded.color(12,20));assertEquals(71,loaded.height(12,20,0));assertEquals(point,loaded.selected().orElseThrow());
        assertEquals(List.of("space_fabric:mars","space_fabric:venus"),AtlasData.visited(root));
    }
    @Test void allPlanetsAndTheOverworldHaveSeparateDataFolders() throws Exception {
        var ids=new ArrayList<String>();ids.add("minecraft:overworld");
        for(String p:List.of("mercury","venus","earth","moon","mars","jupiter","saturn","uranus","neptune"))ids.add("space_fabric:"+p);
        for(int i=0;i<ids.size();i++){var map=new AtlasData(root,ids.get(i));map.record(0,0,20+i,0xFF000000|i);map.flush(1);}
        for(int i=0;i<ids.size();i++){var map=new AtlasData(root,ids.get(i));assertEquals(20+i,map.height(0,0,0));assertEquals(0xFF000000|i,map.color(0,0));}
    }
    @Test void negativeCoordinatesAndTileBoundariesSurviveCompressionAndReload() throws Exception {
        var map=new AtlasData(root,"space_fabric:moon");
        map.record(-1,-1,-20,0xFF123456);map.record(-129,-129,42,0xFFABCDEF);map.record(128,128,120,0xFF445566);map.flush(99);
        var loaded=new AtlasData(root,"space_fabric:moon");
        assertEquals(0xFF123456,loaded.color(-4,-4));assertEquals(-20,loaded.height(-1,-1,0));
        assertEquals(0xFFABCDEF,loaded.color(-129,-129));assertEquals(0xFF445566,loaded.color(128,128));
        assertEquals(0,loaded.color(-128,-128));assertEquals(99,loaded.height(-128,-128,99));
    }
    @Test void unknownTerrainIsNotWrittenByLookingAtIt() throws Exception {
        var map=new AtlasData(root,"space_fabric:mars");
        for(int i=0;i<2000;i++)assertEquals(0,map.color(i*128,10000));map.flush(20);
        try(var files=Files.list(root.resolve(AtlasData.key("space_fabric:mars")).resolve("regions"))){assertEquals(0,files.count());}
    }
    @Test void editingDeletingHidingAndSelectionPersistWithoutDuplicatingMarkers() throws Exception {
        var map=new AtlasData(root,"space_fabric:venus");var one=marker("Antes",10,10);var two=marker("Mina",20,20);
        map.put(one);map.put(two);map.put(new AtlasData.Waypoint(one.id(),"Después",11,80,12,0xEEAA22,false));
        assertEquals(2,map.waypoints().size());map.select(two.id());map.delete(one.id());
        var loaded=new AtlasData(root,"space_fabric:venus");assertEquals(List.of(two),loaded.waypoints());assertEquals(two,loaded.selected().orElseThrow());
        loaded.delete(two.id());assertTrue(new AtlasData(root,"space_fabric:venus").selected().isEmpty());
    }
    @Test void markerLimitIsPerPlanetAndRejectsAdditionalPointsWithoutLosingExistingOnes() throws Exception {
        var map=new AtlasData(root,"space_fabric:mars");
        for(int i=0;i<128;i++)map.put(marker("Punto "+i,i,i));
        assertThrows(IOException.class,()->map.put(marker("Extra",0,0)));assertEquals(128,new AtlasData(root,"space_fabric:mars").waypoints().size());
        var venus=new AtlasData(root,"space_fabric:venus");venus.put(marker("Primero",0,0));assertEquals(1,venus.waypoints().size());
    }
    @Test void fileKeysIsolatePlayersServersAndWorldResetsWithoutExposingAddresses() {
        var player=UUID.randomUUID();String first=AtlasData.sessionKey("server:a/world:1",player);
        assertNotEquals(first,AtlasData.sessionKey("server:a/world:2",player));assertNotEquals(first,AtlasData.sessionKey("server:b/world:1",player));
        assertNotEquals(first,AtlasData.sessionKey("server:a/world:1",UUID.randomUUID()));assertTrue(first.matches("[a-f0-9]{64}"));
    }
    @Test void malformedMetadataIsRejectedAndPreserved() throws Exception {
        new AtlasData(root,"space_fabric:mars");var path=root.resolve(AtlasData.key("space_fabric:mars")).resolve("markers.json");
        Files.writeString(path,"{broken}");assertThrows(IOException.class,()->new AtlasData(root,"space_fabric:mars"));assertEquals("{broken}",Files.readString(path));
    }
    @Test void aDamagedRegionDoesNotBecomeExploredOrOverwriteItsFile() throws Exception {
        var map=new AtlasData(root,"space_fabric:mars");map.record(0,0,70,0xFF112233);map.flush(1);
        var path=root.resolve(AtlasData.key("space_fabric:mars")).resolve("regions/0_0.sfa");Files.writeString(path,"broken");
        var loaded=new AtlasData(root,"space_fabric:mars");assertEquals(0,loaded.color(0,0));assertNotNull(loaded.error());loaded.record(0,0,70,0xFF222222);loaded.flush(99);
        assertEquals("broken",Files.readString(path));
    }
    @Test void flushBudgetAndLastVisitedPositionPersist() throws Exception {
        var map=new AtlasData(root,"space_fabric:neptune");
        map.record(0,0,70,1);map.record(128,0,80,2);map.record(256,0,90,3);map.position(400,301,-200);map.flush(2);
        try(var files=Files.list(root.resolve(AtlasData.key("space_fabric:neptune")).resolve("regions"))){assertEquals(2,files.count());}
        var loaded=new AtlasData(root,"space_fabric:neptune");assertEquals(400,loaded.lastX());assertEquals(301,loaded.lastY());assertEquals(-200,loaded.lastZ());
        map.flush(2);assertNotEquals(0,new AtlasData(root,"space_fabric:neptune").color(256,0));
    }
    @Test void waypointValidationKeepsNamesReadableAndBoundsCoordinates() {
        var point=new AtlasData.Waypoint(UUID.randomUUID(),"\n  Base\u00a7\t ",0,70,0,0x112233,true);assertEquals("Base",point.name());
        assertThrows(IllegalArgumentException.class,()->marker(" ",0,0));assertThrows(IllegalArgumentException.class,()->marker("Lejos",30_000_000,0));
        assertThrows(IllegalArgumentException.class,()->new AtlasData.Waypoint(UUID.randomUUID(),"Base",0,5000,0,0,true));
        assertFalse(AtlasData.validDimension("../../outside"));
    }
    @Test void coldRegionReadsAreBudgetedAndDeferredUntilTheNextTick() throws Exception {
        var map=new AtlasData(root,"space_fabric:mars");
        for(int i=0;i<30;i++)map.record(i*128,0,70,0xFF112233);map.flush(99);
        var loaded=new AtlasData(root,"space_fabric:mars");loaded.beginTick();loaded.beginView();
        for(int i=0;i<24;i++)assertEquals(0xFF112233,loaded.color(i*128,0));
        assertEquals(0,loaded.color(24*128,0));assertTrue(loaded.deferredReads());
        loaded.beginTick();loaded.beginView();for(int i=24;i<30;i++)assertEquals(0xFF112233,loaded.color(i*128,0));
        assertFalse(loaded.deferredReads());
    }
}

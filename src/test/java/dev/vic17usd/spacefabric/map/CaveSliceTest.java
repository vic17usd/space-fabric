package dev.vic17usd.spacefabric.map;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class CaveSliceTest {
    @TempDir Path root;
    @Test void findsTheWalkableCaveFloorInsteadOfTheSurfaceOrRoof() {
        assertEquals(10,CaveSlice.floor(11,-64,319,y->y<=10||y>=17));
        assertEquals(-50,CaveSlice.floor(-49,-64,319,y->y<=-50||y>=-43));
        assertEquals(19,CaveSlice.floor(20,-64,319,y->y<=19||(y>=24&&y<=27)||y>=32));
    }
    @Test void solidRockAndOneBlockCracksRemainWalls() {
        assertEquals(Integer.MIN_VALUE,CaveSlice.floor(20,-64,319,y->true));
        assertEquals(Integer.MIN_VALUE,CaveSlice.floor(20,-64,319,y->y!=20));
        assertEquals(Integer.MIN_VALUE,CaveSlice.floor(20,-64,319,y->false));
    }
    @Test void scansAreBoundedEvenAtBuildLimitsAndNegativeBands() {
        var reads=new AtomicInteger();
        CaveSlice.floor(-63,-64,319,y->{assertTrue(y>=-64&&y<=319);reads.incrementAndGet();return true;});
        assertTrue(reads.get()<=63);assertEquals(-4,CaveSlice.band(-1));assertEquals(4,CaveSlice.band(0));
        CaveSlice.floor(319,-64,319,y->{assertTrue(y>=-64&&y<=319);return y<=316;});
    }
    @Test void caveAltitudeAndDimensionCachesCannotReplaceTheSurface() throws Exception {
        var surface=new AtlasData(root,"space_fabric:mars");surface.record(0,0,90,0xFFBB6633);surface.flush(2);
        var cave=new AtlasData(root.resolve("caves/4"),"space_fabric:mars");cave.record(0,0,2,0xFF999999);cave.flush(2);
        var deeper=new AtlasData(root.resolve("caves/-4"),"space_fabric:mars");assertEquals(0,deeper.color(0,0));
        var venus=new AtlasData(root.resolve("caves/4"),"space_fabric:venus");assertEquals(0,venus.color(0,0));
        assertEquals(0xFFBB6633,new AtlasData(root,"space_fabric:mars").color(0,0));
        assertEquals(0xFF999999,new AtlasData(root.resolve("caves/4"),"space_fabric:mars").color(0,0));
    }
}

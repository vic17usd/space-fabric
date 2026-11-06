package dev.vic17usd.spacefabric.space;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LandingSearchTest {
    private static class Chunks implements LandingSearch.Chunks {
        final ArrayList<ChunkPos> requests = new ArrayList<>();
        CompletableFuture<LandingPlanner.World> future = new CompletableFuture<>();
        boolean border = true;
        int keeps;
        @Override public boolean insideBorder(ChunkPos chunk) { return border; }
        @Override public CompletableFuture<LandingPlanner.World> load(ChunkPos chunk) { requests.add(chunk);return future; }
        @Override public void keepLoaded(ChunkPos chunk) { keeps++; }
    }

    @Test void incompleteGenerationNeverBlocksOrAccessesTheWorldAndResumesOnceReady() {
        var chunks = new Chunks();var search = new LandingSearch(chunks,false,0);
        for(int i=0;i<40;i++)search.tick();
        assertFalse(search.done());assertNull(search.floor());assertEquals(1,chunks.requests.size());
        chunks.future.complete(new LandingPlannerTest.World());search.tick();
        assertTrue(search.done());assertEquals(new BlockPos(8,63,8),search.floor());
        int previous=chunks.keeps;search.tick();assertEquals(previous+1,chunks.keeps);
        search.close();search.tick();assertEquals(previous+1,chunks.keeps);
    }
    @Test void emptySurfaceScansOneChunkPerTickAndCannotCreateAFloorInTheSky() {
        var chunks = new Chunks();chunks.future.complete(new LandingPlannerTest.World() {
            @Override public boolean ground(BlockPos pos) { return false; }
        });
        var search = new LandingSearch(chunks,false,0);
        for(int i=0;i<LandingPlanner.CHUNK_COUNT;i++) {
            search.tick();assertEquals(i+1,chunks.requests.size());
        }
        search.tick();assertTrue(search.done());assertNull(search.floor());assertNull(search.world());
    }
    @Test void neverCompletingGenerationHasBothPerChunkAndTotalTimeLimits() {
        var chunks = new Chunks();var search = new LandingSearch(chunks,false,0);
        for(int i=0;i<=LandingSearch.MAX_CHUNK_TICKS;i++)search.tick();
        assertEquals(2,chunks.requests.size());assertFalse(search.done());
        for(int i=0;i<=LandingSearch.MAX_TICKS;i++)search.tick();
        assertTrue(search.done());assertNull(search.floor());
        assertTrue(chunks.requests.size()<=7);assertFalse(chunks.future.isCancelled());
    }
    @Test void failuresCancellationsAndWorldBordersCannotLeakAnUnboundedSearch() {
        for(boolean cancelled:new boolean[]{false,true}) {
            var chunks = new Chunks();
            if(cancelled)chunks.future.cancel(false);else chunks.future.completeExceptionally(new IllegalStateException("unloaded"));
            var search = new LandingSearch(chunks,false,Integer.MIN_VALUE);
            for(int i=0;i<=LandingPlanner.CHUNK_COUNT;i++)search.tick();
            assertTrue(search.done());assertNull(search.floor());assertEquals(57,chunks.requests.size());
        }
        var outside = new Chunks();outside.border=false;var search=new LandingSearch(outside,false,0);
        for(int i=0;i<=LandingPlanner.CHUNK_COUNT;i++)search.tick();
        assertTrue(search.done());assertTrue(outside.requests.isEmpty());assertEquals(0,outside.keeps);
    }
    @Test void disconnectedOrUnloadedFlightsStopRefreshingAndIgnoreLateChunkCompletions() {
        var chunks = new Chunks();var search = new LandingSearch(chunks,false,0);search.tick();
        int previous=chunks.keeps;search.close();chunks.future.complete(new LandingPlannerTest.World());
        for(int i=0;i<50;i++)search.tick();
        assertEquals(previous,chunks.keeps);assertNull(search.floor());assertTrue(search.done());
    }
    @Test void onlyGasGiantsUseAnOrbitalFloor() {
        var chunks = new Chunks();chunks.future.complete(new LandingPlannerTest.World());
        var search = new LandingSearch(chunks,true,0);search.tick();
        assertTrue(search.done());assertEquals(239,search.floor().getY());assertTrue(search.orbital());
    }
}

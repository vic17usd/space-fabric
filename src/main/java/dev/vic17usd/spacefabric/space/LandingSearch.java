package dev.vic17usd.spacefabric.space;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/** One asynchronous chunk at a time, polled only on the server thread. Never joins a future. */
public final class LandingSearch implements AutoCloseable {
    public static final int MAX_TICKS = 1200, MAX_CHUNK_TICKS = 200;

    public interface Chunks {
        boolean insideBorder(ChunkPos chunk);
        CompletableFuture<LandingPlanner.World> load(ChunkPos chunk);
        void keepLoaded(ChunkPos chunk);
    }

    private final Chunks chunks;
    private final boolean orbital;
    private final int offset;
    private int index, ticks, chunkTicks;
    private ChunkPos chunk;
    private CompletableFuture<LandingPlanner.World> pending;
    private LandingPlanner.World world;
    private BlockPos floor;
    private boolean done, closed;

    public LandingSearch(Chunks chunks, boolean orbital, int offset) {
        this.chunks = chunks;
        this.orbital = orbital;
        this.offset = offset;
    }

    public void tick() {
        if (closed) return;
        if (done) {
            if (floor != null) chunks.keepLoaded(chunk);
            return;
        }
        if (++ticks > MAX_TICKS || index >= LandingPlanner.CHUNK_COUNT) { fail(); return; }
        if (pending == null) {
            chunk = LandingPlanner.candidateChunk(index, offset);
            if (!chunks.insideBorder(chunk)) { index++; return; }
            chunks.keepLoaded(chunk);
            pending = chunks.load(chunk);
            chunkTicks = 0;
        }
        chunks.keepLoaded(chunk);
        if (!pending.isDone()) {
            if (++chunkTicks >= MAX_CHUNK_TICKS) next();
            return;
        }
        LandingPlanner.World loaded;
        try { loaded = pending.getNow(null); }
        catch (CompletionException | java.util.concurrent.CancellationException failed) { loaded = null; }
        if (loaded != null) {
            var candidate = LandingPlanner.findInChunk(loaded, chunk, orbital, offset);
            if (candidate.isPresent()) {
                world = loaded; floor = candidate.get(); done = true; return;
            }
        }
        next();
    }

    private void next() { index++; pending = null; }
    private void fail() { done = true; world = null; floor = null; pending = null; }
    public boolean done() { return done; }
    public BlockPos floor() { return floor; }
    public LandingPlanner.World world() { return world; }
    public boolean orbital() { return orbital; }
    /** Shared expiring tickets are not removed: another pilot may be using the same chunk. */
    @Override public void close() { closed = true; fail(); }
}

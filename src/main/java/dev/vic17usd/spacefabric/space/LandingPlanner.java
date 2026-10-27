package dev.vic17usd.spacefabric.space;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/** Read-only landing policy. Rocky worlds never receive an artificial floor. */
public final class LandingPlanner {
    private static final int[] LOCAL = {8, 5, 11, 2, 13};
    private static final int[][] DIRECTIONS = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
    public static final int CHUNK_COUNT = 57; // Origin, then eight directions at radii 1..64 chunks.
    public interface World {
        /** Y of the top supporting block, not the first air block. */
        int surface(int x, int z);
        int minY();
        int maxY();
        boolean air(BlockPos pos);
        boolean pad(BlockPos pos);
        /** Full, dry natural terrain with sky access; excludes pads, roofs and foliage. */
        boolean ground(BlockPos pos);
        boolean available(BlockPos floor);
    }
    private LandingPlanner() { }
    /** Clear vertical approach, capped at 48 blocks and below the build limit. */
    public static int approachHeight(World world,BlockPos floor,double hullHeight) {
        int clear=0;
        outer:for(int y=1;y<=Math.min(54,world.maxY()-floor.getY()-1);y++) {
            for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)if(!world.air(floor.offset(x,y,z)))break outer;
            clear=y;
        }
        return Math.min(48,Math.max(0,clear-(int)Math.ceil(hullHeight)-1));
    }
    /** All samples and their 5x5 clearance stay inside the already loaded chunk. */
    public static Optional<BlockPos> findInChunk(World world, ChunkPos chunk, boolean orbital, int offset) {
        for (int i = 0; i < LOCAL.length * LOCAL.length; i++) {
            int cell = Math.floorMod(i + (long) offset, 25);
            int x = chunk.getMinBlockX() + LOCAL[cell % 5], z = chunk.getMinBlockZ() + LOCAL[cell / 5];
            int y = orbital ? 239 : world.surface(x, z);
            var floor = new BlockPos(x, y, z);
            if (suitable(world, floor, orbital)) return Optional.of(floor);
        }
        return Optional.empty();
    }

    public static ChunkPos candidateChunk(int index, int offset) {
        if (index < 0 || index >= CHUNK_COUNT) throw new IllegalArgumentException("Landing search budget exceeded");
        if (index == 0) return new ChunkPos(0, 0);
        int radius = 1 << ((index - 1) / 8);
        var direction = DIRECTIONS[Math.floorMod((index - 1L) + offset, 8)];
        return new ChunkPos(direction[0] * radius, direction[1] * radius);
    }

    /** Rechecked immediately before teleporting: players may have built or moved here meanwhile. */
    public static boolean suitable(World world, BlockPos center, boolean orbital) {
        if (center.getY() < world.minY() || center.getY() + 6 >= world.maxY()) return false;
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) {
            var floor = center.offset(x, 0, z);
            if (orbital) {
                if (!world.air(floor) && !world.pad(floor)) return false;
            } else if (Math.abs(x) <= 1 && Math.abs(z) <= 1 && !world.ground(floor)) {
                // Every landing leg must have support at the same height, including at cliff edges.
                return false;
            }
            for (int y = 1; y <= 5; y++) if (!world.air(floor.above(y))) return false;
        }
        return world.available(center);
    }
}

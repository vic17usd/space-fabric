package dev.vic17usd.spacefabric.space;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LandingPlannerTest {
    static class World implements LandingPlanner.World {
        Set<BlockPos> obstacles = new HashSet<>(), holes = new HashSet<>();
        boolean available = true;
        int checked;
        @Override public int surface(int x, int z) { return 63; }
        @Override public int minY() { return -64; }
        @Override public int maxY() { return 384; }
        @Override public boolean air(BlockPos pos) { return pos.getY() >= 64 && !obstacles.contains(pos); }
        @Override public boolean pad(BlockPos pos) { return false; }
        @Override public boolean ground(BlockPos pos) { return pos.getY() == 63 && !holes.contains(pos); }
        @Override public boolean available(BlockPos floor) { checked++; return available; }
    }
    @Test void naturalSurfaceUsesTheSupportingBlockRatherThanTheFirstAirBlock() {
        var world = new World(); var floor = LandingPlanner.findInChunk(world, new ChunkPos(0,0), false, 0).orElseThrow();
        assertEquals(new BlockPos(8,63,8), floor);
        assertEquals(64, floor.above().getY());
        assertEquals(1, world.checked);
    }
    @Test void obstaclesIncludingTheCornerAndCeilingChooseAnotherPatchWithoutDeletingIt() {
        var world = new World(); var obstruction = new BlockPos(6,68,6); world.obstacles.add(obstruction);
        assertEquals(new BlockPos(11,63,8), LandingPlanner.findInChunk(world, new ChunkPos(0,0), false, 0).orElseThrow());
        assertTrue(world.obstacles.contains(obstruction));
    }
    @Test void allLegsNeedFlatSupportAndACliffEdgeIsRejected() {
        var world = new World(); world.holes.add(new BlockPos(9,63,8));
        assertFalse(LandingPlanner.suitable(world, new BlockPos(8,63,8), false));
        assertEquals(new BlockPos(5,63,8), LandingPlanner.findInChunk(world, new ChunkPos(0,0), false, 0).orElseThrow());
    }
    @Test void occupiedOrOutOfBorderSitesFailWithinAFixedBudget() {
        var world = new World(); world.available = false;
        assertTrue(LandingPlanner.findInChunk(world, new ChunkPos(0,0), false, 0).isEmpty());
        assertEquals(25, world.checked);
    }
    @Test void orbitalPlatformsCanBeReusedButSolidTerrainIsNotReplaced() {
        var world = new World() {
            @Override public boolean air(BlockPos pos) { return pos.getY() > 239; }
            @Override public boolean pad(BlockPos pos) { return pos.getY() == 239; }
        };
        assertEquals(239, LandingPlanner.findInChunk(world, new ChunkPos(0,0), true, 0).orElseThrow().getY());
        var blocked = new World() { @Override public boolean air(BlockPos pos) { return pos.getY() > 239; } };
        assertTrue(LandingPlanner.findInChunk(blocked, new ChunkPos(0,0), true, 0).isEmpty());
    }
    @Test void airWaterRoofsAndLegacyPadsNeverBecomeARockyWorldLandingFloor() {
        var world = new World() {
            @Override public boolean ground(BlockPos pos) { return false; }
            @Override public boolean pad(BlockPos pos) { return true; }
        };
        assertTrue(LandingPlanner.findInChunk(world, new ChunkPos(0,0), false, 0).isEmpty());
        assertFalse(LandingPlanner.suitable(world, new BlockPos(8,376,8), false));
    }
    @Test void thereIsNoFallbackPlatformInTheSkyWhenTheSurfaceIsBlocked() {
        var world = new World() { @Override public boolean air(BlockPos pos) { return pos.getY() > 100; } };
        assertTrue(LandingPlanner.findInChunk(world, new ChunkPos(0,0), false, 0).isEmpty());
    }
    @Test void orbitalArrivalStaysAboveTheLethalPressureBoundary() {
        var floor = LandingPlanner.findInChunk(new World(), new ChunkPos(0,0), true, Integer.MIN_VALUE).orElseThrow();
        assertEquals(239, floor.getY()); assertTrue(floor.above().getY() > 160);
    }
    @Test void searchCoversUniqueNearAndFarChunksAndStaysBoundedForAnySeed() {
        for(int seed:new int[]{0,71,Integer.MIN_VALUE,Integer.MAX_VALUE}) {
            var seen=new HashSet<ChunkPos>();
            for(int i=0;i<LandingPlanner.CHUNK_COUNT;i++) {
                var chunk=LandingPlanner.candidateChunk(i,seed); assertTrue(seen.add(chunk));
                assertTrue(Math.abs(chunk.x())<=64 && Math.abs(chunk.z())<=64);
                // A full clearance square must not synchronously load a neighboring chunk.
                var world=new World() {
                    @Override public boolean air(BlockPos pos) { assertEquals(chunk,new ChunkPos(pos.getX()>>4,pos.getZ()>>4));return super.air(pos); }
                    @Override public boolean ground(BlockPos pos) { assertEquals(chunk,new ChunkPos(pos.getX()>>4,pos.getZ()>>4));return super.ground(pos); }
                };
                assertTrue(LandingPlanner.findInChunk(world,chunk,false,seed).isPresent());
            }
        }
        assertThrows(IllegalArgumentException.class,()->LandingPlanner.candidateChunk(57,0));
    }
    @Test void changingTerrainAndEntitiesAreRecheckedImmediatelyBeforeTransfer() {
        var world=new World();var floor=new BlockPos(8,63,8);
        assertTrue(LandingPlanner.suitable(world,floor,false));
        world.available=false;assertFalse(LandingPlanner.suitable(world,floor,false));
        world.available=true;world.holes.add(floor);assertFalse(LandingPlanner.suitable(world,floor,false));
    }
    @Test void approachUsesAFreeColumnAndStopsBelowNewRoofsWithoutDeletingThem() {
        var world=new World();var floor=new BlockPos(8,63,8);
        assertEquals(48,LandingPlanner.approachHeight(world,floor,3.6));
        var roof=floor.offset(2,12,-2);world.obstacles.add(roof);
        assertEquals(6,LandingPlanner.approachHeight(world,floor,3.6));
        assertTrue(world.obstacles.contains(roof));
    }
    @Test void approachNeverSpawnsTheHullAboveTheBuildLimit() {
        var floor=new BlockPos(8,376,8);var world=new World();
        int height=LandingPlanner.approachHeight(world,floor,3.6);
        assertEquals(2,height);assertTrue(floor.getY()+1+height+3.6<world.maxY());
        assertFalse(LandingPlanner.suitable(world,new BlockPos(8,-65,8),false));
        assertFalse(LandingPlanner.suitable(world,new BlockPos(8,380,8),false));
    }
}

package dev.vic17usd.spacefabric.space;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LandingPlannerTest {
    private static class World implements LandingPlanner.World {
        Set<BlockPos> obstacles = new HashSet<>();
        boolean available = true;
        int checked;
        @Override public int surface(int x, int z) { return 64; }
        @Override public int maxY() { return 384; }
        @Override public boolean air(BlockPos pos) { return pos.getY() >= 64 && !obstacles.contains(pos); }
        @Override public boolean pad(BlockPos pos) { return false; }
        @Override public boolean available(BlockPos floor) { checked++; return available; }
    }
    @Test void surfaceArrivalLeavesSpaceForTheWholeRocketAndPilot() {
        var world = new World(); var floor = LandingPlanner.find(world, false, 0).orElseThrow();
        assertEquals(new BlockPos(8, 64, 8), floor);
        assertEquals(1, world.checked);
    }
    @Test void anyObstacleIncludingCornerOrCeilingChoosesAnotherSiteWithoutDeletingIt() {
        var world = new World(); var obstruction = new BlockPos(6, 69, 6); world.obstacles.add(obstruction);
        assertEquals(new BlockPos(24, 64, 8), LandingPlanner.find(world, false, 0).orElseThrow());
        assertTrue(world.obstacles.contains(obstruction));
    }
    @Test void occupiedOrOutOfBorderSitesFailWithinAFixedBudget() {
        var world = new World(); world.available = false;
        assertTrue(LandingPlanner.find(world, false, 0).isEmpty()); assertEquals(32, world.checked);
        world.checked = 0;
        assertTrue(LandingPlanner.find(world, true, 0).isEmpty()); assertEquals(16, world.checked);
    }
    @Test void liquidsAndSolidFloorsAreNotReplaceableButOurPlatformCanBeReused() {
        var world = new World() {
            @Override public boolean air(BlockPos pos) { return pos.getY() > 239; }
            @Override public boolean pad(BlockPos pos) { return pos.getY() == 239; }
        };
        assertEquals(239, LandingPlanner.find(world, true, 0).orElseThrow().getY());
        var blocked = new World() { @Override public boolean air(BlockPos pos) { return pos.getY() > 239; } };
        assertTrue(LandingPlanner.find(blocked, true, 0).isEmpty());
    }
    @Test void blockedTerrainCanUseASkyPlatformWithoutExcavation() {
        var world = new World() { @Override public boolean air(BlockPos pos) { return pos.getY() > 100; } };
        assertEquals(376, LandingPlanner.find(world, false, 0).orElseThrow().getY());
    }
    @Test void orbitalArrivalStaysAboveTheLethalPressureBoundary() {
        var floor = LandingPlanner.find(new World(), true, Integer.MIN_VALUE).orElseThrow();
        assertEquals(239, floor.getY()); assertTrue(floor.above().getY() > 160);
        assertTrue(floor.getX() >= 8 && floor.getX() <= 56);
    }
}

package dev.vic17usd.spacefabric.space;

import java.util.Optional;
import net.minecraft.core.BlockPos;

/** Política acotada y comprobable: nunca borra bloques para abrir espacio. */
public final class LandingPlanner {
    public interface World {
        int surface(int x, int z);
        int maxY();
        boolean air(BlockPos pos);
        boolean pad(BlockPos pos);
        boolean available(BlockPos floor);
    }
    private LandingPlanner() { }
    /** Clear vertical approach above the pad; does not remove terrain or roof blocks. */
    public static int approachHeight(World world,BlockPos floor,double hullHeight) {
        int clear=0;
        outer:for(int y=1;y<=Math.min(54,world.maxY()-floor.getY()-1);y++) {
            for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)if(!world.air(floor.offset(x,y,z)))break outer;
            clear=y;
        }
        return Math.min(48,Math.max(0,clear-(int)Math.ceil(hullHeight)-1));
    }
    public static Optional<BlockPos> find(World world, boolean orbital, int offset) {
        for (int pass = 0; pass < (orbital ? 1 : 2); pass++) {
            for (int i = 0; i < 16; i++) {
                int cell = Math.floorMod(i + offset, 16), x = 8 + (cell % 4) * 16, z = 8 + (cell / 4) * 16;
                int y = orbital ? 239 : Math.min(world.maxY() - 7, pass == 0 ? world.surface(x, z) : world.maxY() - 8);
                var floor = new BlockPos(x, y, z);
                if (world.available(floor) && clear(world, floor)) return Optional.of(floor);
            }
        }
        return Optional.empty();
    }
    private static boolean clear(World world, BlockPos center) {
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) {
            var floor = center.offset(x, 0, z);
            if (!world.air(floor) && !world.pad(floor)) return false;
            for (int y = 1; y <= 5; y++) if (!world.air(floor.above(y))) return false;
        }
        return true;
    }
}

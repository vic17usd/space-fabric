package dev.vic17usd.spacefabric.planet;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Volumen de prueba: ejecuta una Feature real y rechaza escrituras fuera del chunk. */
final class VoxelWorld {
    final Map<BlockPos, BlockState> blocks = new HashMap<>();
    final WorldGenLevel level;
    private final int groundY;

    VoxelWorld(int chunkX, int chunkZ) {
        this(chunkX, chunkZ, Integer.MIN_VALUE);
    }

    VoxelWorld(int chunkX, int chunkZ, int groundY) {
        this.groundY = groundY;
        level = (WorldGenLevel) Proxy.newProxyInstance(WorldGenLevel.class.getClassLoader(), new Class<?>[] {WorldGenLevel.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getBlockState" -> state((BlockPos) args[0]);
                    case "getFluidState" -> state((BlockPos) args[0]).getFluidState();
                    case "getBlockEntity" -> null;
                    case "blockUpdated" -> null;
                    case "isEmptyBlock" -> state((BlockPos) args[0]).isAir();
                    case "ensureCanWrite" -> true;
                    case "getMinY" -> -64;
                    case "getMaxY" -> 447;
                    case "getHeight" -> args == null || args.length == 0 ? 512 : heightAt((int) args[1], (int) args[2]);
                    case "canSeeSky" -> heightAt(((BlockPos) args[0]).getX(), ((BlockPos) args[0]).getZ()) <= ((BlockPos) args[0]).getY();
                    case "setBlock" -> {
                        var pos = ((BlockPos) args[0]).immutable();
                        if ((pos.getX() >> 4) != chunkX || (pos.getZ() >> 4) != chunkZ || pos.getY() < -64 || pos.getY() > 447)
                            throw new AssertionError("Escritura de worldgen fuera del volumen: " + pos);
                        blocks.put(pos, (BlockState) args[1]);
                        yield true;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    BlockState state(BlockPos pos) {
        return blocks.getOrDefault(pos, pos.getY() <= groundY
                ? dev.vic17usd.spacefabric.block.ModBlocks.MERCURY_SILICATE.defaultBlockState() : Blocks.AIR.defaultBlockState());
    }

    private int heightAt(int x, int z) {
        for (int y = 447; y >= -64; y--) if (!state(new BlockPos(x, y, z)).isAir()) return y + 1;
        return -64;
    }
}

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

    VoxelWorld(int chunkX, int chunkZ) {
        level = (WorldGenLevel) Proxy.newProxyInstance(WorldGenLevel.class.getClassLoader(), new Class<?>[] {WorldGenLevel.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getBlockState" -> state((BlockPos) args[0]);
                    case "isEmptyBlock" -> state((BlockPos) args[0]).isAir();
                    case "ensureCanWrite" -> true;
                    case "getMinY" -> -64;
                    case "getMaxY" -> 447;
                    case "getHeight" -> 512;
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

    BlockState state(BlockPos pos) { return blocks.getOrDefault(pos, Blocks.AIR.defaultBlockState()); }
}

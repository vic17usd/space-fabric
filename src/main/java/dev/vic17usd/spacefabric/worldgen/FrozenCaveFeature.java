package dev.vic17usd.spacefabric.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/** Cuerpo helado con cámara profunda y galería lateral. Todas las escrituras quedan en un chunk. */
public final class FrozenCaveFeature extends Feature<BlockStateConfiguration> {
    public static final int CENTER_Y = 218;
    public FrozenCaveFeature() { super(BlockStateConfiguration.CODEC); }

    public static boolean inBody(int dx, int dy, int dz) {
        return (dx * dx + dz * dz) / 42.25 + dy * dy / 484.0 <= 1.0;
    }

    public static boolean inCave(int dx, int dy, int dz) {
        boolean chamber = dx * dx + dz * dz <= 16 && dy >= -14 && dy <= -6;
        int floor = -13 + dz;
        boolean entrance = Math.abs(dx) <= 1 && dz >= 0 && dz <= 6 && dy >= floor && dy <= floor + 3;
        return chamber || entrance;
    }

    @Override public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        int cx = (context.origin().getX() & ~15) + 8;
        int cz = (context.origin().getZ() & ~15) + 8;
        if (!OrbitalMaterialFeature.inOrbitalBand(cx, cz)) return false;
        boolean changed = false;
        for (int dx = -6; dx <= 6; dx++) for (int dz = -6; dz <= 6; dz++) for (int dy = -22; dy <= 22; dy++) {
            if (!inBody(dx, dy, dz)) continue;
            var pos = new BlockPos(cx + dx, CENTER_Y + dy, cz + dz);
            if (inCave(dx, dy, dz)) {
                if (context.level().getBlockState(pos).is(context.config().state.getBlock())) {
                    context.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    changed = true;
                }
            } else if (context.level().isEmptyBlock(pos)) {
                context.level().setBlock(pos, context.config().state, 2);
                changed = true;
            }
        }
        return changed;
    }
}

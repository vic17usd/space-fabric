package dev.vic17usd.spacefabric.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/** Pequeños cuerpos de hielo/meteoritos, no una superficie continua del gigante. */
public final class OrbitalMaterialFeature extends Feature<BlockStateConfiguration> {
    public static final int MIN_RADIUS = 512;
    public static final int MAX_RADIUS = 768;
    public static final int ORBIT_Y = 224;

    public OrbitalMaterialFeature() { super(BlockStateConfiguration.CODEC); }

    public static boolean inOrbitalBand(int x, int z) {
        double distanceSquared = (double) x * x + (double) z * z;
        return distanceSquared >= (double) MIN_RADIUS * MIN_RADIUS
                && distanceSquared <= (double) MAX_RADIUS * MAX_RADIUS;
    }

    @Override public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        int cx = (context.origin().getX() & ~15) + 8;
        int cz = (context.origin().getZ() & ~15) + 8;
        if (!inOrbitalBand(cx, cz)) return false;
        int radius = 3 + context.random().nextInt(3);
        int cy = ORBIT_Y + context.random().nextInt(9);
        boolean placed = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if ((dx * dx + dz * dz) / (double) (radius * radius) + dy * dy / 4.0 > 1.0) continue;
                    var pos = new BlockPos(cx + dx, cy + dy, cz + dz);
                    if (context.level().isEmptyBlock(pos)) {
                        context.level().setBlock(pos, context.config().state, 2);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }
}

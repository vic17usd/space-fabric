package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/** Trampa fría cubierta: genera roca que proyecta sombra permanente sobre el hielo del cráter. */
public final class ShadowCraterIceFeature extends Feature<BlockStateConfiguration> {
    public static final int POLAR_START_Z = 4096;
    public ShadowCraterIceFeature() { super(BlockStateConfiguration.CODEC); }
    public static boolean inPolarBand(int z) { return Math.abs((long) z) >= POLAR_START_Z; }

    @Override public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        var level = context.level();
        int cx = (context.origin().getX() & ~15) + 8;
        int cz = (context.origin().getZ() & ~15) + 8;
        if (!inPolarBand(cz)) return false;
        int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, cx, cz) - 1;
        if (surface < level.getMinY() + 16 || surface > level.getMaxY() - 8) return false;
        int bottom = surface - 5;
        // Cuenca pequeña, sin escrituras fuera del chunk de decoración.
        for (int dx = -5; dx <= 5; dx++) for (int dz = -5; dz <= 5; dz++) {
            int squared = dx * dx + dz * dz;
            if (squared > 25) continue;
            int floor = bottom + Math.round(squared / 25.0F * 5);
            int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, cx + dx, cz + dz) - 1;
            if (top - floor > 20 || floor > top) continue;
            for (int y = floor + 1; y <= top; y++) level.setBlock(new BlockPos(cx + dx, y, cz + dz), Blocks.AIR.defaultBlockState(), 2);
        }
        var stone = ModBlocks.MERCURY_SILICATE.defaultBlockState();
        // Techo sobre la mitad posterior; no depende de que sea de noche al generar.
        for (int dx = -3; dx <= 3; dx++) for (int dz = 0; dz <= 4; dz++) {
            if (dx * dx + dz * dz <= 16) level.setBlock(new BlockPos(cx + dx, bottom + 3, cz + dz), stone, 2);
        }
        boolean changed = false;
        for (int dx = -2; dx <= 2; dx++) for (int dz = 1; dz <= 3; dz++) {
            if (dx * dx + dz * dz > 9) continue;
            var pos = new BlockPos(cx + dx, bottom, cz + dz);
            if (level.getBlockState(pos.above(3)).is(ModBlocks.MERCURY_SILICATE)) {
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 2);
                level.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), 2);
                level.setBlock(pos, context.config().state, 2);
                changed = true;
            }
        }
        return changed;
    }
}

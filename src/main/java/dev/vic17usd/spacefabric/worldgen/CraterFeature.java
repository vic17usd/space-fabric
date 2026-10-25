package dev.vic17usd.spacefabric.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Cráteres pequeños, deterministas y contenidos en el chunk de decoración. */
public final class CraterFeature extends Feature<NoneFeatureConfiguration> {
    public CraterFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level = context.level();
        int cx = (context.origin().getX() & ~15) + 8;
        int cz = (context.origin().getZ() & ~15) + 8;
        int radius = 4 + context.random().nextInt(3);
        int centerY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, cx, cz) - 1;
        boolean changed = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz) / radius;
                if (distance > 1.0) continue;
                int x = cx + dx;
                int z = cz + dz;
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                int floor = centerY - 4 + (int) Math.round(distance * distance * 4);
                // No rellena valles ni toca el fondo del mundo; tampoco escribe en chunks vecinos.
                if (floor < level.getMinY() + 8 || floor > top || top - floor > 20) continue;
                for (int y = floor + 1; y <= top; y++) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, floor, z), Blocks.TUFF.defaultBlockState(), 2);
                changed = true;
            }
        }
        return changed;
    }
}

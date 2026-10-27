package dev.vic17usd.spacefabric.worldgen.structure;

import dev.vic17usd.spacefabric.block.ModBlocks;
import java.util.function.IntBinaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/** La altura se decide una vez; cada chunk prepara únicamente sus propias columnas. */
public final class SurfacePlot {
    private SurfacePlot() { }

    /** Muestreo acotado, incluidos bordes: /locate no calcula decenas de miles de columnas de ruido. */
    public static int sampleHighest(BoundingBox plot, IntBinaryOperator height) {
        int highest = Integer.MIN_VALUE;
        for (int x = plot.minX(); ; x = Math.min(x + 16, plot.maxX())) {
            for (int z = plot.minZ(); ; z = Math.min(z + 16, plot.maxZ())) {
                highest = Math.max(highest, height.applyAsInt(x, z));
                if (z == plot.maxZ()) break;
            }
            if (x == plot.maxX()) break;
        }
        return highest;
    }

    public static void prepare(WorldGenLevel level, BoundingBox plot, int originY, BoundingBox clip) {
        prepare(level,plot,originY,clip,ModBlocks.IRON_OXIDE_REGOLITH.defaultBlockState(),ModBlocks.MARTIAN_STONE.defaultBlockState());
    }
    public static void prepareVenus(WorldGenLevel level,BoundingBox plot,int originY,BoundingBox clip) {
        prepare(level,plot,originY,clip,ModBlocks.VULCANIZED_BASALT.defaultBlockState(),Blocks.BASALT.defaultBlockState());
    }
    private static void prepare(WorldGenLevel level,BoundingBox plot,int originY,BoundingBox clip,
            net.minecraft.world.level.block.state.BlockState surface,net.minecraft.world.level.block.state.BlockState foundation) {
        int lowX = Math.max(plot.minX(), clip.minX()), highX = Math.min(plot.maxX(), clip.maxX());
        int lowZ = Math.max(plot.minZ(), clip.minZ()), highZ = Math.min(plot.maxZ(), clip.maxZ());
        var cursor = new BlockPos.MutableBlockPos();
        for (int x = lowX; x <= highX; x++) for (int z = lowZ; z <= highZ; z++) {
            // Recorta también picos entre muestras: ningún bloque natural queda dentro del edificio.
            int top = Math.min(level.getMaxY(), level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1);
            for (int y = Math.max(originY, clip.minY()); y <= Math.min(top, clip.maxY()); y++) {
                cursor.set(x, y, z);
                if (!level.getBlockState(cursor).isAir()) level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 18);
            }
            // Rellena cañones hasta suelo sólido, con un mínimo de cuatro capas de soporte.
            for (int y = originY - 1; y >= Math.max(level.getMinY() + 4, clip.minY()); y--) {
                cursor.set(x, y, z);
                var existing = level.getBlockState(cursor);
                if (y < originY - 4 && existing.canOcclude() && existing.getFluidState().isEmpty()) break;
                if (y > clip.maxY()) continue;
                var fill = y == originY - 1 ? surface : foundation;
                if (!existing.equals(fill)) level.setBlock(cursor, fill, 18);
            }
        }
    }
}

package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.SulfurClusterBlock;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Sparse outcrops, fully inside their chunk and resting on nine natural, level ground blocks. */
public final class SulfurClusterFeature extends Feature<NoneFeatureConfiguration> {
    public SulfurClusterFeature() { super(NoneFeatureConfiguration.CODEC); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level=context.level();
        if (!level.getLevel().dimension().equals(PlanetDimensions.key(Planet.VENUS))) return false;
        return placeOnSurface(level,context.origin(),context.random());
    }
    public static boolean placeOnSurface(net.minecraft.world.level.WorldGenLevel level, BlockPos origin, net.minecraft.util.RandomSource random) {
        int x=(origin.getX()&~15)+2+random.nextInt(10);
        int z=(origin.getZ()&~15)+2+random.nextInt(10);
        var anchor=new BlockPos(x,level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z),z);
        if (!SulfurClusterBlock.canPlace(level,anchor)) return false;
        var host=TagKey.create(Registries.BLOCK,SpaceFabric.id("venus_host_rocks"));
        for (int dx=0;dx<3;dx++) for (int dz=0;dz<3;dz++)
            if (!level.getBlockState(anchor.offset(dx,-1,dz)).is(host)) return false;
        SulfurClusterBlock.placePieces(level,anchor);
        return true;
    }
}

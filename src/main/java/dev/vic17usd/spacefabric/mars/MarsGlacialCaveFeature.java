package dev.vic17usd.spacefabric.mars;

import dev.vic17usd.spacefabric.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/** A deep ice-lined chamber. Checks the complete shell before changing any block. */
public final class MarsGlacialCaveFeature extends Feature<BlockStateConfiguration> {
    public MarsGlacialCaveFeature(){super(BlockStateConfiguration.CODEC);}
    public static double shape(int x,int y,int z){return (x*x+z*z)/36.0+y*y/16.0;}
    private static boolean host(BlockState s){return s.is(ModBlocks.MARTIAN_STONE)||s.is(ModBlocks.IRON_OXIDE_REGOLITH)||s.is(MarsPolarContent.ICE)||s.is(MarsPolarContent.CO2);}
    public static boolean carve(WorldGenLevel level,BlockPos center){
        // Center must leave the full six-block radius inside the decorating chunk.
        if((center.getX()&15)!=8||(center.getZ()&15)!=8||center.getY()-4<level.getMinY()+6||center.getY()+4>=level.getMaxY())return false;
        for(int x=-6;x<=6;x++)for(int z=-6;z<=6;z++)for(int y=-4;y<=4;y++)if(shape(x,y,z)<=1){
            var p=center.offset(x,y,z);if(!host(level.getBlockState(p))||!level.getFluidState(p).isEmpty())return false;
        }
        for(int x=-6;x<=6;x++)for(int z=-6;z<=6;z++)for(int y=-4;y<=4;y++){
            double q=shape(x,y,z);if(q>1)continue;
            level.setBlock(center.offset(x,y,z),q<.55?Blocks.CAVE_AIR.defaultBlockState():MarsPolarContent.ICE.defaultBlockState(),2);
        }
        level.setBlock(center.offset(0,-2,0),MarsPolarContent.NODE.defaultBlockState(),2);return true;
    }
    @Override public boolean place(FeaturePlaceContext<BlockStateConfiguration> context){
        var origin=context.origin();var chunk=context.level().getChunk(origin);
        if(!chunk.getAllStarts().isEmpty()||!chunk.getAllReferences().isEmpty())return false;
        return carve(context.level(),new BlockPos((origin.getX()&~15)+8,origin.getY(),(origin.getZ()&~15)+8));
    }
}

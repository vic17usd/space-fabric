package dev.vic17usd.spacefabric.sphere;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Writes only its own chunk, without creating terrain elsewhere in the dimension. */
public final class SphereFeature extends Feature<NoneFeatureConfiguration> {
    public SphereFeature(){super(NoneFeatureConfiguration.CODEC);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> c){
        int cx=c.origin().getX()&~15,cz=c.origin().getZ()&~15;
        if(cx>32||cx+15< -32||cz>32||cz+15< -32)return false;
        boolean changed=false;
        for(int x=cx;x<cx+16;x++)for(int z=cz;z<cz+16;z++)for(int y=72;y<=120;y++)if(SphereGeometry.solid(x,y,z)){
            var relative=new net.minecraft.world.phys.Vec3(x+.5,y+.5,z+.5).subtract(SphereGeometry.CENTER);
            var block=Blocks.STONE;
            if(relative.length()>22){
                if(Math.abs(relative.y)>17)block=Blocks.SNOW_BLOCK;
                else if(Math.abs(relative.y)<1.5)block=Blocks.YELLOW_CONCRETE;
                else if(Math.abs(relative.x)<1.5)block=Blocks.RED_CONCRETE;
                else block=Blocks.GREEN_CONCRETE;
            }
            c.level().setBlock(new BlockPos(x,y,z),block.defaultBlockState(),2);changed=true;
        }
        return changed;
    }
}

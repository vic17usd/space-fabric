package dev.vic17usd.spacefabric.venus;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.util.*;

/** Bounded terrain operations before settlements are placed. Every write stays in the decorated chunk. */
public final class VenusLandscapeFeature extends Feature<NoneFeatureConfiguration> {
    public enum Kind { VENTS("venus_vent_field"),LAKE("venus_acid_lakes"),FISSURE("venus_lava_fissures");
        public final String path;Kind(String path){this.path=path;} }
    private static final TagKey<net.minecraft.world.level.block.Block> HOST=TagKey.create(Registries.BLOCK,SpaceFabric.id("venus_host_rocks"));
    private final Kind kind;
    public VenusLandscapeFeature(Kind kind){super(NoneFeatureConfiguration.CODEC);this.kind=kind;}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if(!context.level().getLevel().dimension().equals(PlanetDimensions.key(Planet.VENUS)))return false;
        return placePatch(context.level(),context.origin(),context.random(),kind);
    }
    private static BlockPos ground(WorldGenLevel level,int x,int z) {return new BlockPos(x,level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1,z);}
    private static boolean dry(WorldGenLevel level,BlockPos pos) {
        return pos.getY()>level.getMinY()+5&&pos.getY()+4<level.getMaxY()&&level.getBlockState(pos).is(HOST)
                &&level.getBlockState(pos).getFluidState().isEmpty()&&level.getBlockState(pos.above()).isAir();
    }
    public static boolean placePatch(WorldGenLevel level,BlockPos origin,RandomSource random,Kind kind) {
        int x=(origin.getX()&~15)+8,z=(origin.getZ()&~15)+8;
        if(kind==Kind.VENTS) {
            var writes=new LinkedHashMap<BlockPos,BlockState>();
            for(int i=0;i<3;i++) {
                var p=ground(level,x-3+random.nextInt(7),z-3+random.nextInt(7));
                if(!dry(level,p)||!level.getBlockState(p.above(2)).isAir())continue;
                writes.put(p,VenusGeology.EMBER_CRUST.defaultBlockState());
                writes.put(p.above(),VenusGeology.VENT.defaultBlockState());
                if(random.nextBoolean())writes.put(p.above(2),VenusGeology.VENT.defaultBlockState());
                for(var direction:net.minecraft.core.Direction.Plane.HORIZONTAL) {
                    var edge=ground(level,p.getX()+direction.getStepX(),p.getZ()+direction.getStepZ());
                    if(!dry(level,edge)||writes.containsKey(edge.above()))continue;
                    writes.put(edge,VenusGeology.SULFUR_CRUST.defaultBlockState());
                    if(random.nextBoolean())writes.put(edge.above(),VenusGeology.ACID_POCKET.defaultBlockState());
                }
            }
            writes.forEach((p,state)->level.setBlock(p,state,2));return !writes.isEmpty();
        }
        if(kind==Kind.FISSURE) {
            boolean alongX=random.nextBoolean();int length=6+random.nextInt(5);var points=new ArrayList<BlockPos>();
            int low=Integer.MAX_VALUE,high=Integer.MIN_VALUE;
            for(int i=0;i<length;i++) {
                var p=ground(level,x-5+(alongX?i:5),z-5+(alongX?5:i));
                if(!dry(level,p))return false;points.add(p);low=Math.min(low,p.getY());high=Math.max(high,p.getY());
            }
            if(high-low>2)return false;
            int y=low-1;var writes=new LinkedHashMap<BlockPos,BlockState>();
            for(var p:points) {
                var q=new BlockPos(p.getX(),y,p.getZ());
                writes.put(q.below(),VenusGeology.SUBSTRATE.defaultBlockState());
                for(var d:net.minecraft.core.Direction.Plane.HORIZONTAL)writes.put(q.relative(d),VenusGeology.SLAG.defaultBlockState());
            }
            for(var p:points) {
                writes.put(new BlockPos(p.getX(),y,p.getZ()),dev.vic17usd.spacefabric.fluid.PlanetFluids.VISCOUS_LAVA_BLOCK.defaultBlockState());
                for(int yy=y+1;yy<=p.getY();yy++)writes.put(new BlockPos(p.getX(),yy,p.getZ()),Blocks.AIR.defaultBlockState());
            }
            writes.forEach((p,state)->level.setBlock(p,state,2));return true;
        }
        int radius=4+random.nextInt(3);var columns=new ArrayList<BlockPos>();int low=Integer.MAX_VALUE,high=Integer.MIN_VALUE;
        for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++) {
            if(dx*dx+dz*dz>radius*radius)continue;
            var p=ground(level,x+dx,z+dz);if(!dry(level,p))return false;
            columns.add(p);low=Math.min(low,p.getY());high=Math.max(high,p.getY());
        }
        if(high-low>2)return false;
        int floor=low-2;
        for(var p:columns) {
            int dx=p.getX()-x,dz=p.getZ()-z;boolean rim=dx*dx+dz*dz>(radius-1)*(radius-1);
            level.setBlock(new BlockPos(p.getX(),floor,p.getZ()),VenusGeology.SUBSTRATE.defaultBlockState(),2);
            level.setBlock(new BlockPos(p.getX(),floor+1,p.getZ()),rim?VenusGeology.SLAG.defaultBlockState():VenusGeology.ACID_POOL.defaultBlockState(),2);
            for(int yy=floor+2;yy<=p.getY();yy++)level.setBlock(new BlockPos(p.getX(),yy,p.getZ()),Blocks.AIR.defaultBlockState(),2);
        }
        return true;
    }
}

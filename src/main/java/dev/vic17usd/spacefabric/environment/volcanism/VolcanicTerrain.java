package dev.vic17usd.spacefabric.environment.volcanism;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.venus.VenusGeology;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Loaded-only queries shared by the live adapter and native collision tests. Never writes blocks. */
public final class VolcanicTerrain {
    private static final TagKey<Block> ROCKS=TagKey.create(Registries.BLOCK,SpaceFabric.id("venus_host_rocks"));
    public interface View {
        boolean loaded(BlockPos pos);
        BlockState state(BlockPos pos);
        BlockGetter blocks();
    }
    private VolcanicTerrain() { }
    private static boolean air(View view,BlockPos pos){return view.loaded(pos)&&view.state(pos).isAir();}
    public static VolcanicCycle.Kind source(View view,BlockPos pos){
        if(!view.loaded(pos)||!air(view,pos.above())||!air(view,pos.above(2)))return null;
        var state=view.state(pos);
        if(state.is(VenusGeology.VENT))return VolcanicCycle.Kind.GAS;
        if(!state.getFluidState().is(FluidTags.LAVA)||!state.getFluidState().isSource())return null;
        int rocks=0;
        for(var d:Direction.Plane.HORIZONTAL){var next=pos.relative(d);if(!view.loaded(next))return null;if(view.state(next).is(ROCKS))rocks++;}
        return rocks>=2?VolcanicCycle.Kind.ERUPTION:null;
    }
    public static boolean clearRay(View view,Vec3 start,Vec3 end){
        double length=start.distanceTo(end);if(!Double.isFinite(length)||length>16)return false;
        if(length<1.0E-6){var p=BlockPos.containing(start);return view.loaded(p)&&view.state(p).getCollisionShape(view.blocks(),p).isEmpty();}
        // Native voxel traversal visits even cells crossed briefly at a diagonal corner.
        return !BlockGetter.traverseBlocks(start,end,view,(world,p)->{
            if(!world.loaded(p))return Boolean.TRUE;
            var shape=world.state(p).getCollisionShape(world.blocks(),p);
            return !shape.isEmpty()&&shape.clip(start,end,p)!=null?Boolean.TRUE:null;
        },world->Boolean.FALSE);
    }
}

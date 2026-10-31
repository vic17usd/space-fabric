package dev.vic17usd.spacefabric.venus.regions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;

/** One-pixel layer matching the supplied top-face model; removable with a shovel. */
public final class SulfurAshBlock extends CarpetBlock {
    public static final MapCodec<SulfurAshBlock> CODEC=simpleCodec(SulfurAshBlock::new);
    private static final VoxelShape SHAPE=Block.box(0,0,0,16,1,16);
    public SulfurAshBlock(Properties p){super(p);}
    @Override public MapCodec<SulfurAshBlock> codec(){return CODEC;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
}

package dev.vic17usd.spacefabric.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

/** La caída, el aterrizaje y el drop al no poder colocarse los gestiona FallingBlock. */
public final class PlanetRegolithBlock extends FallingBlock {
    public static final MapCodec<PlanetRegolithBlock> CODEC = simpleCodec(PlanetRegolithBlock::new);

    public PlanetRegolithBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<PlanetRegolithBlock> codec() { return CODEC; }

    @Override public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getMapColor(level, pos).col;
    }
}

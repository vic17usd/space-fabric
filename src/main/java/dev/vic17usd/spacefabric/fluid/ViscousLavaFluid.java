package dev.vic17usd.spacefabric.fluid;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Lava de propagación lenta: viscosidad de juego, sin simulación física del fluido. */
public abstract class ViscousLavaFluid extends LavaFluid {
    @Override public Fluid getSource() { return PlanetFluids.VISCOUS_LAVA; }
    @Override public Fluid getFlowing() { return PlanetFluids.FLOWING_VISCOUS_LAVA; }
    @Override public Item getBucket() { return PlanetFluids.VISCOUS_LAVA_BUCKET; }
    @Override public boolean isSame(Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }
    @Override public int getTickDelay(LevelReader level) { return 80; }
    @Override public int getSpreadDelay(Level level, BlockPos pos, FluidState current, FluidState next) { return 80; }
    @Override public int getSlopeFindDistance(LevelReader level) { return 1; }
    @Override public int getDropOff(LevelReader level) { return 3; }
    @Override public BlockState createLegacyBlock(FluidState state) {
        return PlanetFluids.VISCOUS_LAVA_BLOCK.defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static final class Source extends ViscousLavaFluid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }

    public static final class Flowing extends ViscousLavaFluid {
        @Override protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }
}

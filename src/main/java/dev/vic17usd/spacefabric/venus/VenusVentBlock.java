package dev.vic17usd.spacefabric.venus;

import com.mojang.serialization.MapCodec;
import dev.vic17usd.spacefabric.block.HotVolcanicRockBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Full natural rock; only exposed vents emit smoke. Venus fauna retain their heat immunity. */
public final class VenusVentBlock extends Block {
    public static final MapCodec<VenusVentBlock> CODEC=simpleCodec(VenusVentBlock::new);
    public VenusVentBlock(Properties properties){super(properties);}
    @Override protected MapCodec<VenusVentBlock> codec(){return CODEC;}
    @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){super.stepOn(level,pos,state,entity);HotVolcanicRockBlock.applyHeat(level,entity);}
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random) {
        if(random.nextInt(3)!=0||!level.getBlockState(pos.above()).isAir())return;
        level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,pos.getX()+.5,pos.getY()+1.05,pos.getZ()+.5,0,.025,0);
        if(random.nextInt(5)==0)level.addParticle(ParticleTypes.SMALL_FLAME,pos.getX()+.5,pos.getY()+1.02,pos.getZ()+.5,0,.015,0);
    }
}

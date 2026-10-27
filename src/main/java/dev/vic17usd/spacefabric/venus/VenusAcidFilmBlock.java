package dev.vic17usd.spacefabric.venus;

import com.mojang.serialization.MapCodec;
import dev.vic17usd.spacefabric.equipment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;

/** Shallow corrosive deposit from the supplied design, not a simulated real Venus liquid. */
public final class VenusAcidFilmBlock extends net.minecraft.world.level.block.CarpetBlock {
    public static final MapCodec<VenusAcidFilmBlock> CODEC=simpleCodec(VenusAcidFilmBlock::new);
    private static final VoxelShape SHAPE=Block.box(0,0,0,16,2,16);
    public VenusAcidFilmBlock(Properties properties){super(properties);}
    @Override public MapCodec<VenusAcidFilmBlock> codec(){return CODEC;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {
        return level.getBlockState(pos.below()).getFluidState().isEmpty()&&level.getBlockState(pos.below()).isCollisionShapeFullBlock(level,pos.below());
    }
    @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity,InsideBlockEffectApplier effects,boolean inside) {
        if(entity.getBoundingBox().minY>pos.getY()+.2)return;
        contact(level,entity);
    }
    private static void contact(Level level,Entity entity) {
        if(!(level instanceof ServerLevel server)||!(entity instanceof LivingEntity living)||!living.isAlive()||living.tickCount%20!=0)return;
        if(living instanceof dev.vic17usd.spacefabric.entity.venus.VenusMob)return;
        if(living instanceof Player player) {
            if(player.isCreative()||player.isSpectator())return;
            if(SuitProtection.resistsAcid(player))return;
        }
        living.addEffect(new MobEffectInstance(MobEffects.POISON,60));
        living.hurtServer(server,server.damageSources().magic(),1);
    }
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random) {
        if(random.nextInt(7)==0&&level.getBlockState(pos.above()).isAir())level.addParticle(ParticleTypes.SMOKE,pos.getX()+random.nextDouble(),pos.getY()+.15,pos.getZ()+random.nextDouble(),0,.02,0);
    }
}

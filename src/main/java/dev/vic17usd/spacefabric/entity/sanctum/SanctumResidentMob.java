package dev.vic17usd.spacefabric.entity.sanctum;

import dev.vic17usd.spacefabric.entity.MartianCopperTraderEntity;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Residentes persistentes: hogar, oxígeno propio y gravedad planetaria. */
public abstract class SanctumResidentMob extends PathfinderMob {
    protected SanctumResidentMob(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level); setPersistenceRequired();
        goalSelector.addGoal(0, new ReturnHomeGoal());
    }
    @Override public void tick() {
        if (!level().isClientSide() && !hasHome()) setHomeTo(blockPosition(), 20);
        super.tick();
        if (!level().isClientSide() && getTarget() != null && (level().getDifficulty() == Difficulty.PEACEFUL
                || getTarget().distanceToSqr(getHomePosition().getCenter()) > 28 * 28)) setTarget(null);
    }
    @Override public boolean canAttack(LivingEntity target) {
        return level().getDifficulty() != Difficulty.PEACEFUL && !(target instanceof SanctumResidentMob) && !isAlliedTo(target) && super.canAttack(target);
    }
    @Override public boolean isInvulnerableTo(ServerLevel level, DamageSource damage) {
        return MartianCopperTraderEntity.resistsAtmosphere(damage) || super.isInvulnerableTo(level, damage);
    }
    @Override public boolean canBreatheUnderwater() { return true; }
    @Override protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (hasHome()) output.store("SanctumHome", BlockPos.CODEC, getHomePosition());
    }
    @Override protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read("SanctumHome", BlockPos.CODEC).ifPresent(home -> setHomeTo(home, 20));
        setPersistenceRequired(); setNoGravity(false); noPhysics = false;
    }
    private final class ReturnHomeGoal extends Goal {
        private ReturnHomeGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() { return hasHome() && getHomePosition().distSqr(blockPosition()) > 20 * 20; }
        @Override public boolean canContinueToUse() { return hasHome() && getHomePosition().distSqr(blockPosition()) > 8 * 8; }
        @Override public void tick() {
            setTarget(null); var home = getHomePosition();
            getNavigation().moveTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5, 1.1);
        }
    }
}

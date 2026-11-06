package dev.vic17usd.spacefabric.entity.fauna;

import dev.vic17usd.spacefabric.item.ModItems;
import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Variante de superficie: detecta movimiento y golpea con onda expansiva sin excavar ni teletransportarse. */
public final class RedDuneIronmawEntity extends RedDuneIronmawRigEntity {
    public RedDuneIronmawEntity(EntityType<? extends RedDuneIronmawEntity> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() {
        return RedDuneIronmawRigEntity.createAttributes().add(Attributes.MAX_HEALTH, 90).add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.FOLLOW_RANGE, 40).add(Attributes.ATTACK_DAMAGE, 14).add(Attributes.ARMOR, 8)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85).add(Attributes.SCALE, 3.8 / RedDuneIronmawRigProfile.HEIGHT);
    }
    @Override public int getMaxSpawnClusterSize() { return 1; }
    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this)); goalSelector.addGoal(1, new SurfaceHuntGoal());
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false,
                (entity, level) -> entity instanceof Player player && !player.isCreative() && !player.isSpectator()
                        && player.getDeltaMovement().horizontalDistanceSqr() > 0.00035));
    }
    private final class SurfaceHuntGoal extends Goal {
        private long nextAttack;
        private boolean active, delivered;
        private SurfaceHuntGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() { return getTarget() != null && getTarget().isAlive(); }
        @Override public boolean canContinueToUse() { return canUse() && distanceToSqr(getTarget()) < 2304; }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void tick() {
            var target = getTarget(); if (target == null) return;
            getLookControl().setLookAt(target, 45, 45);
            if (active) {
                getNavigation().stop();
                long impact = RedDuneIronmawRigProfile.effectStartMillis("primary_attack");
                if (!delivered && impact >= 0 && actionTimeMillis(0) >= impact) {
                    delivered = true;
                    if (distanceToSqr(target) <= 25 && getSensing().hasLineOfSight(target)) shockwave();
                }
                if (actionIndex() != RedDuneIronmawRigProfile.actionIndex("primary_attack")) { active = false; nextAttack = level().getGameTime() + 60; }
            } else if (distanceToSqr(target) > 20 || !getSensing().hasLineOfSight(target)) getNavigation().moveTo(target, 1.1);
            else {
                getNavigation().stop();
                if (level().getGameTime() >= nextAttack && playAction("primary_attack")) { active = true; delivered = false; }
            }
        }
        @Override public void stop() { active = false; stopAction(); getNavigation().stop(); }
    }
    private void shockwave() {
        var server = (ServerLevel) level();
        playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.7F, 0.72F);
        server.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1, getZ(), 28, 1.8, 0.45, 1.8, 0.035);
        for (var victim : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5),
                entity -> entity != this && entity.isAlive() && !isAlliedTo(entity) && distanceToSqr(entity) <= 25
                        && hasLineOfSight(entity) && !(entity instanceof Player player && (player.isCreative() || player.isSpectator())))) {
            if (doHurtTarget(server, victim)) {
                victim.setRemainingFireTicks(80);
                var direction = victim.position().subtract(position()).normalize();
                victim.push(direction.x * 0.6, 0.18, direction.z * 0.6); victim.hurtMarked = true;
            }
        }
    }
    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean player) {
        super.dropCustomDeathLoot(level, source, player);
        spawnAtLocation(level, new ItemStack(ModItems.VIBRATION_SENSOR, 1 + random.nextInt(3)));
        spawnAtLocation(level, new ItemStack(ModItems.CRUSHED_IRON_ORE, 2 + random.nextInt(4)));
    }
}

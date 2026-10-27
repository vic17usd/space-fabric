package dev.vic17usd.spacefabric.entity.sanctum;

import java.util.Comparator;
import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Guardia terrestre con escudo frontal y respuesta al daño del jefe. */
public final class BasaltBosswardenEntity extends BasaltBosswardenRigEntity {
    private BasaltEmberGolemEntity boss;
    private long nextScan, nextShield;
    public BasaltBosswardenEntity(EntityType<? extends BasaltBosswardenEntity> type, Level level) { super(type, level); xpReward = 30; }
    public static AttributeSupplier.Builder createAttributes() {
        return BasaltBosswardenRigEntity.createAttributes().add(Attributes.MAX_HEALTH, 80)
                .add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.ATTACK_DAMAGE, 14)
                .add(Attributes.ARMOR, 14).add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
                .add(Attributes.SCALE, 3.0 / BasaltBosswardenRigProfile.HEIGHT)
                .add(Attributes.STEP_HEIGHT, 1).add(Attributes.SAFE_FALL_DISTANCE, 8);
    }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new GuardCombatGoal());
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    @Override public void tick() {
        super.tick();
        if (!level().isClientSide() && level().getGameTime() >= nextScan) {
            nextScan = level().getGameTime() + 40;
            boss = level().getEntitiesOfClass(BasaltEmberGolemEntity.class, getBoundingBox().inflate(48), LivingEntity::isAlive)
                    .stream().min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
        }
        if (!level().isClientSide() && boss != null && getTarget() != null && getTarget().distanceToSqr(boss) > 24 * 24) setTarget(null);
    }
    public void rallyTo(BasaltEmberGolemEntity protector, Entity attacker) {
        boss = protector; addEffect(new MobEffectInstance(MobEffects.SPEED, 160, 1));
        if (attacker instanceof LivingEntity living && canAttack(living)) setTarget(living);
        if (actionIndex() < 0) playAction("boss_rage");
    }
    public boolean isShieldRaised() { return actionIndex() == BasaltBosswardenRigProfile.actionIndex("shield_block"); }
    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (isShieldRaised() && !source.is(DamageTypeTags.BYPASSES_SHIELD) && source.getSourcePosition() != null) {
            var toward = source.getSourcePosition().subtract(position()).normalize();
            if (getLookAngle().dot(toward) > 0.1) {
                playSound(SoundEvents.SHIELD_BLOCK.value(), 0.9F, 0.85F);
                if (source.getDirectEntity() instanceof LivingEntity attacker && attacker.getMainHandItem().is(ItemTags.AXES)) {
                    stopAction(); nextShield = level.getGameTime() + 140;
                } else return false;
            }
        }
        return super.hurtServer(level, source, amount);
    }
    private final class GuardCombatGoal extends Goal {
        private boolean delivered;
        private long nextAttack;
        private GuardCombatGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() { return getTarget() != null && canAttack(getTarget()) || boss != null && boss.isAlive(); }
        @Override public boolean canContinueToUse() { return canUse(); }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void tick() {
            var target = getTarget();
            if (target == null || !target.isAlive()) {
                if (boss != null && boss.isAlive() && distanceToSqr(boss) > 64 && actionIndex() < 0) getNavigation().moveTo(boss, 0.95);
                else getNavigation().stop();
                return;
            }
            getLookControl().setLookAt(target, 30, 30);
            if (actionIndex() == BasaltBosswardenRigProfile.actionIndex("primary_attack")) {
                getNavigation().stop();
                if (!delivered && actionTimeMillis(0) >= BasaltBosswardenRigProfile.effectStartMillis("primary_attack")) {
                    delivered = true;
                    if (distanceToSqr(target) <= 9 && hasLineOfSight(target)) doHurtTarget((ServerLevel) level(), target);
                    nextAttack = level().getGameTime() + 35;
                }
            } else if (actionIndex() >= 0) getNavigation().stop();
            else if (distanceToSqr(target) > 9 || !hasLineOfSight(target)) getNavigation().moveTo(target, 1.15);
            else {
                getNavigation().stop(); long now = level().getGameTime();
                if (now >= nextShield) { nextShield = now + 120; playAction("shield_block"); }
                else if (now >= nextAttack && playAction("primary_attack")) delivered = false;
            }
        }
        @Override public void stop() { getNavigation().stop(); stopAction(); }
    }
}

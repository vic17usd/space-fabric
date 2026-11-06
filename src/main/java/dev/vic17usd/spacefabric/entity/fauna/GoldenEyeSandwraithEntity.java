package dev.vic17usd.spacefabric.entity.fauna;

import dev.vic17usd.spacefabric.item.ModItems;
import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Acechador terrestre nocturno: conserva el ataque animado de viento sin navegación aérea. */
public final class GoldenEyeSandwraithEntity extends GoldenEyeSandwraithRigEntity {
    public GoldenEyeSandwraithEntity(EntityType<? extends GoldenEyeSandwraithEntity> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() {
        return GoldenEyeSandwraithRigEntity.createAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.FOLLOW_RANGE, 32).add(Attributes.ATTACK_DAMAGE, 5).add(Attributes.SCALE, 1.55 / GoldenEyeSandwraithRigProfile.HEIGHT);
    }
    @Override public int getMaxSpawnClusterSize() { return 2; }
    private boolean hunting() { return level().getSkyDarken() >= 4 || level().isThundering(); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this)); goalSelector.addGoal(1, new WindBlastGoal());
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true) {
            @Override public boolean canUse() { return hunting() && super.canUse(); }
        });
    }
    private final class WindBlastGoal extends Goal {
        private long nextAttack;
        private boolean active, delivered;
        private LivingEntity victim;
        private WindBlastGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() { return getTarget() != null && getTarget().isAlive(); }
        @Override public boolean canContinueToUse() { return canUse() && distanceToSqr(getTarget()) < 1600; }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void tick() {
            var target = getTarget(); if (target == null) return;
            getLookControl().setLookAt(target, 30, 30);
            if (active) {
                getNavigation().stop();
                long impact = GoldenEyeSandwraithRigProfile.effectStartMillis("primary_attack");
                if (!delivered && impact >= 0 && actionTimeMillis(0) >= impact) {
                    delivered = true;
                    if (target == victim && target.isAlive() && distanceToSqr(target) <= 121 && getSensing().hasLineOfSight(target)) blast(target);
                }
                if (actionIndex() != GoldenEyeSandwraithRigProfile.actionIndex("primary_attack")) {
                    active = false; victim = null; nextAttack = level().getGameTime() + 45;
                }
            } else if (distanceToSqr(target) > 100 || !getSensing().hasLineOfSight(target)) {
                getNavigation().moveTo(target, 1.1);
            } else {
                getNavigation().stop();
                if (level().getGameTime() >= nextAttack && playAction("primary_attack")) { active = true; delivered = false; victim = target; }
            }
        }
        @Override public void stop() { active = false; victim = null; stopAction(); getNavigation().stop(); }
    }
    private void blast(LivingEntity victim) {
        var server = (ServerLevel) level();
        var direction = victim.position().subtract(position()).normalize();
        server.sendParticles(ParticleTypes.GUST, victim.getX(), victim.getY() + victim.getBbHeight() / 2, victim.getZ(), 5, 0.25, 0.25, 0.25, 0.02);
        playSound(SoundEvents.GHAST_SHOOT, 1, 0.85F);
        if (victim.hurtServer(server, damageSources().mobAttack(this), 5)) {
            victim.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 70), this);
            // Impulso vertical reducido para la gravedad 0,38 de Marte.
            victim.push(direction.x, 0.20, direction.z); victim.hurtMarked = true;
        }
    }
    @Override public void die(DamageSource source) { if (!level().isClientSide()) playAction("death"); super.die(source); }
    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean player) {
        super.dropCustomDeathLoot(level, source, player);
        spawnAtLocation(level, new ItemStack(ModItems.CONDENSED_STARDUST, 1 + random.nextInt(2)));
    }
}

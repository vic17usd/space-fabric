package dev.vic17usd.spacefabric.entity.sanctum;

import dev.vic17usd.spacefabric.item.ModItems;
import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Jefe del altar: impacto animado, sin explosiones que destruyan la arena. */
public final class BasaltEmberGolemEntity extends BasaltEmberGolemRigEntity {
    private final ServerBossEvent bar = new ServerBossEvent(java.util.UUID.randomUUID(), getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    private final java.util.Set<ServerPlayer> tracked = new java.util.HashSet<>();
    public BasaltEmberGolemEntity(EntityType<? extends BasaltEmberGolemEntity> type, Level level) { super(type, level); xpReward = 250; }
    public static AttributeSupplier.Builder createAttributes() {
        return BasaltEmberGolemRigEntity.createAttributes().add(Attributes.MAX_HEALTH, 1200)
                .add(Attributes.MOVEMENT_SPEED, 0.12).add(Attributes.FOLLOW_RANGE, 40)
                .add(Attributes.ATTACK_DAMAGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.SCALE, 8.1 / BasaltEmberGolemRigProfile.HEIGHT)
                .add(Attributes.STEP_HEIGHT, 1).add(Attributes.SAFE_FALL_DISTANCE, 8);
    }
    @Override protected void registerGoals() {
        // Mob llama este método antes de inicializar los campos de la subclase.
        goalSelector.addGoal(1, new SlamGoal());
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 20));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    @Override public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            bar.setProgress(getHealth() / getMaxHealth()); bar.setName(getDisplayName());
            for (var player : tracked) {
                if (isAlive() && player.level() == level() && distanceToSqr(player) <= 48 * 48 && Math.abs(player.getY() - getY()) <= 24)
                    bar.addPlayer(player);
                else bar.removePlayer(player);
            }
        }
    }
    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); tracked.add(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); tracked.remove(player); bar.removePlayer(player); }
    @Override public boolean hurtServer(ServerLevel level, DamageSource damage, float amount) {
        boolean hurt = super.hurtServer(level, damage, amount);
        if (hurt) for (var guard : level.getEntitiesOfClass(BasaltBosswardenEntity.class, getBoundingBox().inflate(48))) guard.rallyTo(this, damage.getEntity());
        return hurt;
    }
    @Override public void die(DamageSource source) { tracked.clear(); bar.removeAllPlayers(); super.die(source); }
    @Override protected void dropCustomDeathLoot(ServerLevel level, DamageSource damage, boolean player) {
        super.dropCustomDeathLoot(level, damage, player); spawnAtLocation(level, new ItemStack(ModItems.OBSIDIAN_CORE));
    }
    private void slam() {
        var server = (ServerLevel) level();
        for (Player player : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(9),
                player -> canAttack(player) && !player.isSpectator() && !player.isCreative() && hasLineOfSight(player))) {
            if (position().distanceToSqr(player.position()) > 81) continue;
            if (player.hurtServer(server, damageSources().mobAttack(this), 24)) {
                var away = player.position().subtract(position()).normalize();
                player.push(away.x * 1.2, 0.22, away.z * 1.2); player.hurtMarked = true;
            }
        }
        playSound(SoundEvents.IRON_GOLEM_ATTACK, 2.5F, 0.55F);
        server.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.2, getZ(), 1, 0, 0, 0, 0);
        server.sendParticles(ParticleTypes.LAVA, getX(), getY() + 0.2, getZ(), 20, 1.4, 0.1, 1.4, 0.02);
    }
    private final class SlamGoal extends Goal {
        private boolean active, delivered;
        private long nextAttack;
        private SlamGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() { return getTarget() != null && getTarget().isAlive() && canAttack(getTarget()); }
        @Override public boolean canContinueToUse() { return canUse(); }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void tick() {
            var target = getTarget(); if (target == null) return;
            getLookControl().setLookAt(target, 30, 30);
            if (active) {
                getNavigation().stop();
                if (!delivered && actionTimeMillis(0) >= BasaltEmberGolemRigProfile.effectStartMillis("primary_attack")) { delivered = true; slam(); }
                if (actionIndex() < 0) { active = false; nextAttack = level().getGameTime() + 30; }
            } else if (distanceToSqr(target) > 36 || !hasLineOfSight(target)) getNavigation().moveTo(target, 0.72);
            else {
                getNavigation().stop();
                if (level().getGameTime() >= nextAttack && playAction("primary_attack")) { active = true; delivered = false; }
            }
        }
        @Override public void stop() { active = false; stopAction(); getNavigation().stop(); }
    }
}

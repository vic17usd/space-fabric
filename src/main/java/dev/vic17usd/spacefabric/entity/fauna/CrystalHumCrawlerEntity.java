package dev.vic17usd.spacefabric.entity.fauna;

import dev.vic17usd.spacefabric.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Habitante pasivo de la superficie: consume polvo, produce fragmentos y se protege al recibir daño. */
public final class CrystalHumCrawlerEntity extends CrystalHumCrawlerRigEntity {
    private static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(CrystalHumCrawlerEntity.class, EntityDataSerializers.BOOLEAN);
    private long nextBite, nextHum, hideUntil;
    private int bites;
    public CrystalHumCrawlerEntity(EntityType<? extends CrystalHumCrawlerEntity> type, Level level) {
        super(type, level); nextBite = level.getGameTime() + 240; nextHum = level.getGameTime() + 500;
    }
    public static AttributeSupplier.Builder createAttributes() {
        return CrystalHumCrawlerRigEntity.createAttributes().add(Attributes.MAX_HEALTH, 8).add(Attributes.MOVEMENT_SPEED, 0.17)
                .add(Attributes.SCALE, 0.65 / CrystalHumCrawlerRigProfile.HEIGHT).add(Attributes.SAFE_FALL_DISTANCE, 6);
    }
    @Override public int getMaxSpawnClusterSize() { return 3; }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(HIDING, false); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 5));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }
    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt && isAlive()) startHide();
        return hurt;
    }
    @Override public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        return entityData.get(HIDING) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !(source.getEntity() instanceof Player player && player.isCreative()) || super.isInvulnerableTo(level, source);
    }
    public void startHide() {
        if (level().isClientSide() || entityData.get(HIDING)) return;
        entityData.set(HIDING, true); hideUntil = level().getGameTime() + 48; playAction("hide");
        goalSelector.setControlFlag(Goal.Flag.MOVE, false); getNavigation().stop();
        playSound(ModMartianFauna.CRAWLER_HUM, 0.55F, 1);
    }
    @Override public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        long now = level().getGameTime();
        if (entityData.get(HIDING) && now >= hideUntil) {
            entityData.set(HIDING, false); goalSelector.setControlFlag(Goal.Flag.MOVE, true); stopAction();
        }
        if (now >= nextHum) { playSound(ModMartianFauna.CRAWLER_HUM, 0.28F, 0.92F + random.nextFloat() * 0.16F); nextHum = now + 500 + random.nextInt(501); }
        if (now >= nextBite && !entityData.get(HIDING)) {
            nextBite = now + 240;
            if (onGround() && level().getBlockState(blockPosition().below()).is(MartianSpawnRules.SURFACE) && ++bites >= 25) {
                bites = 0; spawnAtLocation((ServerLevel) level(), new ItemStack(ModItems.ENERGY_CRYSTAL_SHARD));
                ((ServerLevel) level()).sendParticles(ParticleTypes.END_ROD, getX(), getY() + getBbHeight() / 2, getZ(), 5, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }
    @Override protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output); output.putInt("DustBites", bites); output.putLong("NextDustBite", nextBite); output.putLong("NextHum", nextHum);
    }
    @Override protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input); bites = Math.clamp(input.getIntOr("DustBites", 0), 0, 24);
        nextBite = input.getLongOr("NextDustBite", level().getGameTime() + 240); nextHum = input.getLongOr("NextHum", level().getGameTime() + 500);
        entityData.set(HIDING, false); goalSelector.setControlFlag(Goal.Flag.MOVE, true);
    }
}

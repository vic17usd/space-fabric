package dev.vic17usd.spacefabric.entity.colossus;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


public final class SulfurColossusEntity extends SulfurColossusRigEntity {
   private static final String CLAW = "primary_attack";
   private static final String SLAM = "ground_slam";
   private static final String VENT = "venting";
   private static final String BURST = "volcanic_burst";
   private static final String STAGGER = "stagger";
   private static final String DEATH = "death";
   private static final long CORE_OPEN_TICKS = 50L;
   private static final long PHASE_ONE_COOLDOWN = 100L;
   private static final long PHASE_TWO_COOLDOWN = 75L;
   private final ServerBossEvent bossBar;
   private final java.util.Set<ServerPlayer> tracked=new java.util.HashSet<>();
   private boolean lootGranted;
   private BlockPos arenaCenter;
   private long attackReadyAt;
   private long coreOpenUntil;
   private long lastActionTime;
   private boolean effectApplied;
   private String currentAction = "";
   private long lastHitAt;
   private final List<Vec3> burstMarks = new ArrayList<>();
   private static final EntityDataAccessor<Boolean> PHASE_TWO = SynchedEntityData.defineId(SulfurColossusEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CORE_OPEN = SynchedEntityData.defineId(SulfurColossusEntity.class, EntityDataSerializers.BOOLEAN);

   public SulfurColossusEntity(EntityType<? extends SulfurColossusEntity> type, Level level) {
      super(type, level);
      this.bossBar = new ServerBossEvent(this.getUUID(), getDisplayName(), BossBarColor.YELLOW, BossBarOverlay.PROGRESS);
      this.bossBar.setDarkenScreen(false);
      this.bossBar.setCreateWorldFog(false);
      setPersistenceRequired();xpReward=120;
   }

   public static Builder createAttributes() {
      double scale = 1.4084507231437082;
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 400.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2)
         .add(Attributes.ATTACK_DAMAGE, 16.0)
         .add(Attributes.FOLLOW_RANGE, 40.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
         .add(Attributes.STEP_HEIGHT, 1.1)
         .add(Attributes.SCALE, scale);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SulfurColossusEntity.BossCombatGoal(this));
      this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.65, 70) {
         {
            Objects.requireNonNull(SulfurColossusEntity.this);
         }

         public boolean canUse() {
            return super.canUse() && SulfurColossusEntity.this.getTarget() == null && !SulfurColossusEntity.this.isDeadOrDying();
         }
      });
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 20.0F));
      this.targetSelector
         .addGoal(
            1,
            new NearestAttackableTargetGoal<>(
               this,
               Player.class,
               10,
               true,
               false,
               (candidate, serverLevel) -> validTarget(candidate)
            )
         );
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.arenaCenter == null) {
            this.arenaCenter = this.blockPosition();
         }

         this.bossBar.setName(getDisplayName());
         for(var viewer:tracked){if(isAlive()&&viewer.level()==level()&&distanceToSqr(viewer)<=48*48&&Math.abs(viewer.getY()-getY())<=16)bossBar.addPlayer(viewer);else bossBar.removePlayer(viewer);}
         this.bossBar.setProgress(Math.max(0.0F, Math.min(1.0F, this.getHealth() / this.getMaxHealth())));
         this.entityData.set(PHASE_TWO, this.getHealth() <= this.getMaxHealth() * 0.5F);
         this.entityData.set(CORE_OPEN, this.tickCount < this.coreOpenUntil);
         if (this.getHealth() <= this.getMaxHealth() * 0.5F && this.tickCount % 12 == 0 && this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + this.getBbHeight() * 0.45, this.getZ(), 3, 0.75, 0.5, 0.75, 0.01);
         }

         LivingEntity target = this.getTarget();
         if (target != null && (!this.validTarget(target) || target.distanceToSqr(this) > 2304.0)) {
            this.setTarget(null);
            target = null;
         }

         if (target == null && !this.currentAction.isEmpty() && !this.currentAction.equals("death")) {
            this.finishAction();
         }

         if (this.distanceToSqr(Vec3.atCenterOf(this.arenaCenter)) > 2304.0) {
            this.setTarget(null);
            this.finishAction();
            this.getNavigation().moveTo(this.arenaCenter.getX() + 0.5, this.arenaCenter.getY(), this.arenaCenter.getZ() + 0.5, 1.0);
         } else {
            if (this.actionIndex() >= 0) {
               this.tickAction();
            } else if (!this.currentAction.isEmpty()) {
               this.finishAction();
            }

            if (this.getTarget() == null && this.getNavigation().isDone() && this.distanceToSqr(Vec3.atCenterOf(this.arenaCenter)) > 144.0) {
               this.getNavigation().moveTo(this.arenaCenter.getX() + 0.5, this.arenaCenter.getY(), this.arenaCenter.getZ() + 0.5, 0.75);
            }
         }
      }
   }

   private void tickAction() {
      long now = this.actionTimeMillis(0.0F);
      long previous = this.lastActionTime;
      String action = this.currentAction;
      if (!action.equals("death")) {
         long cue = SulfurColossusRigProfile.effectStartMillis(action);
         long effectEnd = SulfurColossusRigProfile.effectEndMillis(action);
         boolean matchingAction = SulfurColossusRigProfile.actionIndex(action) == this.actionIndex();
         if (!this.effectApplied && matchingAction && cue >= 0L && effectEnd >= cue && previous <= effectEnd && now >= cue) {
            LivingEntity target = this.getTarget();
            if (target != null && this.validTarget(target)) {
               this.applyActionEffect(action, target);
            }

            this.effectApplied = true;
         }

         if (action.equals("volcanic_burst") && cue >= 0L && now < cue) {
            this.showBurstWarnings();
         }

         this.lastActionTime = now;
         if (now >= SulfurColossusRigProfile.actionDurationMillis(action)) {
            this.finishAction();
         }
      }
   }

   private void applyActionEffect(String action, LivingEntity target) {
      if (this.level() instanceof ServerLevel server) {
         switch (action) {
            case "primary_attack":
               if (this.withinMeleeReach(target, 1.8) && this.hasLineOfSight(target)) {
                  target.hurtServer(server, this.damageSources().mobAttack(this), 22.0F);
                  this.lastHitAt = this.tickCount;
               }

               this.openCore();
               this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.4F, 0.55F);
               break;
            case "ground_slam":
               this.dealRadialImpact(server, 6.0, 20.0F, 0.95, true);
               this.openCore();
               server.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 0.1, this.getZ(), 32, 3.2, 0.15, 3.2, 0.04);
               server.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 0.2, this.getZ(), 12, 2.3, 0.25, 2.3, 0.02);
               this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 2.0F, 0.55F);
               break;
            case "venting":
               // Gas is filtered by a complete Venus-rated suit.
               AABB cloud = this.getBoundingBox().inflate(4.0, 2.5, 4.0);

               for (LivingEntity victimx : this.level().getEntitiesOfClass(LivingEntity.class, cloud, e -> e != this && e.isAlive())) {
                  if (this.validTarget(victimx) && this.hasLineOfSight(victimx)) {
                     if(victimx instanceof ServerPlayer sheltered && (dev.vic17usd.spacefabric.space.RocketCabin.protects(sheltered)
                         || dev.vic17usd.spacefabric.colony.HabitatSystem.protects(sheltered))) continue;
                     if(victimx instanceof ServerPlayer player&&dev.vic17usd.spacefabric.equipment.SuitProtection.resistsAcid(player)){
                        if(dev.vic17usd.spacefabric.equipment.SuitEquipment.get(player).tier()!=dev.vic17usd.spacefabric.equipment.SuitTier.NONE)
                           dev.vic17usd.spacefabric.equipment.SuitEquipment.environmentalWear(player,1);
                        else for(var slot:new net.minecraft.world.entity.EquipmentSlot[]{net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.entity.EquipmentSlot.CHEST,net.minecraft.world.entity.EquipmentSlot.LEGS,net.minecraft.world.entity.EquipmentSlot.FEET})
                           player.getItemBySlot(slot).hurtAndBreak(1,player,slot);
                     }else{victimx.hurtServer(server,damageSources().mobAttack(this),6);victimx.addEffect(new MobEffectInstance(MobEffects.POISON,60));victimx.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,40));}
                  }
               }

               this.openCore();
               server.sendParticles(
                  ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + this.getBbHeight() * 0.72, this.getZ(), 30, 1.8, 1.0, 1.8, 0.03
               );
               break;
            case "volcanic_burst":
               for (Vec3 mark : this.burstMarks) {
                  server.sendParticles(ParticleTypes.LAVA, mark.x, mark.y + 0.2, mark.z, 16, 0.8, 0.35, 0.8, 0.02);
                  server.sendParticles(ParticleTypes.FLAME, mark.x, mark.y + 0.35, mark.z, 20, 0.8, 0.7, 0.8, 0.04);
                  AABB area = new AABB(mark.x - 2.0, mark.y - 0.3, mark.z - 2.0, mark.x + 2.0, mark.y + 2.5, mark.z + 2.0);

                  for (LivingEntity victim : this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this && e.isAlive())) {
                     if (this.validTarget(victim) && this.hasLineOfSight(victim) && victim.distanceToSqr(mark) <= 5.0) {
                        victim.hurtServer(server, this.damageSources().mobAttack(this), 12.0F);
                        victim.push(0.0, 0.3, 0.0);
                     }
                  }
               }

               this.openCore();
               this.burstMarks.clear();
         }
      }
   }

   private void dealRadialImpact(ServerLevel server, double radius, float damage, double knockback, boolean heavy) {
      AABB area = this.getBoundingBox().inflate(radius, Math.max(2.0, radius * 0.45), radius);

      for (LivingEntity victim : this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this && e.isAlive())) {
         if (this.validTarget(victim) && this.hasLineOfSight(victim) && this.withinMeleeReach(victim, radius)) {
            victim.hurtServer(server, this.damageSources().mobAttack(this), damage);
            double dx = victim.getX() - this.getX();
            double dz = victim.getZ() - this.getZ();
            double length = Math.max(0.001, Math.sqrt(dx * dx + dz * dz));
            victim.push(dx / length * knockback, heavy ? 0.42 : 0.12, dz / length * knockback);
            this.lastHitAt = this.tickCount;
         }
      }
   }

   private boolean withinMeleeReach(LivingEntity target, double reachFromBounds) {
      AABB expanded = this.getBoundingBox().inflate(reachFromBounds, 1.4, reachFromBounds);
      return expanded.intersects(target.getBoundingBox());
   }

   private void showBurstWarnings() {
      if(tickCount%5!=0)return; // Bounded warning traffic; marks stay fixed until impact.
      if (this.level() instanceof ServerLevel server) {
         if (this.burstMarks.isEmpty()) {
            LivingEntity target = this.getTarget();
            if (target == null) {
               return;
            }

            for (int i = 0; i < 3; i++) {
               double angle = (Math.PI * 2) * i / 3.0 + this.getRandom().nextDouble() * 0.5;
               double radius = 2.5 + this.getRandom().nextDouble() * 2.0;
               double x = target.getX() + Math.cos(angle) * radius;
               double z = target.getZ() + Math.sin(angle) * radius;
               var floor=localFloor(server,BlockPos.containing(x,target.getY(),z));
               if(floor!=null)this.burstMarks.add(new Vec3(x,floor.getY()+1,z));
            }
         }

         for (Vec3 mark : this.burstMarks) {
            server.sendParticles(ParticleTypes.FLAME, mark.x, mark.y + 0.12, mark.z, 2, 0.16, 0.02, 0.16, 0.0);
            server.sendParticles(ParticleTypes.SMOKE, mark.x, mark.y + 0.22, mark.z, 1, 0.12, 0.08, 0.12, 0.0);
         }
      }
   }

   /** Search the arena floor, not the surface heightmap above an underground victim. */
   public static BlockPos localFloor(ServerLevel level,BlockPos near){
      if(level.getChunkSource().getChunkNow(near.getX()>>4,near.getZ()>>4)==null)return null;
      for(int d=1;d<=8;d++){
         var pos=near.below(d);if(pos.getY()<level.getMinY())return null;
         if(level.getBlockState(pos).isCollisionShapeFullBlock(level,pos)&&level.getFluidState(pos).isEmpty()&&level.getBlockState(pos.above()).getCollisionShape(level,pos.above()).isEmpty())return pos;
      }
      return null;
   }
   private void beginAction(String action) {
      if (!this.level().isClientSide() && !this.isDeadOrDying()) {
         if (SulfurColossusRigProfile.actionIndex(action) >= 0) {
            if (this.playAction(action)) {
               this.currentAction = action;
               this.lastActionTime = 0L;
               this.effectApplied = false;
               if (!action.equals("stagger") && !action.equals("death")) {
                  boolean phaseTwo = this.getHealth() <= this.getMaxHealth() * 0.5F;
                  long recovery = phaseTwo ? 75L : 100L;
                  long durationTicks = Math.max(0L, SulfurColossusRigProfile.actionDurationMillis(action) / 50L);
                  this.attackReadyAt = this.tickCount + durationTicks + recovery;
               }

               this.getNavigation().stop();
               this.setDeltaMovement(this.getDeltaMovement().x * 0.2, this.getDeltaMovement().y, this.getDeltaMovement().z * 0.2);
               if (action.equals("venting")) {
                  this.playSound(ColossusContent.SULFUR_VENT_HISS, 1.8F, 0.8F);
               } else if (action.equals("volcanic_burst")) {
                  this.burstMarks.clear();
                  this.playSound(SoundEvents.FIRECHARGE_USE, 1.2F, 0.65F);
               } else if (action.equals("ground_slam")) {
                  this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.6F, 0.65F);
               }
            }
         }
      }
   }

   private void finishAction() {
      this.stopAction();
      this.currentAction = "";
      this.lastActionTime = 0L;
      this.effectApplied = false;
      this.burstMarks.clear();
   }

   private void openCore() {
      this.coreOpenUntil = this.tickCount + 50L;
      this.entityData.set(CORE_OPEN, true);
      if (this.level() instanceof ServerLevel server) {
         server.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + this.getBbHeight() * 0.35, this.getZ(), 12, 0.5, 0.4, 0.5, 0.05);
      }
   }

   private boolean validTarget(LivingEntity target) {
      return target instanceof Player player && target.isAlive() && !player.isCreative() && !player.isSpectator() && canAttack(target) && (arenaCenter==null||target.distanceToSqr(arenaCenter.getCenter())<=48*48);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PHASE_TWO, false);
      builder.define(CORE_OPEN, false);
   }

   public boolean isSecondPhase() {
      return (Boolean)this.entityData.get(PHASE_TWO);
   }

   public boolean isCoreExposed() {
      return (Boolean)this.entityData.get(CORE_OPEN);
   }

   @Override
   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      double scale = 1.4084507231437082;
      return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / Math.max(0.001, scale);
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      float adjusted = this.isCoreExposed() ? damage * 1.35F : damage;
      boolean hurt = super.hurtServer(level, source, adjusted);
      if (hurt && damage >= 35.0F && !this.isDeadOrDying() && !this.currentAction.equals("death")) {
         this.finishAction();
         this.beginAction("stagger");
      }

      return hurt;
   }

   public boolean canBeAffected(MobEffectInstance effect) {
      return !effect.is(MobEffects.POISON) && !effect.is(MobEffects.WITHER) ? super.canBeAffected(effect) : false;
   }

   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("ColossusLootGranted",lootGranted);
      if (this.arenaCenter != null) {
         output.putInt("ArenaX", this.arenaCenter.getX());
         output.putInt("ArenaY", this.arenaCenter.getY());
         output.putInt("ArenaZ", this.arenaCenter.getZ());
      }
   }

   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      lootGranted=input.getBooleanOr("ColossusLootGranted",false);setPersistenceRequired();finishAction();attackReadyAt=tickCount+100;coreOpenUntil=0;
      Optional<Integer> x = input.getInt("ArenaX");
      Optional<Integer> y = input.getInt("ArenaY");
      Optional<Integer> z = input.getInt("ArenaZ");
      if (x.isPresent() && y.isPresent() && z.isPresent()) {
         this.arenaCenter = new BlockPos(x.get(), y.get(), z.get());
      }
   }

   protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
      super.dropCustomDeathLoot(level, source, killedByPlayer);
      if(!killedByPlayer||lootGranted)return;lootGranted=true;
      this.spawnAtLocation(level, new ItemStack(dev.vic17usd.spacefabric.item.ModItems.SULFUR_CRYSTAL, 5 + this.getRandom().nextInt(4)));
      this.spawnAtLocation(level, new ItemStack(ColossusContent.COLOSSUS_CORE));
   }

   public void die(DamageSource source) {
      if (!this.level().isClientSide() && !this.isDeadOrDying()) {
         this.finishAction();
         this.getNavigation().stop();
         this.setTarget(null);
      }

      super.die(source);
      if (!this.level().isClientSide()) {
         this.playAction("death");
         this.currentAction = "death";
         this.bossBar.setProgress(0.0F);
      }
   }

   protected void tickDeath() {
      this.deathTime++;
      if (!this.level().isClientSide() && this.deathTime >= 80) {
         this.bossBar.removeAllPlayers();
         this.remove(RemovalReason.KILLED);
      }
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      this.tracked.add(player);
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      this.tracked.remove(player);this.bossBar.removePlayer(player);
   }

   @Override public void remove(RemovalReason reason) {
      bossBar.removeAllPlayers();tracked.clear();super.remove(reason);
   }
   public void checkDespawn() { }


   @Override
   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      return InteractionResult.PASS;
   }

   private static final class BossCombatGoal extends Goal {
      private final SulfurColossusEntity boss;

      private BossCombatGoal(SulfurColossusEntity boss) {
         this.boss = boss;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.boss.getTarget();
         return target != null && this.validCombatTarget(target) && !this.boss.isDeadOrDying();
      }

      public boolean canContinueToUse() {
         LivingEntity target = this.boss.getTarget();
         return target != null && this.validCombatTarget(target) && !this.boss.isDeadOrDying();
      }

      public void stop() {
         if (this.boss.currentAction.isEmpty()) {
            this.boss.getNavigation().stop();
         }
      }

      public void tick() {
         LivingEntity target = this.boss.getTarget();
         if (target != null && this.validCombatTarget(target)) {
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.boss.currentAction.isEmpty()) {
               boolean phaseTwo = this.boss.getHealth() <= this.boss.getMaxHealth() * 0.5F;
               if (this.boss.tickCount >= this.boss.attackReadyAt) {
                  double distance = this.boss.distanceToSqr(target);
                  if (distance > 81.0 && !this.boss.withinMeleeReach(target, 6.0)) {
                     this.boss.getNavigation().moveTo(target, phaseTwo ? 1.12 : 0.95);
                     return;
                  }

                  this.boss.getNavigation().stop();
                  if (this.boss.withinMeleeReach(target, 1.8) && this.boss.hasLineOfSight(target)&&this.boss.getRandom().nextInt(4)==0) {
                     this.boss.beginAction("primary_attack");
                  } else {
                     int choice = this.boss.getRandom().nextInt(phaseTwo ? 3 : 4);

                     this.boss.beginAction(switch (choice) {
                        case 0 -> "ground_slam";
                        case 1 -> "venting";
                        case 2 -> "volcanic_burst";
                        default -> "ground_slam";
                     });
                  }
               } else if (this.boss.withinMeleeReach(target, 1.0) && this.boss.tickCount - this.boss.lastHitAt > 24L) {
                  this.boss.getNavigation().stop();
               } else {
                  this.boss.getNavigation().moveTo(target, phaseTwo ? 1.12 : 0.95);
               }
            }
         } else {
            this.boss.setTarget(null);
         }
      }

      private boolean validCombatTarget(LivingEntity target) {
         return boss.validTarget(target);
      }
   }
}

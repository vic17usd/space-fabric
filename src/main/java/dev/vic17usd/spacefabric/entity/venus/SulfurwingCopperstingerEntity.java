package dev.vic17usd.spacefabric.entity.venus;

import java.util.EnumSet;
import java.util.Locale;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import dev.vic17usd.spacefabric.entity.venus.ModVenusEntities;

public final class SulfurwingCopperstingerEntity extends SulfurwingCopperstingerRigEntity {
   private static final String PRIMARY_ATTACK = "primary_attack";
   private static final double TARGET_BODY_HEIGHT_BLOCKS = 3.2;
   private static final double RUNTIME_SCALE = 3.2 / Math.max(0.001, 4.5);

   public SulfurwingCopperstingerEntity(EntityType<? extends SulfurwingCopperstingerEntity> type, Level world) {
      super(type, world);
      this.moveControl = new FlyingMoveControl(this, 30, true);
      this.setNoGravity(true);
   }

   @Override public int getMaxSpawnClusterSize() {return 1;}
   public static Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 160.0)
         .add(Attributes.MOVEMENT_SPEED, 0.34)
         .add(Attributes.FLYING_SPEED, 0.42)
         .add(Attributes.ATTACK_DAMAGE, 16.0)
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.SCALE, RUNTIME_SCALE);
   }

   protected PathNavigation createNavigation(Level world) {
      return new FlyingPathNavigation(this, world);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SulfurwingCopperstingerEntity.BossCombatGoal(this));
      this.goalSelector.addGoal(6, new VenusHoverPatrolGoal(this));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   @Override
   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      return InteractionResult.PASS;
   }

   public boolean requiresCustomPersistence() {
      return true;
   }

   @Override
   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
      if (isAcidDamage(source)) {
         amount *= 0.2F;
      }

      return super.hurtServer(world, source, amount);
   }

   private static boolean isAcidDamage(DamageSource source) {
      String messageId = source.getMsgId().toLowerCase(Locale.ROOT);
      return !messageId.contains("acid") && !messageId.contains("corrosive") && !messageId.contains("sulfur")
         ? source.typeHolder().unwrapKey().map(key -> {
            String path = key.identifier().getPath().toLowerCase(Locale.ROOT);
            return path.contains("acid") || path.contains("corrosive") || path.contains("sulfur");
         }).orElse(false)
         : true;
   }

   protected void dropFromLootTable(ServerLevel world, DamageSource damageSource, boolean causedByPlayer) {
      super.dropFromLootTable(world, damageSource, causedByPlayer);
      ItemEntity core = new ItemEntity(
         world, this.getX(), this.getY() + 0.4, this.getZ(), new ItemStack(ModVenusEntities.SULFUR_CORE)
      );
      world.addFreshEntity(core);
   }

   private static final class BossCombatGoal extends Goal {
      private final SulfurwingCopperstingerEntity boss;
      private LivingEntity combatTarget;
      private int activeActionIndex = -1;
      private int cooldownTicks;
      private long previousActionTime;
      private boolean hitDelivered;

      private BossCombatGoal(SulfurwingCopperstingerEntity boss) {
         this.boss = boss;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.boss.getTarget();
         return target != null && target.isAlive() && this.boss.hasLineOfSight(target);
      }

      public boolean canContinueToUse() {
         LivingEntity target = this.boss.getTarget();
         return target != null && target.isAlive() && this.boss.distanceToSqr(target) <= 4096.0;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void start() {
         this.combatTarget = this.boss.getTarget();
         this.cooldownTicks = 0;
         this.activeActionIndex = -1;
         this.hitDelivered = false;
         this.previousActionTime = 0L;
      }

      public void stop() {
         this.boss.getNavigation().stop();
         if (this.activeActionIndex >= 0) {
            this.boss.stopAction();
         }

         this.activeActionIndex = -1;
         this.hitDelivered = false;
         this.combatTarget = null;
      }

      public void tick() {
         LivingEntity target = this.boss.getTarget();
         if (target != null && target.isAlive()) {
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.activeActionIndex >= 0) {
               this.tickAction(target);
            } else {
               if (this.cooldownTicks > 0) {
                  this.cooldownTicks--;
               }

               Vec3 offset = this.targetOffset(target);
               double desiredX = target.getX() + offset.x;
               double desiredZ = target.getZ() + offset.z;
               double desiredY = target.getY() + target.getBbHeight() * 0.35;
               double dx = this.boss.getX() - target.getX();
               double dz = this.boss.getZ() - target.getZ();
               double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
               double desiredDistance = (this.boss.getBbWidth() + target.getBbWidth()) * 0.5 + 0.85;
               boolean needsApproach = horizontalDistance > desiredDistance
                  || Math.abs(this.boss.getY() - desiredY) > 0.45;
               if (needsApproach) {
                  if(horizontalDistance<desiredDistance+3&&this.boss.hasLineOfSight(target)) {
                     this.boss.getNavigation().stop();
                     this.boss.getMoveControl().setWantedPosition(desiredX,desiredY,desiredZ,1.15);
                  } else this.boss.getNavigation().moveTo(desiredX, desiredY, desiredZ, 1.15);
               } else {
                  this.boss.getNavigation().stop();
                  this.boss.setDeltaMovement(this.boss.getDeltaMovement().multiply(0.55, 0.65, 0.55));
               }

               if (this.cooldownTicks == 0 && this.canStrike(target)) {
                  long effectStart = SulfurwingCopperstingerRigProfile.effectStartMillis("primary_attack");
                  long effectEnd = SulfurwingCopperstingerRigProfile.effectEndMillis("primary_attack");
                  int actionIndex = SulfurwingCopperstingerRigProfile.actionIndex("primary_attack");
                  if (actionIndex >= 0 && effectStart >= 0L && effectEnd >= effectStart && this.boss.playAction("primary_attack")) {
                     this.activeActionIndex = actionIndex;
                     this.combatTarget = target;
                     this.previousActionTime = 0L;
                     this.hitDelivered = false;
                     this.boss.getNavigation().stop();
                     this.boss
                        .level()
                        .playSound(
                           null,
                           this.boss.getX(),
                           this.boss.getY(),
                           this.boss.getZ(),
                           SoundEvents.BEE_STING,
                           SoundSource.HOSTILE,
                           1.0F,
                           0.72F
                        );
                  }
               }
            }
         } else {
            this.cancelAction();
         }
      }

      private void tickAction(LivingEntity target) {
         int expectedIndex = SulfurwingCopperstingerRigProfile.actionIndex("primary_attack");
         long duration = SulfurwingCopperstingerRigProfile.actionDurationMillis("primary_attack");
         long effectStart = SulfurwingCopperstingerRigProfile.effectStartMillis("primary_attack");
         long effectEnd = SulfurwingCopperstingerRigProfile.effectEndMillis("primary_attack");
         long now = this.boss.actionTimeMillis(0.0F);
         if (duration >= 0L && now >= duration && this.activeActionIndex == expectedIndex && target == this.combatTarget) {
            this.boss.stopAction();
            this.activeActionIndex = -1;
            this.hitDelivered = false;
            this.cooldownTicks = 35;
         } else if (this.boss.actionIndex() == this.activeActionIndex
            && this.activeActionIndex == expectedIndex
            && target == this.combatTarget
            && duration >= 0L
            && effectStart >= 0L
            && effectEnd >= effectStart) {
            this.boss.getNavigation().stop();
            this.boss.setDeltaMovement(this.boss.getDeltaMovement().multiply(0.35, 0.65, 0.35));
            if (!this.hitDelivered && this.previousActionTime < effectStart && now >= effectStart && this.previousActionTime <= effectEnd) {
               this.hitDelivered = true;
               if (target.isAlive()
                  && this.boss.hasLineOfSight(target)
                  && this.canStrike(target)
                  && this.boss.level() instanceof ServerLevel serverWorld) {
                  this.boss.doHurtTarget(serverWorld, target);
               }
            }

            this.previousActionTime = now;
         } else {
            this.cancelAction();
         }
      }

      private void cancelAction() {
         if (this.activeActionIndex >= 0) {
            this.boss.stopAction();
         }

         this.activeActionIndex = -1;
         this.hitDelivered = false;
         this.previousActionTime = 0L;
      }

      private boolean canStrike(LivingEntity target) {
         if (target.isAlive() && this.boss.hasLineOfSight(target)) {
            double verticalGap=Math.abs(this.boss.getY()+this.boss.getBbHeight()*.5-(target.getY()+target.getBbHeight()*.5))-(this.boss.getBbHeight()+target.getBbHeight())*.5;
            boolean verticalOverlap = verticalGap<=.35;
            if (!verticalOverlap) {
               return false;
            } else {
               double dx = this.boss.getX() - target.getX();
               double dz = this.boss.getZ() - target.getZ();
               double horizontalGap = Math.sqrt(dx * dx + dz * dz) - (this.boss.getBbWidth() + target.getBbWidth()) * 0.5;
               return horizontalGap <= 1.0;
            }
         } else {
            return false;
         }
      }

      private Vec3 targetOffset(LivingEntity target) {
         double dx = this.boss.getX() - target.getX();
         double dz = this.boss.getZ() - target.getZ();
         double length = Math.sqrt(dx * dx + dz * dz);
         if (length < 0.001) {
            double yaw = Math.toRadians(this.boss.getYRot());
            dx = -Math.sin(yaw);
            dz = Math.cos(yaw);
            length = 1.0;
         }

         double radius = (this.boss.getBbWidth() + target.getBbWidth()) * 0.5 + 0.65;
         return new Vec3(dx / length * radius, 0.0, dz / length * radius);
      }
   }
}

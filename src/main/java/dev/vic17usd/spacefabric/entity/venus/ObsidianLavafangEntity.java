package dev.vic17usd.spacefabric.entity.venus;

import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class ObsidianLavafangEntity extends ObsidianLavafangRigEntity {
   private static final String PRIMARY_ATTACK = "primary_attack";
   private static final double TARGET_BODY_WIDTH_BLOCKS = 0.95;
   private static final double RUNTIME_SCALE = 0.95 / Math.max(0.01, 1.35F);
   private ObsidianLavafangEntity.BiteGoal biteGoal;

   public ObsidianLavafangEntity(EntityType<? extends ObsidianLavafangEntity> type, Level level) {
      super(type, level);
   }

   @Override public int getMaxSpawnClusterSize() { return 2; }
   public static Builder createAttributes() {
      return ObsidianLavafangRigEntity.createAttributes()
         .add(Attributes.MAX_HEALTH, 28.0)
         .add(Attributes.MOVEMENT_SPEED, 0.36)
         .add(Attributes.FOLLOW_RANGE, 36.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
         .add(Attributes.SCALE, RUNTIME_SCALE);
   }

   public boolean fireImmune() {
      return true;
   }

   @Override
   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      return InteractionResult.PASS;
   }

   @Override
   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      double groundDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      return groundDistance / RUNTIME_SCALE;
   }

   @Override
   protected void registerGoals() {
      this.biteGoal = new ObsidianLavafangEntity.BiteGoal(this);
      this.goalSelector.addGoal(1, this.biteGoal);
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   @Override
   public void tick() {
      super.tick();
      if (this.isDeadOrDying() && this.biteGoal != null) {
         this.biteGoal.cancelAction();
      }
   }

   private static final class BiteGoal extends Goal {
      private static final long RECOVERY_COOLDOWN_TICKS = 12L;
      private final ObsidianLavafangEntity mob;
      private LivingEntity actionTarget;
      private boolean attacking;
      private boolean effectApplied;
      private long actionStartedAt;
      private long cooldownUntil;
      private long previousActionTime;

      private BiteGoal(ObsidianLavafangEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.mob.getTarget();
         return target != null && target.isAlive();
      }

      public boolean canContinueToUse() {
         LivingEntity target = this.mob.getTarget();
         return target != null && target.isAlive();
      }

      public void start() {
         this.actionTarget = this.mob.getTarget();
      }

      public void stop() {
         this.cancelAction();
         this.actionTarget = null;
         this.mob.getNavigation().stop();
      }

      public void tick() {
         LivingEntity target = this.mob.getTarget();
         if (target != null && target.isAlive()) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.attacking) {
               this.tickAction(target);
            } else if (this.mob.level().getGameTime() < this.cooldownUntil) {
               this.mob.getNavigation().moveTo(target, 1.55);
            } else {
               if (this.withinBiteReach(target) && this.mob.hasLineOfSight(target)) {
                  this.mob.getNavigation().stop();
                  this.actionTarget = target;
                  if (this.mob.playAction("primary_attack")) {
                     this.attacking = true;
                     this.effectApplied = false;
                     this.previousActionTime = 0L;
                     this.actionStartedAt = this.mob.level().getGameTime();
                  } else {
                     this.cooldownUntil = this.mob.level().getGameTime() + 12L;
                  }
               } else {
                  this.mob.getNavigation().moveTo(target, 1.55);
               }
            }
         } else {
            this.cancelAction();
         }
      }

      private void tickAction(LivingEntity currentTarget) {
         long elapsed = this.mob.actionTimeMillis(0.0F);
         long duration = ObsidianLavafangRigProfile.actionDurationMillis("primary_attack");
         int expectedIndex = ObsidianLavafangRigProfile.actionIndex("primary_attack");
         if (currentTarget != this.actionTarget || !this.validBiteTarget(this.actionTarget)) {
            this.cancelAction();
         } else if (duration >= 0L && elapsed >= duration) {
            this.finishAction();
         } else if (this.mob.actionIndex() != expectedIndex) {
            this.cancelAction();
         } else {
            long effectStart = ObsidianLavafangRigProfile.effectStartMillis("primary_attack");
            long effectEnd = ObsidianLavafangRigProfile.effectEndMillis("primary_attack");
            boolean crossedImpact = effectStart >= 0L && effectEnd >= effectStart && this.previousActionTime < effectStart && elapsed >= effectStart;
            if (!this.effectApplied && crossedImpact && elapsed <= effectEnd && this.validBiteTarget(this.actionTarget)) {
               if (this.mob.level() instanceof ServerLevel serverLevel) {
                  this.mob.doHurtTarget(serverLevel, this.actionTarget);
               }

               this.effectApplied = true;
            }

            this.previousActionTime = elapsed;
            if (duration < 0L && this.mob.level().getGameTime() - this.actionStartedAt >= 22L) {
               this.finishAction();
            }
         }
      }

      private boolean validBiteTarget(LivingEntity target) {
         return target != null && target.isAlive() && this.mob.getTarget() == target && this.withinBiteReach(target) && this.mob.hasLineOfSight(target);
      }

      private boolean withinBiteReach(Entity target) {
         double reach = this.mob.getBbWidth() * 0.5 + target.getBbWidth() * 0.5 + 0.45;
         double dx = this.mob.getX() - target.getX();
         double dz = this.mob.getZ() - target.getZ();
         AABB mobBox = this.mob.getBoundingBox();
         AABB targetBox = target.getBoundingBox();
         boolean verticalOverlap = mobBox.maxY + 0.35 >= targetBox.minY && targetBox.maxY + 0.35 >= mobBox.minY;
         return verticalOverlap && dx * dx + dz * dz <= reach * reach;
      }

      private void finishAction() {
         this.mob.stopAction();
         this.attacking = false;
         this.actionTarget = null;
         this.effectApplied = false;
         this.previousActionTime = 0L;
         this.cooldownUntil = this.mob.level().getGameTime() + 12L;
      }

      private void cancelAction() {
         if (this.attacking) {
            this.mob.stopAction();
         }

         this.attacking = false;
         this.effectApplied = false;
         this.actionTarget = null;
         this.previousActionTime = 0L;
      }
   }
}

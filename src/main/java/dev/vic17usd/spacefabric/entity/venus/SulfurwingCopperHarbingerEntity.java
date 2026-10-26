package dev.vic17usd.spacefabric.entity.venus;

import java.util.EnumSet;
import java.util.Locale;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import dev.vic17usd.spacefabric.entity.venus.ModVenusEntities;

public final class SulfurwingCopperHarbingerEntity extends SulfurwingCopperHarbingerRigEntity {
   public static final double TARGET_HEIGHT_BLOCKS = 3.4;
   private static final String PRIMARY_ATTACK = "primary_attack";
   private static final double ATTACK_DAMAGE = 18.0;
   private static final double ATTACK_HORIZONTAL_EDGE_REACH = 0.65;
   private static final double ATTACK_VERTICAL_EDGE_REACH = 0.55;

   public SulfurwingCopperHarbingerEntity(EntityType<? extends SulfurwingCopperHarbingerEntity> type, Level level) {
      super(type, level);
   }

   @Override public int getMaxSpawnClusterSize() { return 1; }
   public static Builder createAttributes() {
      double scale = 3.4 / Math.max(0.01, 4.7F);
      return SulfurwingCopperHarbingerRigEntity.createAttributes()
         .add(Attributes.MAX_HEALTH, 180.0)
         .add(Attributes.ATTACK_DAMAGE, 18.0)
         .add(Attributes.MOVEMENT_SPEED, 0.34)
         .add(Attributes.FLYING_SPEED, 0.48)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.SCALE, scale);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SulfurwingCopperHarbingerEntity.MeleeStrikeGoal(this));
      this.goalSelector.addGoal(5, new SulfurwingCopperHarbingerEntity.HoverPatrolGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   @Override
   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      String damageName = source.type().msgId().toLowerCase(Locale.ROOT);
      return damageName.contains("acid") ? super.hurtServer(level, source, damage * 0.25F) : super.hurtServer(level, source, damage);
   }

   protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
      super.dropCustomDeathLoot(level, source, killedByPlayer);
      this.spawnAtLocation(level, new ItemStack(ModVenusEntities.SULFUR_CORE));
   }

   @Override
   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      return InteractionResult.PASS;
   }

   private boolean isWithinStrikeReach(LivingEntity target) {
      double dx = target.getX() - this.getX();
      double dz = target.getZ() - this.getZ();
      double horizontalEdgeGap = Math.sqrt(dx * dx + dz * dz) - (this.getBbWidth() * 0.5 + target.getBbWidth() * 0.5);
      double verticalEdgeGap = Math.abs(target.getY() + target.getBbHeight() * 0.5 - (this.getY() + this.getBbHeight() * 0.5))
         - (this.getBbHeight() * 0.5 + target.getBbHeight() * 0.5);
      return horizontalEdgeGap <= 0.65 && verticalEdgeGap <= 0.55;
   }

   private boolean isValidCombatTarget(LivingEntity target) {
      return target != null && target.isAlive() && !target.isRemoved() && target.level() == this.level();
   }

   private static final class HoverPatrolGoal extends Goal {
      private final SulfurwingCopperHarbingerEntity mob;
      private int cooldown;

      private HoverPatrolGoal(SulfurwingCopperHarbingerEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.cooldown = 20;
      }

      public boolean canUse() {
         if (this.mob.getTarget() != null || !this.mob.getNavigation().isDone()) {
            return false;
         } else if (this.cooldown > 0) {
            this.cooldown--;
            return false;
         } else {
            return true;
         }
      }

      public boolean canContinueToUse() {
         return this.mob.getTarget() == null && !this.mob.getNavigation().isDone();
      }

      public void start() {
         RandomSource random = this.mob.getRandom();
         double angle = random.nextDouble() * Math.PI * 2.0;
         double radius = 3.0 + random.nextDouble() * 5.0;
         double x = this.mob.getX() + Math.cos(angle) * radius;
         double z = this.mob.getZ() + Math.sin(angle) * radius;
         double y = Math.min(this.mob.level().getMaxY()-10,this.mob.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(int)Math.floor(x),(int)Math.floor(z))+6+random.nextInt(9));
         this.mob.getNavigation().moveTo(x, y, z, 0.75);
      }

      public void stop() {
         this.cooldown = 15 + this.mob.getRandom().nextInt(25);
      }
   }

   private static final class MeleeStrikeGoal extends Goal {
      private final SulfurwingCopperHarbingerEntity mob;
      private LivingEntity actionTarget;
      private int cooldown;
      private int repathDelay;
      private int expectedActionIndex = -1;
      private long previousActionTime;
      private boolean actionActive;
      private boolean effectDelivered;

      private MeleeStrikeGoal(SulfurwingCopperHarbingerEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return this.mob.isValidCombatTarget(this.mob.getTarget());
      }

      public boolean canContinueToUse() {
         return this.mob.isValidCombatTarget(this.mob.getTarget());
      }

      public void start() {
         this.repathDelay = 0;
      }

      public void stop() {
         this.cancelAction();
         this.mob.getNavigation().stop();
      }

      public void tick() {
         LivingEntity target = this.mob.getTarget();
         if (!this.mob.isValidCombatTarget(target)) {
            this.cancelAction();
         } else {
            this.mob.getLookControl().setLookAt(target, 35.0F, 35.0F);
            if (this.cooldown > 0) {
               this.cooldown--;
            }

            if (this.actionActive) {
               this.tickAction(target);
            } else if (this.cooldown == 0 && this.mob.isWithinStrikeReach(target) && this.mob.hasLineOfSight(target)) {
               this.beginAction(target);
            } else {
               if (this.repathDelay-- <= 0 || this.mob.getNavigation().isDone()) {
                  this.mob.getNavigation().moveTo(target, 1.15);
                  this.repathDelay = 10;
               }
            }
         }
      }

      private void beginAction(LivingEntity target) {
         this.mob.getNavigation().stop();
         if (this.mob.playAction("primary_attack")) {
            this.actionTarget = target;
            this.expectedActionIndex = SulfurwingCopperHarbingerRigProfile.actionIndex("primary_attack");
            this.previousActionTime = 0L;
            this.actionActive = true;
            this.effectDelivered = false;
         }
      }

      private void tickAction(LivingEntity currentTarget) {
         if (this.actionTarget == currentTarget && this.mob.isValidCombatTarget(this.actionTarget)) {
            int activeIndex = this.mob.actionIndex();
            if (activeIndex != this.expectedActionIndex) {
               this.actionActive = false;
               this.actionTarget = null;
               this.cooldown = 24;
            } else {
               long elapsed = this.mob.actionTimeMillis(0.0F);
               long impactStart = SulfurwingCopperHarbingerRigProfile.effectStartMillis("primary_attack");
               long impactEnd = SulfurwingCopperHarbingerRigProfile.effectEndMillis("primary_attack");
               boolean crossedImpact = impactStart >= 0L && this.previousActionTime < impactStart && elapsed >= impactStart;
               if (!this.effectDelivered
                  && crossedImpact
                  && elapsed <= impactEnd
                  && this.mob.isValidCombatTarget(this.actionTarget)
                  && this.mob.isWithinStrikeReach(this.actionTarget)
                  && this.mob.hasLineOfSight(this.actionTarget)) {
                  this.deliverImpact(this.actionTarget);
               }

               this.previousActionTime = elapsed;
            }
         } else {
            this.cancelAction();
         }
      }

      private void deliverImpact(LivingEntity target) {
         if (this.mob.level() instanceof ServerLevel serverLevel) {
            this.effectDelivered = true;
            float var5 = (float)this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            boolean hit = target.hurtServer(serverLevel, serverLevel.damageSources().mobAttack(this.mob), var5);
            if (hit) {
               this.mob.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.78F + this.mob.getRandom().nextFloat() * 0.18F);
            }
         }
      }

      private void cancelAction() {
         if (this.actionActive && this.mob.actionIndex() == this.expectedActionIndex) {
            this.mob.stopAction();
         }

         this.actionActive = false;
         this.actionTarget = null;
         this.expectedActionIndex = -1;
         this.effectDelivered = false;
      }
   }
}

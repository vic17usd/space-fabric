package dev.vic17usd.spacefabric.entity.venus;

import java.util.EnumSet;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class AshplumeEmberwalkerEntity extends AshplumeEmberwalkerRigEntity {
   private static final String FLEE_ACTION = "flee_startle";
   private static final double BODY_HEIGHT_BLOCKS = 1.8;
   private static final double MAX_REST_DIMENSION_BLOCKS = 16.0;
   private static final double RUNTIME_SCALE = runtimeScale();
   private int fleeTicks;
   private Vec3 fleeFrom;
   private int repathTicks;

   public AshplumeEmberwalkerEntity(EntityType<? extends AshplumeEmberwalkerEntity> type, Level level) {
      super(type, level);
   }

   private static double runtimeScale() {
      double profileWidth = Math.max(0.01, 0.9F);
      double profileHeight = Math.max(0.01, 3.3F);
      double heightScale = 1.8 / profileHeight;
      double envelopeScale = 16.0 / Math.max(profileWidth, profileHeight);
      return Math.min(heightScale, envelopeScale);
   }

   @Override public int getMaxSpawnClusterSize() { return 2; }
   public static Builder createAttributes() {
      return AshplumeEmberwalkerRigEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.1).add(Attributes.SCALE, RUNTIME_SCALE);
   }

   @Override
   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / RUNTIME_SCALE;
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new AshplumeEmberwalkerEntity.FleeWhenHurtGoal());
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.65));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
   }

   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      if (source.is(DamageTypes.HOT_FLOOR)) {
         return false;
      } else {
         boolean hurt = super.hurtServer(level, source, damage);
         if (hurt && this.isAlive()) {
            this.fleeTicks = Math.max(this.fleeTicks, 100);
            LivingEntity attacker = source.getEntity() instanceof LivingEntity living ? living : null;
            this.fleeFrom = attacker == null
               ? new Vec3(this.getX() + this.random.nextDouble() * 2.0 - 1.0, this.getY(), this.getZ() + this.random.nextDouble() * 2.0 - 1.0)
               : attacker.position();
            if (AshplumeEmberwalkerRigProfile.actionIndex("flee_startle") >= 0) {
               this.playAction("flee_startle");
            }
         }

         return hurt;
      }
   }

   public boolean fireImmune() {
      return true;
   }

   @Override
   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      return InteractionResult.PASS;
   }

   private final class FleeWhenHurtGoal extends Goal {
      private FleeWhenHurtGoal() {
         Objects.requireNonNull(AshplumeEmberwalkerEntity.this);
         super();
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return AshplumeEmberwalkerEntity.this.fleeTicks > 0;
      }

      public boolean canContinueToUse() {
         return AshplumeEmberwalkerEntity.this.fleeTicks > 0;
      }

      public void start() {
         AshplumeEmberwalkerEntity.this.repathTicks = 0;
         this.chooseEscapePath();
      }

      public void tick() {
         AshplumeEmberwalkerEntity.this.fleeTicks--;
         if (--AshplumeEmberwalkerEntity.this.repathTicks <= 0 || AshplumeEmberwalkerEntity.this.getNavigation().isDone()) {
            this.chooseEscapePath();
         }
      }

      public void stop() {
         AshplumeEmberwalkerEntity.this.getNavigation().stop();
         AshplumeEmberwalkerEntity.this.fleeTicks = 0;
      }

      private void chooseEscapePath() {
         AshplumeEmberwalkerEntity mob = AshplumeEmberwalkerEntity.this;
         Vec3 away = mob.fleeFrom == null ? null : mob.position().subtract(mob.fleeFrom);
         Vec3 destination = away != null && !(away.lengthSqr() < 0.01)
            ? DefaultRandomPos.getPosAway(mob, 16, 7, mob.fleeFrom)
            : DefaultRandomPos.getPos(mob, 16, 7);
         if (destination == null) {
            destination = DefaultRandomPos.getPos(mob, 16, 7);
         }

         if (destination != null) {
            mob.getNavigation().moveTo(destination.x, destination.y, destination.z, 2.1);
         }

         mob.repathTicks = 20;
      }
   }
}

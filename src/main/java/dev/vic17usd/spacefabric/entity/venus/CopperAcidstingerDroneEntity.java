package dev.vic17usd.spacefabric.entity.venus;

import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;

public final class CopperAcidstingerDroneEntity extends CopperAcidstingerDroneRigEntity {
   private static final String PRIMARY_ATTACK = "primary_attack";
   private static final String FLEE_ACTION = "flee";
   private static final double DESIGNED_MAX_DIMENSION = 0.9;
   private static final double LOW_HEALTH_FRACTION = 0.3;
   private static final double FLIGHT_SPEED = 0.46;
   private static final double FLEE_SPEED = 0.58;
   private static final double FOLLOW_RADIUS = 22.0;
   private boolean hasHomePosition;
   private double homeX;
   private double homeY;
   private double homeZ;
   private boolean fleeing;
   private boolean attackEffectApplied;
   private String activeAbility;
   private UUID attackTargetId;
   private long attackCooldownUntil;
   private long previousActionTime = -1L;

   public CopperAcidstingerDroneEntity(EntityType<? extends CopperAcidstingerDroneEntity> type, Level world) {
      super(type, world);
      this.setNoGravity(true);
   }

   private static double droneScale() {
      double profileSize = Math.max(0.567187F, 1.05F);
      return profileSize <= 0.0 ? 1.0 : 0.9 / profileSize;
   }

   @Override public int getMaxSpawnClusterSize() {return 2;}
   public static Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.42)
         .add(Attributes.FLYING_SPEED, 0.55)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.FOLLOW_RANGE, 28.0)
         .add(Attributes.SCALE, droneScale());
   }

   @Override
   protected void registerGoals() {
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   @Override
   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   @Override
   public void tick() {
      super.tick();
      this.setNoGravity(true);
      if (!this.level().isClientSide()) {
         if (!this.hasHomePosition) {
            this.homeX = this.getX();
            this.homeY = this.getY();
            this.homeZ = this.getZ();
            this.hasHomePosition = true;
         }

         if (!this.fleeing && this.getHealth() <= this.getMaxHealth() * 0.3) {
            this.beginFlee();
         }

         if (this.fleeing) {
            this.setTarget(null);
            this.cancelAttack();
            this.finishExpiredAbility();
            this.steerAwayFromThreat();
         } else {
            Player target = this.currentPlayerTarget();
            if (this.activeAbility != null && "primary_attack".equals(this.activeAbility)) {
               if (this.processAttackAction()) {
                  return;
               }
            } else if (this.activeAbility != null) {
               this.finishExpiredAbility();
            }

            if (target != null) {
               if (this.canStartMelee(target) && this.level().getGameTime() >= this.attackCooldownUntil) {
                  this.beginAttack(target);
                  if ("primary_attack".equals(this.activeAbility)) {
                     this.processAttackAction();
                     return;
                  }
               }

               this.steerToward(target.position().add(0.0, target.getBbHeight() * 0.45, 0.0), 0.46);
            } else {
               this.setTarget(null);
               this.followBossOrHome();
            }
         }
      }
   }

   private Player currentPlayerTarget() {
      if (this.getTarget() instanceof Player player && player.isAlive() && !player.isSpectator() && !player.isCreative()) {
         return player;
      } else {
         this.setTarget(null);
         return null;
      }
   }

   private boolean canStartMelee(Player target) {
      return target.isAlive() && !target.isSpectator() && !target.isCreative() && this.hasLineOfSight(target) && this.isInMeleeReach(target);
   }

   private boolean isInMeleeReach(Player target) {
      return this.getBoundingBox().inflate(0.18).intersects(target.getBoundingBox());
   }

   private void beginAttack(Player target) {
      int action = CopperAcidstingerDroneRigProfile.actionIndex("primary_attack");
      if (action >= 0 && this.playAction("primary_attack")) {
         this.activeAbility = "primary_attack";
         this.attackTargetId = target.getUUID();
         this.attackEffectApplied = false;
         this.previousActionTime = -1L;
         long duration = CopperAcidstingerDroneRigProfile.actionDurationMillis("primary_attack");
         long cooldownTicks = duration > 0L ? (duration + 49L) / 50L + 4L : 16L;
         this.attackCooldownUntil = this.level().getGameTime() + Math.max(12L, cooldownTicks);
         this.getNavigation().stop();
         this.setDeltaMovement(Vec3.ZERO);
      }
   }

   private boolean processAttackAction() {
      int attackIndex = CopperAcidstingerDroneRigProfile.actionIndex("primary_attack");
      if (attackIndex >= 0 && this.actionIndex() == attackIndex) {
         Player target = this.resolveAttackTarget();
         if (this.attackEffectApplied
            || target != null
               && target.isAlive()
               && !target.isSpectator()
               && !target.isCreative()
               && this.hasLineOfSight(target)
               && this.isInMeleeReach(target)) {
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
            long effectStart = CopperAcidstingerDroneRigProfile.effectStartMillis("primary_attack");
            long effectEnd = CopperAcidstingerDroneRigProfile.effectEndMillis("primary_attack");
            long now = this.actionTimeMillis(0.0F);
            boolean crossedStart = effectStart >= 0L && this.previousActionTime < effectStart && now >= effectStart;
            boolean insideWindow = effectEnd < 0L || now <= effectEnd;
            if (!this.attackEffectApplied
               && crossedStart
               && insideWindow
               && target != null
               && this.level() instanceof ServerLevel serverWorld
               && target.isAlive()
               && !target.isCreative()
               && !target.isSpectator()
               && this.hasLineOfSight(target)
               && this.isInMeleeReach(target)) {
               this.doHurtTarget(serverWorld, target);
               this.attackEffectApplied = true;
               this.playSound(SoundEvents.BLAZE_SHOOT, 0.8F, 1.35F);
            }

            this.previousActionTime = now;
            return true;
         } else {
            this.cancelAttack();
            return false;
         }
      } else {
         this.clearAbilityState();
         return false;
      }
   }

   private Player resolveAttackTarget() {
      if (this.attackTargetId != null && this.level() instanceof ServerLevel serverWorld) {
         return serverWorld.getEntity(this.attackTargetId) instanceof Player player ? player : null;
      } else {
         return null;
      }
   }

   private void cancelAttack() {
      if ("primary_attack".equals(this.activeAbility)) {
         this.stopAction();
      }

      this.clearAbilityState();
   }

   private void clearAbilityState() {
      this.activeAbility = null;
      this.attackTargetId = null;
      this.attackEffectApplied = false;
      this.previousActionTime = -1L;
   }

   private void beginFlee() {
      this.fleeing = true;
      this.cancelAttack();
      this.setTarget(null);
      this.getNavigation().stop();
      if (CopperAcidstingerDroneRigProfile.actionIndex("flee") >= 0 && this.playAction("flee")) {
         this.activeAbility = "flee";
         this.previousActionTime = -1L;
      }

      this.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.75F, 1.4F);
   }

   private void finishExpiredAbility() {
      if (this.activeAbility != null) {
         int index = CopperAcidstingerDroneRigProfile.actionIndex(this.activeAbility);
         if (index < 0 || this.actionIndex() != index) {
            this.clearAbilityState();
         }
      }
   }

   private void steerToward(Vec3 destination, double speed) {
      Vec3 difference = destination.subtract(this.position());
      double distance = difference.length();
      Vec3 wanted = distance < 0.35 ? Vec3.ZERO : difference.scale(Math.min(speed, distance * 0.28) / distance);
      this.setDeltaMovement(this.getDeltaMovement().lerp(wanted, 0.42));
      this.needsSync = true;
   }

   private void steerAwayFromThreat() {
      Entity threat = this.nearestThreat();
      Vec3 away = this.position().subtract(threat == null ? this.getHomeOrBossPosition() : threat.position());
      if (away.horizontalDistanceSqr() < 1.0E-4) {
         away = new Vec3(1.0, 0.25, 0.0);
      }

      Vec3 direction = new Vec3(away.x, Math.max(0.18, away.y), away.z).normalize();
      this.setDeltaMovement(direction.scale(0.58));
      this.needsSync = true;
   }

   private Entity nearestThreat() {
      Player player = this.level().getNearestPlayer(this, 24.0);
      return (Entity)(player != null && player.isAlive() && !player.isCreative() && !player.isSpectator()
         ? player
         : (Entity)this.level()
            .getEntitiesOfClass(VenusMob.class, this.getBoundingBox().inflate(22.0), candidate -> candidate.isAlive() && candidate instanceof SulfurwingCopperstingerEntity)
            .stream()
            .findFirst()
            .orElse(null));
   }

   private void followBossOrHome() {
      VenusMob boss = this.level()
         .getEntitiesOfClass(
            VenusMob.class,
            this.getBoundingBox().inflate(22.0),
            candidate -> candidate.isAlive() && (candidate instanceof SulfurwingCopperstingerEntity || candidate instanceof SulfurwingCopperHarbingerEntity)
         )
         .stream()
         .min((a, b) -> Double.compare(this.distanceToSqr(a), this.distanceToSqr(b)))
         .orElse(null);
      Vec3 anchor = boss == null ? this.getHomeOrBossPosition() : boss.position();
      if (boss == null) {
         this.steerToward(anchor.add(0.0, 0.6, 0.0), 0.161);
      } else {
         double angle = this.getId() * 2.399963229728653;
         Vec3 formationPoint = anchor.add(Math.cos(angle) * 2.1, 1.8, Math.sin(angle) * 2.1);
         this.steerToward(formationPoint, 0.3588);
      }
   }

   private Vec3 getHomeOrBossPosition() {
      VenusMob boss = this.level()
         .getEntitiesOfClass(VenusMob.class, this.getBoundingBox().inflate(22.0), candidate -> candidate.isAlive() && candidate instanceof SulfurwingCopperstingerEntity)
         .stream()
         .min((a, b) -> Double.compare(this.distanceToSqr(a), this.distanceToSqr(b)))
         .orElse(null);
      return boss == null ? new Vec3(this.homeX, this.homeY, this.homeZ) : boss.position();
   }
}

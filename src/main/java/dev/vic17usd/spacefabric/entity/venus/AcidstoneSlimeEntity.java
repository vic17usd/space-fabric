package dev.vic17usd.spacefabric.entity.venus;

import dev.vic17usd.spacefabric.entity.venus.ModVenusEntities;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class AcidstoneSlimeEntity extends AcidstoneSlimeRigEntity {
   private static final double TARGET_HEIGHT_BLOCKS = 0.9;
   private static final String AVOIDANCE_ACTION = "avoidance_bounce";
   private static final String LAND_BOUNCE_ACTION = "land_bounce";
   private int nextLandBounceTick = this.random.nextInt(40) + 35;
   private int landActionStopTick;
   private boolean avoidingPlayers;

   public AcidstoneSlimeEntity(EntityType<? extends AcidstoneSlimeEntity> type, Level level) {
      super(type, level);
   }

   @Override public int getMaxSpawnClusterSize() { return 2; }
   public static Builder createAttributes() {
      double scale = 0.9 / Math.max(0.001, 1.72F);
      return AcidstoneSlimeRigEntity.createAttributes().add(Attributes.MAX_HEALTH, 12.0).add(Attributes.MOVEMENT_SPEED, 0.12).add(Attributes.SCALE, scale);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new AvoidEntityGoal(this, Player.class, 7.0F, 1.15, 1.35));
      this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.65) {
         {
            Objects.requireNonNull(AcidstoneSlimeEntity.this);
         }

         public boolean canUse() {
            return !AcidstoneSlimeEntity.this.isInWater() && super.canUse();
         }

         public boolean canContinueToUse() {
            return !AcidstoneSlimeEntity.this.isInWater() && super.canContinueToUse();
         }
      });
      this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         boolean playerNearby = !this.level()
            .getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(7.0), player -> player.isAlive() && !player.isSpectator())
            .isEmpty();
         if (playerNearby != this.avoidingPlayers) {
            this.avoidingPlayers = playerNearby;
            if (playerNearby) {
               this.playAction("avoidance_bounce");
            } else if (this.actionIndex() == AcidstoneSlimeRigProfile.actionIndex("avoidance_bounce")) {
               this.stopAction();
            }
         }

         if (this.landActionStopTick > 0 && this.tickCount >= this.landActionStopTick) {
            if (this.actionIndex() == AcidstoneSlimeRigProfile.actionIndex("land_bounce")) {
               this.stopAction();
            }

            this.landActionStopTick = 0;
         }

         if (!this.isInWater() && this.onGround()) {
            if (this.tickCount >= this.nextLandBounceTick) {
               if (!playerNearby) {
                  this.getJumpControl().jump();
                  if (this.playAction("land_bounce")) {
                     long duration = AcidstoneSlimeRigProfile.actionDurationMillis("land_bounce");
                     this.landActionStopTick = this.tickCount + (int)Math.max(1L, (duration + 49L) / 50L);
                  }
               }

               this.nextLandBounceTick = this.tickCount + this.random.nextInt(41) + 45;
            }
         }
      }
   }

   @Override
   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      return this.isInWater() ? Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) : super.locomotionDistance(deltaX, deltaY, deltaZ);
   }

   @Override
   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      return InteractionResult.PASS;
   }

   protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
      super.dropCustomDeathLoot(level, source, killedByPlayer);
      this.spawnAtLocation(level, new ItemStack(ModVenusEntities.ACID_BOTTLE));
   }
}

package dev.vic17usd.spacefabric.entity.venus;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;

public abstract class CopperAcidstingerDroneRigEntity extends VenusMob {
   private static final double MOVEMENT_SPEED = 0.25;
   private static final double WALK_POSITION_PER_BLOCK = 4.0;
   private static final EntityDataAccessor<Integer> ACTION_INDEX = SynchedEntityData.defineId(CopperAcidstingerDroneRigEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Long> ACTION_STARTED_AT = SynchedEntityData.defineId(CopperAcidstingerDroneRigEntity.class, EntityDataSerializers.LONG);
   private static final String[] ACTION_KEYS = new String[]{"primary_attack", "flee"};
   private static final float[] ACTION_LENGTHS = new float[]{600.0F, 800.0F};
   private static final boolean[] ACTION_ONCE = new boolean[]{true, true};
   private static final boolean[] ACTION_HOLD = new boolean[]{false, false};
   private double horizontalTravel;
   private double horizontalTravelOld;
   private double horizontalTravelSpeed;

   public CopperAcidstingerDroneRigEntity(EntityType<? extends VenusMob> entityType, Level world) {
      super(entityType, world);
      this.setNoGravity(true);
      this.moveControl=new net.minecraft.world.entity.ai.control.FlyingMoveControl(this,20,true);
      this.refreshDimensions();
   }

   @Override protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
      var nav=new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this,level);nav.setCanFloat(true);return nav;
   }
   public static Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.TEMPT_RANGE, 10.0);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ACTION_INDEX, -1);
      builder.define(ACTION_STARTED_AT, 0L);
   }

   public boolean playAction(String action) {
      if (this.level().isClientSide()) {
         return false;
      } else {
         for (int i = 0; i < ACTION_KEYS.length; i++) {
            if (ACTION_KEYS[i].equals(action)) {
               this.entityData.set(ACTION_STARTED_AT, this.level().getGameTime());
               this.entityData.set(ACTION_INDEX, i);
               return true;
            }
         }

         return false;
      }
   }

   public void stopAction() {
      if (!this.level().isClientSide()) {
         this.entityData.set(ACTION_INDEX, -1);
      }
   }

   public int actionIndex() {
      int index = (Integer)this.entityData.get(ACTION_INDEX);
      if (index >= 0 && index < ACTION_KEYS.length) {
         return ACTION_ONCE[index] && (float)this.actionTimeMillis(0.0F) >= ACTION_LENGTHS[index] ? -1 : index;
      } else {
         return -1;
      }
   }

   public long actionTimeMillis(float tickDelta) {
      int index = (Integer)this.entityData.get(ACTION_INDEX);
      if (index >= 0 && index < ACTION_KEYS.length) {
         long elapsed = (long)(
            Math.max(0.0, (double)((float)(this.level().getGameTime() - (Long)this.entityData.get(ACTION_STARTED_AT)) + tickDelta)) * 50.0
         );
         return ACTION_HOLD[index] ? Math.min(elapsed, (long)ACTION_LENGTHS[index]) : elapsed;
      } else {
         return 0L;
      }
   }

   public void tick() {
      this.horizontalTravelOld = this.horizontalTravel;
      double startX = this.getX();
      double startY = this.getY();
      double startZ = this.getZ();
      super.tick();
      double deltaX = this.getX() - startX;
      double deltaY = this.getY() - startY;
      double deltaZ = this.getZ() - startZ;
      this.horizontalTravelSpeed = this.locomotionDistance(deltaX, deltaY, deltaZ);
      if (deltaX * deltaX + deltaZ * deltaZ > 1.0E-8) {
         float travelYaw = (float)Math.toDegrees(Math.atan2(-deltaX, deltaZ));
         this.setYBodyRot(travelYaw);
         this.setYHeadRot(travelYaw);
      }

      this.horizontalTravel = this.horizontalTravel + this.horizontalTravelSpeed;
   }

   protected double locomotionDistance(double deltaX, double deltaY, double deltaZ) {
      return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
   }

   public float horizontalTravelPosition(float tickDelta) {
      double interpolated = this.horizontalTravelOld + (this.horizontalTravel - this.horizontalTravelOld) * tickDelta;
      return (float)(interpolated * 4.0);
   }

   public float horizontalTravelSpeed() {
      return (float)(this.horizontalTravelSpeed * 4.0);
   }

   protected void registerGoals() {
   }

   @Override protected InteractionResult mobInteract(Player player, InteractionHand hand){return InteractionResult.PASS;}
}

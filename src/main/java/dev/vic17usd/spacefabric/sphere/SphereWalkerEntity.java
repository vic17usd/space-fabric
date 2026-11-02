package dev.vic17usd.spacefabric.sphere;

import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.*;
import net.minecraft.world.phys.Vec3;

/** Experimental gravity harness; vanilla passenger networking avoids false vertical-flight kicks. */
public final class SphereWalkerEntity extends Entity {
    private static final EntityDataAccessor<Float> FX=SynchedEntityData.defineId(SphereWalkerEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FY=SynchedEntityData.defineId(SphereWalkerEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FZ=SynchedEntityData.defineId(SphereWalkerEntity.class,EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> YAW=SynchedEntityData.defineId(SphereWalkerEntity.class,EntityDataSerializers.FLOAT);
    private final InterpolationHandler interpolation=new InterpolationHandler(this,3);
    private double height,velocity;private boolean jumping;
    public SphereWalkerEntity(EntityType<? extends SphereWalkerEntity> type,Level level){super(type,level);noPhysics=true;setNoGravity(true);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder b){b.define(FX,0F);b.define(FY,0F);b.define(FZ,1F);b.define(YAW,0F);}
    public Vec3 forward(){return new Vec3(entityData.get(FX),entityData.get(FY),entityData.get(FZ));}
    public float headingYaw(){return entityData.get(YAW);}
    public void start(float yaw){entityData.set(YAW,yaw);setPos(SphereGeometry.CENTER.add(0,SphereGeometry.surface(new Vec3(0,1,0)),0));}
    @Override public InterpolationHandler getInterpolation(){return interpolation;}
    @Override public void tick(){
        super.tick();resetFallDistance();
        if(level().isClientSide()){interpolation.interpolate();return;}
        if(!(getFirstPassenger() instanceof ServerPlayer player)){discard();return;}
        var input=player.getLastClientInput();
        if(input.shift()&&tickCount>10){SphereDemo.leave(player);return;}
        player.resetFallDistance();player.noPhysics=true;
        var up=position().subtract(SphereGeometry.CENTER).normalize();
        var forward=SphereGeometry.rotate(SphereGeometry.tangent(forward(),up),up,-Math.toRadians(net.minecraft.util.Mth.wrapDegrees(player.getYRot()-headingYaw())));
        var step=SphereGeometry.step(up,forward,(input.forward()?1:0)-(input.backward()?1:0),(input.right()?1:0)-(input.left()?1:0),input.sprint()?.28:.16);
        if(input.jump()&&!jumping&&height<=.001)velocity=.28;
        jumping=input.jump();velocity-=.025;height+=velocity;
        if(height<0){height=0;velocity=0;}
        setPos(SphereGeometry.CENTER.add(step.up().scale(SphereGeometry.surface(step.up())+height)));
        entityData.set(FX,(float)step.forward().x);entityData.set(FY,(float)step.forward().y);entityData.set(FZ,(float)step.forward().z);entityData.set(YAW,player.getYRot());
        setDeltaMovement(Vec3.ZERO);
    }
    @Override protected void positionRider(Entity passenger,MoveFunction move){move.accept(passenger,getX(),getY(),getZ());}
    @Override protected boolean canAddPassenger(Entity e){return getPassengers().isEmpty()&&e instanceof Player;}
    @Override public boolean hurtServer(ServerLevel l,net.minecraft.world.damagesource.DamageSource d,float n){return false;}
    @Override protected void readAdditionalSaveData(ValueInput in){discard();}
    @Override protected void addAdditionalSaveData(ValueOutput out){ }
}

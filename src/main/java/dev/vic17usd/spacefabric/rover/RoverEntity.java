package dev.vic17usd.spacefabric.rover;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.ColonyContent;
import dev.vic17usd.spacefabric.industry.IndustryContent;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.recovery.RecoveryContent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.*;
import net.minecraft.world.phys.*;

/** One driver, six-wheel mesh, nine cargo slots; only native server input drives movement. */
public final class RoverEntity extends Entity implements MenuProvider {
    public static final EntityType<RoverEntity> TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,SpaceFabric.id("venus_rover"),
            EntityType.Builder.of(RoverEntity::new,MobCategory.MISC).sized(4.04F,2.92F).clientTrackingRange(12).updateInterval(1).fireImmune()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("venus_rover"))));
    public static final EntityType<RoverEntity> AEROSTAT_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,SpaceFabric.id("venus_aerostat"),
            EntityType.Builder.of(RoverEntity::new,MobCategory.MISC).sized(5.8F,7.4F).clientTrackingRange(16).updateInterval(1).fireImmune()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("venus_aerostat"))));
    public boolean aerial(){return getType()==AEROSTAT_TYPE;}
    private static final EntityDataAccessor<Integer> ENERGY=SynchedEntityData.defineId(RoverEntity.class,EntityDataSerializers.INT),
            OXYGEN=SynchedEntityData.defineId(RoverEntity.class,EntityDataSerializers.INT),COOLANT=SynchedEntityData.defineId(RoverEntity.class,EntityDataSerializers.INT),
            HULL=SynchedEntityData.defineId(RoverEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> OPTIMIZED=SynchedEntityData.defineId(RoverEntity.class,EntityDataSerializers.BOOLEAN);
    private final SimpleContainer inventory=new SimpleContainer(14);
    private final InterpolationHandler interpolation=new InterpolationHandler(this,3);
    private double speed;
    public RoverEntity(EntityType<? extends RoverEntity> type,Level level){super(type,level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){data.define(ENERGY,0);data.define(OXYGEN,0);data.define(COOLANT,0);data.define(HULL,100);data.define(OPTIMIZED,false);}
    public RoverSystems.State systems(){return new RoverSystems.State(entityData.get(ENERGY),entityData.get(OXYGEN),entityData.get(COOLANT),entityData.get(HULL),entityData.get(OPTIMIZED));}
    public void restoreSystems(RoverSystems.State state){entityData.set(ENERGY,state.energy());entityData.set(OXYGEN,state.oxygen());entityData.set(COOLANT,state.coolant());entityData.set(HULL,state.hull());entityData.set(OPTIMIZED,state.optimized());}
    public boolean installEfficiency(){
        if(aerial()||isVehicle()||Math.abs(speed)>.01||systems().optimized()||level()!=null&&level().isClientSide())return false;
        restoreSystems(systems().optimize());return true;
    }
    public SimpleContainer inventory(){return inventory;}
    public Planet planet(){return level()==null?Planet.EARTH:PlanetDimensions.find(level()).orElse(Planet.EARTH);}
    public boolean protects(){return !isRemoved()&&systems().protects(planet(),getY())&&!isUnderWater();}
    @Override public InterpolationHandler getInterpolation(){return interpolation;}
    @Override public boolean isPickable(){return true;}
    @Override public boolean isAttackable(){return true;}
    @Override public boolean canBeCollidedWith(Entity other){return !isRemoved()&&!isPassengerOfSameVehicle(other);}
    @Override public float maxUpStep(){return 1F;}
    @Override protected double getDefaultGravity(){return .08*(level()==null?1:planet().gravityScale());}
    @Override protected boolean canAddPassenger(Entity entity){return getPassengers().isEmpty()&&entity instanceof Player;}
    @Override public Vec3 getPassengerRidingPosition(Entity passenger) {
        var seat=new Vec3(0,.35,1.15).yRot((float)Math.toRadians(-getYRot())).add(position());
        // Honor the native attachment contract, including render interpolation.
        return seat.add(passenger.getVehicleAttachmentPoint(this));
    }
    public static boolean input(int slot,ItemStack stack){return switch(slot){case 0->stack.is(ColonyContent.CHARGED_POWER_CELL);case 1->stack.is(ColonyContent.OXYGEN_CANISTER);case 2->stack.is(ColonyContent.WATER_CANISTER)||stack.is(RecoveryContent.CHILLED_COOLANT);default->slot>=3&&slot<12;};}
    public boolean absorb(int slot) {
        if(slot<0||slot>2)return false;
        var source=inventory.getItem(slot);if(source.isEmpty()||!input(slot,source))return false;
        var supply=slot==0?RoverSystems.Supply.POWER:slot==1?RoverSystems.Supply.OXYGEN:
                source.is(RecoveryContent.CHILLED_COOLANT)?RoverSystems.Supply.CHILLED:RoverSystems.Supply.WATER;
        int output=slot==0?12:13;
        var empty=slot==0?ColonyContent.EMPTY_POWER_CELL:ColonyContent.EMPTY_CANISTER;
        var next=systems().supply(supply);if(next.equals(systems()))return false;
        var returned=inventory.getItem(output);
        if(!returned.isEmpty()&&(!returned.is(empty)||returned.getCount()>=returned.getMaxStackSize()))return false;
        restoreSystems(next);source.shrink(1);if(returned.isEmpty())inventory.setItem(output,new ItemStack(empty));else returned.grow(1);inventory.setChanged();return true;
    }
    private void absorbSupplies(){for(int slot=0;slot<3;slot++)absorb(slot);}
    public boolean board(Player player){absorbSupplies();return player.startRiding(this);}
    @Override public InteractionResult interact(Player player,InteractionHand hand,Vec3 hit) {
        if(!(level() instanceof ServerLevel))return InteractionResult.SUCCESS;
        if(!player.isAlive()||player.isSpectator())return InteractionResult.PASS;
        var held=player.getItemInHand(hand);
        if(held.is(dev.vic17usd.spacefabric.research.ResearchContent.ROVER_MODULE)){
            if(!installEfficiency()){player.sendSystemMessage(dev.vic17usd.spacefabric.research.ResearchSystem.text("rover_hint"));return InteractionResult.FAIL;}
            held.consume(1,player);player.sendSystemMessage(dev.vic17usd.spacefabric.research.ResearchSystem.text("rover_installed"));return InteractionResult.SUCCESS_SERVER;
        }
        if(held.is(IndustryContent.COMPOSITE_PLATE)&&systems().hull()<100) {
            restoreSystems(systems().supply(RoverSystems.Supply.REPAIR));held.consume(1,player);return InteractionResult.SUCCESS_SERVER;
        }
        if(player.isSecondaryUseActive()||getFirstPassenger()==player){player.openMenu(this);return InteractionResult.SUCCESS_SERVER;}
        return board(player)?InteractionResult.SUCCESS_SERVER:InteractionResult.FAIL;
    }
    @Override public void tick() {
        super.tick();
        if(level().isClientSide()){interpolation.interpolate();return;}
        if(tickCount%20==0)absorbSupplies();
        var pilot=getFirstPassenger() instanceof ServerPlayer player?player:null;
        var state=systems();int throttle=0,steer=0;boolean brake=false;
        if(pilot!=null) {
            var input=pilot.getLastClientInput();throttle=(input.forward()?1:0)-(input.backward()?1:0);steer=(input.left()?1:0)-(input.right()?1:0);brake=input.jump();
        }
        speed=RoverSystems.speed(speed,throttle,pilot!=null&&state.energy()>0&&state.hull()>0,brake&&!aerial());
        if(Math.abs(speed)<.001)speed=0;
        if(pilot!=null&&(aerial()||Math.abs(speed)>.005))setYRot(getYRot()-steer*2.2F*(speed<0?-1:1));
        var forward=new Vec3(0,0,speed).yRot((float)Math.toRadians(-getYRot()));
        double dy=aerial()?dev.vic17usd.spacefabric.atmosphere.VenusLayers.flightVertical(pilot==null?0:pilot.getXRot(),throttle,brake,
            state.energy()>0&&state.hull()>0&&planet()==Planet.VENUS,getDeltaMovement().y):(onGround()?-.08:getDeltaMovement().y)-getGravity();
        if(aerial()&&getY()>=340)dy=Math.min(0,dy);
        setDeltaMovement(forward.x,Math.max(-1.5,dy),forward.z);move(MoverType.SELF,getDeltaMovement());
        if(horizontalCollision)speed*=.3;
        if(onGround())setDeltaMovement(getDeltaMovement().multiply(1,0,1));
        if(aerial()&&pilot!=null&&tickCount%40==0){
            ((ServerLevel)level()).playSound(null,blockPosition(),net.minecraft.sounds.SoundEvents.BEACON_AMBIENT,net.minecraft.sounds.SoundSource.PLAYERS,.12F,.65F);
            if(getDeltaMovement().y<0)((ServerLevel)level()).sendParticles(pilot,net.minecraft.core.particles.ParticleTypes.END_ROD,false,false,getX(),getY()-.1,getZ(),1,.8,.05,.8,0);
        }
        resetFallDistance();getPassengers().forEach(Entity::resetFallDistance);
        var after=state.tick(pilot!=null,Math.abs(speed)>.005,planet(),tickCount);
        if(aerial()&&!onGround())after=new RoverSystems.State(after.energy()-1,after.oxygen(),after.coolant(),after.hull(),after.optimized());
        if(tickCount%20==0&&!RoverSystems.rated(planet(),getY()))after=new RoverSystems.State(after.energy(),after.oxygen(),after.coolant(),after.hull()-5,after.optimized());
        restoreSystems(after);
        if(pilot!=null&&tickCount%20==0)pilot.sendOverlayMessage(Component.translatable(protects()?"rover.space_fabric.telemetry":"rover.space_fabric.warning",after.energy(),after.oxygen(),after.coolant(),after.hull()));
    }
    @Override public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        for(var offset:new int[][]{{3,0},{-3,0},{0,3},{0,-3}})for(int dy=0;dy<=2;dy++) {
            var pos=position().add(offset[0],dy,offset[1]);var feet=net.minecraft.core.BlockPos.containing(pos);
            if(!level().hasChunkAt(feet)||!level().getWorldBorder().isWithinBounds(feet))continue;
            if(!level().getBlockState(feet.below()).isCollisionShapeFullBlock(level(),feet.below())||!level().getFluidState(feet).isEmpty())continue;
            if(level().noCollision(passenger,passenger.getDimensions(Pose.STANDING).makeBoundingBox(pos)))return pos;
        }
        return position().add(0,3,0);
    }
    @Override public boolean hurtServer(ServerLevel level,DamageSource source,float amount) {
        if(isRemoved()||!Float.isFinite(amount)||amount<=0)return false;
        if(source.getEntity() instanceof Player player&&player.isSecondaryUseActive()&&!isVehicle()&&Math.abs(speed)<.01) {
            if(!player.isCreative())spawnAtLocation(level,RoverItem.packed(systems(),aerial()));
            for(var stack:inventory.getItems())if(!stack.isEmpty())spawnAtLocation(level,stack.copy());
            inventory.clearContent();discard();return true;
        }
        restoreSystems(new RoverSystems.State(systems().energy(),systems().oxygen(),systems().coolant(),systems().hull()-(int)Math.clamp(amount,1,25),systems().optimized()));return true;
    }
    @Override protected void addAdditionalSaveData(ValueOutput output){output.store("RoverSystems",RoverSystems.State.CODEC,systems());ContainerHelper.saveAllItems(output,inventory.getItems());}
    @Override protected void readAdditionalSaveData(ValueInput input){restoreSystems(input.read("RoverSystems",RoverSystems.State.CODEC).orElse(RoverSystems.State.EMPTY));inventory.clearContent();ContainerHelper.loadAllItems(input,inventory.getItems());speed=0;setDeltaMovement(Vec3.ZERO);}
    @Override public Component getDisplayName(){return Component.translatable(aerial()?"entity.space_fabric.venus_aerostat":"entity.space_fabric.venus_rover");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new dev.vic17usd.spacefabric.menu.RoverMenu(id,inventory,this);}
    public static void initialize(){ }
}

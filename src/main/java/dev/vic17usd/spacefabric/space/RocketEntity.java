package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.menu.RocketMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

/** Un ocupante, dos slots de combustible/envases y nueve de carga. El cliente no gobierna la física. */
public final class RocketEntity extends Entity implements ExtendedMenuProvider<Integer> {
    private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DESTINATION = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FLIGHT = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HULL = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final int INVENTORY_SIZE = 11;
    private final SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE);
    private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);
    private int flightTicks;

    public RocketEntity(EntityType<? extends RocketEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VARIANT,0); builder.define(HULL,100); builder.define(FUEL, 0); builder.define(DESTINATION, 0); builder.define(FLIGHT, RocketRules.IDLE);
    }
    public RocketVariant variant(){return RocketVariant.byId(entityData.get(VARIANT));}
    public int hull(){return entityData.get(HULL);}
    public void restoreHull(int value){entityData.set(HULL,Math.clamp(value,0,100));}
    public void configureVariant(RocketVariant variant){
        entityData.set(VARIANT,variant.ordinal());
        entityData.set(DESTINATION,TravelDestination.indexOf(dev.vic17usd.spacefabric.planet.PlanetDimensions.key(variant.mission)));
    }
    public boolean permits(TravelDestination destination){return variant().permits(destination.planet(),RocketFlightConfig.exclusive());}
    public boolean install(RocketVariant target){
        if(isFlying()||isVehicle())return false;
        configureVariant(target);restoreHull(100);return true;
    }
    public int fuelCapacity(){return variant().fuelCapacity();}
    @Override protected Component getTypeName(){return variant().label();}
    public RocketNavigation.State navigation(Player player) {
        int source=level()==null?-1:TravelDestination.indexOf(level().dimension());
        return new RocketNavigation.State(variant(),source,destinationIndex(),flightState(),fuel(),requiredFuel(),fuelCapacity(),hull(),
                isPilot(player),isVehicle(),RocketFlightConfig.exclusive(),true);
    }
    public int fuel() { return entityData.get(FUEL); }
    void restorePackedFuel(int fuel) { entityData.set(FUEL, Math.clamp(fuel, 0, fuelCapacity())); }
    public int destinationIndex() { return entityData.get(DESTINATION); }
    public int flightState() { return entityData.get(FLIGHT); }
    public SimpleContainer inventory() { return inventory; }
    public boolean isFlying() { return flightState() != RocketRules.IDLE; }
    public boolean isPilot(Player player) { return player!=null && getFirstPassenger() == player; }
    @Override public boolean isPickable() { return true; }
    @Override public boolean isAttackable() { return true; }
    @Override protected boolean canAddPassenger(Entity passenger) { return getPassengers().isEmpty() && passenger instanceof Player; }
    @Override public InterpolationHandler getInterpolation() { return interpolation; }
    // No getControllingPassenger: el movimiento no se acepta mediante paquetes de vehículo del cliente.

    @Override public InteractionResult interact(Player player, InteractionHand hand, Vec3 hit) {
        if (!(level() instanceof ServerLevel)) return InteractionResult.SUCCESS;
        if (isFlying()||!player.isAlive()||player.isSpectator()) return InteractionResult.PASS;
        var stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof RocketUpgradeItem kit) {
            if(!install(kit.variant())) { player.sendOverlayMessage(Component.translatable("message.space_fabric.upgrade_empty"));return InteractionResult.FAIL; }
            if(!player.isCreative())stack.shrink(1);
            player.sendOverlayMessage(Component.translatable("message.space_fabric.rocket_upgraded",variant().label()));
            return InteractionResult.SUCCESS_SERVER;
        }
        if(stack.is(net.minecraft.world.item.Items.IRON_INGOT)&&hull()<100) {
            restoreHull(hull()+25);if(!player.isCreative())stack.shrink(1);return InteractionResult.SUCCESS_SERVER;
        }
        if (stack.is(ModItems.ROCKET_FUEL_CELL)) {
            if (!RocketRules.fitsCell(fuel(),fuelCapacity())) {
                player.sendOverlayMessage(Component.translatable("message.space_fabric.fuel_cell_space",RocketRules.FUEL_PER_CELL,fuel(),fuelCapacity()));
                return InteractionResult.FAIL;
            }
            entityData.set(FUEL, fuel() + RocketRules.FUEL_PER_CELL);
            if (!player.isCreative()) {
                stack.shrink(1);
                var empty = new ItemStack(ModItems.EMPTY_FUEL_CELL);
                if (!player.addItem(empty)) player.drop(empty, false);
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        if (!player.isSecondaryUseActive() && !isPilot(player) && !player.startRiding(this)) return InteractionResult.FAIL;
        ((ServerPlayer) player).openMenu(this);
        return InteractionResult.SUCCESS_SERVER;
    }

    public boolean selectDestination(Player player, int index) {
        if(player==null||!player.isAlive()||player.isSpectator()||isRemoved()||player.level()!=level()||player.distanceToSqr(this)>64)return false;
        if(navigation(player).destination(index)!=RocketNavigation.Reason.READY)return false;
        entityData.set(DESTINATION, index);
        return true;
    }

    public int requiredFuel() {
        int source = level()==null?-1:TravelDestination.indexOf(level().dimension());
        return source < 0 ? 0 : RocketRules.cost(TravelDestination.at(source), TravelDestination.at(destinationIndex()));
    }

    public boolean requestLaunch(ServerPlayer pilot) {
        if (!(level() instanceof ServerLevel server) || isRemoved() || !pilot.isAlive() || pilot.isSpectator() || pilot.level() != server) return false;
        var destination = TravelDestination.at(destinationIndex());
        int cost = requiredFuel();
        var reason=navigation(pilot).launch();
        if(reason!=RocketNavigation.Reason.READY) {
            pilot.sendOverlayMessage(Component.translatable(reason.key,variant().label()));return false;
        }
        if (server.getServer().getLevel(destination.world()) == null || !server.noCollision(this, getBoundingBox().move(0, 1, 0))) {
            pilot.sendOverlayMessage(Component.translatable("message.space_fabric.launch_blocked")); return false;
        }
        entityData.set(FUEL, fuel() - cost); // Reserva/consumo único del vuelo completo, protegido frente a doble pulsación.
        entityData.set(FLIGHT, RocketRules.ASCENDING);
        flightTicks = 0;
        pilot.closeContainer();
        return true;
    }

    @Override public void tick() {
        super.tick();
        if (level().isClientSide()) { interpolation.interpolate(); return; }
        var server = (ServerLevel) level();
        absorbFuelCell();
        if(tickCount%20==0 && dev.vic17usd.spacefabric.planet.PlanetDimensions.find(level())
                .map(planet->!variant().protects(planet,getY())).orElse(false)) {
            restoreHull(hull()-10);
            if(hull()==0)abort();
        }
        if (flightState() == RocketRules.IDLE) {
            setDeltaMovement(Vec3.ZERO);
            if (getFirstPassenger() instanceof ServerPlayer pilot && pilot.getLastClientInput().jump()) requestLaunch(pilot);
            return;
        }
        if (flightState() != RocketRules.ASCENDING) return;
        if (!(getFirstPassenger() instanceof ServerPlayer pilot) || !pilot.isAlive() || ++flightTicks > 1200) {
            abort(); return;
        }
        if (getY() >= level().getMaxY() - 8) {
            entityData.set(FLIGHT, RocketRules.TRANSFERRING);
            if (!SpaceTravel.transfer(this)) { abort(); pilot.sendOverlayMessage(Component.translatable("message.space_fabric.no_landing")); }
            return;
        }
        setDeltaMovement(0, RocketRules.ascentSpeed(flightTicks), 0);
        move(MoverType.SELF, getDeltaMovement());
        if (verticalCollision) { abort(); pilot.sendOverlayMessage(Component.translatable("message.space_fabric.launch_blocked")); return; }
        if (tickCount % 3 == 0) {
            server.sendParticles(ParticleTypes.FLAME, getX(), getY() - 0.1, getZ(), 8, 0.3, 0.2, 0.3, 0.04);
            server.sendParticles(ParticleTypes.SMOKE, getX(), getY() - 0.3, getZ(), 4, 0.4, 0.2, 0.4, 0.02);
        }
    }

    private void absorbFuelCell() {
        var input = inventory.getItem(0);
        var output = inventory.getItem(1);
        if (!RocketRules.fitsCell(fuel(),fuelCapacity()) || !input.is(ModItems.ROCKET_FUEL_CELL)
                || (!output.isEmpty() && (!output.is(ModItems.EMPTY_FUEL_CELL) || output.getCount() >= output.getMaxStackSize()))) return;
        input.shrink(1);
        if (output.isEmpty()) inventory.setItem(1, new ItemStack(ModItems.EMPTY_FUEL_CELL)); else output.grow(1);
        entityData.set(FUEL, fuel() + RocketRules.FUEL_PER_CELL);
        inventory.setChanged();
    }

    public void finishLanding() { abort(); resetFallDistance(); getPassengers().forEach(Entity::resetFallDistance); }
    private void abort() { entityData.set(FLIGHT, RocketRules.IDLE); setDeltaMovement(Vec3.ZERO); flightTicks = 0; }

    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (isRemoved() || isVehicle() || isFlying() || !(source.getEntity() instanceof Player player)) return false;
        if (!player.isCreative()) {
            spawnAtLocation(level, RocketItem.packed(fuel(),variant(),hull()));
            for (var stack : inventory.getItems()) if (!stack.isEmpty()) spawnAtLocation(level, stack.copy());
        }
        discard();
        return true;
    }

    @Override public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        for (var delta : new int[][] {{2, 0}, {-2, 0}, {0, 2}, {0, -2}}) {
            var pos = position().add(delta[0], 0, delta[1]);
            if (level().noCollision(passenger, new AABB(pos.x - 0.3, pos.y, pos.z - 0.3, pos.x + 0.3, pos.y + 1.9, pos.z + 0.3))) return pos;
        }
        return position().add(0, 3.5, 0);
    }

    @Override protected void readAdditionalSaveData(ValueInput input) {
        String savedVariant=input.getStringOr("RocketVariant","");
        var restored=RocketVariant.byName(savedVariant);
        if(savedVariant.isEmpty() && level()!=null) {
            var planet=dev.vic17usd.spacefabric.planet.PlanetDimensions.find(level()).orElse(dev.vic17usd.spacefabric.planet.Planet.EARTH);
            restored=planet==dev.vic17usd.spacefabric.planet.Planet.EARTH?RocketVariant.BASE:RocketVariant.byName(planet.path());
        }
        entityData.set(VARIANT,restored.ordinal()); restoreHull(input.getIntOr("Hull",100));
        entityData.set(FUEL, Math.clamp(input.getIntOr("Fuel", 0), 0, fuelCapacity()));
        entityData.set(DESTINATION, Math.clamp(input.getIntOr("Destination", 0), 0, TravelDestination.ALL.size() - 1));
        inventory.clearContent();
        ContainerHelper.loadAllItems(input, inventory.getItems());
        abort(); // Un cohete cargado desde disco no inicia automáticamente otro traslado.
    }
    @Override protected void addAdditionalSaveData(ValueOutput output) {
        output.putString("RocketVariant",variant().path);output.putInt("Hull",hull());
        output.putInt("Fuel", fuel()); output.putInt("Destination", destinationIndex());
        ContainerHelper.saveAllItems(output, inventory.getItems());
    }
    @Override public Integer getScreenOpeningData(ServerPlayer player) { return getId(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new RocketMenu(id, playerInventory, this);
    }
}

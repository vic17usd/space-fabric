package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;

/** Nine slots: inputs 0..2, products 3..5, returned vessels 6; spare storage 7..8. */
public final class ColonyMachineBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> items=NonNullList.withSize(9,ItemStack.EMPTY);
    private final LifeSupport support=new LifeSupport();
    private SealScanner.Room room;
    private int progress;
    private String recipe="",status="idle";
    private long lastWork=Long.MIN_VALUE,lastValidation=Long.MIN_VALUE;
    private boolean active;
    public ColonyMachineBlockEntity(BlockPos pos,BlockState state){super(ColonyContent.MACHINE_TYPE,pos,state);}
    public ColonyContent.Machine kind(){return ColonyContent.kind(getBlockState().getBlock());}
    public LifeSupport support(){return support;}
    public int progress(){return progress;}
    public Component status() {
        return kind()==ColonyContent.Machine.HABITAT?Component.translatable("colony.space_fabric.habitat_status",
                Component.translatable("colony.space_fabric."+status),support.oxygen(),support.energy(),support.coolant(),room==null?0:room.air().size()):
                Component.translatable("colony.space_fabric.machine_status",Component.translatable("colony.space_fabric."+status),progress);
    }
    public static void tick(Level level,BlockPos pos,BlockState state,ColonyMachineBlockEntity machine) {
        if(!(level instanceof ServerLevel server))return;
        if(machine.kind()==ColonyContent.Machine.HABITAT) {
            HabitatSystem.register(server,machine);
            if(machine.lastWork==Long.MIN_VALUE||level.getGameTime()-machine.lastWork>=20) {
                machine.lastWork=level.getGameTime();machine.tickHabitat(server);machine.setChanged();
            }
        } else machine.tickProcessing(server);
    }
    private boolean validate(ServerLevel level) {
        if(room==null||!room.sealed())return false;
        if(lastValidation==level.getGameTime())return room.sealed();
        lastValidation=level.getGameTime();
        if(room.unchanged(HabitatSystem.world(level)))return true;
        room=null;active=false;support.vent();status="leak";setChanged();return false;
    }
    public boolean protects(BlockPos head) {
        return active && level instanceof ServerLevel server && validate(server) && room.air().contains(head);
    }
    private void tickHabitat(ServerLevel level) {
        var planet=PlanetDimensions.find(level).orElse(Planet.EARTH);
        if(planet!=Planet.EARTH&&planet!=Planet.MARS&&planet!=Planet.VENUS){active=false;status="unsupported";return;}
        boolean venus=planet==Planet.VENUS;
        if(!validate(level)) {
            room=SealScanner.scan(HabitatSystem.world(level),worldPosition.above(),venus);
            lastValidation=level.getGameTime();
        }
        if(!room.sealed()){active=false;support.vent();status=room.status().name().toLowerCase(java.util.Locale.ROOT);return;}
        refill(0,ColonyContent.OXYGEN_CANISTER,ColonyContent.EMPTY_CANISTER,6,0);
        refill(1,ColonyContent.CHARGED_POWER_CELL,ColonyContent.EMPTY_POWER_CELL,5,1);
        if(venus)refill(2,ColonyContent.WATER_CANISTER,ColonyContent.EMPTY_CANISTER,6,2);
        int occupants=(int)level.players().stream().filter(p->p.isAlive()&&!p.isSpectator()&&room.air().contains(BlockPos.containing(p.getEyePosition()))).count();
        active=support.tick(room.air().size(),occupants,venus);status=active?"pressurized":"supplies";
    }
    private void refill(int input,Item full,Item empty,int output,int kind) {
        var stack=items.get(input);var old=items.get(output);
        if(!stack.is(full)||!old.isEmpty()&&(!old.is(empty)||old.getCount()>=old.getMaxStackSize()))return;
        if(!support.fill(kind))return;
        stack.shrink(1);if(old.isEmpty())items.set(output,new ItemStack(empty));else old.grow(1);
    }
    private void tickProcessing(ServerLevel level) {
        var formula=ColonyRecipes.ALL.stream().filter(f->f.machine()==kind()&&f.accepts(this)).findFirst().orElse(null);
        if(formula==null){if(progress!=0){progress=0;setChanged();}recipe="";status="inputs";return;}
        if(!recipe.equals(formula.id())){recipe=formula.id();progress=0;}
        if(!formula.fits(this)){status="output_full";return;}
        if(kind()==ColonyContent.Machine.SOLAR) {
            long phase=Math.floorMod(level.getDefaultClockTime(),24000);
            if(PlanetDimensions.find(level).orElse(null)==Planet.VENUS||phase>=12000||!level.canSeeSky(worldPosition.above())||level.isRainingAt(worldPosition.above())) {status="sunlight";return;}
        }
        if(kind()==ColonyContent.Machine.GEOTHERMAL&&!level.getFluidState(worldPosition.below()).is(FluidTags.LAVA)){status="heat";return;}
        status="processing";progress++;setChanged();
        if(progress>=formula.duration()&&formula.commit(this)){progress=0;}
    }
    @Override public void setRemoved(){HabitatSystem.remove(this);active=false;super.setRemoved();}
    @Override public int getContainerSize(){return 9;}
    @Override protected NonNullList<ItemStack> getItems(){return items;}
    @Override protected void setItems(NonNullList<ItemStack> items){this.items=items;}
    @Override protected Component getDefaultName(){return getBlockState().getBlock().getName();}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return new DispenserMenu(id,inventory,this);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return ColonyRecipes.accepts(kind(),slot,stack);}
    @Override public int[] getSlotsForFace(Direction face){return face==Direction.DOWN?new int[]{3,4,5,6,7,8}:face==Direction.UP?new int[]{0}:new int[]{1,2};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){return slot>=3;}
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);items=NonNullList.withSize(9,ItemStack.EMPTY);ContainerHelper.loadAllItems(input,items);
        progress=Math.clamp(input.getIntOr("Progress",0),0,400);recipe=input.getStringOr("Recipe","");
        support.restore(input.getIntOr("Oxygen",0),input.getIntOr("Energy",0),input.getIntOr("Coolant",0),input.getIntOr("Volume",0),input.getBooleanOr("Pressurized",false));
        active=false;room=null;lastWork=lastValidation=Long.MIN_VALUE;
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);ContainerHelper.saveAllItems(output,items);output.putInt("Progress",progress);output.putString("Recipe",recipe);
        output.putInt("Oxygen",support.oxygen());output.putInt("Energy",support.energy());output.putInt("Coolant",support.coolant());
        output.putInt("Volume",room!=null&&room.sealed()?room.air().size():0);output.putBoolean("Pressurized",support.pressurized());
    }
}

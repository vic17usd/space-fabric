package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;

/** Nine slots: inputs 0..2, products 3..5, returned vessels 6; spare storage 7..8. */
public final class ColonyMachineBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer,dev.vic17usd.spacefabric.maintenance.Maintainable {
    private NonNullList<ItemStack> items=NonNullList.withSize(9,ItemStack.EMPTY);
    private final dev.vic17usd.spacefabric.maintenance.MachineWear maintenance=new dev.vic17usd.spacefabric.maintenance.MachineWear();
    @Override public dev.vic17usd.spacefabric.maintenance.MachineWear maintenance(){return maintenance;}
    public boolean sheltersMachine(BlockPos pos){return level instanceof ServerLevel server&&validate(server)&&dev.vic17usd.spacefabric.maintenance.MachineExposure.enclosed(room,pos,HabitatSystem.world(server));}
    private final LifeSupport support=new LifeSupport();
    private final ThermalBudget thermal=new ThermalBudget();
    private ColonyMachineBlockEntity thermalOwner;
    private int coolingCharge;
    private boolean dockEnabled;
    private int naturalMission;
    public boolean dockEnabled(){return dockEnabled;}
    public void toggleDock(){dockEnabled=!dockEnabled;setChanged();}
    public int naturalMission(){return naturalMission;}
    public boolean operating(){return active&&!maintenance.failed();}
    private boolean workedSinceThermal;
    public ThermalBudget localThermal(){return thermal;}
    public ThermalBudget thermal(){return thermalOwner!=null&&!thermalOwner.isRemoved()?thermalOwner.thermal:thermal;}
    void thermalOwner(ColonyMachineBlockEntity owner){thermalOwner=owner;}
    void markWorked(){workedSinceThermal=true;}
    boolean takeWorked(){boolean worked=workedSinceThermal;workedSinceThermal=false;return worked;}
    public int coolingCharge(){return coolingCharge;}
    boolean passiveCooling(){return active&&!maintenance.failed()&&support.coolant()>0;}
    int extractCooling(int requested){int amount=maintenance.failed()?0:Math.min(coolingCharge,Math.max(0,requested));coolingCharge-=amount;if(amount>0)setChanged();return amount;}
    public void restoreThermal(ItemStack stack){
        var data=stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if(data==null)return;
        var tag=data.copyTag();restoreThermal(tag.getIntOr("IndustrialHeat",0),tag.getBooleanOr("ThermalTrip",false),tag.getIntOr("CoolingCharge",0));
    }
    public void restoreThermal(int heat,boolean trip,int charge){thermal.restore(heat,trip);coolingCharge=Math.clamp(charge,0,ThermalSystem.CHARGE);setChanged();}
    private SealScanner.Room room;
    private SealScanner.Room lastEnclosure;
    private int progress;
    private int completedBatches;
    public int completedBatches(){return completedBatches;}
    private String recipe="",status="idle";
    private long lastWork=Long.MIN_VALUE,lastValidation=Long.MIN_VALUE,lastSealScan=Long.MIN_VALUE;
    private boolean active;
    public ColonyMachineBlockEntity(BlockPos pos,BlockState state){super(ColonyContent.MACHINE_TYPE,pos,state);}
    public ColonyContent.Machine kind(){return ColonyContent.kind(getBlockState().getBlock());}
    public LifeSupport support(){return support;}
    public int progress(){return progress;}

    public SealScanner.Room enclosure() { return room!=null&&room.sealed()?room:null; }
    /** Diagnostic coverage survives a breach, but can never grant atmospheric protection. */
    public boolean monitors(BlockPos head) {
        return room!=null&&room.air().contains(head) || lastEnclosure!=null&&lastEnclosure.air().contains(head);
    }
    public HabitatReadings readings() {
        if(level instanceof ServerLevel server)validate(server);
        var condition=switch(status) {
            case "pressurized" -> active?HabitatReadings.Condition.PRESSURIZED:HabitatReadings.Condition.IDLE;
            case "leak" -> HabitatReadings.Condition.LEAK;
            case "too_large" -> HabitatReadings.Condition.TOO_LARGE;
            case "unloaded" -> HabitatReadings.Condition.UNLOADED;
            case "weak_wall" -> HabitatReadings.Condition.WEAK_WALL;
            case "blocked_vent" -> HabitatReadings.Condition.BLOCKED_VENT;
            case "unsupported" -> HabitatReadings.Condition.UNSUPPORTED;
            case "maintenance_failure" -> HabitatReadings.Condition.MAINTENANCE_FAILURE;
            case "supplies" -> switch(support.failure()) {
                case OXYGEN -> HabitatReadings.Condition.NEEDS_OXYGEN;
                case POWER -> HabitatReadings.Condition.NEEDS_POWER;
                case COOLANT -> HabitatReadings.Condition.NEEDS_COOLANT;
                default -> HabitatReadings.Condition.IDLE;
            };
            default -> HabitatReadings.Condition.IDLE;
        };
        boolean venus=level!=null&&PlanetDimensions.find(level).orElse(null)==Planet.VENUS;
        return new HabitatReadings(condition,support.oxygen(),support.energy(),support.coolant(),room!=null&&room.sealed()?room.air().size():0,venus,maintenance.integrity(),maintenance.state().coating(),maintenance.state().filter(),thermal().heat(),thermal().tripped());
    }
    public ContainerData machineData() {
        return new ContainerData() {
            public int getCount(){return 11;}public void set(int index,int value) { }
            public int get(int index) {
                return switch(index) {
                    case 0->progress;
                    case 1->ColonyRecipes.ALL.stream().filter(f->f.id().equals(recipe)).findFirst().map(ColonyRecipes.Formula::duration).orElse(0);
                    case 2->Math.max(0,java.util.List.of("idle","inputs","output_full","sunlight","heat","processing","maintenance_failure","collector_environment","collector_intake","overheating","cooling_ready","greenhouse_room","greenhouse_light","recovery_wait","dock_off","dock_wait","dock_ready","survey_ready").indexOf(status));
                    case 3->maintenance.integrity();case 4->maintenance.state().coating();case 5->maintenance.state().filter();case 6->maintenance.risk().ordinal();case 7->kind().ordinal();case 8->thermal().heat();case 9->thermal().tripped()?1:0;default->coolingCharge;
                };
            }
        };
    }
    public Component status() {
        return kind()==ColonyContent.Machine.HABITAT?Component.translatable("colony.space_fabric.habitat_status",
                Component.translatable("colony.space_fabric."+status),support.oxygen(),support.energy(),support.coolant(),room==null?0:room.air().size()):
                Component.translatable("colony.space_fabric.machine_status",Component.translatable("colony.space_fabric."+status),progress);
    }
    public static void tick(Level level,BlockPos pos,BlockState state,ColonyMachineBlockEntity machine) {
        if(!(level instanceof ServerLevel server))return;
        ThermalSystem.register(server,machine);
        if(machine.kind()==ColonyContent.Machine.HABITAT){HabitatSystem.register(server,machine);machine.refreshSeal(server);}
        if(dev.vic17usd.spacefabric.maintenance.MachineExposure.tick(server,machine,machine.maintenance))machine.setChanged();
        if(machine.maintenance.failed()){machine.active=false;machine.status="maintenance_failure";return;}
        if(machine.kind()==ColonyContent.Machine.HABITAT) {
            if(machine.lastWork==Long.MIN_VALUE||level.getGameTime()-machine.lastWork>=20) {
                machine.lastWork=level.getGameTime();machine.tickHabitat(server);machine.setChanged();
            }
        } else if(machine.kind()==ColonyContent.Machine.DOCK) {
            if(level.getGameTime()%20==0){machine.status=machine.dockEnabled?"dock_wait":"dock_off";
                if(machine.dockEnabled&&!machine.thermal().tripped()&&RoverDockService.tick(server,machine))machine.status="dock_ready";}
        } else if(machine.maintenance.allowsWork(level.getGameTime()))machine.tickProcessing(server);
    }
    boolean validate(ServerLevel level) {
        if(room==null||!room.sealed())return false;
        if(lastValidation==level.getGameTime())return room.sealed();
        lastValidation=level.getGameTime();
        if(room.unchanged(HabitatSystem.world(level)))return true;
        room=null;active=false;support.vent();status="leak";setChanged();return false;
    }
    public boolean protects(BlockPos head) {
        return active && room!=null && room.air().contains(head) && level instanceof ServerLevel server && validate(server);
    }
    private void refreshSeal(ServerLevel level) {
        var planet=PlanetDimensions.find(level).orElse(Planet.EARTH);
        if(planet!=Planet.EARTH&&planet!=Planet.MARS&&planet!=Planet.VENUS)return;
        if(validate(level))return;
        if(lastSealScan!=Long.MIN_VALUE&&level.getGameTime()-lastSealScan<20)return;
        lastSealScan=level.getGameTime();room=SealScanner.scan(HabitatSystem.world(level),worldPosition.above(),planet==Planet.VENUS);
        lastValidation=level.getGameTime();if(room.sealed())lastEnclosure=room;
    }
    @Override public void resumeAfterRepair(){lastWork=Long.MIN_VALUE;}
    private void tickHabitat(ServerLevel level) {
        var planet=PlanetDimensions.find(level).orElse(Planet.EARTH);
        if(planet!=Planet.EARTH&&planet!=Planet.MARS&&planet!=Planet.VENUS){active=false;status="unsupported";return;}
        boolean venus=planet==Planet.VENUS;
        refreshSeal(level);
        if(room==null||!room.sealed()){active=false;support.vent();status=room==null?"idle":room.status().name().toLowerCase(java.util.Locale.ROOT);return;}
        lastEnclosure=room;
        refillFromItems(venus);
        int occupants=(int)level.players().stream().filter(p->p.isAlive()&&!p.isSpectator()&&room.air().contains(BlockPos.containing(p.getEyePosition()))).count();
        active=support.tick(room.air().size(),occupants,venus);status=active?"pressurized":"supplies";
    }
    private void refill(int input,Item full,Item empty,int output,int kind) {
        var stack=items.get(input);var old=items.get(output);
        if(!stack.is(full)||!old.isEmpty()&&(!old.is(empty)||old.getCount()>=old.getMaxStackSize()))return;
        if(!support.fill(kind))return;
        stack.shrink(1);if(old.isEmpty())items.set(output,new ItemStack(empty));else old.grow(1);
    }
    /** Container conversion grants no protection: the room and operating controller remain mandatory. */
    public void refillFromItems(boolean venus) {
        if(maintenance.failed())return;
        refill(0,ColonyContent.OXYGEN_CANISTER,ColonyContent.EMPTY_CANISTER,6,0);
        refill(1,ColonyContent.CHARGED_POWER_CELL,ColonyContent.EMPTY_POWER_CELL,5,1);
        if(venus)refill(2,ColonyContent.WATER_CANISTER,ColonyContent.EMPTY_CANISTER,6,2);
        setChanged();
    }
    private void tickProcessing(ServerLevel level) {
        processTick(ColonyEnvironment.read(level,worldPosition,kind()));
    }
    /** Server processing path, also usable by native inventory/persistence tests without a game server. */
    public void processTick(ColonyEnvironment environment) {
        if(maintenance.failed()){status="maintenance_failure";return;}
        if(kind()==ColonyContent.Machine.SURVEY){status="survey_ready";return;}
        if(kind()==ColonyContent.Machine.DOCK)return;
        if(environment.venus()&&kind()!=ColonyContent.Machine.EXCHANGER&&thermal().tripped()){status="overheating";return;}
        if(kind()==ColonyContent.Machine.EXCHANGER&&coolingCharge>0){status="cooling_ready";return;}
        var formula=ColonyRecipes.ALL.stream().filter(f->f.machine()==kind()&&f.accepts(this)).findFirst().orElse(null);
        if(formula==null){if(progress!=0){progress=0;setChanged();}recipe="";status="inputs";return;}
        if(!recipe.equals(formula.id())){recipe=formula.id();progress=0;}
        if(!formula.fits(this)){status="output_full";return;}
        var blocker=environment.blocker(kind());
        if(!blocker.isEmpty()){status=blocker;return;}
        status="processing";workedSinceThermal=true;progress++;setChanged();
        if(progress>=formula.duration()&&(kind()!=ColonyContent.Machine.RECLAIMER||environment.recovery()!=null&&environment.recovery().respired()>=4800)&&formula.commit(this)){if(kind()==ColonyContent.Machine.RECLAIMER)environment.recovery().reclaim();if(kind()==ColonyContent.Machine.EXCHANGER)coolingCharge=ThermalSystem.CHARGE;progress=0;completedBatches=Math.min(Integer.MAX_VALUE-1,completedBatches)+1;}
    }
    @Override public void setRemoved(){HabitatSystem.remove(this);ThermalSystem.remove(this);thermalOwner=null;active=false;super.setRemoved();}
    @Override public int getContainerSize(){return 9;}
    @Override protected NonNullList<ItemStack> getItems(){return items;}
    @Override protected void setItems(NonNullList<ItemStack> items){this.items=items;}
    @Override protected Component getDefaultName(){return getBlockState().getBlock().getName();}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return kind()==ColonyContent.Machine.HABITAT
            ?new dev.vic17usd.spacefabric.menu.HabitatMenu(id,inventory,this,dev.vic17usd.spacefabric.menu.HabitatMenu.data(this::readings))
            :new dev.vic17usd.spacefabric.menu.ColonyMachineMenu(id,inventory,this,machineData());}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return ColonyRecipes.accepts(kind(),slot,stack);}
    @Override public int[] getSlotsForFace(Direction face){if(kind()==ColonyContent.Machine.DOCK)return face==Direction.DOWN?new int[]{3,4,5,6,7}:face==Direction.UP?new int[]{0,8}:new int[]{1,2,8};return face==Direction.DOWN?new int[]{3,4,5,6,7,8}:face==Direction.UP?new int[]{0}:new int[]{1,2};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){return slot>=3&&(kind()!=ColonyContent.Machine.DOCK||slot!=8);}
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);items=NonNullList.withSize(9,ItemStack.EMPTY);ContainerHelper.loadAllItems(input,items);
        progress=Math.clamp(input.getIntOr("Progress",0),0,400);recipe=input.getStringOr("Recipe","");
        completedBatches=Math.max(0,input.getIntOr("CompletedBatches",0));
        support.restoreRespired(input.getIntOr("Respired",0));dockEnabled=input.getBooleanOr("DockEnabled",false);naturalMission=Math.clamp(input.getIntOr("NaturalMission",0),0,3);
        support.restore(input.getIntOr("Oxygen",0),input.getIntOr("Energy",0),input.getIntOr("Coolant",0),input.getIntOr("Volume",0),input.getBooleanOr("Pressurized",false));
        restoreThermal(input.getIntOr("IndustrialHeat",0),input.getBooleanOr("ThermalTrip",false),input.getIntOr("CoolingCharge",0));
        maintenance.restore(input.read("Maintenance",dev.vic17usd.spacefabric.maintenance.MachineWear.State.CODEC).orElse(dev.vic17usd.spacefabric.maintenance.MachineWear.State.FRESH));
        active=false;room=null;lastEnclosure=null;lastWork=lastValidation=lastSealScan=Long.MIN_VALUE;
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);output.putInt("Respired",support.respired());output.putBoolean("DockEnabled",dockEnabled);output.putInt("NaturalMission",naturalMission);output.putInt("IndustrialHeat",thermal.heat());output.putBoolean("ThermalTrip",thermal.tripped());output.putInt("CoolingCharge",coolingCharge);output.store("Maintenance",dev.vic17usd.spacefabric.maintenance.MachineWear.State.CODEC,maintenance.state());ContainerHelper.saveAllItems(output,items);output.putInt("Progress",progress);output.putString("Recipe",recipe);
        output.putInt("CompletedBatches",completedBatches);output.putInt("Oxygen",support.oxygen());output.putInt("Energy",support.energy());output.putInt("Coolant",support.coolant());
        output.putInt("Volume",room!=null&&room.sealed()?room.air().size():0);output.putBoolean("Pressurized",support.pressurized());
    }
}

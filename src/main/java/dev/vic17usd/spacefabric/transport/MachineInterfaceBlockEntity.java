package dev.vic17usd.spacefabric.transport;

import dev.vic17usd.spacefabric.colony.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;

/** Container-aware adapter. Transport never invents a canister or cell to feed a machine. */
public final class MachineInterfaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public enum Status { IDLE,DISABLED,NO_MACHINE,BROKEN,MATERIALS,CONTAINERS,RESOURCE,SPACE,WORKING,UNLOADED,TOO_LARGE,MIXED,NO_STORAGE }
    private NonNullList<ItemStack> items=NonNullList.withSize(2,ItemStack.EMPTY);
    private boolean enabled=true;
    private String selected="",resolved="";
    private Status status=Status.IDLE;
    private Status exportIssue;
    public MachineInterfaceBlockEntity(BlockPos pos,BlockState state){super(TransportContent.INTERFACE_TYPE,pos,state);}
    public Status status(){return status;}
    public boolean enabled(){return enabled&&!getBlockState().getValue(MachineInterfaceBlock.POWERED);}
    public void toggle(){enabled=!enabled;setChanged();}
    public ColonyMachineBlockEntity target() {
        if(!(level instanceof net.minecraft.server.level.ServerLevel server))return null;
        var pos=worldPosition.relative(getBlockState().getValue(MachineInterfaceBlock.FACING));
        if(server.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null&&server.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine)return machine;
        return null;
    }
    public static void tick(Level level,BlockPos pos,BlockState state,MachineInterfaceBlockEntity port){if(level instanceof net.minecraft.server.level.ServerLevel server)TransportService.register(server,port);}
    @Override public void setRemoved(){TransportService.remove(this);super.setRemoved();}
    private static List<ColonyRecipes.Formula> recipes(ColonyMachineBlockEntity machine){return machine==null?List.of():ColonyRecipes.ALL.stream().filter(f->f.machine()==machine.kind()).toList();}
    public void select(int delta) {
        var choices=recipes(target());if(choices.isEmpty())return;
        int index=-1;for(int i=0;i<choices.size();i++)if(choices.get(i).id().equals(selected))index=i;
        int next=Math.floorMod(index+1+delta,choices.size()+1)-1;selected=next<0?"":choices.get(next).id();resolved=selected;setChanged();
    }
    private static int carrier(Item item){return item==ColonyContent.EMPTY_CANISTER?0:item==ColonyContent.EMPTY_POWER_CELL?1:-1;}
    private static boolean ordinary(ColonyRecipes.Part part){return carrier(part.item())<0&&TransportResource.of(part.stack())==TransportResource.NONE;}
    private ColonyRecipes.Formula formula(ColonyMachineBlockEntity machine) {
        var choices=recipes(machine);if(choices.isEmpty())return null;
        var explicit=choices.stream().filter(f->f.id().equals(selected)).findFirst();if(explicit.isPresent())return explicit.get();
        if(!selected.isEmpty()){selected="";setChanged();}
        var present=choices.stream().filter(f->f.inputs().stream().anyMatch(p->ordinary(p)&&machine.getItem(p.slot()).is(p.item())))
                .filter(f->f.inputs().stream().filter(MachineInterfaceBlockEntity::ordinary).allMatch(p->machine.getItem(p.slot()).is(p.item())&&machine.getItem(p.slot()).getCount()>=p.count())).findFirst();
        if(present.isPresent())return present.get();
        var matching=choices.stream().filter(f->f.inputs().stream().anyMatch(p->p.slot()==0&&TransportResource.of(p.stack())!=TransportResource.NONE&&machine.getItem(p.slot()).is(p.item()))).findFirst();
        if(matching.isPresent())return matching.get();
        return choices.stream().filter(f->f.id().equals("electrolysis")).findFirst().orElse(choices.getFirst());
    }
    private static boolean outputFits(ItemStack old,Item item,int count){return old.isEmpty()||old.is(item)&&old.getCount()+count<=old.getMaxStackSize();}
    /** Export completed supplies first; solid products and recipe ingredients remain untouched. */
    public boolean exportOutputs(ColonyMachineBlockEntity machine,List<TransportGraph> graphs) {
        exportIssue=null;
        if(!enabled())return false;boolean moved=false;
        for(int slot=3;slot<machine.getContainerSize();slot++) {
            var source=machine.getItem(slot);if(source.isEmpty())continue;
            int empty=carrier(source.getItem());
            if(empty>=0) {
                var reserve=items.get(empty);if(!reserve.isEmpty()&&!reserve.is(source.getItem()))continue;
                int count=Math.min(source.getCount(),reserve.isEmpty()?64:reserve.getMaxStackSize()-reserve.getCount());
                if(count<=0)continue;
                if(reserve.isEmpty())items.set(empty,new ItemStack(source.getItem(),count));else reserve.grow(count);
                source.shrink(count);moved=true;continue;
            }
            var resource=TransportResource.of(source);
            if(resource==TransportResource.NONE||source.is(Items.WATER_BUCKET))continue;
            int buffer=resource==TransportResource.POWER?1:0;var reserve=items.get(buffer);
            if(!outputFits(reserve,resource.emptyItem(),1)){exportIssue=Status.SPACE;continue;}
            if(!TransportGraph.transfer(graphs,resource,resource.quantum,true)){exportIssue=unavailable(graphs,Status.SPACE);continue;}
            source.shrink(1);if(reserve.isEmpty())items.set(buffer,new ItemStack(resource.emptyItem()));else reserve.grow(1);moved=true;
        }
        if(moved){machine.setChanged();setChanged();}return moved;
    }
    private Status unavailable(List<TransportGraph> graphs,Status fallback) {
        for(var problem:List.of(TransportGraph.Problem.MIXED,TransportGraph.Problem.TOO_LARGE,TransportGraph.Problem.UNLOADED))
            if(graphs.stream().anyMatch(g->g.problem()==problem))return switch(problem){case MIXED->Status.MIXED;case TOO_LARGE->Status.TOO_LARGE;default->Status.UNLOADED;};
        return graphs.stream().anyMatch(g->g.problem()==TransportGraph.Problem.NONE)?fallback:Status.NO_STORAGE;
    }
    /** Feed at most one recipe's worth; the actual machine remains authoritative over processing. */
    public boolean supplyInputs(ColonyMachineBlockEntity machine,List<TransportGraph> graphs,boolean venus) {
        resolved="";
        if(!enabled()){status=Status.DISABLED;return false;}
        if(machine==null){status=Status.NO_MACHINE;return false;}
        if(machine.maintenance().failed()){status=Status.BROKEN;return false;}
        List<ColonyRecipes.Part> inputs;
        if(machine.kind()==ColonyContent.Machine.HABITAT) {
            inputs=new ArrayList<>(List.of(new ColonyRecipes.Part(0,ColonyContent.OXYGEN_CANISTER,1),new ColonyRecipes.Part(1,ColonyContent.CHARGED_POWER_CELL,1)));
            if(venus)inputs.add(new ColonyRecipes.Part(2,ColonyContent.WATER_CANISTER,1));
        } else {
            var formula=formula(machine);if(formula==null){status=Status.MATERIALS;return false;}
            resolved=formula.id();inputs=formula.inputs();
            if(inputs.stream().filter(MachineInterfaceBlockEntity::ordinary).anyMatch(p->!machine.getItem(p.slot()).is(p.item())||machine.getItem(p.slot()).getCount()<p.count())){status=Status.MATERIALS;return false;}
        }
        boolean moved=false;Status blocked=null;
        for(var part:inputs) {
            var old=machine.getItem(part.slot());if(old.is(part.item())&&old.getCount()>=part.count())continue;
            if(!old.isEmpty()&&!old.is(part.item())){blocked=Status.MATERIALS;continue;}
            int carrier=carrier(part.item());var resource=TransportResource.of(part.stack());
            if(carrier<0&&resource==TransportResource.NONE)continue;
            int buffer=carrier>=0?carrier:resource==TransportResource.POWER?1:0;var reserve=items.get(buffer);
            if(!reserve.is(buffer==0?ColonyContent.EMPTY_CANISTER:ColonyContent.EMPTY_POWER_CELL)){blocked=Status.CONTAINERS;continue;}
            if(resource!=TransportResource.NONE&&!TransportGraph.transfer(graphs,resource,resource.quantum,false)){blocked=unavailable(graphs,Status.RESOURCE);continue;}
            reserve.shrink(1);if(old.isEmpty())machine.setItem(part.slot(),new ItemStack(part.item()));else old.grow(1);moved=true;
        }
        status=exportIssue!=null?exportIssue:blocked==null?Status.WORKING:blocked;
        if(moved){machine.setChanged();setChanged();}return moved;
    }
    public ContainerData data(){return new ContainerData(){
        public int getCount(){return 5;}public void set(int i,int value) { }
        public int get(int i) {
            var target=target();
            return switch(i){case 0->selected.isEmpty()?0:1;case 1->target==null?-1:target.kind().ordinal();case 2->{int id=-1;for(int n=0;n<ColonyRecipes.ALL.size();n++)if(ColonyRecipes.ALL.get(n).id().equals(resolved))id=n;yield id;}
                case 3->status.ordinal();default->enabled?1:0;};
        }
    };}
    @Override public int getContainerSize(){return 2;}
    @Override protected NonNullList<ItemStack> getItems(){return items;}
    @Override protected void setItems(NonNullList<ItemStack> items){this.items=items;}
    @Override protected Component getDefaultName(){return getBlockState().getBlock().getName();}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return new dev.vic17usd.spacefabric.menu.MachineInterfaceMenu(id,inventory,this);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0?stack.is(ColonyContent.EMPTY_CANISTER):slot==1&&stack.is(ColonyContent.EMPTY_POWER_CELL);}
    @Override public int[] getSlotsForFace(Direction face){return new int[]{0,1};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){return false;}
    @Override protected void saveAdditional(ValueOutput output){super.saveAdditional(output);ContainerHelper.saveAllItems(output,items);output.putBoolean("Enabled",enabled);output.putString("SelectedRecipe",selected);}
    @Override protected void loadAdditional(ValueInput input){super.loadAdditional(input);items=NonNullList.withSize(2,ItemStack.EMPTY);ContainerHelper.loadAllItems(input,items);enabled=input.getBooleanOr("Enabled",true);selected=input.getStringOr("SelectedRecipe","");resolved="";status=Status.IDLE;}
}

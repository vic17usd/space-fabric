package dev.vic17usd.spacefabric.transport;

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

/** Container input/output is separate from the bulk content copied into a mined tank item. */
public final class TransportTankBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> items=NonNullList.withSize(2,ItemStack.EMPTY);
    private final TransportBuffer buffer;
    private int mode,status;
    public TransportTankBlockEntity(BlockPos pos,BlockState state){super(TransportContent.TANK_TYPE,pos,state);buffer=new TransportBuffer(((TransportTankBlock)state.getBlock()).channel());}
    public TransportBuffer buffer(){return buffer;}
    public int mode(){return mode;}
    public int status(){return status;}
    public void toggleMode(){mode=1-mode;setChanged();}
    public boolean cycleResource() {
        var choices=TransportResource.choices(buffer.channel());int next=(choices.indexOf(buffer.state().resource())+1)%choices.size();
        boolean changed=buffer.select(choices.get(next));if(changed)setChanged();return changed;
    }
    public static void tick(Level level,BlockPos pos,BlockState state,TransportTankBlockEntity tank){if(!level.isClientSide()&&level.getGameTime()%20==0)tank.processContainer();}
    private boolean fits(ItemStack result){var old=items.get(1);return old.isEmpty()||ItemStack.isSameItemSameComponents(old,result)&&old.getCount()+result.getCount()<=old.getMaxStackSize();}
    public boolean processContainer() {
        var input=items.get(0);if(input.isEmpty()){status=1;return false;}
        var resource=mode==0?TransportResource.of(input):buffer.state().resource();
        if(resource==TransportResource.NONE||resource.channel!=buffer.channel()){status=3;return false;}
        Item result;
        if(mode==0)result=input.is(Items.WATER_BUCKET)?Items.BUCKET:resource.emptyItem();
        else if(input.is(resource.emptyItem()))result=resource.fullItem();
        else if(resource==TransportResource.WATER&&input.is(Items.BUCKET))result=Items.WATER_BUCKET;
        else {status=1;return false;}
        var product=new ItemStack(result);if(!fits(product)){status=2;return false;}
        if(mode==0) {
            if(buffer.free(resource)<resource.quantum){status=buffer.state().resource()!=TransportResource.NONE&&buffer.state().resource()!=resource?3:4;return false;}
            buffer.insert(resource,resource.quantum);
        } else {
            if(buffer.available(resource)<resource.quantum){status=5;return false;}
            buffer.extract(resource,resource.quantum);
        }
        input.shrink(1);if(items.get(1).isEmpty())items.set(1,product);else items.get(1).grow(1);
        status=6;setChanged();return true;
    }
    public ContainerData data(){return new ContainerData(){
        public int getCount(){return 6;}public void set(int i,int value) { }
        public int get(int i){return switch(i){case 0->buffer.state().resource().ordinal();case 1->buffer.state().amount();case 2->buffer.capacity();case 3->mode;case 4->status;default->buffer.channel().ordinal();};}
    };}
    @Override public int getContainerSize(){return 2;}
    @Override protected NonNullList<ItemStack> getItems(){return items;}
    @Override protected void setItems(NonNullList<ItemStack> items){this.items=items;}
    @Override protected Component getDefaultName(){return getBlockState().getBlock().getName();}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return new dev.vic17usd.spacefabric.menu.TransportTankMenu(id,inventory,this);}
    public static boolean accepts(TransportChannel channel,ItemStack stack){
        var resource=TransportResource.of(stack);
        return resource!=TransportResource.NONE&&resource.channel==channel||stack.is(channel==TransportChannel.ENERGY?dev.vic17usd.spacefabric.colony.ColonyContent.EMPTY_POWER_CELL:dev.vic17usd.spacefabric.colony.ColonyContent.EMPTY_CANISTER)
                ||channel==TransportChannel.LIQUID&&stack.is(Items.BUCKET);
    }
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&accepts(buffer.channel(),stack);}
    @Override public int[] getSlotsForFace(Direction face){return face==Direction.DOWN?new int[]{1}:new int[]{0};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){return slot==1;}
    @Override protected void saveAdditional(ValueOutput output){super.saveAdditional(output);ContainerHelper.saveAllItems(output,items);output.store(TransportTankItem.STORED,TransportBuffer.State.CODEC,buffer.state());output.putInt("Mode",mode);}
    @Override protected void loadAdditional(ValueInput input){super.loadAdditional(input);items=NonNullList.withSize(2,ItemStack.EMPTY);ContainerHelper.loadAllItems(input,items);buffer.restore(input.read(TransportTankItem.STORED,TransportBuffer.State.CODEC).orElse(TransportBuffer.State.EMPTY));mode=Math.clamp(input.getIntOr("Mode",0),0,1);status=0;}
}

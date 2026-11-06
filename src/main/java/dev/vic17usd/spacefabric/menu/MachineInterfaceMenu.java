package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.transport.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class MachineInterfaceMenu extends AbstractContainerMenu {
    private final Container storage;private final ContainerData data;private final MachineInterfaceBlockEntity port;
    public MachineInterfaceMenu(int id,Inventory inventory){this(id,inventory,null,new SimpleContainer(2),new SimpleContainerData(5));}
    public MachineInterfaceMenu(int id,Inventory inventory,MachineInterfaceBlockEntity port){this(id,inventory,port,port,port.data());}
    private MachineInterfaceMenu(int id,Inventory inventory,MachineInterfaceBlockEntity port,Container storage,ContainerData data) {
        super(ModMenus.MACHINE_INTERFACE,id);this.storage=storage;this.port=port;this.data=data;
        for(int i=0;i<2;i++){final int index=i;addSlot(new Slot(storage,i,12+i*20,64){@Override public boolean mayPlace(ItemStack stack){return stack.is(index==0?ColonyContent.EMPTY_CANISTER:ColonyContent.EMPTY_POWER_CELL);}});}
        addStandardInventorySlots(inventory,44,146);addDataSlots(data);
    }
    public int value(int i){return data.get(i);}
    public ColonyRecipes.Formula formula(){return value(2)>=0&&value(2)<ColonyRecipes.ALL.size()?ColonyRecipes.ALL.get(value(2)):null;}
    public MachineInterfaceBlockEntity.Status status(){return MachineInterfaceBlockEntity.Status.values()[Math.clamp(value(3),0,MachineInterfaceBlockEntity.Status.values().length-1)];}
    @Override public boolean stillValid(Player player){return storage.stillValid(player);}
    @Override public boolean clickMenuButton(Player player,int button) {
        if(port==null||!stillValid(player)||!player.mayBuild()||!player.level().mayInteract(player,port.getBlockPos()))return false;
        if(button==0||button==1){port.select(button==0?-1:1);return true;}if(button==2){port.toggle();return true;}return false;
    }
    @Override public ItemStack quickMoveStack(Player player,int i) {
        if(i<0||i>=slots.size())return ItemStack.EMPTY;var slot=slots.get(i);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();boolean moved=false;
        if(i<2)moved=moveItemStackTo(stack,2,slots.size(),true);
        else if(stack.is(ColonyContent.EMPTY_CANISTER))moved=moveItemStackTo(stack,0,1,false);
        else if(stack.is(ColonyContent.EMPTY_POWER_CELL))moved=moveItemStackTo(stack,1,2,false);
        if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
    }
}

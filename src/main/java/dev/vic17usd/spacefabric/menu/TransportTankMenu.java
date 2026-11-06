package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.transport.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class TransportTankMenu extends AbstractContainerMenu {
    private final Container storage;private final ContainerData data;
    private final TransportTankBlockEntity tank;
    public TransportTankMenu(int id,Inventory inventory){this(id,inventory,null,new SimpleContainer(2),new SimpleContainerData(6));}
    public TransportTankMenu(int id,Inventory inventory,TransportTankBlockEntity tank){this(id,inventory,tank,tank,tank.data());}
    private TransportTankMenu(int id,Inventory inventory,TransportTankBlockEntity tank,Container storage,ContainerData data) {
        super(ModMenus.TRANSPORT_TANK,id);this.storage=storage;this.data=data;this.tank=tank;
        addSlot(new Slot(storage,0,12,54){@Override public boolean mayPlace(ItemStack stack){return TransportTankBlockEntity.accepts(TransportChannel.values()[Math.clamp(data.get(5),0,2)],stack);}});
        addSlot(new Slot(storage,1,62,54){@Override public boolean mayPlace(ItemStack stack){return false;}});
        addStandardInventorySlots(inventory,44,146);addDataSlots(data);
    }
    public int value(int i){return data.get(i);}
    public TransportResource resource(){return TransportResource.values()[Math.clamp(value(0),0,TransportResource.values().length-1)];}
    @Override public boolean stillValid(Player player){return storage.stillValid(player);}
    @Override public boolean clickMenuButton(Player player,int button) {
        if(tank==null||!stillValid(player)||!player.mayBuild()||!player.level().mayInteract(player,tank.getBlockPos()))return false;
        if(button==0){tank.toggleMode();return true;}return button==1&&tank.cycleResource();
    }
    @Override public ItemStack quickMoveStack(Player player,int i) {
        if(i<0||i>=slots.size())return ItemStack.EMPTY;var slot=slots.get(i);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();boolean moved=i<2?moveItemStackTo(stack,2,slots.size(),true):storage.canPlaceItem(0,stack)&&moveItemStackTo(stack,0,1,false);
        if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
    }
}

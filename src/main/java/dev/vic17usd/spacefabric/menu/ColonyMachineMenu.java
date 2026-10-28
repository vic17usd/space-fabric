package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.colony.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Processing machines retain nine saved slots; maintenance has its own native data fields. */
public final class ColonyMachineMenu extends AbstractContainerMenu {
    private final Container storage;
    private final ContainerData data;
    public ColonyMachineMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(9),new SimpleContainerData(8));}
    public ColonyMachineMenu(int id,Inventory inventory,Container storage,ContainerData data) {
        super(ModMenus.COLONY_MACHINE,id);this.storage=storage;this.data=data;
        checkContainerSize(storage,9);checkContainerDataCount(data,8);
        for(int i=0;i<9;i++) {
            final int index=i;
            addSlot(new Slot(storage,i,12+i%3*18,48+i/3*18) {
                @Override public boolean mayPlace(ItemStack stack){return ColonyRecipes.accepts(kind(),index,stack);}
            });
        }
        addStandardInventorySlots(inventory,44,134);addDataSlots(data);
    }
    public ColonyContent.Machine kind(){return ColonyContent.Machine.values()[Math.clamp(data.get(7),0,ColonyContent.Machine.values().length-1)];}
    public int progress(){return data.get(0);}
    public int duration(){return data.get(1);}
    public int status(){return data.get(2);}
    public int integrity(){return data.get(3);}
    public int coating(){return data.get(4);}
    public int filter(){return data.get(5);}
    public int risk(){return data.get(6);}
    @Override public boolean stillValid(Player player){return storage.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0||index>=slots.size())return ItemStack.EMPTY;
        var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();boolean moved=false;
        if(index<9)moved=moveItemStackTo(stack,9,slots.size(),true);
        else for(int i=0;i<3;i++)if(ColonyRecipes.accepts(kind(),i,stack)){moved=moveItemStackTo(stack,i,i+1,false);break;}
        if(!moved)return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
    }
}

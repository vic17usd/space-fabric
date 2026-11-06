package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.equipment.SuitEquipment;
import dev.vic17usd.spacefabric.equipment.SuitProtection;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class SuitMenu extends AbstractContainerMenu {
    private final Container equipment;
    public SuitMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(4));}
    public SuitMenu(int id,Inventory inventory,Container equipment) {
        super(ModMenus.SUIT,id);this.equipment=equipment;
        for(int i=0;i<4;i++) {
            final int part=i;
            addSlot(new Slot(equipment,i,16+i*40,49) {
                @Override public boolean mayPlace(ItemStack stack){return SuitProtection.accepts(part,stack);}
                @Override public int getMaxStackSize(){return 1;}
                @Override public boolean mayPickup(Player player){return SuitEquipment.removable(player,getItem());}
            });
        }
        addStandardInventorySlots(inventory,8,102);
    }
    public dev.vic17usd.spacefabric.equipment.SuitTier suitTier(){
        return SuitProtection.tier(equipment.getItem(0),equipment.getItem(1),equipment.getItem(2),equipment.getItem(3));
    }
    @Override public boolean stillValid(Player player){return equipment.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0||index>=slots.size())return ItemStack.EMPTY;
        var slot=slots.get(index);if(!slot.hasItem()||!slot.mayPickup(player))return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();boolean moved;
        if(index<4)moved=moveItemStackTo(stack,4,slots.size(),true);
        else {
            int part=SuitProtection.slot(stack);if(part<0||slots.get(part).hasItem())return ItemStack.EMPTY;
            moved=moveItemStackTo(stack,part,part+1,false);
        }
        if(!moved)return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();
        slot.onTake(player,stack);return copy;
    }
}

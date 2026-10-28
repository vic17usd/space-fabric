package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.machine.RefineryProcess;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class RefineryMenu extends AbstractContainerMenu {
    private final Container storage;
    private final ContainerData data;
    public RefineryMenu(int id, Inventory inventory) { this(id, inventory, new SimpleContainer(4), new SimpleContainerData(6)); }
    public RefineryMenu(int id, Inventory inventory, Container storage, ContainerData data) {
        super(ModMenus.REFINERY, id); this.storage = storage; this.data = data;
        checkContainerSize(storage, 4); checkContainerDataCount(data, 6);
        for (int i = 0; i < 4; i++) {
            final int slot = i;
            addSlot(new Slot(storage, slot, 26 + i * 44, 45) {
                @Override public boolean mayPlace(ItemStack stack) { return RefineryProcess.accepts(slot, stack); }
            });
        }
        addStandardInventorySlots(inventory, 28, 104); addDataSlots(data);
    }
    public int integrity(){return data.get(2);}
    public int coating(){return data.get(3);}
    public int filter(){return data.get(4);}
    public int risk(){return data.get(5);}
    public int progress() { return data.get(0); }
    public int duration() { return data.get(1); }
    @Override public boolean stillValid(Player player) { return storage.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var copy = stack.copy(); boolean moved = false;
        if (index < 4) moved = moveItemStackTo(stack, 4, slots.size(), true);
        else for (int i = 0; i < 3; i++) if (RefineryProcess.accepts(i, stack)) { moved = moveItemStackTo(stack, i, i + 1, false); break; }
        if (!moved) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack); return copy;
    }
}

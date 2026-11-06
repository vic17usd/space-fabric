package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.recovery.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class FoundryMenu extends AbstractContainerMenu {
    private final FoundryConsoleBlockEntity console;
    private final ContainerData data;
    public FoundryMenu(int id,Inventory inventory){this(id,inventory,null,new SimpleContainerData(6));}
    public FoundryMenu(int id,Inventory inventory,FoundryConsoleBlockEntity console){this(id,inventory,console,console.data());}
    private FoundryMenu(int id,Inventory inventory,FoundryConsoleBlockEntity console,ContainerData data) {
        super(ModMenus.FOUNDRY,id);this.console=console;this.data=data;addStandardInventorySlots(inventory,62,146);addDataSlots(data);
    }
    public int value(int i){return data.get(i);}
    @Override public boolean stillValid(Player player){return console==null||!console.isRemoved()&&player.level()==console.getLevel()&&player.distanceToSqr(console.getBlockPos().getCenter())<=64;}
    @Override public boolean clickMenuButton(Player player,int button){return button==0&&console!=null&&stillValid(player)&&player instanceof ServerPlayer server&&console.activate(server);}
    @Override public ItemStack quickMoveStack(Player player,int i) {
        if(i<0||i>=slots.size())return ItemStack.EMPTY;var slot=slots.get(i);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();
        if(!moveItemStackTo(stack,i<27?27:0,i<27?36:27,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
}

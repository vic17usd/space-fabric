package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.rover.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class RoverMenu extends AbstractContainerMenu {
    private final RoverEntity rover;private final ContainerData data;
    public RoverMenu(int id,Inventory inventory){this(id,inventory,null,new SimpleContainer(14),new SimpleContainerData(4));}
    public RoverMenu(int id,Inventory inventory,RoverEntity rover){this(id,inventory,rover,rover.inventory(),new ContainerData(){
        public int getCount(){return 4;}public void set(int i,int value){ }
        public int get(int i){var s=rover.systems();return switch(i){case 0->s.energy();case 1->s.oxygen();case 2->s.coolant();default->s.hull();};}
    });}
    private RoverMenu(int id,Inventory inventory,RoverEntity rover,Container storage,ContainerData data) {
        super(ModMenus.ROVER,id);this.rover=rover;this.data=data;
        for(int i=0;i<3;i++){final int index=i;addSlot(new Slot(storage,i,12+i*18,47){@Override public boolean mayPlace(ItemStack stack){return RoverEntity.input(index,stack);}});}
        for(int i=0;i<9;i++)addSlot(new Slot(storage,i+3,12+i%3*18,83+i/3*18));
        for(int i=12;i<14;i++)addSlot(new Slot(storage,i,84+(i-12)*18,47){@Override public boolean mayPlace(ItemStack stack){return false;}});
        addStandardInventorySlots(inventory,44,146);addDataSlots(data);
    }
    public int value(int i){return data.get(i);}
    @Override public boolean stillValid(Player player){return rover==null||!rover.isRemoved()&&player.isAlive()&&!player.isSpectator()&&player.level()==rover.level()&&player.distanceToSqr(rover)<=64;}
    @Override public boolean clickMenuButton(Player player,int button){if(button!=0||rover==null||!stillValid(player)||!(player instanceof net.minecraft.server.level.ServerPlayer server)||!rover.board(player))return false;server.closeContainer();return true;}
    @Override public ItemStack quickMoveStack(Player player,int i) {
        if(i<0||i>=slots.size())return ItemStack.EMPTY;var slot=slots.get(i);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();boolean moved=false;
        if(i<14)moved=moveItemStackTo(stack,14,slots.size(),true);
        else {for(int s=0;s<3;s++)if(RoverEntity.input(s,stack)){moved=moveItemStackTo(stack,s,s+1,false);break;}if(!moved)moved=moveItemStackTo(stack,3,12,false);}
        if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
    }
}

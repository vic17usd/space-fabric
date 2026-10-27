package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.colony.*;
import java.util.function.Supplier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Same nine saved slots as 0.29; only the presentation changes. Native data sync is server-owned. */
public final class HabitatMenu extends AbstractContainerMenu {
    private final Container storage;
    private final ContainerData data;
    public HabitatMenu(int id,Inventory inventory) { this(id,inventory,new SimpleContainer(9),new SimpleContainerData(6)); }
    public HabitatMenu(int id,Inventory inventory,Container storage,ContainerData data) {
        super(ModMenus.HABITAT,id);this.storage=storage;this.data=data;
        checkContainerSize(storage,9);checkContainerDataCount(data,6);
        for(int i=0;i<9;i++) {
            final int index=i;
            addSlot(new Slot(storage,i,12+(i%3)*18,48+(i/3)*18) {
                @Override public boolean mayPlace(ItemStack stack){return ColonyRecipes.accepts(ColonyContent.Machine.HABITAT,index,stack);}
            });
        }
        addStandardInventorySlots(inventory,44,134);addDataSlots(data);
    }
    public static ContainerData data(Supplier<HabitatReadings> source) {
        return new ContainerData() {
            public int getCount(){return 6;}
            public void set(int index,int value) { }
            public int get(int index) {
                var r=source.get();
                return switch(index){case 0->r.condition().ordinal();case 1->r.oxygen();case 2->r.energy();case 3->r.coolant();case 4->r.volume();case 5->r.venus()?1:0;default->1;};
            }
        };
    }
    public HabitatReadings readings() {
        int id=data.get(0);var conditions=HabitatReadings.Condition.values();
        return new HabitatReadings(id>=0&&id<conditions.length?conditions[id]:HabitatReadings.Condition.IDLE,data.get(1),data.get(2),data.get(3),data.get(4),data.get(5)!=0);
    }
    @Override public boolean stillValid(Player player){return storage.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0||index>=slots.size())return ItemStack.EMPTY;
        var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();boolean moved=false;
        if(index<9)moved=moveItemStackTo(stack,9,slots.size(),true);
        else for(int i=0;i<3;i++)if(ColonyRecipes.accepts(ColonyContent.Machine.HABITAT,i,stack)){moved=moveItemStackTo(stack,i,i+1,false);break;}
        if(!moved)return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();
        slot.onTake(player,stack);return copy;
    }
}

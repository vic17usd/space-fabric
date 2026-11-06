package dev.vic17usd.spacefabric.equipment;

import java.util.ArrayList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Live view of the attachment, not a second inventory; death/respawn cannot leave stale copies. */
public final class SuitContainer implements Container {
    private final java.util.function.Supplier<SuitLoadout> source;
    private final java.util.function.Consumer<SuitLoadout> sink;
    private final java.util.function.Predicate<Player> valid;
    public SuitContainer(Player owner){this(()->SuitEquipment.get(owner),value->SuitEquipment.set(owner,value),p->p==owner&&p.isAlive()&&!p.isSpectator());}
    public SuitContainer(java.util.function.Supplier<SuitLoadout> source,java.util.function.Consumer<SuitLoadout> sink,java.util.function.Predicate<Player> valid) {
        this.source=source;this.sink=sink;this.valid=valid;
    }
    @Override public int getContainerSize(){return 4;}
    @Override public int getMaxStackSize(){return 1;}
    @Override public boolean isEmpty(){return source.get().items().stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return source.get().items().get(slot);}
    @Override public ItemStack removeItem(int slot,int count){
        var items=new ArrayList<>(source.get().items());var result=ContainerHelper.removeItem(items,slot,count);
        sink.accept(new SuitLoadout(items));return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot){return removeItem(slot,1);}
    @Override public void setItem(int slot,ItemStack stack){
        if(!stack.isEmpty()&&(!canPlaceItem(slot,stack)||stack.getCount()>1))throw new IllegalArgumentException("Invalid suit slot item");
        var items=new ArrayList<>(source.get().items());items.set(slot,stack);sink.accept(new SuitLoadout(items));
    }
    @Override public void setChanged(){sink.accept(source.get().snapshot());}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return SuitProtection.accepts(slot,stack);}
    @Override public boolean stillValid(Player player){return valid.test(player);}
    @Override public void clearContent(){sink.accept(SuitLoadout.empty());}
}

package dev.vic17usd.spacefabric.mixin;

import dev.vic17usd.spacefabric.equipment.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

/** Both logical sides use the same real slots and vanilla inventory click packets. */
@Mixin(value=InventoryMenu.class,remap=false)
public abstract class InventorySuitMixin extends AbstractContainerMenu {
    protected InventorySuitMixin(){super(null,0);}
    @ModifyArg(method="<init>",at=@At(value="INVOKE",target="Lnet/minecraft/world/inventory/InventoryMenu$1;<init>(Lnet/minecraft/world/inventory/InventoryMenu;Lnet/minecraft/world/Container;IIILnet/minecraft/world/entity/player/Player;)V"),index=3)
    private int spaceFabric$offhandPosition(int original){return InventorySuitSlots.OFFHAND_X;}
    @Inject(method="<init>",at=@At("TAIL"))
    private void spaceFabric$appendSuitSlots(Inventory inventory,boolean active,Player owner,CallbackInfo callback) {
        var container=new SuitContainer(owner);
        for(int part=0;part<4;part++)addSlot(InventorySuitSlots.create(container,part));
    }
    @Inject(method="quickMoveStack",at=@At("HEAD"),cancellable=true)
    private void spaceFabric$shiftSuit(Player player,int index,CallbackInfoReturnable<ItemStack> callback) {
        if(index<9||index>=InventorySuitSlots.END)return;
        var source=slots.get(index);
        int part=SuitProtection.slot(source.getItem());
        boolean fromSuit=index>=InventorySuitSlots.START;
        if(!fromSuit&&(index>=45||part<0))return;
        // An occupied dedicated slot must not send another suit into the combat armor slots.
        if(!fromSuit&&slots.get(InventorySuitSlots.START+part).hasItem()){callback.setReturnValue(ItemStack.EMPTY);return;}
        if(!source.hasItem()||!source.mayPickup(player)){callback.setReturnValue(ItemStack.EMPTY);return;}
        var stack=source.getItem();var copy=stack.copy();
        boolean moved=fromSuit?moveItemStackTo(stack,9,45,true)
            :moveItemStackTo(stack,InventorySuitSlots.START+part,InventorySuitSlots.START+part+1,false);
        if(!moved){callback.setReturnValue(ItemStack.EMPTY);return;}
        if(stack.isEmpty())source.setByPlayer(ItemStack.EMPTY);else source.setChanged();
        source.onTake(player,stack);callback.setReturnValue(copy);
    }
}

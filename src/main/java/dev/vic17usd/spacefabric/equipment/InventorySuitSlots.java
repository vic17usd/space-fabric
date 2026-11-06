package dev.vic17usd.spacefabric.equipment;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Appended IDs preserve every vanilla inventory/crafting/armor index. */
public final class InventorySuitSlots {
    public static final int START=46,END=50,X=77,Y=8,OFFHAND_X=98,OFFHAND_Y=62;
    private static final Identifier[] ICONS={InventoryMenu.EMPTY_ARMOR_SLOT_HELMET,
        InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS};
    private InventorySuitSlots() { }
    public static Slot create(Container container,int part) {
        return new Slot(container,part,X,Y+part*18) {
            @Override public boolean mayPlace(ItemStack stack){return SuitProtection.accepts(part,stack);}
            @Override public int getMaxStackSize(){return 1;}
            @Override public boolean mayPickup(Player player){return SuitEquipment.removable(player,getItem());}
            @Override public Identifier getNoItemIcon(){return ICONS[part];}
        };
    }
}

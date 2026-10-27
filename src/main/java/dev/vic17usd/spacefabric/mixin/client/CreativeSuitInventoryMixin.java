package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.equipment.InventorySuitSlots;
import net.minecraft.core.NonNullList;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/** Creative's vanilla packet accepts only IDs 0..45; use J for its dedicated suit menu. */
@Mixin(value=CreativeModeInventoryScreen.class,remap=false)
public abstract class CreativeSuitInventoryMixin {
    @Redirect(method={"selectTab","slotClicked"},at=@At(value="INVOKE",target="Lnet/minecraft/core/NonNullList;size()I"))
    private int spaceFabric$vanillaCreativeSlots(NonNullList<?> slots) {
        return Math.min(slots.size(),InventorySuitSlots.START);
    }
}

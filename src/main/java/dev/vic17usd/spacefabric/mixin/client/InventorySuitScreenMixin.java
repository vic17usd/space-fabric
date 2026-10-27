package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.equipment.InventorySuitSlots;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=InventoryScreen.class,remap=false)
public abstract class InventorySuitScreenMixin extends AbstractContainerScreen<InventoryMenu> {
    protected InventorySuitScreenMixin(InventoryMenu menu,Inventory inventory,Component title){super(menu,inventory,title);}
    @Inject(method="getRecipeBookButtonPosition",at=@At("HEAD"),cancellable=true)
    private void spaceFabric$bookPosition(CallbackInfoReturnable<ScreenPosition> callback) {
        callback.setReturnValue(new ScreenPosition(leftPos+149,topPos+61));
    }
    @Inject(method="extractBackground",at=@At("TAIL"))
    private void spaceFabric$slotFrames(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta,CallbackInfo callback) {
        for(int part=0;part<4;part++)spaceFabric$frame(gui,InventorySuitSlots.X,InventorySuitSlots.Y+part*18);
        spaceFabric$frame(gui,InventorySuitSlots.OFFHAND_X,InventorySuitSlots.OFFHAND_Y);
    }
    private void spaceFabric$frame(GuiGraphicsExtractor gui,int x,int y) {
        x+=leftPos;y+=topPos;
        gui.fill(x-1,y-1,x+17,y+17,0xFF373737);
        gui.fill(x,y,x+17,y+17,0xFFFFFFFF);
        gui.fill(x,y,x+16,y+16,0xFF8B8B8B);
    }
}

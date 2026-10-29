package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.FoundryMenu;
import dev.vic17usd.spacefabric.recovery.FoundryConsoleBlockEntity.Status;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class FoundryScreen extends SpaceContainerScreen<FoundryMenu> {
    private Button activate;
    public FoundryScreen(FoundryMenu menu,Inventory inventory,Component title){super(menu,inventory,title,300,232);}
    @Override protected void init(){super.init();activate=addRenderableWidget(Button.builder(Component.translatable("foundry.space_fabric.activate"),b->send(0)).bounds(leftPos+8,topPos+114,284,18).build());}
    @Override protected void extractLabels(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        gui.text(font,title,8,7,0xFFE5F5FF);
        String site=menu.value(3)==0?"no_site":menu.value(4)==1?"restored":"objective";
        gui.text(font,Component.translatable("foundry.space_fabric."+site),8,21,0xFF65E5BE);
        for(int i=0;i<3;i++) {
            int status=Math.clamp(menu.value(i),0,Status.values().length-1);
            gui.text(font,Component.translatable("foundry.space_fabric.unit_"+i),8,38+i*24,0xFFE5F5FF);
            gui.text(font,Component.translatable("foundry.space_fabric.status_"+Status.values()[status].name().toLowerCase(java.util.Locale.ROOT)),8,49+i*24,status==Status.READY.ordinal()?0xFF65E5BE:0xFFFFCD69);
        }
        activate.active=menu.value(3)==1&&menu.value(4)==0&&menu.value(5)==1;
        gui.text(font,playerInventoryTitle,62,134,0xFFB5D6E5);
    }
}

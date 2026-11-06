package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.RoverMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class RoverScreen extends SpaceContainerScreen<RoverMenu> {
    public RoverScreen(RoverMenu menu,Inventory inventory,Component title){super(menu,inventory,title,248,232);}
    @Override protected void init(){super.init();addRenderableWidget(Button.builder(Component.translatable("rover.space_fabric.board"),b->send(0)).bounds(leftPos+176,topPos+113,62,18).build());}
    @Override protected void extractLabels(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        gui.text(font,title,8,7,0xFFE5F5FF);
        gui.text(font,Component.translatable("rover.space_fabric.slots"),12,32,0xFFB5D6E5);
        gui.text(font,Component.translatable("rover.space_fabric.cargo"),12,69,0xFFB5D6E5);
        for(int i=0;i<3;i++)gui.text(font,Component.translatable("rover.space_fabric.reserve_"+i,menu.value(i)),82,82+i*11,0xFF65E5BE);
        gui.text(font,Component.translatable("maintenance.space_fabric.short_integrity",menu.value(3)),82,117,0xFFFFCD69);
        gui.text(font,playerInventoryTitle,44,134,0xFFB5D6E5);
    }
    @Override protected void extractTooltip(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        super.extractTooltip(gui,mouseX,mouseY);
        if(hoveredSlot!=null&&!hoveredSlot.hasItem()&&hoveredSlot.index<3)
            gui.setTooltipForNextFrame(font,Component.translatable("rover.space_fabric.slot_"+hoveredSlot.index),mouseX,mouseY);
    }
}

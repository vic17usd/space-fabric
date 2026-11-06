package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.TransportTankMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class TransportTankScreen extends SpaceContainerScreen<TransportTankMenu> {
    private Button mode,filter;
    public TransportTankScreen(TransportTankMenu menu,Inventory inventory,Component title){super(menu,inventory,title,248,232);}
    @Override protected void init(){super.init();mode=addRenderableWidget(Button.builder(Component.empty(),b->send(0)).bounds(leftPos+8,topPos+92,232,18).build());
        filter=addRenderableWidget(Button.builder(Component.translatable("transport.space_fabric.select_content"),b->send(1)).bounds(leftPos+8,topPos+112,232,18).build());}
    @Override public void extractBackground(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta) {
        super.extractBackground(gui,mouseX,mouseY,delta);gui.fill(leftPos+92,topPos+64,leftPos+238,topPos+70,0xFF07121C);
        int width=menu.value(2)<=0?0:Math.clamp(menu.value(1)*146/menu.value(2),0,146);gui.fill(leftPos+92,topPos+64,leftPos+92+width,topPos+70,0xFF65D5E5);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        gui.text(font,title,8,7,0xFFE5F5FF);gui.text(font,Component.translatable(menu.resource().key()),8,22,0xFF65E5BE);
        gui.text(font,"→",40,58,0xFFE5F5FF);gui.text(font,menu.value(1)+" / "+menu.value(2),92,49,0xFFB5D6E5);
        gui.text(font,Component.translatable("transport.space_fabric.tank_status_"+menu.value(4)),8,78,0xFFB5D6E5);
        mode.setMessage(Component.translatable(menu.value(3)==0?"transport.space_fabric.drain_container":"transport.space_fabric.fill_container"));
        filter.active=menu.value(1)==0&&menu.value(5)!=2;
        gui.text(font,playerInventoryTitle,44,134,0xFFB5D6E5);
    }
}

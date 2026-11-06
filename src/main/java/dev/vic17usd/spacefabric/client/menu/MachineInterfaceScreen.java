package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.MachineInterfaceMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MachineInterfaceScreen extends SpaceContainerScreen<MachineInterfaceMenu> {
    private Button toggle,previous,next;
    public MachineInterfaceScreen(MachineInterfaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,248,232);}
    @Override protected void init(){super.init();
        previous=addRenderableWidget(Button.builder(Component.literal("<"),b->send(0)).bounds(leftPos+8,topPos+94,24,18).build());
        next=addRenderableWidget(Button.builder(Component.literal(">"),b->send(1)).bounds(leftPos+34,topPos+94,24,18).build());
        toggle=addRenderableWidget(Button.builder(Component.empty(),b->send(2)).bounds(leftPos+80,topPos+94,160,18).build());
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        gui.text(font,title,8,7,0xFFE5F5FF);
        gui.text(font,font.plainSubstrByWidth(Component.translatable("transport.space_fabric.port_"+menu.status().name().toLowerCase(java.util.Locale.ROOT)).getString(),232),8,22,0xFF65E5BE);
        gui.text(font,Component.translatable(menu.value(0)==0?"transport.space_fabric.recipe_auto":"transport.space_fabric.recipe_fixed"),8,37,0xFFB5D6E5);
        gui.text(font,Component.translatable("transport.space_fabric.return_reserve"),12,51,0xFFB5D6E5);
        var formula=menu.formula();
        if(formula!=null) {
            for(int i=0;i<formula.inputs().size();i++){var stack=formula.inputs().get(i).stack();gui.item(stack,104+i*19,64);gui.itemDecorations(font,stack,104+i*19,64);}
            gui.text(font,"→",165,68,0xFFE5F5FF);gui.item(formula.outputs().getFirst().stack(),180,64);
        } else if(menu.value(1)==0)gui.text(font,Component.translatable("transport.space_fabric.life_priority"),82,68,0xFFB5D6E5);
        toggle.setMessage(Component.translatable(menu.value(4)==1?"transport.space_fabric.pause":"transport.space_fabric.resume"));
        previous.active=next.active=menu.value(1)>0;
        gui.text(font,playerInventoryTitle,44,134,0xFFB5D6E5);
    }
    @Override protected void extractTooltip(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        super.extractTooltip(gui,mouseX,mouseY);
        if(hoveredSlot!=null&&!hoveredSlot.hasItem()&&hoveredSlot.index<2)gui.setTooltipForNextFrame(font,Component.translatable("transport.space_fabric.reserve_"+hoveredSlot.index),mouseX,mouseY);
        var formula=menu.formula();
        if(formula!=null&&mouseY>=topPos+64&&mouseY<topPos+80) {
            for(int i=0;i<formula.inputs().size();i++)if(mouseX>=leftPos+104+i*19&&mouseX<leftPos+120+i*19)
                gui.setTooltipForNextFrame(font,formula.inputs().get(i).stack().getHoverName(),mouseX,mouseY);
            if(mouseX>=leftPos+180&&mouseX<leftPos+196)gui.setTooltipForNextFrame(font,formula.outputs().getFirst().stack().getHoverName(),mouseX,mouseY);
        }
    }
}

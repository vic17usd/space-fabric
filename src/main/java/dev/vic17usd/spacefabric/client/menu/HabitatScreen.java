package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.client.colony.HabitatPresentation;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.menu.HabitatMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Native container, with no extra model/texture dependency. All readings come from menu data. */
public final class HabitatScreen extends SpaceContainerScreen<HabitatMenu> {
    public HabitatScreen(HabitatMenu menu,Inventory inventory,Component title){super(menu,inventory,title,248,220);}
    @Override public void extractBackground(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta) {
        super.extractBackground(gui,mouseX,mouseY,delta);var r=menu.readings();
        bar(gui,72,r.oxygen(),0xFF65E5BE);bar(gui,92,r.energy(),0xFFFFCD69);
        if(r.venus())bar(gui,112,r.coolant(),0xFF6EC9FF);
    }
    private void bar(GuiGraphicsExtractor gui,int y,int amount,int color) {
        gui.fill(leftPos+82,topPos+y,leftPos+238,topPos+y+4,0xFF07121C);
        gui.fill(leftPos+82,topPos+y,leftPos+82+(int)(156*HabitatReadings.fraction(amount)),topPos+y+4,color);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        var r=menu.readings();gui.text(font,title,8,7,0xFFE5F5FF);
        gui.text(font,HabitatPresentation.warning(r),8,21,HabitatPresentation.stateColor(r));
        gui.text(font,Component.translatable("gui.space_fabric.habitat.inputs"),12,36,0xFFB5D6E5);
        gui.text(font,HabitatPresentation.pressure(r),82,37,0xFFE5F5FF);
        gui.text(font,HabitatPresentation.temperature(r),82,48,0xFFE5F5FF);
        reserve(gui,"oxygen",r.oxygen(),62);reserve(gui,"energy",r.energy(),82);
        if(r.venus())reserve(gui,"coolant",r.coolant(),102);
        else gui.text(font,Component.translatable("gui.space_fabric.habitat.cooling_not_needed"),82,102,0xFFB5D6E5);
        gui.text(font,Component.translatable("gui.space_fabric.habitat.volume",r.volume()),12,108,0xFFB5D6E5);
        gui.text(font,playerInventoryTitle,44,122,0xFFB5D6E5);
    }
    @Override protected void extractTooltip(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        super.extractTooltip(gui,mouseX,mouseY);
        if(hoveredSlot!=null&&!hoveredSlot.hasItem()&&hoveredSlot.index<9)
            gui.setTooltipForNextFrame(font,Component.translatable("gui.space_fabric.habitat.slot_"+hoveredSlot.index),mouseX,mouseY);
    }
    private void reserve(GuiGraphicsExtractor gui,String key,int amount,int y) {
        gui.text(font,Component.translatable("gui.space_fabric.habitat.reserve_"+key,amount,LifeSupport.CAPACITY),82,y,0xFFB5D6E5);
    }
}

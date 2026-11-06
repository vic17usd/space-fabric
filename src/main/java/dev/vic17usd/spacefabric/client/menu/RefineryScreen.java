package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.RefineryMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class RefineryScreen extends SpaceContainerScreen<RefineryMenu> {
    public RefineryScreen(RefineryMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 220, 188); }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        super.extractBackground(gui, mouseX, mouseY, delta);
        gui.fill(leftPos + 28, topPos + 71, leftPos + 192, topPos + 76, 0xFF07121C);
        int width = menu.duration() <= 0 ? 0 : Math.clamp(menu.progress() * 164 / menu.duration(), 0, 164);
        gui.fill(leftPos + 28, topPos + 71, leftPos + 28 + width, topPos + 76, 0xFF45D5B0);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        gui.text(font, title, 8, 7, 0xFFE5F5FF);
        for (int i = 0; i < 4; i++) gui.text(font, Component.translatable("gui.space_fabric.refinery_slot_" + i), 26 + i * 44, 33, 0xFFB5D6E5);
        gui.text(font,MaintenancePresentation.integrity(menu.integrity()),8,20,MaintenancePresentation.color(menu.integrity()));
        if(menu.integrity()==0)gui.text(font,Component.translatable("colony.space_fabric.maintenance_failure"),8,81,0xFFFF8B78);
        else if(menu.integrity()<=5000)gui.text(font,Component.translatable("maintenance.space_fabric.half_speed"),8,81,0xFFFFCD69);
        gui.text(font, playerInventoryTitle, 28, 92, 0xFFB5D6E5);
    }
    @Override protected void extractTooltip(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        super.extractTooltip(gui,mouseX,mouseY);
        if(mouseX>=leftPos+8&&mouseX<leftPos+212&&mouseY>=topPos+18&&mouseY<topPos+30)
            gui.setComponentTooltipForNextFrame(font,MaintenancePresentation.tooltip(menu.integrity(),menu.coating(),menu.filter()),mouseX,mouseY);
    }
}

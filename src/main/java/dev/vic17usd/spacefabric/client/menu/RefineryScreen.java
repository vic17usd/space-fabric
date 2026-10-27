package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.RefineryMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class RefineryScreen extends SpaceContainerScreen<RefineryMenu> {
    public RefineryScreen(RefineryMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 166); }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        super.extractBackground(gui, mouseX, mouseY, delta);
        gui.fill(leftPos + 24, topPos + 62, leftPos + 152, topPos + 68, 0xFF07121C);
        int width = menu.duration() <= 0 ? 0 : Math.clamp(menu.progress() * 128 / menu.duration(), 0, 128);
        gui.fill(leftPos + 24, topPos + 62, leftPos + 24 + width, topPos + 68, 0xFF45D5B0);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        gui.text(font, title, 8, 7, 0xFFE5F5FF);
        for (int i = 0; i < 4; i++) gui.text(font, Component.translatable("gui.space_fabric.refinery_slot_" + i), 24 + i * 36, 23, 0xFFB5D6E5);
        gui.text(font, playerInventoryTitle, 8, 73, 0xFFB5D6E5);
    }
}

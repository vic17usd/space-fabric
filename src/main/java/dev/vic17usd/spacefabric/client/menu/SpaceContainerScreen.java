package dev.vic17usd.spacefabric.client.menu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/** Dibujo vectorial: los paneles no dependen de una textura externa de GUI. */
abstract class SpaceContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    SpaceContainerScreen(T menu, Inventory inventory, Component title, int width, int height) { super(menu, inventory, title, width, height); }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        super.extractBackground(gui, mouseX, mouseY, delta);
        gui.fill(leftPos - 1, topPos - 1, leftPos + imageWidth + 1, topPos + imageHeight + 1, 0xFF6C93A7);
        gui.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF101E2B);
        for (var slot : menu.slots) {
            int x = leftPos + slot.x, y = topPos + slot.y;
            gui.fill(x - 1, y - 1, x + 17, y + 17, 0xFF507082);
            gui.fill(x, y, x + 16, y + 16, 0xFF07121C);
        }
    }
    protected void send(int button) {
        if (minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
    }
}

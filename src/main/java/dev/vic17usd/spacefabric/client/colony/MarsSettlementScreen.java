package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.colony.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Native-font, paged expedition instructions. Only server snapshots indicate completion. */
public final class MarsSettlementScreen extends Screen {
    private final Screen previous;
    private MarsSettlementNetwork.State state;
    private int page, lineOffset;
    private int left, top, panelWidth, panelHeight;
    public MarsSettlementScreen(Screen previous, MarsSettlementNetwork.State state) {
        super(MarsSettlementExpedition.text("title")); this.previous = previous; this.state = state;
    }
    public void accept(MarsSettlementNetwork.State state) { this.state = state; }
    @Override protected void init() {
        panelWidth = Math.min(620, width - 12); panelHeight = Math.min(370, height - 12);
        left = (width - panelWidth) / 2; top = (height - panelHeight) / 2;
        int bw = (panelWidth - 28) / 3;
        button("previous", 10, panelHeight - 54, bw, () -> change(-1));
        button("recipes", 14 + bw, panelHeight - 54, bw, () -> minecraft.setScreen(new dev.vic17usd.spacefabric.client.recipe.RecipeViewerScreen(this)));
        button("next_page", 18 + bw * 2, panelHeight - 54, bw, () -> change(1));
        String[] actions = {"buscar", "investigar", "analizar", "entregar"};
        int aw = (panelWidth - 32) / 4;
        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            button(action, 10 + i * (aw + 4), panelHeight - 30, aw, () -> {
                minecraft.setScreen(null);
                if (minecraft.player != null) minecraft.player.connection.sendCommand("marte " + action);
            });
        }
        addRenderableWidget(Button.builder(Component.literal("×"), b -> onClose()).bounds(left + panelWidth - 28, top + 6, 18, 18).build());
    }
    private void button(String key, int x, int y, int width, Runnable action) {
        addRenderableWidget(Button.builder(MarsSettlementExpedition.text(key), b -> action.run()).bounds(left + x, top + y, width, 18).build());
    }
    private void change(int direction) { page = Math.floorMod(page + direction, 6); lineOffset = 0; }
    @Override public void onClose() { minecraft.setScreen(previous); }
    @Override public boolean isPauseScreen() { return true; }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int x, int y, float delta) { }
    @Override public boolean keyPressed(KeyEvent event) {
        switch (event.key()) {
            case GLFW.GLFW_KEY_LEFT -> { change(-1); return true; }
            case GLFW.GLFW_KEY_RIGHT -> { change(1); return true; }
            case GLFW.GLFW_KEY_DOWN -> { lineOffset++; return true; }
            case GLFW.GLFW_KEY_UP -> { lineOffset = Math.max(0, lineOffset - 1); return true; }
        }
        return super.keyPressed(event);
    }
    @Override public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        gui.fill(0, 0, width, height, 0xCC080D12);
        gui.fill(left, top, left + panelWidth, top + panelHeight, 0xFF101820);
        gui.text(font, title, left + 10, top + 12, 0xFFE5A078);
        gui.text(font, MarsSettlementExpedition.text(state.unlocked() ? "server_stage" : "locked_short", state.stage(), 5), left + 10, top + 32, 0xFF8DD7AF);
        var coordinate = state.point().or(() -> state.site()).map(p -> p.toShortString()).orElse("—");
        gui.text(font, MarsSettlementExpedition.text("coordinates", coordinate), left + 10, top + 47, 0xFFABBAC5);
        gui.text(font, MarsSettlementExpedition.text("page", page + 1, 6), left + 10, top + 64, 0xFFE5A078);
        var lines = font.split(MarsSettlementExpedition.text("help_" + page), panelWidth - 24);
        int capacity = Math.max(1, (panelHeight - 140) / 11);
        lineOffset = Math.clamp(lineOffset, 0, Math.max(0, lines.size() - capacity));
        for (int i = 0; i < capacity && i + lineOffset < lines.size(); i++) gui.text(font, lines.get(i + lineOffset), left + 10, top + 81 + i * 11, 0xFFF0EBE4);
        if (lines.size() > capacity) gui.text(font, Component.literal("↑ ↓"), left + panelWidth - 36, top + panelHeight - 69, 0xFF7DCDE5);
        super.extractRenderState(gui, mouseX, mouseY, delta);
    }
}

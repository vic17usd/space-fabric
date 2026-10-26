package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.RocketMenu;
import dev.vic17usd.spacefabric.space.RocketRules;
import dev.vic17usd.spacefabric.space.RocketVariant;
import dev.vic17usd.spacefabric.space.TravelDestination;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public final class RocketScreen extends SpaceContainerScreen<RocketMenu> {
    private final List<Button> destinations = new ArrayList<>();
    private Button launch, board;
    private static final int[] COLORS = {0xFF69C8A8, 0xFFB6ADA2, 0xFFECC56F, 0xFF448EE0, 0xFFD6DBE1,
            0xFFE66B3C, 0xFFE1AC79, 0xFFEACD91, 0xFF7FE1DF, 0xFF507FE8};
    public RocketScreen(RocketMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 320, 244); }
    @Override protected void init() {
        super.init(); destinations.clear();
        for (int i = 0; i < TravelDestination.ALL.size(); i++) {
            final int destination = i;
            destinations.add(addRenderableWidget(Button.builder(TravelDestination.at(i).label(), b -> send(destination))
                    .bounds(leftPos + 8 + (i % 2) * 75, topPos + 22 + (i / 2) * 16, 72, 14).build()));
        }
        board = addRenderableWidget(Button.builder(Component.translatable("gui.space_fabric.board"), b -> send(RocketMenu.BOARD))
                .bounds(leftPos + 164, topPos + 102, 68, 18).build());
        launch = addRenderableWidget(Button.builder(Component.translatable("gui.space_fabric.launch"), b -> send(RocketMenu.LAUNCH))
                .bounds(leftPos + 238, topPos + 102, 74, 18).build());
        updateButtons();
    }
    private void updateButtons() {
        boolean idle = menu.value(2) == RocketRules.IDLE, pilot = menu.value(4) == 1;
        for (int i = 0; i < destinations.size(); i++) {
            var button = destinations.get(i);
            button.active = idle && pilot && i != menu.value(5) && RocketVariant.byId(menu.value(6)).permits(TravelDestination.at(i).planet(),menu.value(8)==1);
            button.setTooltip(Tooltip.create(Component.translatable(RocketVariant.byId(menu.value(6)).permits(TravelDestination.at(i).planet(),menu.value(8)==1) ? "gui.space_fabric.route_safe" : "gui.space_fabric.route_unsafe")));
            button.setMessage(Component.literal(i == menu.value(1) ? "> " : "").append(TravelDestination.at(i).label()));
        }
        launch.active = idle && pilot && menu.value(7)>0 && RocketVariant.byId(menu.value(6)).permits(TravelDestination.at(menu.value(1)).planet(),menu.value(8)==1) && menu.value(3) > 0 && menu.value(0) >= menu.value(3);
        board.active = idle && !pilot;
    }
    @Override protected void containerTick() { super.containerTick(); updateButtons(); }
    @Override public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_SPACE) { if (launch.active) send(RocketMenu.LAUNCH); return true; }
        return super.keyPressed(event);
    }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        super.extractBackground(gui, mouseX, mouseY, delta);
        // Esquema orbital, no efemérides. La Luna y el regreso al Overworld tienen marcadores propios.
        int cx = leftPos + 238, cy = topPos + 60;
        for (int i = 0; i < 45; i++) {
            int x = leftPos + 164 + (i * 37 % 146), y = topPos + 18 + (i * 23 % 77);
            gui.fill(x, y, x + 1, y + 1, 0xFF6B8396);
        }
        gui.fill(cx - 3, cy - 3, cx + 4, cy + 4, 0xFFFFCC55);
        for (int i = 1; i < 10; i++) {
            double radius = 9 + i * 6;
            for (int angle = 0; angle < 360; angle += 8) {
                double radians = Math.toRadians(angle);
                int x = cx + (int)(Math.cos(radians) * radius), y = cy + (int)(Math.sin(radians) * radius * 0.52);
                gui.fill(x, y, x + 1, y + 1, 0xFF284355);
            }
        }
        for (int i = 0; i < 10; i++) {
            double angle = i * 2.3, radius = 9 + i * 6;
            int x = cx + (int)(Math.cos(angle) * radius), y = cy + (int)(Math.sin(angle) * radius * 0.52);
            if (i == menu.value(1)) gui.fill(x - 4, y - 4, x + 5, y + 5, 0xFFFFFFFF);
            gui.fill(x - 2, y - 2, x + 3, y + 3, COLORS[i]);
        }
        gui.fill(leftPos + 8, topPos + 115, leftPos + 148, topPos + 121, 0xFF07121C);
        int fuelWidth = Math.clamp(menu.value(0) * 140 / RocketRules.TANK_CAPACITY, 0, 140);
        gui.fill(leftPos + 8, topPos + 115, leftPos + 8 + fuelWidth, topPos + 121, 0xFF45D5B0);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        gui.text(font, title, 8, 7, 0xFFE5F5FF);
        var variant=RocketVariant.byId(menu.value(6));
        gui.text(font, Component.translatable("rocket.space_fabric.variant."+variant.path),164,7,variant.color);
        gui.text(font, Component.translatable("gui.space_fabric.hull",menu.value(7)),164,89,menu.value(7)>25?0xFF45D5B0:0xFFFF7555);
        gui.text(font, Component.translatable("gui.space_fabric.fuel", menu.value(0), menu.value(3)), 8, 104, 0xFFB5D6E5);
        gui.text(font, Component.translatable("gui.space_fabric.cargo"), 8, 127, 0xFFB5D6E5);
        gui.text(font, Component.translatable("gui.space_fabric.cells"), 190, 127, 0xFFB5D6E5);
    }
}

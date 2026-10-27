package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.RocketMenu;
import dev.vic17usd.spacefabric.space.RocketRules;
import dev.vic17usd.spacefabric.space.RocketVariant;
import dev.vic17usd.spacefabric.space.RocketNavigation;
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
    private Component reason(RocketNavigation.Reason reason,int destination) {
        var state=menu.state();
        if(reason==RocketNavigation.Reason.UNSUPPORTED&&destination>=0&&destination<TravelDestination.ALL.size()) {
            var target=TravelDestination.at(destination).planet();
            if(state.exclusive()&&target!=dev.vic17usd.spacefabric.planet.Planet.EARTH&&target!=state.variant().mission)
                return Component.translatable("gui.space_fabric.rocket_protection.exclusive");
            var failure=state.variant().protectionFailure(target,240);
            return Component.translatable("gui.space_fabric.rocket_protection."+failure.name().toLowerCase(java.util.Locale.ROOT));
        }
        return Component.translatable(reason.key);
    }
    private void updateButtons() {
        var state=menu.state();
        for(int i=0;i<destinations.size();i++) {
            var button=destinations.get(i);var reason=state.destination(i);
            button.active=reason==RocketNavigation.Reason.READY;
            button.setTooltip(Tooltip.create(reason(reason,i)));
            button.setMessage(Component.literal(i==state.selected()?"> ":"").append(TravelDestination.at(i).label()));
        }
        launch.active=state.launch()==RocketNavigation.Reason.READY;
        launch.setTooltip(Tooltip.create(reason(state.launch(),state.selected())));
        board.active=state.board()==RocketNavigation.Reason.READY;
        board.setTooltip(Tooltip.create(reason(state.board(),state.selected())));
    }
    @Override protected void containerTick() { super.containerTick(); updateButtons(); }
    @Override public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_SPACE) { if (launch.active) send(RocketMenu.LAUNCH); return true; }
        return super.keyPressed(event);
    }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        super.extractBackground(gui, mouseX, mouseY, delta);
        // Esquema orbital, no efemérides. La Luna y el regreso al Overworld tienen marcadores propios.
        int cx = leftPos + 238, cy = topPos + 48;
        for (int i = 0; i < 45; i++) {
            int x = leftPos + 164 + (i * 37 % 146), y = topPos + 18 + (i * 23 % 54);
            gui.fill(x, y, x + 1, y + 1, 0xFF6B8396);
        }
        gui.fill(cx - 3, cy - 3, cx + 4, cy + 4, 0xFFFFCC55);
        for (int i = 1; i < 10; i++) {
            double radius = 9 + i * 6;
            for (int angle = 0; angle < 360; angle += 8) {
                double radians = Math.toRadians(angle);
                int x = cx + (int)(Math.cos(radians) * radius), y = cy + (int)(Math.sin(radians) * radius * 0.36);
                gui.fill(x, y, x + 1, y + 1, 0xFF284355);
            }
        }
        for (int i = 0; i < 10; i++) {
            double angle = i * 2.3, radius = 9 + i * 6;
            int x = cx + (int)(Math.cos(angle) * radius), y = cy + (int)(Math.sin(angle) * radius * 0.36);
            if (i == menu.value(1)) gui.fill(x - 4, y - 4, x + 5, y + 5, 0xFFFFFFFF);
            gui.fill(x - 2, y - 2, x + 3, y + 3, COLORS[i]);
        }
        gui.fill(leftPos + 8, topPos + 115, leftPos + 148, topPos + 121, 0xFF07121C);
        int fuelWidth = menu.state().fuelBarWidth(140);
        gui.fill(leftPos + 8, topPos + 115, leftPos + 8 + fuelWidth, topPos + 121, 0xFF45D5B0);
        if(mouseX>=leftPos+8&&mouseX<=leftPos+148&&mouseY>=topPos+104&&mouseY<=topPos+121)
            gui.setComponentTooltipForNextFrame(font,List.of(
                Component.translatable("item.space_fabric.rocket_fuel",menu.value(0),menu.value(9)),
                Component.translatable("gui.space_fabric.fuel_per_cell",RocketRules.FUEL_PER_CELL),
                Component.translatable("gui.space_fabric.fuel_cell_note")),mouseX,mouseY);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        var state=menu.state();
        Component rocket=state.ready()?state.variant().label():Component.translatable("gui.space_fabric.rocket_reason.sync");
        gui.text(font,font.plainSubstrByWidth(rocket.getString(),148),8,7,state.variant().color);
        var destination=TravelDestination.at(Math.clamp(state.selected(),0,TravelDestination.ALL.size()-1));
        gui.text(font,font.plainSubstrByWidth(Component.translatable("gui.space_fabric.selected_destination",destination.label()).getString(),148),164,7,0xFFE5F5FF);
        gui.text(font,Component.translatable("gui.space_fabric.hull",state.hull()),164,78,state.hull()>25?0xFF45D5B0:0xFFFF7555);
        gui.text(font,Component.translatable("gui.space_fabric.trip_cost",state.cost()),164,89,state.fuel()>=state.cost()?0xFFB5D6E5:0xFFFF7555);
        gui.text(font,state.ready()?Component.translatable("gui.space_fabric.fuel",state.fuel(),state.capacity()):Component.translatable("gui.space_fabric.rocket_reason.sync"),8,104,0xFFB5D6E5);
        gui.text(font, Component.translatable("gui.space_fabric.cargo"), 8, 127, 0xFFB5D6E5);
        gui.text(font, Component.translatable("gui.space_fabric.cells"), 190, 127, 0xFFB5D6E5);
    }
}

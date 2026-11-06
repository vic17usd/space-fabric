package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.space.RocketEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Transparent top-left checklist, separated from the top-right minimap and habitat diagnostics. */
public final class MarsGuideHud {
    private static final MarsMissionTracker STATE = new MarsMissionTracker();
    public static MarsSettlementNetwork.State settlement = MarsSettlementNetwork.State.EMPTY;
    private MarsGuideHud() { }
    public static void initialize() {
        MarsGuideScene.initialize();
        ClientPlayNetworking.registerGlobalReceiver(MarsSettlementNetwork.State.TYPE, (payload, context) -> {
            settlement = payload;
            if (context.client().screen instanceof MarsSettlementScreen screen) screen.accept(payload);
            else if (payload.open()) context.client().setScreen(new MarsSettlementScreen(context.client().screen, payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(MarsGuideNetwork.State.TYPE, (payload, context) -> {
            STATE.accept(payload);
            if (context.client().screen instanceof MarsGuideScreen guide) guide.accept(payload);
        });
        ClientPlayNetworking.registerGlobalReceiver(MarsGuideNetwork.OpenGuide.TYPE, (payload, context) -> {
            var client = context.client();
            if (client.player == null) return;
            if (client.screen instanceof MarsGuideScreen guide) guide.accept(payload.state());
            else { client.player.closeContainer(); client.setScreen(new MarsGuideScreen(payload.state())); }
        });
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen
                    || screen instanceof net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen)
                net.fabricmc.fabric.api.client.screen.v1.Screens.getWidgets(screen).add(
                        net.minecraft.client.gui.components.Button.builder(Component.translatable("mars.tutorial.button"), b -> {
                            if (client.player != null) { client.player.closeContainer(); client.player.connection.sendCommand("marte guia"); }
                        }).bounds(6, 6, 108, 20).build());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> { STATE.clear(); settlement = MarsSettlementNetwork.State.EMPTY; });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null || !client.player.isAlive()
                    || PlanetDimensions.find(client.level).orElse(null) != Planet.MARS) STATE.clear();
            else if (!client.isPaused()) STATE.tick();
        });
        HudElementRegistry.addLast(SpaceFabric.id("mars_missions"), (gui, delta) -> draw(gui));
    }
    private static int text(GuiGraphicsExtractor gui, Component text, int y, int width, int color) {
        var font = Minecraft.getInstance().font;
        for (var line : font.split(text, width)) {
            gui.text(font, line, 0, y, color);
            y += font.lineHeight + 2;
        }
        return y;
    }
    private static void draw(GuiGraphicsExtractor gui) {
        var client = Minecraft.getInstance();
        if (!STATE.available() || client.level == null || client.player == null || !client.player.isAlive()
                || client.player.isSpectator() || client.options.hideGui || client.screen != null
                || PlanetDimensions.find(client.level).orElse(null) != Planet.MARS
                || client.player.getVehicle() instanceof RocketEntity rocket && rocket.isFlying()) return;
        var state = STATE.state();
        float scale = .85F;
        // The native habitat panel occupies 206 GUI pixels at the right; leave a gap even at 320px.
        int width = (int)(Math.max(82, Math.min(224, client.getWindow().getGuiScaledWidth() - 228)) / scale);
        var pose = gui.pose();
        pose.pushMatrix();
        try {
            pose.translate(8, 8); pose.scale(scale, scale);
            int y = text(gui, Component.translatable(state.has(MarsProgress.COMPLETE) ? "mars.settlement.title" : "mars.guide.title"), 0, width, 0xFFFFD9AE) + 3;
            if (state.has(MarsProgress.COMPLETE)) {
                y += 3;
                y = text(gui, Component.translatable("mars.settlement.stage_" + settlement.stage()), y + 3, width, 0xFFE0E7EB);
                text(gui, Component.literal("/marte expedicion"), y + 3, width, 0xFF65D9ED);
                return;
            }
            if (!state.has(MarsProgress.ARRIVAL))
                y = text(gui, Component.translatable("mars.guide.arrival.detail"), y, width, 0xFF65D9ED) + 3;
            String[] steps = {"refuge", "sealed", "supplies", "water"};
            for (int i = 0; i < steps.length; i++) {
                boolean done = state.has(2 << i);
                var line = Component.literal(done ? "✓ " : "○ ").append(Component.translatable("mars.guide." + steps[i]));
                y = text(gui, line, y, width, done ? 0xFF8DE9BE : 0xFFE0E7EB) + 2;
            }
            if (!state.has(MarsProgress.ARRIVAL)) return;
            String hint = !state.has(MarsProgress.REFUGE) ? "select" : !state.has(MarsProgress.SEALED) ? "seal_hint"
                    : !state.has(MarsProgress.SUPPLIES) ? "supply_hint" : !state.has(MarsProgress.WATER) ? "water_hint" : "inside";
            y = text(gui, Component.translatable("mars.guide." + hint), y + 2, width, 0xFF65D9ED);
            text(gui, Component.translatable("mars.guide.help"), y + 3, width, 0xFFB5C3CD);
        } finally {
            pose.popMatrix();
        }
    }
}

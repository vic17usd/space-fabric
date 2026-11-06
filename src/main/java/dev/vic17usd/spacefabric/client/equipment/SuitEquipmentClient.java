package dev.vic17usd.spacefabric.client.equipment;

import dev.vic17usd.spacefabric.equipment.SuitEquipment;
import dev.vic17usd.spacefabric.menu.ModMenus;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;

public final class SuitEquipmentClient {
    private SuitEquipmentClient() { }
    private static void open(Minecraft client) {
        if(client.player==null)return;
        if(ClientPlayNetworking.canSend(SuitEquipment.Open.TYPE)) {
            client.player.closeContainer();ClientPlayNetworking.send(new SuitEquipment.Open());
        } else client.player.sendOverlayMessage(Component.translatable("message.space_fabric.suit_server"));
    }
    public static void initialize() {
        MenuScreens.register(ModMenus.SUIT,dev.vic17usd.spacefabric.client.menu.SuitScreen::new);
        var key=dev.vic17usd.spacefabric.client.ModKeyBindings.SUIT;
        ClientTickEvents.END_CLIENT_TICK.register(client->{while(key.consumeClick())if(client.screen==null)open(client);});
        ScreenEvents.AFTER_INIT.register((client,screen,width,height)->{
            if(screen instanceof CreativeModeInventoryScreen)
                Screens.getWidgets(screen).add(Button.builder(Component.translatable("gui.space_fabric.suit_inventory"),button->open(client)).bounds(width-86,30,80,20).build());
        });
    }
}

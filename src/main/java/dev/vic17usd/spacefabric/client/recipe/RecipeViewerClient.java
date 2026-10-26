package dev.vic17usd.spacefabric.client.recipe;

import dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class RecipeViewerClient {
    private RecipeViewerClient() { }
    public static void initialize() {
        var key=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.space_fabric.recipes",GLFW.GLFW_KEY_K,KeyMapping.Category.INVENTORY));
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            while(key.consumeClick())if(client.player!=null&&client.screen==null)client.setScreen(new RecipeViewerScreen(null));
        });
        ScreenEvents.AFTER_INIT.register((client,screen,width,height)->{
            if(screen instanceof InventoryScreen||screen instanceof CreativeModeInventoryScreen)
                Screens.getWidgets(screen).add(Button.builder(Component.translatable("gui.space_fabric.recipe_viewer"),
                    button->{
                        // Close the old menu normally so its client result slot and server state stay synchronized.
                        client.player.closeContainer();
                        client.setScreen(new RecipeViewerScreen(screen));
                    }).bounds(width-86,6,80,20).build());
        });
        ClientPlayNetworking.registerGlobalReceiver(RecipeViewerNetwork.Reply.TYPE,(reply,context)->{
            if(context.client().screen instanceof RecipeViewerScreen viewer)viewer.accept(reply);
        });
    }
}

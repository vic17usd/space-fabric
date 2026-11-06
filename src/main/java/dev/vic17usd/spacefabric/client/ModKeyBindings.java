package dev.vic17usd.spacefabric.client;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/** Native Controls section. Existing translation IDs also preserve users' options.txt bindings. */
public final class ModKeyBindings {
    public static final KeyMapping.Category CATEGORY=KeyMapping.Category.register(SpaceFabric.id("controls"));
    public static final KeyMapping RECIPES=key("recipes",GLFW.GLFW_KEY_K);
    public static final KeyMapping SUIT=key("suit_inventory",GLFW.GLFW_KEY_J);
    public static final KeyMapping ROVER=key("rover_inventory",GLFW.GLFW_KEY_V);
    public static final KeyMapping ATLAS=key("atlas",GLFW.GLFW_KEY_M);
    public static final KeyMapping MARKER=key("map_marker",GLFW.GLFW_KEY_B);
    public static final KeyMapping MINIMAP=key("minimap",GLFW.GLFW_KEY_N);
    public static final KeyMapping ITEM_RECIPES=key("item_recipes",GLFW.GLFW_KEY_R);
    public static final KeyMapping ITEM_USES=key("item_uses",GLFW.GLFW_KEY_U);
    private ModKeyBindings() { }
    private static KeyMapping key(String name,int code){return KeyMappingHelper.registerKeyMapping(new KeyMapping("key.space_fabric."+name,code,CATEGORY));}
}

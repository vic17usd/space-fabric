package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

public final class ExpansionContent {
    public static final Item AEROSTAT=Registry.register(BuiltInRegistries.ITEM,SpaceFabric.id("venus_aerostat"),
        new dev.vic17usd.spacefabric.rover.RoverItem(new Item.Properties().stacksTo(1).fireResistant().setId(ResourceKey.create(Registries.ITEM,SpaceFabric.id("venus_aerostat")))));
    public static final Item NAVIGATOR=register("venus_navigator",true),BLACK_BOX=register("venus_black_box",false),AIR_SAMPLE=register("venus_air_sample",false);
    private static Item register(String name,boolean book){var id=SpaceFabric.id(name);var p=new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM,id));return Registry.register(BuiltInRegistries.ITEM,id,book?new OperationsNavigator(p):new Item(p));}
    private ExpansionContent() { }
    public static void initialize(){ExpeditionSystem.initialize();OperationsSystem.initialize();dev.vic17usd.spacefabric.environment.VenusActivity.initialize();}
}

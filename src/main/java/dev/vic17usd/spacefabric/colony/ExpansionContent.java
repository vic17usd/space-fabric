package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

public final class ExpansionContent {
    public static final Item AEROSTAT=Registry.register(BuiltInRegistries.ITEM,SpaceFabric.id("venus_aerostat"),
        new dev.vic17usd.spacefabric.rover.RoverItem(new Item.Properties().stacksTo(1).fireResistant().setId(ResourceKey.create(Registries.ITEM,SpaceFabric.id("venus_aerostat")))));
    private ExpansionContent() { }
    public static void initialize(){ExpeditionSystem.initialize();}
}

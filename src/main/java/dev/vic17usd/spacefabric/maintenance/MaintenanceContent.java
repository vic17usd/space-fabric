package dev.vic17usd.spacefabric.maintenance;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;

public final class MaintenanceContent {
    private static final Map<String,Item> ITEMS=new LinkedHashMap<>();
    public static final Item FILTER=item("acid_filter");
    public static final Item SPENT_FILTER=item("spent_acid_filter");
    public static final Item COATING=item("anticorrosive_coating");
    public static final Item REPAIR_KIT=item("machine_repair_kit");
    private MaintenanceContent() { }
    private static Item item(String name) {
        var id=SpaceFabric.id(name);var item=Registry.register(BuiltInRegistries.ITEM,id,new Item(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM,id)).stacksTo(16).component(DataComponents.LORE,new ItemLore(List.of(Component.translatable("item.space_fabric."+name+".hint"))))));
        ITEMS.put(name,item);return item;
    }
    public static Map<String,Item> items(){return Collections.unmodifiableMap(ITEMS);}
    public static void initialize() { }
}

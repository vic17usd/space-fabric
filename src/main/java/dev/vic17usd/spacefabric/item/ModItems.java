package dev.vic17usd.spacefabric.item;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import dev.vic17usd.spacefabric.space.RocketItem;
import java.util.function.Function;

public final class ModItems {
    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    public static final Item MARTIAN_COPPER_TRADER_SPAWN_EGG = register("martian_copper_trader_spawn_egg",
            net.minecraft.world.item.SpawnEggItem::new, new Item.Properties().spawnEgg(
                    dev.vic17usd.spacefabric.entity.ModMarsEntities.COPPER_TRADER));
    public static final Item ROCKET = register("rocket", RocketItem::new, new Item.Properties().stacksTo(1).fireResistant());
    public static final Item EMPTY_FUEL_CELL = register("empty_fuel_cell", new Item.Properties());
    public static final Item ROCKET_FUEL_CELL = register("rocket_fuel_cell", new Item.Properties().stacksTo(16));
    public static final Item ARES_CRYSTAL = register("ares_crystal", new Item.Properties());
    public static final Item RAW_PHOBOSITE = register("raw_phobosite", new Item.Properties());
    public static final Item THERMAL_BOOTS = register("thermal_boots", new Item.Properties()
            .humanoidArmor(ArmorMaterials.NETHERITE, ArmorType.BOOTS).fireResistant());

    public static final Item DENSE_IRON_CHUNK = register("dense_iron_chunk", new Item.Properties().fireResistant());
    public static final Item CRATER_ICE_SHARD = register("crater_ice_shard", new Item.Properties());
    public static final Item VENUSIAN_OLIVINE = register("venusian_olivine", new Item.Properties());
    public static final Item PYROXENE = register("pyroxene", new Item.Properties());
    public static final Item SULFUR_CRYSTAL = register("sulfur_crystal", new Item.Properties());
    public static final Item ACID_CONDENSATE = register("acid_condensate", new Item.Properties());
    public static final Item IRON_OXIDE_DUST = register("iron_oxide_dust", new Item.Properties());
    public static final Item MARTIAN_OLIVINE = register("martian_olivine", new Item.Properties());
    public static final Item SULFATE_DUST = register("sulfate_dust", new Item.Properties());

    private ModItems() { }

    private static Item register(String path, Item.Properties properties) {
        return register(path, Item::new, properties);
    }

    private static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties properties) {
        var id = SpaceFabric.id(path);
        var item = Registry.register(BuiltInRegistries.ITEM, id,
                factory.apply(properties.setId(ResourceKey.create(Registries.ITEM, id))));
        ITEMS.put(path, item);
        return item;
    }

    public static Map<String, Item> all() { return Collections.unmodifiableMap(ITEMS); }
    public static void initialize() { }
}

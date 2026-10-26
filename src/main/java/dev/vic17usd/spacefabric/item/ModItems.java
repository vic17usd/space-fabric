package dev.vic17usd.spacefabric.item;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

public final class ModItems {
    public static final Item ARES_CRYSTAL = register("ares_crystal", new Item.Properties());
    public static final Item RAW_PHOBOSITE = register("raw_phobosite", new Item.Properties());
    public static final Item THERMAL_BOOTS = register("thermal_boots", new Item.Properties()
            .humanoidArmor(ArmorMaterials.NETHERITE, ArmorType.BOOTS).fireResistant());

    private ModItems() { }

    private static Item register(String path, Item.Properties properties) {
        var id = SpaceFabric.id(path);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new Item(properties.setId(ResourceKey.create(Registries.ITEM, id))));
    }

    public static void initialize() { }
}

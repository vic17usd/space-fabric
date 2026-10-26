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
import dev.vic17usd.spacefabric.space.RocketVariant;
import dev.vic17usd.spacefabric.space.RocketUpgradeItem;
import java.util.function.Function;

public final class ModItems {
    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    public static final Item VACUUM_SUIT_HELMET = register("vacuum_suit_helmet", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.HELMET).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.vacuum"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VACUUM_SUIT_CHESTPLATE = register("vacuum_suit_chestplate", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.CHESTPLATE).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.vacuum"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VACUUM_SUIT_LEGGINGS = register("vacuum_suit_leggings", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.LEGGINGS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.vacuum"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VACUUM_SUIT_BOOTS = register("vacuum_suit_boots", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.BOOTS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.vacuum"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VENUS_SUIT_HELMET = register("venus_suit_helmet", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.HELMET).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.venus"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VENUS_SUIT_CHESTPLATE = register("venus_suit_chestplate", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.CHESTPLATE).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.venus"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VENUS_SUIT_LEGGINGS = register("venus_suit_leggings", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.LEGGINGS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.venus"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VENUS_SUIT_BOOTS = register("venus_suit_boots", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.BOOTS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.venus"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item CRYO_SUIT_HELMET = register("cryo_suit_helmet", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.HELMET).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.cryo"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item CRYO_SUIT_CHESTPLATE = register("cryo_suit_chestplate", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.CHESTPLATE).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.cryo"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item CRYO_SUIT_LEGGINGS = register("cryo_suit_leggings", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.LEGGINGS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.cryo"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item CRYO_SUIT_BOOTS = register("cryo_suit_boots", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.BOOTS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.cryo"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item ABYSSAL_SUIT_HELMET = register("abyssal_suit_helmet", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.HELMET).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.abyssal"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item ABYSSAL_SUIT_CHESTPLATE = register("abyssal_suit_chestplate", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.CHESTPLATE).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.abyssal"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item ABYSSAL_SUIT_LEGGINGS = register("abyssal_suit_leggings", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.LEGGINGS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.abyssal"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item ABYSSAL_SUIT_BOOTS = register("abyssal_suit_boots", new Item.Properties().humanoidArmor(ArmorMaterials.IRON, ArmorType.BOOTS).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.abyssal"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));
    public static final Item VENUS_ACID_BOTTLE=register("venus_acid_bottle",new Item.Properties().stacksTo(16));
    public static final Item SULFUR_CORE=register("sulfur_core",new Item.Properties().fireResistant());
public static final Item ACIDSTONE_SLIME_SPAWN_EGG=register("acidstone_slime_spawn_egg",net.minecraft.world.item.SpawnEggItem::new,new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.ACIDSTONE_SLIME));
public static final Item OBSIDIAN_LAVAFANG_SPAWN_EGG=register("obsidian_lavafang_spawn_egg",net.minecraft.world.item.SpawnEggItem::new,new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.OBSIDIAN_LAVAFANG));
public static final Item ASHPLUME_EMBERWALKER_SPAWN_EGG=register("ashplume_emberwalker_spawn_egg",net.minecraft.world.item.SpawnEggItem::new,new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.ASHPLUME_EMBERWALKER));
public static final Item SULFURWING_COPPER_HARBINGER_SPAWN_EGG=register("sulfurwing_copper_harbinger_spawn_egg",net.minecraft.world.item.SpawnEggItem::new,new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.SULFURWING_COPPER_HARBINGER));
    public static final Item OBSIDIAN_CORE = register("obsidian_core", new Item.Properties().fireResistant());
    public static final Item BASALT_EMBER_GOLEM_SPAWN_EGG = register("basalt_ember_golem_spawn_egg", net.minecraft.world.item.SpawnEggItem::new,
            new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.sanctum.ModSanctumEntities.GOLEM));
    public static final Item BASALT_BOSSWARDEN_SPAWN_EGG = register("basalt_bosswarden_spawn_egg", net.minecraft.world.item.SpawnEggItem::new,
            new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.sanctum.ModSanctumEntities.GUARD));
    public static final Item ENERGY_CRYSTAL_SHARD = register("energy_crystal_shard", new Item.Properties());
    public static final Item CONDENSED_STARDUST = register("condensed_stardust", new Item.Properties());
    public static final Item VIBRATION_SENSOR = register("vibration_sensor", new Item.Properties());
    public static final Item CRUSHED_IRON_ORE = register("crushed_iron_ore", new Item.Properties());
    public static final Item CRYSTAL_HUM_CRAWLER_SPAWN_EGG = register("crystal_hum_crawler_spawn_egg", net.minecraft.world.item.SpawnEggItem::new,
            new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.CRAWLER));
    public static final Item GOLDEN_EYE_SANDWRAITH_SPAWN_EGG = register("golden_eye_sandwraith_spawn_egg", net.minecraft.world.item.SpawnEggItem::new,
            new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.SANDWRAITH));
    public static final Item RED_DUNE_IRONMAW_SPAWN_EGG = register("red_dune_ironmaw_spawn_egg", net.minecraft.world.item.SpawnEggItem::new,
            new Item.Properties().spawnEgg(dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.IRONMAW));
    public static final Item MARTIAN_COPPER_TRADER_SPAWN_EGG = register("martian_copper_trader_spawn_egg",
            net.minecraft.world.item.SpawnEggItem::new, new Item.Properties().spawnEgg(
                    dev.vic17usd.spacefabric.entity.ModMarsEntities.COPPER_TRADER));
    public static final Item ROCKET = register("rocket", RocketItem::new, new Item.Properties().stacksTo(1).fireResistant());
    public static final Item MERCURY_ROCKET = register("mercury_rocket", p -> new RocketItem(p, RocketVariant.MERCURY), rocketProperties(RocketVariant.MERCURY, false));
    public static final Item MERCURY_ROCKET_KIT = register("mercury_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.MERCURY), rocketProperties(RocketVariant.MERCURY, true));
    public static final Item VENUS_ROCKET = register("venus_rocket", p -> new RocketItem(p, RocketVariant.VENUS), rocketProperties(RocketVariant.VENUS, false));
    public static final Item VENUS_ROCKET_KIT = register("venus_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.VENUS), rocketProperties(RocketVariant.VENUS, true));
    public static final Item EARTH_ROCKET = register("earth_rocket", p -> new RocketItem(p, RocketVariant.EARTH), rocketProperties(RocketVariant.EARTH, false));
    public static final Item EARTH_ROCKET_KIT = register("earth_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.EARTH), rocketProperties(RocketVariant.EARTH, true));
    public static final Item MOON_ROCKET = register("moon_rocket", p -> new RocketItem(p, RocketVariant.MOON), rocketProperties(RocketVariant.MOON, false));
    public static final Item MOON_ROCKET_KIT = register("moon_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.MOON), rocketProperties(RocketVariant.MOON, true));
    public static final Item MARS_ROCKET = register("mars_rocket", p -> new RocketItem(p, RocketVariant.MARS), rocketProperties(RocketVariant.MARS, false));
    public static final Item MARS_ROCKET_KIT = register("mars_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.MARS), rocketProperties(RocketVariant.MARS, true));
    public static final Item JUPITER_ROCKET = register("jupiter_rocket", p -> new RocketItem(p, RocketVariant.JUPITER), rocketProperties(RocketVariant.JUPITER, false));
    public static final Item JUPITER_ROCKET_KIT = register("jupiter_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.JUPITER), rocketProperties(RocketVariant.JUPITER, true));
    public static final Item SATURN_ROCKET = register("saturn_rocket", p -> new RocketItem(p, RocketVariant.SATURN), rocketProperties(RocketVariant.SATURN, false));
    public static final Item SATURN_ROCKET_KIT = register("saturn_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.SATURN), rocketProperties(RocketVariant.SATURN, true));
    public static final Item URANUS_ROCKET = register("uranus_rocket", p -> new RocketItem(p, RocketVariant.URANUS), rocketProperties(RocketVariant.URANUS, false));
    public static final Item URANUS_ROCKET_KIT = register("uranus_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.URANUS), rocketProperties(RocketVariant.URANUS, true));
    public static final Item NEPTUNE_ROCKET = register("neptune_rocket", p -> new RocketItem(p, RocketVariant.NEPTUNE), rocketProperties(RocketVariant.NEPTUNE, false));
    public static final Item NEPTUNE_ROCKET_KIT = register("neptune_rocket_kit", p -> new RocketUpgradeItem(p, RocketVariant.NEPTUNE), rocketProperties(RocketVariant.NEPTUNE, true));
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

    private static Item.Properties rocketProperties(RocketVariant variant, boolean kit) {
        var lore = new java.util.ArrayList<net.minecraft.network.chat.Component>();
        lore.add(net.minecraft.network.chat.Component.translatable("item.space_fabric.rocket_rating",
                (int)(variant.maxKpa/100), (int)variant.minCelsius, (int)variant.maxCelsius));
        if(kit) lore.add(net.minecraft.network.chat.Component.translatable("item.space_fabric.rocket_kit_help"));
        return new Item.Properties().fireResistant().component(net.minecraft.core.component.DataComponents.LORE,
                new net.minecraft.world.item.component.ItemLore(lore));
    }

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

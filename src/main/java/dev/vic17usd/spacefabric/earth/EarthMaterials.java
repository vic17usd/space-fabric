package dev.vic17usd.spacefabric.earth;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.PlanetOreBlock;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Terrestrial geology has its own catalog; all registrations happen before registry freeze. */
public final class EarthMaterials {
    private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    static {
        for (var mineral : EarthMineral.values()) {
            registerOre(mineral, false);
            registerOre(mineral, true);
            registerBlock(mineral.storageId(), new Block(BlockBehaviour.Properties
                    .ofFullCopy(mineral == EarthMineral.FLUORITE ? Blocks.AMETHYST_BLOCK : Blocks.IRON_BLOCK)
                    .strength(4.0F, 6.0F).requiresCorrectToolForDrops()
                    .setId(ResourceKey.create(Registries.BLOCK, SpaceFabric.id(mineral.storageId())))));
            if (mineral.isMetal()) registerItem(mineral.rawId());
            registerItem(mineral.productId());
        }
    }
    private EarthMaterials() { }

    private static void registerOre(EarthMineral mineral, boolean deep) {
        String path = mineral.oreId(deep);
        var properties = BlockBehaviour.Properties.ofFullCopy(deep ? Blocks.DEEPSLATE : Blocks.STONE)
                .strength(deep ? 4.5F : 3.0F, 3.0F).requiresCorrectToolForDrops()
                .sound(deep ? SoundType.DEEPSLATE : SoundType.STONE)
                .setId(ResourceKey.create(Registries.BLOCK, SpaceFabric.id(path)));
        // Metals grant XP on refining; the crystal ore grants XP when mined, respecting Silk Touch.
        registerBlock(path, new PlanetOreBlock(UniformInt.of(mineral.isMetal() ? 0 : 2,
                mineral.isMetal() ? 0 : 5), properties));
    }
    private static void registerBlock(String path, Block block) {
        var id = SpaceFabric.id(path);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        var item = new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))
                .useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, id, item);
        BLOCKS.put(path, block);
    }
    private static void registerItem(String path) {
        var id = SpaceFabric.id(path);
        var item = Registry.register(BuiltInRegistries.ITEM, id,
                new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
        ITEMS.put(path, item);
    }
    public static Block ore(EarthMineral mineral, boolean deep) { return BLOCKS.get(mineral.oreId(deep)); }
    public static Block storage(EarthMineral mineral) { return BLOCKS.get(mineral.storageId()); }
    public static Item raw(EarthMineral mineral) { return mineral.isMetal() ? ITEMS.get(mineral.rawId()) : product(mineral); }
    public static Item product(EarthMineral mineral) { return ITEMS.get(mineral.productId()); }
    public static Map<String, Block> blocks() { return Collections.unmodifiableMap(BLOCKS); }
    public static Map<String, Item> items() { return Collections.unmodifiableMap(ITEMS); }
    public static void initialize() { }
}

package dev.vic17usd.spacefabric.block;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
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
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    static {
        Registry.register(BuiltInRegistries.BLOCK_TYPE, SpaceFabric.id("regolith"), PlanetRegolithBlock.CODEC);
        Registry.register(BuiltInRegistries.BLOCK_TYPE, SpaceFabric.id("hot_volcanic_rock"), HotVolcanicRockBlock.CODEC);
        Registry.register(BuiltInRegistries.BLOCK_TYPE, SpaceFabric.id("planet_ore"), PlanetOreBlock.CODEC);
        Registry.register(BuiltInRegistries.BLOCK_TYPE, SpaceFabric.id("methane_ice"), MethaneIceBlock.CODEC);
    }

    public static final Block MERCURY_REGOLITH = register("mercury_regolith", PlanetRegolithBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.STONE).strength(0.6F));
    public static final Block LUNAR_REGOLITH = register("lunar_regolith", PlanetRegolithBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.STONE).strength(0.5F));
    public static final Block VENUS_HOT_ROCK = register("venus_hot_rock", HotVolcanicRockBlock::new,
            stoneProperties().strength(3.5F, 9.0F).lightLevel(state -> 5));
    public static final Block MARTIAN_STONE = register("martian_stone", Block::new,
            stoneProperties().mapColor(MapColor.COLOR_RED).strength(3.0F, 6.0F));
    public static final Block ARES_CRYSTAL_ORE = register("ares_crystal_ore",
            properties -> new PlanetOreBlock(UniformInt.of(3, 7), properties), stoneProperties().strength(4.5F, 8.0F));
    public static final Block PHOBOSITE_ORE = register("phobosite_ore",
            properties -> new PlanetOreBlock(UniformInt.of(4, 8), properties), stoneProperties().strength(5.0F, 10.0F));
    public static final Block URANIAN_METHANE_ICE = register("uranian_methane_ice", MethaneIceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_ICE).strength(0.8F).sound(SoundType.GLASS));
    public static final Block NEPTUNIAN_METHANE_ICE = register("neptunian_methane_ice", MethaneIceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_ICE).strength(1.0F).sound(SoundType.GLASS));
    public static final Block TERRESTRIAL_BASALT = register("terrestrial_basalt", Block::new,
            stoneProperties().strength(2.5F, 7.0F));
    public static final Block JOVIAN_METEORITE = register("jovian_meteorite", Block::new,
            stoneProperties().strength(4.0F, 10.0F));
    public static final Block SATURN_RING_ICE = register("saturn_ring_ice", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_ICE).strength(0.8F));

    private ModBlocks() { }

    private static BlockBehaviour.Properties stoneProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops();
    }

    private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory,
                                  BlockBehaviour.Properties properties) {
        var id = SpaceFabric.id(path);
        Block block = Registry.register(BuiltInRegistries.BLOCK, id,
                factory.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id))));
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block,
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix()));
        BLOCKS.put(path, block);
        return block;
    }

    public static Map<String, Block> all() { return Collections.unmodifiableMap(BLOCKS); }
    public static void initialize() { }
}

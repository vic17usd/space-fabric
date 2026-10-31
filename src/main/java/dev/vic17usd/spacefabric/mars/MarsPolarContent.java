package dev.vic17usd.spacefabric.mars;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.PlanetOreBlock;
import java.util.*;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Native 26.1 materials. Ice stays solid under ordinary light and never releases water on Mars. */
public final class MarsPolarContent {
    private static final List<Block> BLOCKS=new ArrayList<>();
    public static final Block ICE=block("martian_glacier_ice",Block::new,BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).strength(.8F).friction(.98F));
    public static final Block CO2=block("martian_frozen_co2",Block::new,BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).strength(.5F).friction(.98F));
    public static final Block ORE=block("martian_subglacial_ore",p->new PlanetOreBlock(UniformInt.of(1,3),p),BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).strength(3).requiresCorrectToolForDrops());
    public static final Block NODE=block("martian_frost_crystal_node",p->new PlanetOreBlock(UniformInt.of(1,2),p),BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).strength(1.5F).requiresCorrectToolForDrops().lightLevel(s->5));
    public static final Item CRYSTAL=item("martian_frost_crystal"),HEATSTONE=item("martian_heatstone");
    private MarsPolarContent(){}
    private static Item item(String name){var id=SpaceFabric.id(name);return Registry.register(BuiltInRegistries.ITEM,id,new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id))));}
    private static Block block(String name,Function<BlockBehaviour.Properties,Block> factory,BlockBehaviour.Properties p){
        var id=SpaceFabric.id(name);var block=Registry.register(BuiltInRegistries.BLOCK,id,factory.apply(p.setId(ResourceKey.create(Registries.BLOCK,id))));
        Registry.register(BuiltInRegistries.ITEM,id,new BlockItem(block,new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).useBlockDescriptionPrefix()));BLOCKS.add(block);return block;
    }
    public static List<Block> blocks(){return List.copyOf(BLOCKS);}
    public static void initialize(){
        Registry.register(BuiltInRegistries.BIOME_SOURCE,SpaceFabric.id("mars_polar"),MarsPolarBiomeSource.CODEC);
        Registry.register(BuiltInRegistries.FEATURE,SpaceFabric.id("mars_glacial_cave"),new MarsGlacialCaveFeature());
    }
}

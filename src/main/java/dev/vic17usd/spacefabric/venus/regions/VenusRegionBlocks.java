package dev.vic17usd.spacefabric.venus.regions;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.*;
import java.util.*;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Original imported art, registered as native 26.1 blocks; no old mod classes are loaded. */
public final class VenusRegionBlocks {
    private static final Map<String,Block> BLOCKS=new LinkedHashMap<>();
    static {Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id("venus_sulfur_ash"),SulfurAshBlock.CODEC);}
    public static final Block RIFT_OBSIDIAN=block("venus_rift_obsidian",Block::new,rock(20,1200));
    public static final Block RIFT_IRON=block("venus_rift_iron",p->new PlanetOreBlock(UniformInt.of(1,3),p),rock(5,9));
    public static final Block RIFT_EMBER=block("venus_rift_ember_crust",HotVolcanicRockBlock::new,rock(3,8).lightLevel(s->7));
    public static final Block TESSERA=block("venus_tessera_rock",Block::new,rock(4,9));
    public static final Block FISSURE=block("venus_ember_fissure",HotVolcanicRockBlock::new,rock(3,8).lightLevel(s->9));
    public static final Block LANTERN=block("venus_sulfur_lantern",Block::new,rock(2,5).lightLevel(s->15));
    public static final Block BASALT_SHEET=block("venus_basalt_sheet",Block::new,rock(3,8));
    public static final Block CHARRED_ROCK=block("venus_charred_rock",Block::new,rock(8,15));
    public static final Block ASH=block("venus_sulfur_ash",SulfurAshBlock::new,BlockBehaviour.Properties.ofFullCopy(Blocks.GRAY_CARPET).strength(.2F).noCollision().noOcclusion());
    public static final Item RIFT_SHARD=item("venus_rift_shard"),SULFUR_DUST=item("venus_sulfur_dust");
    private VenusRegionBlocks() { }
    private static BlockBehaviour.Properties rock(float hardness,float resistance){return BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).requiresCorrectToolForDrops().strength(hardness,resistance);}
    private static Block block(String name,Function<BlockBehaviour.Properties,Block> factory,BlockBehaviour.Properties p){
        var id=SpaceFabric.id(name);var block=Registry.register(BuiltInRegistries.BLOCK,id,factory.apply(p.setId(ResourceKey.create(Registries.BLOCK,id))));
        Registry.register(BuiltInRegistries.ITEM,id,new BlockItem(block,new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).useBlockDescriptionPrefix()));BLOCKS.put(name,block);return block;
    }
    private static Item item(String name){var id=SpaceFabric.id(name);return Registry.register(BuiltInRegistries.ITEM,id,new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id))));}
    public static Collection<Block> blocks(){return Collections.unmodifiableCollection(BLOCKS.values());}
    public static void initialize(){VenusRegionFeature.initialize();}
}

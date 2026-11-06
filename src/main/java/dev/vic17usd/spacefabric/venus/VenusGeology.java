package dev.vic17usd.spacefabric.venus;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.HotVolcanicRockBlock;
import dev.vic17usd.spacefabric.worldgen.PlanetBiomes;
import java.util.*;
import java.util.function.Function;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class VenusGeology {
    private static final Map<String,Block> BLOCKS=new LinkedHashMap<>();
    static {
        Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id("venus_vent"),VenusVentBlock.CODEC);
        Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id("venus_acid_film"),VenusAcidFilmBlock.CODEC);
    }
    public static final Block VENT=block("venus_vent_basalt",VenusVentBlock::new,rock(4).lightLevel(s->3));
    public static final Block SULFUR_CRUST=block("venus_sulfur_crust",HotVolcanicRockBlock::new,rock(1.5F));
    public static final Block EMBER_CRUST=block("venus_ember_crust",HotVolcanicRockBlock::new,rock(3).lightLevel(s->7));
    public static final Block ACID_POCKET=block("venus_acid_pocket",VenusAcidFilmBlock::new,rock(.3F).noCollision().noOcclusion());
    public static final Block SUBSTRATE=block("venus_basalt_substrate",Block::new,rock(4));
    public static final Block CRACKED=block("venus_cracked_stone",HotVolcanicRockBlock::new,rock(3));
    public static final Block SLAG=block("venus_obsidian_slag",Block::new,rock(20).strength(20,1200));
    public static final Block SULFUR_CRYSTAL=block("venus_sulfur_crystal_block",Block::new,rock(2).lightLevel(s->4));
    public static final Block ACID_POOL=block("venus_acid_pool",VenusAcidFilmBlock::new,rock(.3F).noCollision().noOcclusion());
    private VenusGeology() { }
    private static BlockBehaviour.Properties rock(float strength){return BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).strength(strength).requiresCorrectToolForDrops();}
    private static Block block(String name,Function<BlockBehaviour.Properties,Block> factory,BlockBehaviour.Properties p) {
        var id=SpaceFabric.id(name);var block=Registry.register(BuiltInRegistries.BLOCK,id,factory.apply(p.setId(ResourceKey.create(Registries.BLOCK,id))));
        Registry.register(BuiltInRegistries.ITEM,id,new BlockItem(block,new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).useBlockDescriptionPrefix()));BLOCKS.put(name,block);return block;
    }
    public static Map<String,Block> blocks(){return Collections.unmodifiableMap(BLOCKS);}
    public static void initialize() {
        for(var kind:VenusLandscapeFeature.Kind.values()) {
            var id=SpaceFabric.id(kind.path);
            Registry.register(BuiltInRegistries.FEATURE,id,new VenusLandscapeFeature(kind));
            BiomeModifications.addFeature(context->kind==VenusLandscapeFeature.Kind.FISSURE?PlanetBiomes.isVenus(context.getBiomeKey()):context.getBiomeKey().equals(kind==VenusLandscapeFeature.Kind.VENTS?PlanetBiomes.VENUS_FUMAROLES:PlanetBiomes.VENUS_BASINS),
                    GenerationStep.Decoration.LOCAL_MODIFICATIONS,ResourceKey.create(Registries.PLACED_FEATURE,id));
        }
    }
}

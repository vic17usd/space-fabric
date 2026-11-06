package dev.vic17usd.spacefabric.fluid;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public final class PlanetFluids {
    private PlanetFluids() { }

    public static final FlowingFluid VISCOUS_LAVA = Registry.register(BuiltInRegistries.FLUID,
            SpaceFabric.id("viscous_lava"), new ViscousLavaFluid.Source());
    public static final FlowingFluid FLOWING_VISCOUS_LAVA = Registry.register(BuiltInRegistries.FLUID,
            SpaceFabric.id("flowing_viscous_lava"), new ViscousLavaFluid.Flowing());
    public static final Block VISCOUS_LAVA_BLOCK = Registry.register(BuiltInRegistries.BLOCK,
            SpaceFabric.id("viscous_lava"), new LavaBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA)
                    .setId(ResourceKey.create(Registries.BLOCK, SpaceFabric.id("viscous_lava")))));
    public static final Item VISCOUS_LAVA_BUCKET = Registry.register(BuiltInRegistries.ITEM,
            SpaceFabric.id("viscous_lava_bucket"), new BucketItem(VISCOUS_LAVA,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                            .setId(ResourceKey.create(Registries.ITEM, SpaceFabric.id("viscous_lava_bucket")))));

    private static final class LavaBlock extends LiquidBlock {
        private LavaBlock(BlockBehaviour.Properties properties) { super(VISCOUS_LAVA, properties); }
    }

    public static void initialize() { }
}

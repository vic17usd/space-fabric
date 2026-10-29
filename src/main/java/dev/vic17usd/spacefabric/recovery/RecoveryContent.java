package dev.vic17usd.spacefabric.recovery;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.ColonyContent;
import dev.vic17usd.spacefabric.rover.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class RecoveryContent {
    static {Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id("foundry_console"),FoundryConsoleBlock.CODEC);}
    public static final Block CONSOLE=Registry.register(BuiltInRegistries.BLOCK,SpaceFabric.id("foundry_console"),
            new FoundryConsoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().strength(4,25)
                    .setId(ResourceKey.create(Registries.BLOCK,SpaceFabric.id("foundry_console")))));
    public static final Item CONSOLE_ITEM=Registry.register(BuiltInRegistries.ITEM,SpaceFabric.id("foundry_console"),new BlockItem(CONSOLE,
            new Item.Properties().useBlockDescriptionPrefix().setId(ResourceKey.create(Registries.ITEM,SpaceFabric.id("foundry_console")))));
    public static final Item CHILLED_COOLANT=Registry.register(BuiltInRegistries.ITEM,SpaceFabric.id("chilled_coolant_canister"),new Item(
            new Item.Properties().stacksTo(16).craftRemainder(ColonyContent.EMPTY_CANISTER).setId(ResourceKey.create(Registries.ITEM,SpaceFabric.id("chilled_coolant_canister")))));
    public static final Item ROVER=Registry.register(BuiltInRegistries.ITEM,SpaceFabric.id("venus_rover"),new RoverItem(
            new Item.Properties().stacksTo(1).fireResistant().setId(ResourceKey.create(Registries.ITEM,SpaceFabric.id("venus_rover")))));
    public static final BlockEntityType<FoundryConsoleBlockEntity> CONSOLE_TYPE=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,SpaceFabric.id("foundry_console"),
            FabricBlockEntityTypeBuilder.create(FoundryConsoleBlockEntity::new,CONSOLE).build());
    private RecoveryContent() { }
    public static void initialize(){RoverEntity.initialize();RoverNetwork.initialize();}
}

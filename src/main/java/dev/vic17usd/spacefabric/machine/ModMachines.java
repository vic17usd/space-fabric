package dev.vic17usd.spacefabric.machine;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModMachines {
    public static final BlockEntityType<FuelRefineryBlockEntity> FUEL_REFINERY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, SpaceFabric.id("fuel_refinery"),
            FabricBlockEntityTypeBuilder.create(FuelRefineryBlockEntity::new, ModBlocks.FUEL_REFINERY).build());
    private ModMachines() { }
    public static void initialize() { }
}

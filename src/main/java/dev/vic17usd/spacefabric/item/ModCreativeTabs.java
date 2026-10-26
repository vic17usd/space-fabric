package dev.vic17usd.spacefabric.item;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.fluid.PlanetFluids;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    public static final CreativeModeTab MATERIALS = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            SpaceFabric.id("materials"), FabricCreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.space_fabric.materials"))
                    .icon(() -> new ItemStack(ModBlocks.MARTIAN_STONE))
                    .displayItems((parameters, output) -> {
                        ModBlocks.all().values().forEach(output::accept);
                        output.accept(ModItems.ARES_CRYSTAL);
                        output.accept(ModItems.RAW_PHOBOSITE);
                        output.accept(ModItems.THERMAL_BOOTS);
                        output.accept(PlanetFluids.VISCOUS_LAVA_BUCKET);
                    }).build());

    private ModCreativeTabs() { }
    public static void initialize() { }
}

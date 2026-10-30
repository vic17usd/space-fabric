package dev.vic17usd.spacefabric.mining;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

public final class MiningContent {
    public static final Item SCANNER=register("venus_geological_scanner",true);
    public static final Item BIT=register("venus_drill_bit",false);
    private MiningContent() { }
    private static Item register(String name,boolean scanner){
        var id=SpaceFabric.id(name);var p=new Item.Properties().fireResistant().setId(ResourceKey.create(Registries.ITEM,id));
        return Registry.register(BuiltInRegistries.ITEM,id,scanner?new GeologicalScannerItem(p.stacksTo(1)):new Item(p.durability(256).component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(ProspectingSystem.text("bit_hint"))))));
    }
    public static void initialize(){ProspectingSystem.initialize();}
}

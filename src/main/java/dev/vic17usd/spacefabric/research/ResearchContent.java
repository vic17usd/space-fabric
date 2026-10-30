package dev.vic17usd.spacefabric.research;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

public final class ResearchContent {
    public static final Item LOG=register("venus_research_log",0),ROVER_MODULE=register("venus_rover_efficiency_module",1),SCANNER_LENS=register("venus_deep_survey_lens",2);
    private ResearchContent() { }
    private static Item register(String name,int kind){
        var id=SpaceFabric.id(name);var p=new Item.Properties().stacksTo(1).fireResistant().setId(ResourceKey.create(Registries.ITEM,id));
        return Registry.register(BuiltInRegistries.ITEM,id,kind==0?new ResearchLogItem(p):new ResearchModuleItem(p,kind));
    }
    public static void initialize(){ResearchNetwork.initialize();ResearchSystem.initialize();}
}

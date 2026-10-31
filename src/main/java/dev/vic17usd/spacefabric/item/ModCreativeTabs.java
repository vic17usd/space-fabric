package dev.vic17usd.spacefabric.item;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.colony.ColonyContent;
import dev.vic17usd.spacefabric.maintenance.MaintenanceContent;
import dev.vic17usd.spacefabric.mining.MiningContent;
import dev.vic17usd.spacefabric.research.ResearchContent;
import dev.vic17usd.spacefabric.transport.TransportContent;
import java.util.*;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.EntityBlock;

/** Each registered mod item appears exactly once, grouped by its primary use. */
public final class ModCreativeTabs {
    public enum Category {
        BLOCKS("blocks"), MATERIALS("materials"), TOOLS("tools"), SUITS("suits"),
        VEHICLES("vehicles"), MACHINES("machines"), SUPPLIES("supplies"), CREATURES("creatures");
        public final String id;
        Category(String id){this.id=id;}
        public ItemStack icon(){return new ItemStack(switch(this){
            case BLOCKS->ModBlocks.MARTIAN_STONE.asItem();case MATERIALS->ModItems.ARES_CRYSTAL;
            case TOOLS->MiningContent.SCANNER;case SUITS->ModItems.VENUS_SUIT_HELMET;
            case VEHICLES->ModItems.MARS_ROCKET;case MACHINES->ModBlocks.FUEL_REFINERY.asItem();
            case SUPPLIES->MaintenanceContent.FILTER;case CREATURES->dev.vic17usd.spacefabric.entity.colossus.ColossusContent.SPAWN_EGG;
        });}
    }
    private static final Map<Category,CreativeModeTab> TABS=new EnumMap<>(Category.class);
    static {
        for(var category:Category.values())TABS.put(category,Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            SpaceFabric.id(category.id),FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.space_fabric."+category.id)).icon(category::icon)
                .displayItems((parameters,output)->items(category).forEach(output::accept)).build()));
    }
    /** Preserve the existing tab registry ID; it now contains materials only. */
    public static final CreativeModeTab MATERIALS=TABS.get(Category.MATERIALS);
    private ModCreativeTabs(){}
    public static CreativeModeTab tab(Category category){return TABS.get(category);}
    public static Category category(Item item){
        String path=BuiltInRegistries.ITEM.getKey(item).getPath();
        if(item instanceof SpawnEggItem)return Category.CREATURES;
        if(item instanceof dev.vic17usd.spacefabric.equipment.SuitItem||item.components().get(DataComponents.EQUIPPABLE)!=null)return Category.SUITS;
        if(item instanceof dev.vic17usd.spacefabric.space.RocketItem||item instanceof dev.vic17usd.spacefabric.space.RocketUpgradeItem
            ||item instanceof dev.vic17usd.spacefabric.rover.RoverItem||item==ResearchContent.ROVER_MODULE)return Category.VEHICLES;
        if(item instanceof BlockItem block){
            if(block.getBlock()==ModBlocks.LANDING_PAD)return Category.VEHICLES;
            if(block.getBlock() instanceof EntityBlock||ColonyContent.blocks().containsValue(block.getBlock())
                ||TransportContent.blocks().containsValue(block.getBlock()))return Category.MACHINES;
            return Category.BLOCKS;
        }
        if(item instanceof WrittenBookItem||item==MiningContent.SCANNER||item==MiningContent.BIT||item==ResearchContent.SCANNER_LENS
            ||item.components().get(DataComponents.TOOL)!=null||item.components().get(DataComponents.WEAPON)!=null)return Category.TOOLS;
        if(MaintenanceContent.items().containsValue(item)||item instanceof BucketItem||path.endsWith("_canister")
            ||path.endsWith("_power_cell")||path.endsWith("_fuel_cell")||item.components().get(DataComponents.FOOD)!=null
            ||item==dev.vic17usd.spacefabric.mars.MarsPolarContent.HEATSTONE)return Category.SUPPLIES;
        return Category.MATERIALS;
    }
    /** Registry-driven population includes items added by future modules without duplicating them. */
    public static List<Item> items(Category category){
        return BuiltInRegistries.ITEM.stream().filter(item->BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(SpaceFabric.MOD_ID))
            .filter(item->category(item)==category).sorted(Comparator.comparing(item->BuiltInRegistries.ITEM.getKey(item).getPath())).toList();
    }
    public static void initialize(){}
}

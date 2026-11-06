package dev.vic17usd.spacefabric.industry;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.ColonyContent;
import dev.vic17usd.spacefabric.equipment.SuitItem;
import dev.vic17usd.spacefabric.item.SuitMaterials;
import java.util.*;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.equipment.*;

/** Venus industry uses the colony's vessels, power cells, menus and service system. */
public final class IndustryContent {
    private static final Map<String,Item> ITEMS=new LinkedHashMap<>();
    public static final Item ATMOSPHERE=gas("venus_atmosphere_canister");
    public static final Item CARBON_DIOXIDE=gas("carbon_dioxide_canister");
    public static final Item NITROGEN=gas("nitrogen_canister");
    public static final Item REFINED_SULFUR=material("refined_sulfur");
    public static final Item CERAMIC=material("pyroxene_ceramic");
    public static final Item COMPOSITE_PLATE=material("venus_composite_plate");
    public static final Item CARBON_POWDER=material("carbon_powder");
    public static final Item MACHINE_LINING=register("venus_machine_lining",Item::new,new Item.Properties().stacksTo(16));
    private static final ArmorMaterial REINFORCED=reinforcedMaterial();
    public static final Item HELMET=suit("helmet",ArmorType.HELMET);
    public static final Item CHESTPLATE=suit("chestplate",ArmorType.CHESTPLATE);
    public static final Item LEGGINGS=suit("leggings",ArmorType.LEGGINGS);
    public static final Item BOOTS=suit("boots",ArmorType.BOOTS);
    private IndustryContent() { }
    private static ArmorMaterial reinforcedMaterial() {
        var base=SuitMaterials.forTier("venus");
        return new ArmorMaterial(base.durability()*2,base.defense(),base.enchantmentValue(),base.equipSound(),
                base.toughness(),base.knockbackResistance(),TagKey.create(Registries.ITEM,SpaceFabric.id("repairs_reinforced_venus_suit")),base.assetId());
    }
    private static Item gas(String name){return register(name,Item::new,new Item.Properties().stacksTo(16).craftRemainder(ColonyContent.EMPTY_CANISTER));}
    private static Item material(String name){return register(name,Item::new,new Item.Properties());}
    private static Item suit(String part,ArmorType type) {
        return register("reinforced_venus_suit_"+part,SuitItem::new,new Item.Properties().humanoidArmor(REINFORCED,type).fireResistant()
                .component(DataComponents.LORE,new ItemLore(List.of(Component.translatable("item.space_fabric.suit_rating.venus"),
                        Component.translatable("industry.space_fabric.reinforced_suit")))));
    }
    private static Item register(String name,Function<Item.Properties,Item> factory,Item.Properties properties) {
        if(!name.startsWith("reinforced_venus_suit_"))properties.component(DataComponents.LORE,
                new ItemLore(List.of(Component.translatable("item.space_fabric."+name+".hint"))));
        var id=SpaceFabric.id(name);var item=Registry.register(BuiltInRegistries.ITEM,id,factory.apply(properties.setId(ResourceKey.create(Registries.ITEM,id))));
        ITEMS.put(name,item);return item;
    }
    public static Map<String,Item> items(){return Collections.unmodifiableMap(ITEMS);}
    public static void initialize() { }
}

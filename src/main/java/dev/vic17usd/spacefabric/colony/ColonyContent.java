package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public final class ColonyContent {
    // Append entries: ordinals are synchronized by the existing machine menu.
    public enum Machine { HABITAT, ELECTROLYZER, SOLAR, GEOTHERMAL, ANALYZER, ATMOSPHERIC_COLLECTOR, GAS_SEPARATOR, SULFUR_PROCESSOR, CERAMIC_KILN, COOLER, EXCHANGER }
    private static final Map<String,Block> BLOCKS=new LinkedHashMap<>();
    private static final Map<String,Item> ITEMS=new LinkedHashMap<>();
    static {Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id("colony_machine"),ColonyMachineBlock.CODEC);}
    public static final Block PRESSURE_HULL=block("pressure_hull",Block::new,BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(6,30));
    public static final Block PRESSURE_WINDOW=block("pressure_window",Block::new,BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(5,25));
    public static final Block AIRLOCK_DOOR=block("airlock_door",p->new DoorBlock(BlockSetType.IRON,p),BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR).strength(5,25));
    public static final Block MARTIAN_ICE=block("martian_ground_ice",Block::new,BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).strength(.8F));
    public static final Block HABITAT_CONTROLLER=machine("habitat_controller");
    public static final Block ELECTROLYZER=machine("water_electrolyzer");
    public static final Block SOLAR_CHARGER=machine("solar_charger");
    public static final Block GEOTHERMAL_GENERATOR=machine("geothermal_generator");
    public static final Block SAMPLE_ANALYZER=machine("sample_analyzer");
    public static final Block ATMOSPHERIC_COLLECTOR=machine("venus_atmospheric_collector");
    public static final Block GAS_SEPARATOR=machine("venus_gas_separator");
    public static final Block SULFUR_PROCESSOR=machine("venus_sulfur_processor");
    public static final Block CERAMIC_KILN=machine("venus_ceramic_kiln");
    public static final Block COOLING_UNIT=machine("venus_cooling_unit");
    public static final Block HEAT_EXCHANGER=machine("venus_heat_exchanger");
    public static final Block VENUS_COMPOSITE_HULL=block("venus_composite_hull",Block::new,BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(12,120));
    public static final Item EMPTY_CANISTER=item("empty_canister",new Item.Properties());
    public static final Item OXYGEN_CANISTER=item("oxygen_canister",new Item.Properties().stacksTo(16).craftRemainder(EMPTY_CANISTER));
    public static final Item WATER_CANISTER=item("water_canister",new Item.Properties().stacksTo(16).craftRemainder(EMPTY_CANISTER));
    public static final Item HYDROGEN_CANISTER=item("hydrogen_canister",new Item.Properties().stacksTo(16).craftRemainder(EMPTY_CANISTER));
    public static final Item EMPTY_POWER_CELL=item("empty_power_cell",new Item.Properties());
    public static final Item CHARGED_POWER_CELL=item("charged_power_cell",new Item.Properties().stacksTo(16));
    public static final Item DATA_CARD=item("blank_data_card",new Item.Properties());
    public static final Item MARS_REPORT=item("mars_research_report",new Item.Properties());
    public static final Item VENUS_REPORT=item("venus_research_report",new Item.Properties());
    public static final BlockEntityType<ColonyMachineBlockEntity> MACHINE_TYPE=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SpaceFabric.id("colony_machine"),FabricBlockEntityTypeBuilder.create(ColonyMachineBlockEntity::new,
                    HABITAT_CONTROLLER,ELECTROLYZER,SOLAR_CHARGER,GEOTHERMAL_GENERATOR,SAMPLE_ANALYZER,
                    ATMOSPHERIC_COLLECTOR,GAS_SEPARATOR,SULFUR_PROCESSOR,CERAMIC_KILN,COOLING_UNIT,HEAT_EXCHANGER).build());
    private ColonyContent() { }
    private static Block machine(String name){return block(name,ColonyMachineBlock::new,BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(5,25));}
    private static Block block(String name,Function<BlockBehaviour.Properties,Block> factory,BlockBehaviour.Properties properties) {
        var id=SpaceFabric.id(name);var block=Registry.register(BuiltInRegistries.BLOCK,id,factory.apply(properties.setId(ResourceKey.create(Registries.BLOCK,id))));
        Item item=name.equals("airlock_door")?new DoubleHighBlockItem(block,new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).useBlockDescriptionPrefix()):
                (block instanceof ColonyMachineBlock?new dev.vic17usd.spacefabric.maintenance.MaintenanceBlockItem(block,new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).useBlockDescriptionPrefix()):new BlockItem(block,new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).useBlockDescriptionPrefix()));
        Registry.register(BuiltInRegistries.ITEM,id,item);BLOCKS.put(name,block);return block;
    }
    private static Item item(String name,Item.Properties properties) {
        var id=SpaceFabric.id(name);var item=Registry.register(BuiltInRegistries.ITEM,id,new Item(properties.setId(ResourceKey.create(Registries.ITEM,id))));ITEMS.put(name,item);return item;
    }
    public static Machine kind(Block block) {
        if(block==HABITAT_CONTROLLER)return Machine.HABITAT;if(block==ELECTROLYZER)return Machine.ELECTROLYZER;
        if(block==SOLAR_CHARGER)return Machine.SOLAR;if(block==GEOTHERMAL_GENERATOR)return Machine.GEOTHERMAL;
        if(block==ATMOSPHERIC_COLLECTOR)return Machine.ATMOSPHERIC_COLLECTOR;if(block==GAS_SEPARATOR)return Machine.GAS_SEPARATOR;
        if(block==SULFUR_PROCESSOR)return Machine.SULFUR_PROCESSOR;if(block==CERAMIC_KILN)return Machine.CERAMIC_KILN;
        if(block==COOLING_UNIT)return Machine.COOLER;if(block==HEAT_EXCHANGER)return Machine.EXCHANGER;
        return Machine.ANALYZER;
    }
    public static Block block(Machine kind){return switch(kind){case HABITAT->HABITAT_CONTROLLER;case ELECTROLYZER->ELECTROLYZER;case SOLAR->SOLAR_CHARGER;case GEOTHERMAL->GEOTHERMAL_GENERATOR;case ANALYZER->SAMPLE_ANALYZER;
        case ATMOSPHERIC_COLLECTOR->ATMOSPHERIC_COLLECTOR;case GAS_SEPARATOR->GAS_SEPARATOR;case SULFUR_PROCESSOR->SULFUR_PROCESSOR;case CERAMIC_KILN->CERAMIC_KILN;case COOLER->COOLING_UNIT;case EXCHANGER->HEAT_EXCHANGER;};}
    public static Map<String,Block> blocks(){return Collections.unmodifiableMap(BLOCKS);}
    public static Map<String,Item> items(){return Collections.unmodifiableMap(ITEMS);}
    public static void initialize(){HabitatSystem.initialize();ThermalSystem.initialize();HabitatNetwork.initialize();ColonyWorldgen.initialize();}
}

package dev.vic17usd.spacefabric.transport;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import java.util.function.Function;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class TransportContent {
    private static final Map<String,Block> BLOCKS=new LinkedHashMap<>();
    private static final Map<TransportChannel,Block> PIPES=new EnumMap<>(TransportChannel.class),VALVES=new EnumMap<>(TransportChannel.class),TANKS=new EnumMap<>(TransportChannel.class);
    static {
        for(var channel:TransportChannel.values()) {
            String suffix=channel.path();
            Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id(suffix+"_conduit"),ConduitBlock.codec(channel));
            Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id(suffix+"_bulkhead"),SealedBulkheadBlock.codec(channel));
            Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id(suffix+"_tank"),TransportTankBlock.codec(channel));
            PIPES.put(channel,block(suffix+"_conduit",p->new ConduitBlock(channel,p),false));
            VALVES.put(channel,block(suffix+"_bulkhead",p->new SealedBulkheadBlock(channel,p),true));
            TANKS.put(channel,block(suffix+"_tank",p->new TransportTankBlock(channel,p),true));
        }
        Registry.register(BuiltInRegistries.BLOCK_TYPE,SpaceFabric.id("machine_interface"),MachineInterfaceBlock.CODEC);
    }
    public static final Block INTERFACE=block("machine_interface",MachineInterfaceBlock::new,true);
    public static final BlockEntityType<TransportTankBlockEntity> TANK_TYPE=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,SpaceFabric.id("transport_tank"),
            FabricBlockEntityTypeBuilder.create(TransportTankBlockEntity::new,TANKS.values().toArray(Block[]::new)).build());
    public static final BlockEntityType<MachineInterfaceBlockEntity> INTERFACE_TYPE=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,SpaceFabric.id("machine_interface"),
            FabricBlockEntityTypeBuilder.create(MachineInterfaceBlockEntity::new,INTERFACE).build());
    private static Block block(String name,Function<BlockBehaviour.Properties,Block> factory,boolean full) {
        var id=SpaceFabric.id(name);var properties=BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(full?6:2.5F,full?40:15);
        if(!full)properties.noOcclusion();
        var block=Registry.register(BuiltInRegistries.BLOCK,id,factory.apply(properties.setId(ResourceKey.create(Registries.BLOCK,id))));
        var itemProperties=new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id)).useBlockDescriptionPrefix().fireResistant();
        var item=block instanceof TransportTankBlock?new TransportTankItem(block,itemProperties):block instanceof SealedBulkheadBlock?new SealedBulkheadItem(block,itemProperties):new BlockItem(block,itemProperties);
        Registry.register(BuiltInRegistries.ITEM,id,item);BLOCKS.put(name,block);return block;
    }
    public static Block pipe(TransportChannel channel){return PIPES.get(channel);}
    public static Block valve(TransportChannel channel){return VALVES.get(channel);}
    public static Block tank(TransportChannel channel){return TANKS.get(channel);}
    public static Map<String,Block> blocks(){return Collections.unmodifiableMap(BLOCKS);}
    private TransportContent() { }
    public static void initialize(){TransportService.initialize();}
}

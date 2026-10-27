package dev.vic17usd.spacefabric.colony;

import java.util.*;
import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class HabitatSystem {
    public static final TagKey<Block> PRESSURE_RATED=TagKey.create(Registries.BLOCK,SpaceFabric.id("pressure_rated_shell"));
    private static final Map<ServerLevel,Map<BlockPos,ColonyMachineBlockEntity>> ACTIVE=new IdentityHashMap<>();
    private HabitatSystem() { }
    public static void initialize(){ServerLifecycleEvents.SERVER_STOPPED.register(server->ACTIVE.clear());}
    static void register(ServerLevel level,ColonyMachineBlockEntity machine){ACTIVE.computeIfAbsent(level,k->new HashMap<>()).put(machine.getBlockPos(),machine);}
    static void remove(ColonyMachineBlockEntity machine){if(machine.getLevel() instanceof ServerLevel level){var map=ACTIVE.get(level);if(map!=null)map.remove(machine.getBlockPos(),machine);}}
    public static boolean protects(ServerPlayer player) {
        var planet=dev.vic17usd.spacefabric.planet.PlanetDimensions.find(player.level()).orElse(dev.vic17usd.spacefabric.planet.Planet.EARTH);
        if(planet!=dev.vic17usd.spacefabric.planet.Planet.EARTH&&planet!=dev.vic17usd.spacefabric.planet.Planet.MARS&&planet!=dev.vic17usd.spacefabric.planet.Planet.VENUS)return false;
        var map=ACTIVE.get(player.level());if(map==null)return false;
        var head=BlockPos.containing(player.getEyePosition());
        for(var machine:map.values())if(!machine.isRemoved()&&machine.getBlockPos().distManhattan(head)<=3*SealScanner.MAX_RADIUS+1&&machine.protects(head))return true;
        return false;
    }
    /** Prefer the actual room; a nearby controller is only a diagnostic fallback. */
    public static ColonyMachineBlockEntity controllerFor(ServerPlayer player) {
        var machines=ACTIVE.get(player.level());if(machines==null||!player.isAlive()||player.isSpectator())return null;
        var head=BlockPos.containing(player.getEyePosition());
        ColonyMachineBlockEntity best=null;int score=Integer.MAX_VALUE;
        for(var machine:machines.values()) {
            if(machine.isRemoved())continue;
            int distance=machine.getBlockPos().distManhattan(head);
            if(distance>3*SealScanner.MAX_RADIUS+1)continue;
            int candidate=machine.protects(head)?distance:machine.monitors(head)?100+distance:distance<=6?200+distance:Integer.MAX_VALUE;
            if(candidate<score){best=machine;score=candidate;}
        }
        return best;
    }
    static SealScanner.World world(ServerLevel level) {
        return pos->{
            if(pos.getY()<level.getMinY()||pos.getY()>=level.getMaxY())return SealScanner.Cell.UNLOADED;
            if(level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null)return SealScanner.Cell.UNLOADED;
            return classify(level.getBlockState(pos),level,pos);
        };
    }
    public static SealScanner.Cell classify(net.minecraft.world.level.block.state.BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos) {
        if(state.is(ColonyContent.AIRLOCK_DOOR))return state.getValue(BlockStateProperties.OPEN)?SealScanner.Cell.OPEN:SealScanner.Cell.RATED;
        if(state.is(PRESSURE_RATED))return SealScanner.Cell.RATED;
        if(!state.getFluidState().isEmpty()||state.isCollisionShapeFullBlock(level,pos))return SealScanner.Cell.SOLID;
        return SealScanner.Cell.OPEN;
    }
}

package dev.vic17usd.spacefabric.transport;

import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.planet.*;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/** A single bounded pass per second: harvest first, then supply habitats before industry. */
public final class TransportService {
    public static final int MAX_PORTS=64;
    private static final Map<ServerLevel,Map<BlockPos,MachineInterfaceBlockEntity>> ACTIVE=new IdentityHashMap<>();
    private static final Set<Direction> ALL=Set.copyOf(Arrays.asList(Direction.values()));
    private TransportService() { }
    public static void register(ServerLevel level,MachineInterfaceBlockEntity port){ACTIVE.computeIfAbsent(level,k->new LinkedHashMap<>()).put(port.getBlockPos(),port);}
    public static void remove(MachineInterfaceBlockEntity port){if(port.getLevel() instanceof ServerLevel level){var map=ACTIVE.get(level);if(map!=null)map.remove(port.getBlockPos(),port);}}
    /** Read-only tutorial observation of a loaded port that actually reached its supply-ready state. */
    public static Set<ColonyMachineBlockEntity> servingMachines(ServerLevel level) {
        var result=Collections.newSetFromMap(new IdentityHashMap<ColonyMachineBlockEntity,Boolean>());
        var ports=ACTIVE.get(level);if(ports==null)return result;
        for(var p:ports.values())if(!p.isRemoved()
                &&level.getChunkSource().getChunkNow(p.getBlockPos().getX()>>4,p.getBlockPos().getZ()>>4)!=null
                &&p.enabled()&&p.status()==MachineInterfaceBlockEntity.Status.WORKING){var target=p.target();if(target!=null)result.add(target);}
        return result;
    }
    public static TransportGraph.World world(ServerLevel level) {
        return new TransportGraph.World() {
            private final Map<BlockPos,TransportGraph.Node> cache=new HashMap<>();
            private boolean inside(BlockPos pos){return pos.getY()>=level.getMinY()&&pos.getY()<level.getMaxY();}
            public boolean loaded(BlockPos pos){return !inside(pos)||level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null;}
            public TransportGraph.Node node(BlockPos pos) {
                if(!inside(pos))return null; // Known world boundary, not an unknown/unloaded chunk.
                if(!loaded(pos))throw new IllegalStateException("Transport must never inspect an unloaded chunk");
                if(cache.containsKey(pos))return cache.get(pos);
                var state=level.getBlockState(pos);TransportGraph.Node node=null;
                if(state.getBlock() instanceof ConduitBlock pipe)node=new TransportGraph.Node(pipe.channel(),ALL,null);
                else if(state.getBlock() instanceof SealedBulkheadBlock valve) {
                    var faces=EnumSet.noneOf(Direction.class);
                    if(SealedBulkheadBlock.open(state))for(var direction:Direction.values())if(direction.getAxis()==state.getValue(SealedBulkheadBlock.AXIS))faces.add(direction);
                    node=new TransportGraph.Node(valve.channel(),faces,null);
                } else if(state.getBlock() instanceof TransportTankBlock&&level.getBlockEntity(pos) instanceof TransportTankBlockEntity tank)
                    node=new TransportGraph.Node(tank.buffer().channel(),ALL,new TransportGraph.Endpoint(pos.immutable(),tank.buffer(),tank::setChanged));
                cache.put(pos.immutable(),node);return node;
            }
        };
    }
    public static List<TransportGraph> connections(TransportGraph.World world,BlockPos pos,Direction machineFace) {
        var graphs=new ArrayList<TransportGraph>();
        for(var direction:Direction.values()) {
            if(direction==machineFace)continue;var start=pos.relative(direction);
            if(graphs.stream().anyMatch(g->g.nodes().contains(start)))continue;
            if(!world.loaded(start))continue;
            var node=world.node(start);if(node!=null)graphs.add(TransportGraph.scan(world,start,node.channel(),direction.getOpposite()));
        }
        return List.copyOf(graphs);
    }
    /** Stable priority with rotation inside both groups so factories do not depend on placement order. */
    public static <T> List<T> prioritize(List<T> values,java.util.function.Predicate<T> habitat,long cycle) {
        var life=new ArrayList<T>();var industry=new ArrayList<T>();
        values.forEach(v->{if(habitat.test(v))life.add(v);else industry.add(v);});
        for(var list:List.of(life,industry))if(!list.isEmpty())Collections.rotate(list,-Math.floorMod(Math.floorMod(cycle,list.size())*(long)(MAX_PORTS/2),list.size()));
        int lifeCount=Math.min(life.size(),MAX_PORTS/2),industryCount=Math.min(industry.size(),MAX_PORTS-lifeCount);
        lifeCount=Math.min(life.size(),MAX_PORTS-industryCount);
        var selected=new ArrayList<T>(life.subList(0,lifeCount));selected.addAll(industry.subList(0,industryCount));return List.copyOf(selected);
    }
    private record Job(MachineInterfaceBlockEntity port,ColonyMachineBlockEntity target,List<TransportGraph> graphs) { }
    private static void tick(ServerLevel level) {
        if(level.getGameTime()%20!=0)return;var active=ACTIVE.get(level);if(active==null)return;
        active.entrySet().removeIf(e->e.getValue().isRemoved()||level.getChunkSource().getChunkNow(e.getKey().getX()>>4,e.getKey().getZ()>>4)==null);
        var ordered=prioritize(new ArrayList<>(active.values()),p->{var target=p.target();return target!=null&&target.kind()==ColonyContent.Machine.HABITAT;},level.getGameTime()/20);
        var world=world(level);var jobs=new ArrayList<Job>();
        for(var port:ordered) {
            var target=port.target();var graphs=port.enabled()&&target!=null?connections(world,port.getBlockPos(),port.getBlockState().getValue(MachineInterfaceBlock.FACING)):List.<TransportGraph>of();
            jobs.add(new Job(port,target,graphs));
        }
        for(var job:jobs)if(job.target()!=null)job.port().exportOutputs(job.target(),job.graphs());
        boolean venus=PlanetDimensions.find(level).orElse(null)==Planet.VENUS;
        for(var job:jobs)job.port().supplyInputs(job.target(),job.graphs(),venus);
    }
    public static Component describe(ServerLevel level,BlockPos pos,TransportChannel channel) {
        var graph=TransportGraph.scan(world(level),pos,channel,Direction.DOWN);
        var problem=graph.problem();
        if(problem!=TransportGraph.Problem.NONE)return Component.translatable("transport.space_fabric.graph_"+problem.name().toLowerCase(Locale.ROOT));
        var resource=graph.stores().stream().map(e->e.buffer().state().resource()).filter(r->r!=TransportResource.NONE).findFirst().orElse(TransportResource.NONE);
        int amount=graph.stores().stream().mapToInt(e->e.buffer().state().amount()).sum();
        return Component.translatable("transport.space_fabric.network",Component.translatable(resource.key()),graph.nodes().size(),amount);
    }
    public static void initialize(){ServerTickEvents.END_LEVEL_TICK.register(TransportService::tick);ServerLifecycleEvents.SERVER_STOPPED.register(server->ACTIVE.clear());}
}

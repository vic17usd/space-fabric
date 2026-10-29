package dev.vic17usd.spacefabric.transport;

import java.util.*;
import java.util.function.Predicate;
import net.minecraft.core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransportSafetyTest {
    private static final Set<Direction> ALL=Set.copyOf(Arrays.asList(Direction.values()));
    private static class World implements TransportGraph.World {
        final Map<BlockPos,TransportGraph.Node> nodes=new HashMap<>();Predicate<BlockPos> loaded=p->true;int reads,dirty;
        public boolean loaded(BlockPos p){return loaded.test(p);}
        public TransportGraph.Node node(BlockPos p){assertTrue(loaded(p),"Unloaded chunks must never be inspected");reads++;return nodes.get(p);}
        void pipe(BlockPos p,TransportChannel channel){nodes.put(p,new TransportGraph.Node(channel,ALL,null));}
        TransportBuffer tank(BlockPos p,TransportResource resource,int amount) {
            var tank=new TransportBuffer(resource.channel);tank.restore(new TransportBuffer.State(resource,amount));
            nodes.put(p,new TransportGraph.Node(resource.channel,ALL,new TransportGraph.Endpoint(p,tank,()->dirty++)));return tank;
        }
        TransportGraph scan(BlockPos p,TransportChannel kind){return TransportGraph.scan(this,p,kind,Direction.DOWN);}
    }
    @Test void tankCapacityAndContentLockSurviveEmptyingAndMalformedLoads() {
        var tank=new TransportBuffer(TransportChannel.GAS);assertEquals(16000,tank.free(TransportResource.OXYGEN));
        tank.insert(TransportResource.OXYGEN,1000);assertEquals(0,tank.free(TransportResource.NITROGEN));
        assertThrows(IllegalArgumentException.class,()->tank.insert(TransportResource.NITROGEN,1000));
        assertFalse(tank.select(TransportResource.NITROGEN));tank.extract(TransportResource.OXYGEN,1000);
        assertEquals(TransportResource.OXYGEN,tank.state().resource());assertEquals(0,tank.free(TransportResource.NITROGEN));
        assertTrue(tank.select(TransportResource.NITROGEN));tank.restore(new TransportBuffer.State(TransportResource.NITROGEN,Integer.MAX_VALUE));assertEquals(16000,tank.state().amount());
        tank.restore(new TransportBuffer.State(TransportResource.POWER,24000));assertEquals(TransportBuffer.State.EMPTY,tank.state());
        var power=new TransportBuffer(TransportChannel.ENERGY);power.restore(TransportBuffer.State.EMPTY);assertEquals(TransportResource.POWER,power.state().resource());
        assertEquals(0,new TransportBuffer.State(TransportResource.NONE,10000).amount());
    }
    @Test void loopsAndRepeatedPathsNeverCountTheSameTankTwiceAndFailedTransfersDoNothing() {
        var w=new World();for(var p:List.of(BlockPos.ZERO,BlockPos.ZERO.east(),BlockPos.ZERO.south(),BlockPos.ZERO.east().south()))w.pipe(p,TransportChannel.GAS);
        var a=w.tank(new BlockPos(-1,0,0),TransportResource.OXYGEN,1000);var b=w.tank(new BlockPos(2,0,1),TransportResource.OXYGEN,1000);
        var first=w.scan(BlockPos.ZERO,TransportChannel.GAS);var second=w.scan(BlockPos.ZERO.east(),TransportChannel.GAS);
        assertEquals(6,first.nodes().size());assertEquals(2,first.stores().size());
        assertFalse(TransportGraph.transfer(List.of(first,second),TransportResource.OXYGEN,3000,false));assertEquals(2000,a.state().amount()+b.state().amount());assertEquals(0,w.dirty);
        assertTrue(TransportGraph.transfer(List.of(first,second),TransportResource.OXYGEN,2000,false));assertEquals(0,a.state().amount()+b.state().amount());assertEquals(2,w.dirty);
        assertFalse(TransportGraph.transfer(List.of(first),TransportResource.NITROGEN,1000,true));
        assertTrue(TransportGraph.transfer(List.of(first),TransportResource.OXYGEN,500,true));assertEquals(500,a.state().amount()+b.state().amount());
    }
    @Test void incompatibleLockedGasTanksStopTheWholeComponentEvenIfBothAreEmpty() {
        var w=new World();w.pipe(BlockPos.ZERO,TransportChannel.GAS);var a=w.tank(BlockPos.ZERO.west(),TransportResource.OXYGEN,0);var b=w.tank(BlockPos.ZERO.east(),TransportResource.HYDROGEN,0);
        var graph=w.scan(BlockPos.ZERO,TransportChannel.GAS);assertEquals(TransportGraph.Problem.MIXED,graph.problem());
        assertFalse(TransportGraph.transfer(List.of(graph),TransportResource.OXYGEN,1000,true));assertEquals(0,a.state().amount()+b.state().amount());
        b.select(TransportResource.NONE);assertEquals(TransportGraph.Problem.NONE,graph.problem());assertTrue(TransportGraph.transfer(List.of(graph),TransportResource.OXYGEN,1000,true));
    }
    @Test void closingAnAxialValveDisconnectsStorageAndCrossChannelPipesCannotBridge() {
        var w=new World();w.pipe(BlockPos.ZERO,TransportChannel.GAS);
        w.nodes.put(BlockPos.ZERO.east(),new TransportGraph.Node(TransportChannel.GAS,Set.of(),null));
        var tank=w.tank(BlockPos.ZERO.east(2),TransportResource.OXYGEN,1000);
        assertEquals(TransportGraph.Problem.NO_STORAGE,w.scan(BlockPos.ZERO,TransportChannel.GAS).problem());
        w.nodes.put(BlockPos.ZERO.east(),new TransportGraph.Node(TransportChannel.GAS,Set.of(Direction.WEST,Direction.EAST),null));
        assertTrue(TransportGraph.transfer(List.of(w.scan(BlockPos.ZERO,TransportChannel.GAS)),TransportResource.OXYGEN,1000,false));assertEquals(0,tank.state().amount());
        w.pipe(BlockPos.ZERO.east(),TransportChannel.LIQUID);assertEquals(TransportGraph.Problem.NO_STORAGE,w.scan(BlockPos.ZERO,TransportChannel.GAS).problem());
    }
    @Test void unloadingAnyReachableBoundaryFailsClosedWithoutReadingOrMutatingTheUnloadedArea() {
        var w=new World();w.pipe(BlockPos.ZERO,TransportChannel.GAS);var tank=w.tank(BlockPos.ZERO.west(),TransportResource.OXYGEN,1000);
        w.loaded=p->p.getX()<1;
        var graph=w.scan(BlockPos.ZERO,TransportChannel.GAS);assertEquals(TransportGraph.Problem.UNLOADED,graph.problem());
        assertFalse(TransportGraph.transfer(List.of(graph),TransportResource.OXYGEN,1000,false));assertEquals(1000,tank.state().amount());
        int reads=w.reads;assertEquals(TransportGraph.Problem.UNLOADED,w.scan(new BlockPos(16,0,0),TransportChannel.GAS).problem());assertEquals(reads,w.reads);
    }
    @Test void oversizedNetworksStopWithinTheirFixedBudget() {
        var w=new World();for(int x=0;x<2000;x++)w.pipe(new BlockPos(x,0,0),TransportChannel.ENERGY);
        var graph=w.scan(BlockPos.ZERO,TransportChannel.ENERGY);assertEquals(TransportGraph.Problem.TOO_LARGE,graph.problem());
        assertEquals(128,graph.nodes().size());assertTrue(w.reads<1100);assertFalse(TransportGraph.transfer(List.of(graph),TransportResource.POWER,2400,true));
    }
    @Test void habitatPriorityAndRotatingBudgetsNeverPermanentlyStarveTheOtherGroup() {
        var values=new ArrayList<String>();for(int i=0;i<100;i++){values.add("industry"+i);values.add("habitat"+i);}
        var first=TransportService.prioritize(values,v->v.startsWith("habitat"),0);assertEquals(64,first.size());
        assertTrue(first.subList(0,32).stream().allMatch(v->v.startsWith("habitat")));assertTrue(first.subList(32,64).stream().allMatch(v->v.startsWith("industry")));
        var serviced=new HashSet<String>();for(int cycle=0;cycle<100;cycle++)serviced.addAll(TransportService.prioritize(values,v->v.startsWith("habitat"),cycle));
        assertEquals(new HashSet<>(values),serviced);
        assertEquals(List.of("habitat","industry"),TransportService.prioritize(List.of("industry","habitat"),v->v.equals("habitat"),0));
    }
}

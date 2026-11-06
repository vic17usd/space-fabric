package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.transport.*;
import java.util.*;
import net.minecraft.core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsReserveNetworkTest {
    @Test void connectedStoresAreDeduplicatedBeforeConvertingCompleteCanisters() {
        var a=new BlockPos(0,0,0); var b=new BlockPos(1,0,0);
        var first=new TransportBuffer(TransportChannel.GAS); first.insert(TransportResource.OXYGEN,700);
        var second=new TransportBuffer(TransportChannel.GAS); second.insert(TransportResource.OXYGEN,800);
        var nodes=Map.of(a,new TransportGraph.Node(TransportChannel.GAS,Set.of(Direction.EAST),new TransportGraph.Endpoint(a,first,()->{})),
                b,new TransportGraph.Node(TransportChannel.GAS,Set.of(Direction.WEST),new TransportGraph.Endpoint(b,second,()->{})));
        TransportGraph.World world=new TransportGraph.World() { public boolean loaded(BlockPos p){return true;} public TransportGraph.Node node(BlockPos p){return nodes.get(p);} };
        var left=TransportGraph.scan(world,a,TransportChannel.GAS,Direction.EAST);
        var right=TransportGraph.scan(world,b,TransportChannel.GAS,Direction.WEST);
        assertTrue(left.accepts(TransportResource.OXYGEN));
        var counted=new HashMap<BlockPos,Long>();
        MarsReserveNetwork.add(counted,left,TransportResource.OXYGEN); MarsReserveNetwork.add(counted,right,TransportResource.OXYGEN);
        assertEquals(2,counted.size()); assertEquals(2400,MarsReserveNetwork.allocate(counted,new HashMap<>(),TransportResource.OXYGEN));
        assertEquals(700,first.available(TransportResource.OXYGEN)); assertEquals(800,second.available(TransportResource.OXYGEN));
    }
    @Test void disconnectedFragmentsCannotBeCombinedAndSharedStoresAreUsedOnce() {
        var a=new BlockPos(0,0,0); var b=new BlockPos(1,0,0); var used=new HashMap<BlockPos,Long>();
        assertEquals(0,MarsReserveNetwork.allocate(Map.of(a,700L),used,TransportResource.OXYGEN));
        assertEquals(0,MarsReserveNetwork.allocate(Map.of(b,800L),used,TransportResource.OXYGEN));
        assertEquals(2400,MarsReserveNetwork.allocate(Map.of(a,700L,b,800L),used,TransportResource.OXYGEN));
        assertEquals(0,MarsReserveNetwork.allocate(Map.of(a,700L,b,800L),used,TransportResource.OXYGEN));
    }
    @Test void unloadedAndMixedNetworksCannotSupply() {
        var pos=BlockPos.ZERO; var a=new TransportBuffer(TransportChannel.GAS); a.insert(TransportResource.OXYGEN,1000);
        var node=new TransportGraph.Node(TransportChannel.GAS,Set.of(Direction.EAST),new TransportGraph.Endpoint(pos,a,()->{}));
        TransportGraph.World world=new TransportGraph.World(){public boolean loaded(BlockPos p){return p.equals(pos);} public TransportGraph.Node node(BlockPos p){return node;}};
        var graph=TransportGraph.scan(world,pos,TransportChannel.GAS,Direction.EAST);
        assertEquals(TransportGraph.Problem.UNLOADED,graph.problem()); assertFalse(graph.accepts(TransportResource.OXYGEN));
    }
    @Test void mixedGasNetworkCannotBeCounted() {
        var a=BlockPos.ZERO; var b=a.east();
        var oxygen=new TransportBuffer(TransportChannel.GAS); oxygen.insert(TransportResource.OXYGEN,1000);
        var hydrogen=new TransportBuffer(TransportChannel.GAS); hydrogen.insert(TransportResource.HYDROGEN,1000);
        var nodes=Map.of(a,new TransportGraph.Node(TransportChannel.GAS,Set.of(Direction.EAST),new TransportGraph.Endpoint(a,oxygen,()->{})),
                b,new TransportGraph.Node(TransportChannel.GAS,Set.of(Direction.WEST),new TransportGraph.Endpoint(b,hydrogen,()->{})));
        TransportGraph.World world=new TransportGraph.World(){public boolean loaded(BlockPos p){return true;} public TransportGraph.Node node(BlockPos p){return nodes.get(p);}};
        var graph=TransportGraph.scan(world,a,TransportChannel.GAS,Direction.EAST);
        assertEquals(TransportGraph.Problem.MIXED,graph.problem()); assertFalse(graph.accepts(TransportResource.OXYGEN));
    }
}

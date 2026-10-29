package dev.vic17usd.spacefabric.transport;

import java.util.*;
import net.minecraft.core.*;

/** Loaded-only, bounded topology. Pipes have no in-flight inventory to lose or duplicate. */
public final class TransportGraph {
    public static final int MAX_NODES=128;
    public enum Problem { NONE,NO_STORAGE,UNLOADED,TOO_LARGE,MIXED }
    public record Endpoint(BlockPos position,TransportBuffer buffer,Runnable changed) { }
    public record Node(TransportChannel channel,Set<Direction> faces,Endpoint storage) {
        public Node {faces=Set.copyOf(faces);}
    }
    public interface World {boolean loaded(BlockPos pos);Node node(BlockPos pos);}
    private final Set<BlockPos> nodes;
    private final List<Endpoint> stores;
    private final Problem topology;
    private TransportGraph(Set<BlockPos> nodes,List<Endpoint> stores,Problem topology){this.nodes=Set.copyOf(nodes);this.stores=List.copyOf(stores);this.topology=topology;}
    public Set<BlockPos> nodes(){return nodes;}
    public List<Endpoint> stores(){return stores;}
    public Problem problem() {
        if(topology!=Problem.NONE)return topology;
        if(stores.isEmpty())return Problem.NO_STORAGE;
        return stores.stream().map(e->e.buffer().state().resource()).filter(r->r!=TransportResource.NONE).distinct().limit(2).count()>1?Problem.MIXED:Problem.NONE;
    }
    public boolean accepts(TransportResource resource) {
        return problem()==Problem.NONE&&stores.stream().allMatch(e->e.buffer().channel()==resource.channel
                &&(e.buffer().state().resource()==TransportResource.NONE||e.buffer().state().resource()==resource));
    }
    public static TransportGraph scan(World world,BlockPos start,TransportChannel channel,Direction entryFace) {
        var seen=new LinkedHashSet<BlockPos>();var stores=new ArrayList<Endpoint>();var queue=new ArrayDeque<BlockPos>();
        if(!world.loaded(start))return new TransportGraph(seen,stores,Problem.UNLOADED);
        var first=world.node(start);if(first==null||first.channel()!=channel||!first.faces().contains(entryFace))return new TransportGraph(seen,stores,Problem.NO_STORAGE);
        queue.add(start.immutable());seen.add(start.immutable());Problem problem=Problem.NONE;
        while(!queue.isEmpty()) {
            var pos=queue.removeFirst();var node=world.node(pos);
            if(node.storage()!=null)stores.add(node.storage());
            for(var face:Direction.values()) {
                if(!node.faces().contains(face))continue;
                var next=pos.relative(face);
                if(!world.loaded(next)){problem=Problem.UNLOADED;continue;}
                var other=world.node(next);
                if(other==null||other.channel()!=channel||!other.faces().contains(face.getOpposite())||seen.contains(next))continue;
                if(seen.size()>=MAX_NODES)return new TransportGraph(seen,stores,Problem.TOO_LARGE);
                seen.add(next.immutable());queue.addLast(next.immutable());
            }
        }
        return new TransportGraph(seen,stores,problem);
    }
    /** Deduplicate stores reached by branches/loops, and preflight the entire amount before mutation. */
    public static boolean transfer(List<TransportGraph> graphs,TransportResource resource,int amount,boolean insert) {
        if(resource==TransportResource.NONE||amount<=0)return false;
        var stores=new LinkedHashMap<BlockPos,Endpoint>();
        for(var graph:graphs)if(graph.accepts(resource))for(var endpoint:graph.stores())stores.putIfAbsent(endpoint.position(),endpoint);
        long capacity=stores.values().stream().mapToLong(e->insert?e.buffer().free(resource):e.buffer().available(resource)).sum();
        if(capacity<amount)return false;
        int remaining=amount;var changed=new ArrayList<Endpoint>();
        for(var endpoint:stores.values()) {
            int moved=Math.min(remaining,insert?endpoint.buffer().free(resource):endpoint.buffer().available(resource));
            if(moved==0)continue;
            if(insert)endpoint.buffer().insert(resource,moved);else endpoint.buffer().extract(resource,moved);
            changed.add(endpoint);remaining-=moved;if(remaining==0)break;
        }
        changed.forEach(e->e.changed().run());return true;
    }
}

package dev.vic17usd.spacefabric.colony;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/** Bounded gas flood-fill. No chunk loading, light-level guesses or spherical safe zones. */
public final class SealScanner {
    public static final int MAX_VOLUME=512, MAX_RADIUS=12;
    public enum Cell { OPEN, SOLID, RATED, UNLOADED }
    public enum Status { SEALED, LEAK, TOO_LARGE, UNLOADED, WEAK_WALL, BLOCKED_VENT }
    public interface World { Cell cell(BlockPos pos); }
    public record Room(Status status,Set<BlockPos> air,Map<BlockPos,Cell> boundary) {
        public boolean sealed(){return status==Status.SEALED;}
        public boolean unchanged(World world) {
            return sealed() && air.stream().allMatch(p->world.cell(p)==Cell.OPEN)
                    && boundary.entrySet().stream().allMatch(e->world.cell(e.getKey())==e.getValue());
        }
    }
    private SealScanner() { }
    public static Room scan(World world,BlockPos vent,boolean highPressure) {
        var air=new HashSet<BlockPos>();var boundary=new HashMap<BlockPos,Cell>();var queue=new ArrayDeque<BlockPos>();
        if(world.cell(vent)!=Cell.OPEN)return fail(Status.BLOCKED_VENT);
        air.add(vent.immutable());queue.add(vent.immutable());
        while(!queue.isEmpty()) {
            var pos=queue.removeFirst();
            if(Math.abs(pos.getX()-vent.getX())>MAX_RADIUS||Math.abs(pos.getY()-vent.getY())>MAX_RADIUS||Math.abs(pos.getZ()-vent.getZ())>MAX_RADIUS)
                return fail(Status.LEAK);
            for(var direction:Direction.values()) {
                var next=pos.relative(direction);var cell=world.cell(next);
                if(cell==Cell.UNLOADED)return fail(Status.UNLOADED);
                if(cell!=Cell.OPEN) {
                    boundary.put(next,cell);continue;
                }
                if(air.add(next)) {
                    if(air.size()>MAX_VOLUME)return fail(Status.TOO_LARGE);
                    queue.add(next);
                }
            }
        }
        if(highPressure) {
            // Check the enclosing shell separately: a crafting table inside a rated habitat is not a weak outer wall.
            var checked=new HashSet<BlockPos>();var pressure=new ArrayDeque<BlockPos>();checked.add(vent);pressure.add(vent);
            while(!pressure.isEmpty()) {
              var position=pressure.removeFirst();
              for(var direction:Direction.values()) {
                var next=position.relative(direction);var cell=world.cell(next);
                if(cell==Cell.UNLOADED)return fail(Status.UNLOADED);
                if(cell==Cell.RATED){boundary.put(next,cell);continue;}
                if(checked.add(next)) {
                    if(checked.size()>MAX_VOLUME)return fail(Status.WEAK_WALL);
                    if(cell==Cell.SOLID)boundary.put(next,cell);
                    pressure.addLast(next);
                }
              }
            }
        }
        return new Room(Status.SEALED,Set.copyOf(air),Map.copyOf(boundary));
    }
    private static Room fail(Status status){return new Room(status,Set.of(),Map.of());}
}

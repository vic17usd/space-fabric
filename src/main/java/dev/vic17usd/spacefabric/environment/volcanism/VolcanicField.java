package dev.vic17usd.spacefabric.environment.volcanism;

import java.util.*;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;

/** Bounded source registry, shared by all players in a dimension. No world or entity access. */
public final class VolcanicField {
    public static final int MAX_TRACKED=24,MAX_ALERTS=4;
    private final LinkedHashMap<BlockPos,VolcanicCycle> sources=new LinkedHashMap<>();
    public Map<BlockPos,VolcanicCycle> sources(){return Collections.unmodifiableMap(sources);}
    public boolean discover(BlockPos pos,VolcanicCycle.Kind kind,long seed){
        if(sources.size()>=MAX_TRACKED||sources.keySet().stream().anyMatch(p->p.distSqr(pos)<100))return false;
        long value=net.minecraft.util.Mth.getSeed(pos)^seed;
        sources.put(pos.immutable(),new VolcanicCycle(kind,100+Math.floorMod(value,201),Math.floorMod(value>>>16,601)));return true;
    }
    public void tick(Predicate<BlockPos> valid){
        sources.keySet().removeIf(p->!valid.test(p));
        int alerts=(int)sources.values().stream().filter(VolcanicCycle::alert).count();
        for(var cycle:sources.values()){
            boolean before=cycle.alert();cycle.tick(alerts<MAX_ALERTS);
            if(before!=cycle.alert())alerts+=cycle.alert()?1:-1;
        }
    }
}

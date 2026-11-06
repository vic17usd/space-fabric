package dev.vic17usd.spacefabric.recovery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** Server-owned, per-natural-installation reward ledger. Moving a console cannot reset it. */
public final class FoundryLedger extends SavedData {
    public record Site(BlockPos anchor,List<BlockPos> machines,boolean activated) {
        public Site {anchor=anchor.immutable();machines=List.copyOf(machines);if(machines.size()!=3)throw new IllegalArgumentException("Three recovery units required");}
        public static final Codec<Site> CODEC=RecordCodecBuilder.create(i->i.group(BlockPos.CODEC.fieldOf("anchor").forGetter(Site::anchor),
                BlockPos.CODEC.listOf(3,3).fieldOf("machines").forGetter(Site::machines),Codec.BOOL.optionalFieldOf("activated",false).forGetter(Site::activated)).apply(i,Site::new));
    }
    public static final Codec<FoundryLedger> CODEC=Site.CODEC.listOf().fieldOf("sites").xmap(FoundryLedger::new,l->List.copyOf(l.sites.values())).codec();
    public static final SavedDataType<FoundryLedger> TYPE=new SavedDataType<>(SpaceFabric.id("venus_foundries"),FoundryLedger::new,CODEC,DataFixTypes.LEVEL);
    private final Map<BlockPos,Site> sites=new LinkedHashMap<>();
    public FoundryLedger(){ }
    private FoundryLedger(List<Site> entries){entries.forEach(s->sites.put(s.anchor(),s));}
    public static FoundryLedger get(ServerLevel level){return level.getServer().getDataStorage().computeIfAbsent(TYPE);}
    public Site enroll(BlockPos anchor,List<BlockPos> machines) {
        var previous=sites.get(anchor);if(previous!=null)return previous;
        var site=new Site(anchor,machines,false);sites.put(site.anchor(),site);setDirty();return site;
    }
    public Optional<Site> site(BlockPos anchor){return Optional.ofNullable(sites.get(anchor));}
    public Optional<Site> nearby(BlockPos pos){return sites.values().stream().filter(s->s.anchor().distSqr(pos)<=256).min(Comparator.comparingDouble(s->s.anchor().distSqr(pos)));}
    public boolean activate(BlockPos anchor) {
        var site=sites.get(anchor);if(site==null||site.activated())return false;
        sites.put(anchor,new Site(anchor,site.machines(),true));setDirty();return true;
    }
}

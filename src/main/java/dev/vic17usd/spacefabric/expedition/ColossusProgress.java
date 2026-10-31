package dev.vic17usd.spacefabric.expedition;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** Personal evidence and destination, stored with the server rather than in a copyable book. */
public final class ColossusProgress extends SavedData {
    public record Entry(UUID player,int stage,Optional<BlockPos> target){
        public static final Codec<Entry> CODEC=RecordCodecBuilder.create(i->i.group(
            net.minecraft.core.UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
            Codec.intRange(0,5).fieldOf("stage").forGetter(Entry::stage),
            BlockPos.CODEC.optionalFieldOf("target").forGetter(Entry::target)).apply(i,Entry::new));
    }
    public static final Codec<ColossusProgress> CODEC=Entry.CODEC.listOf().fieldOf("players").xmap(ColossusProgress::new,p->List.copyOf(p.entries.values())).codec();
    public static final SavedDataType<ColossusProgress> TYPE=new SavedDataType<>(SpaceFabric.id("colossus_expedition"),ColossusProgress::new,CODEC,DataFixTypes.LEVEL);
    private final Map<UUID,Entry> entries=new LinkedHashMap<>();
    public ColossusProgress(){}
    private ColossusProgress(List<Entry> values){values.forEach(e->entries.put(e.player,e));}
    public static ColossusProgress get(ServerLevel level){return level.getServer().getDataStorage().computeIfAbsent(TYPE);}
    public Entry entry(UUID player){return entries.getOrDefault(player,new Entry(player,0,Optional.empty()));}
    public boolean advance(UUID player,int expected){
        var e=entry(player);if(expected<0||expected>=5||e.stage!=expected||expected>=3&&e.target.isEmpty())return false;
        entries.put(player,new Entry(player,expected+1,e.target));setDirty();return true;
    }
    public boolean reveal(UUID player,BlockPos target){
        var e=entry(player);if(e.stage!=3||e.target.isPresent())return false;
        entries.put(player,new Entry(player,3,Optional.of(target.immutable())));setDirty();return true;
    }
}

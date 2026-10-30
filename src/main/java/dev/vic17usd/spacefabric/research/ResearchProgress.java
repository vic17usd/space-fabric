package dev.vic17usd.spacefabric.research;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** Ordered personal project, independent of the old expeditions and arrival tutorial. */
public final class ResearchProgress extends SavedData {
    private record Entry(String player,int stage,BlockPos lab){
        static final Codec<Entry> CODEC=RecordCodecBuilder.create(i->i.group(Codec.STRING.fieldOf("player").forGetter(Entry::player),
            Codec.intRange(0,4).fieldOf("stage").forGetter(Entry::stage),BlockPos.CODEC.fieldOf("lab").forGetter(Entry::lab)).apply(i,Entry::new));
    }
    public static final Codec<ResearchProgress> CODEC=Entry.CODEC.listOf().fieldOf("players").xmap(ResearchProgress::new,p->List.copyOf(p.players.values())).codec();
    public static final SavedDataType<ResearchProgress> TYPE=new SavedDataType<>(SpaceFabric.id("venus_research"),ResearchProgress::new,CODEC,DataFixTypes.LEVEL);
    private final Map<String,Entry> players=new LinkedHashMap<>();
    public ResearchProgress() { }
    private ResearchProgress(List<Entry> entries){entries.forEach(e->players.put(e.player,e));}
    public static ResearchProgress get(ServerLevel level){return level.getServer().getDataStorage().computeIfAbsent(TYPE);}
    public int stage(UUID player){var e=players.get(player.toString());return e==null?0:e.stage;}
    public Optional<BlockPos> laboratory(UUID player){var e=players.get(player.toString());return e==null||e.stage<2?Optional.empty():Optional.of(e.lab);}
    public boolean advance(UUID player,int expected,BlockPos site){
        if(expected<0||expected>=4||stage(player)!=expected)return false;
        if(expected==3&&!laboratory(player).orElseThrow().equals(site))return false;
        var lab=expected==1?site.immutable():laboratory(player).orElse(BlockPos.ZERO);
        players.put(player.toString(),new Entry(player.toString(),expected+1,lab));setDirty();return true;
    }
}

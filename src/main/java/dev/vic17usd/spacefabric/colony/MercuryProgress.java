package dev.vic17usd.spacefabric.colony;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** Persistent personal arrival and first-habitat record, independent of other planets. */
public final class MercuryProgress extends SavedData {
    public record Refuge(BlockPos pos, UUID identity) {
        public static final Codec<Refuge> CODEC=RecordCodecBuilder.create(i->i.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Refuge::pos),
                net.minecraft.core.UUIDUtil.STRING_CODEC.fieldOf("identity").forGetter(Refuge::identity)).apply(i,Refuge::new));
    }
    public record Entry(boolean arrived, Optional<Refuge> refuge, boolean complete) {
        static final Entry EMPTY=new Entry(false,Optional.empty(),false);
        public static final Codec<Entry> CODEC=RecordCodecBuilder.create(i->i.group(
                Codec.BOOL.optionalFieldOf("arrived",false).forGetter(Entry::arrived),
                Refuge.CODEC.optionalFieldOf("refuge").forGetter(Entry::refuge),
                Codec.BOOL.optionalFieldOf("complete",false).forGetter(Entry::complete)).apply(i,Entry::new));
    }
    public static final Codec<MercuryProgress> CODEC=RecordCodecBuilder.create(i->i.group(
            Codec.unboundedMap(Codec.STRING,Entry.CODEC).optionalFieldOf("players",Map.of()).forGetter(p->Map.copyOf(p.players))).apply(i,MercuryProgress::new));
    public static final SavedDataType<MercuryProgress> TYPE=new SavedDataType<>(SpaceFabric.id("mercury_expedition"),MercuryProgress::new,CODEC,DataFixTypes.LEVEL);
    private final Map<String,Entry> players=new LinkedHashMap<>();
    public MercuryProgress() { }
    private MercuryProgress(Map<String,Entry> saved){players.putAll(saved);}
    public static MercuryProgress get(ServerLevel level){return level.getServer().getDataStorage().computeIfAbsent(TYPE);}
    public Entry entry(UUID player){return players.getOrDefault(player.toString(),Entry.EMPTY);}
    private void store(UUID player,Entry entry){players.put(player.toString(),entry);setDirty();}
    public boolean land(UUID player){if(entry(player).arrived())return false;store(player,new Entry(true,Optional.empty(),false));return true;}
    public boolean select(UUID player,BlockPos pos,UUID identity){
        var old=entry(player);var refuge=new Refuge(pos.immutable(),identity);
        if(!old.arrived()||old.complete()||old.refuge().filter(refuge::equals).isPresent())return false;
        store(player,new Entry(true,Optional.of(refuge),false));return true;
    }
    public boolean observe(UUID player,Refuge refuge,boolean ready){
        var old=entry(player);
        if(!old.arrived()||old.complete()||!ready||old.refuge().filter(refuge::equals).isEmpty())return false;
        store(player,new Entry(true,old.refuge(),true));return true;
    }
}

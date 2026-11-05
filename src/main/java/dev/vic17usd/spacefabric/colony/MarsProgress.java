package dev.vic17usd.spacefabric.colony;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** Personal expedition history in world SavedData, independent of death and dimension changes. */
public final class MarsProgress extends SavedData {
    public static final int ARRIVAL = 1, REFUGE = 2, SEALED = 4, SUPPLIES = 8, WATER = 16, COMPLETE = 32;
    public static final int REQUIREMENTS = REFUGE | SEALED | SUPPLIES | WATER;
    public record Entry(int milestones, Optional<BlockPos> refuge) {
        public static final Entry EMPTY = new Entry(0, Optional.empty());
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.intRange(0, 63).optionalFieldOf("milestones", 0).forGetter(Entry::milestones),
                BlockPos.CODEC.optionalFieldOf("refuge").forGetter(Entry::refuge)
        ).apply(i, Entry::new));
        public boolean started() { return (milestones & ARRIVAL) != 0; }
        public boolean complete() { return (milestones & COMPLETE) != 0; }
    }
    public static final Codec<MarsProgress> CODEC = Codec.unboundedMap(Codec.STRING, Entry.CODEC)
            .optionalFieldOf("players", Map.of()).xmap(MarsProgress::new, p -> Map.copyOf(p.players)).codec();
    public static final SavedDataType<MarsProgress> TYPE = new SavedDataType<>(
            SpaceFabric.id("mars_expedition"), MarsProgress::new, CODEC, DataFixTypes.LEVEL);
    private final Map<String, Entry> players = new LinkedHashMap<>();

    public MarsProgress() { }
    private MarsProgress(Map<String, Entry> saved) { players.putAll(saved); }
    public static MarsProgress get(ServerLevel level) {
        return level.getServer().getDataStorage().computeIfAbsent(TYPE);
    }
    public Entry entry(UUID player) { return players.getOrDefault(player.toString(), Entry.EMPTY); }
    private boolean update(UUID player, Entry entry) {
        if (entry.equals(entry(player))) return false;
        players.put(player.toString(), entry);
        setDirty();
        return true;
    }
    public boolean land(UUID player) {
        var old = entry(player);
        return !old.started() && update(player, new Entry(old.milestones() | ARRIVAL, old.refuge()));
    }
    public boolean select(UUID player, BlockPos controller) {
        var old = entry(player);
        return old.started() && !old.complete()
                && update(player, new Entry(old.milestones(), Optional.of(controller.immutable())));
    }
    /** History alone never completes the mission: every requirement must hold in this observation. */
    public boolean observe(UUID player, BlockPos controller, MarsExpeditionChecks.Observation observation) {
        var old = entry(player);
        if (!old.started() || old.complete() || !old.refuge().equals(Optional.of(controller))) return false;
        int flags = old.milestones() | observation.flags();
        if (observation.ready()) flags |= COMPLETE;
        update(player, new Entry(flags, old.refuge()));
        return (flags & COMPLETE) != 0;
    }
}

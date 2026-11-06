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
    /** Separate, optional fields preserve first-expedition saves and network flags. */
    public record Settlement(int stage, Optional<BlockPos> site, Optional<BlockPos> point,
                             Optional<BlockPos> analyzer, int baseline, Optional<UUID> ticket) {
        public static final Settlement EMPTY = new Settlement(0, Optional.empty(), Optional.empty(), Optional.empty(), 0, Optional.empty());
        public static final Codec<Settlement> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.intRange(0, 5).optionalFieldOf("stage", 0).forGetter(Settlement::stage),
                BlockPos.CODEC.optionalFieldOf("site").forGetter(Settlement::site),
                BlockPos.CODEC.optionalFieldOf("point").forGetter(Settlement::point),
                BlockPos.CODEC.optionalFieldOf("analyzer").forGetter(Settlement::analyzer),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("baseline", 0).forGetter(Settlement::baseline),
                net.minecraft.core.UUIDUtil.STRING_CODEC.optionalFieldOf("ticket").forGetter(Settlement::ticket)
        ).apply(i, Settlement::new));
    }
    public static final Codec<MarsProgress> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(Codec.STRING, Entry.CODEC).optionalFieldOf("players", Map.of()).forGetter(p -> Map.copyOf(p.players)),
            Codec.unboundedMap(Codec.STRING, Settlement.CODEC).optionalFieldOf("settlements", Map.of()).forGetter(p -> Map.copyOf(p.settlements)),
            Codec.unboundedMap(Codec.STRING, Codec.BOOL).optionalFieldOf("autonomy", Map.of()).forGetter(p -> Map.copyOf(p.autonomy))
    ).apply(i, MarsProgress::new));
    public static final SavedDataType<MarsProgress> TYPE = new SavedDataType<>(
            SpaceFabric.id("mars_expedition"), MarsProgress::new, CODEC, DataFixTypes.LEVEL);
    private final Map<String, Entry> players = new LinkedHashMap<>();

    private final Map<String, Settlement> settlements = new LinkedHashMap<>();
    private final Map<String, Boolean> autonomy = new LinkedHashMap<>();
    public enum AutonomyStatus { LOCKED, AVAILABLE, ACCEPTED }
    public AutonomyStatus autonomyStatus(UUID player) {
        if (!entry(player).complete() || settlement(player).stage() != 5) return AutonomyStatus.LOCKED;
        return Boolean.TRUE.equals(autonomy.get(player.toString())) ? AutonomyStatus.ACCEPTED : AutonomyStatus.AVAILABLE;
    }
    public boolean acceptAutonomy(UUID player) {
        if (autonomyStatus(player) != AutonomyStatus.AVAILABLE) return false;
        autonomy.put(player.toString(), true); setDirty(); return true;
    }
    public MarsProgress() { }
    private MarsProgress(Map<String, Entry> saved, Map<String, Settlement> savedSettlements, Map<String, Boolean> savedAutonomy) {
        players.putAll(saved); settlements.putAll(savedSettlements); autonomy.putAll(savedAutonomy);
    }
    public Settlement settlement(UUID player) { return settlements.getOrDefault(player.toString(), Settlement.EMPTY); }
    private boolean updateSettlement(UUID player, Settlement value) {
        if (value.equals(settlement(player))) return false;
        settlements.put(player.toString(), value); setDirty(); return true;
    }
    /** Callers validate the real structure and its template bounds, excluding its terrain plot. */
    public boolean visitSettlement(UUID player, BlockPos site) {
        return entry(player).complete() && settlement(player).stage() == 0
                && updateSettlement(player, new Settlement(1, Optional.of(site.immutable()), Optional.empty(), Optional.empty(), 0, Optional.empty()));
    }
    public boolean inspectSettlement(UUID player, BlockPos site, BlockPos point) {
        var old = settlement(player);
        return old.stage() == 1 && old.site().filter(site::equals).isPresent()
                && updateSettlement(player, new Settlement(2, old.site(), Optional.of(point.immutable()), Optional.empty(), 0, Optional.empty()));
    }
    public boolean relocatePoint(UUID player, BlockPos site, BlockPos point) {
        var old = settlement(player);
        return old.stage() >= 2 && old.stage() < 5 && old.site().filter(site::equals).isPresent()
                && updateSettlement(player, new Settlement(old.stage(), old.site(), Optional.of(point.immutable()), old.analyzer(), old.baseline(), old.ticket()));
    }
    public boolean selectAnalyzer(UUID player, BlockPos analyzer, int baseline, UUID ticket) {
        var old = settlement(player);
        return old.stage() == 2 && baseline >= 0
                && updateSettlement(player, new Settlement(3, old.site(), old.point(), Optional.of(analyzer.immutable()), baseline, Optional.of(ticket)));
    }
    public boolean cancelAnalysis(UUID player) {
        var old = settlement(player);
        return old.stage() == 3 && updateSettlement(player, new Settlement(2, old.site(), old.point(), Optional.empty(), 0, Optional.empty()));
    }
    /** Called only by the actual processor after a successful recipe transaction. */
    public boolean recordAnalysis(UUID player, BlockPos analyzer, UUID ticket, int completedBatches, boolean martianRecipe) {
        var old = settlement(player);
        return old.stage() == 3 && old.analyzer().filter(analyzer::equals).isPresent()
                && old.ticket().filter(ticket::equals).isPresent() && completedBatches > old.baseline() && martianRecipe
                && updateSettlement(player, new Settlement(4, old.site(), old.point(), old.analyzer(), old.baseline(), Optional.empty()));
    }
    public boolean completeSettlement(UUID player, BlockPos site) {
        var old = settlement(player);
        return old.stage() == 4 && old.site().filter(site::equals).isPresent()
                && updateSettlement(player, new Settlement(5, old.site(), old.point(), old.analyzer(), old.baseline(), Optional.empty()));
    }
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

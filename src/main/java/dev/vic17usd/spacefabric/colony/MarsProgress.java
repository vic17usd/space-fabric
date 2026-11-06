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
    public record AutonomyRefuge(BlockPos pos, UUID controller) {
        public static final Codec<AutonomyRefuge> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(AutonomyRefuge::pos),
                net.minecraft.core.UUIDUtil.STRING_CODEC.fieldOf("controller").forGetter(AutonomyRefuge::controller)
        ).apply(i, AutonomyRefuge::new));
    }
    public record Production(int flags, Optional<BlockPos> machine, Optional<UUID> identity,
                             Optional<UUID> ticket, String kind, int baseline) {
        public static final Production EMPTY = new Production(0, Optional.empty(), Optional.empty(), Optional.empty(), "", 0);
        public static final Codec<Production> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(0,7).optionalFieldOf("flags",0).forGetter(Production::flags),
            BlockPos.CODEC.optionalFieldOf("machine").forGetter(Production::machine),
            net.minecraft.core.UUIDUtil.STRING_CODEC.optionalFieldOf("identity").forGetter(Production::identity),
            net.minecraft.core.UUIDUtil.STRING_CODEC.optionalFieldOf("ticket").forGetter(Production::ticket),
            Codec.STRING.optionalFieldOf("kind", "").forGetter(Production::kind),
            Codec.intRange(0,Integer.MAX_VALUE).optionalFieldOf("baseline",0).forGetter(Production::baseline)
        ).apply(i, Production::new));
    }
    public static final Codec<MarsProgress> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(Codec.STRING, Entry.CODEC).optionalFieldOf("players", Map.of()).forGetter(p -> Map.copyOf(p.players)),
            Codec.unboundedMap(Codec.STRING, Settlement.CODEC).optionalFieldOf("settlements", Map.of()).forGetter(p -> Map.copyOf(p.settlements)),
            Codec.unboundedMap(Codec.STRING, Codec.BOOL).optionalFieldOf("autonomy", Map.of()).forGetter(p -> Map.copyOf(p.autonomy)),
            Codec.unboundedMap(Codec.STRING, AutonomyRefuge.CODEC).optionalFieldOf("autonomy_refuges", Map.of()).forGetter(p -> Map.copyOf(p.autonomyRefuges)),
            Codec.unboundedMap(Codec.STRING, Production.CODEC).optionalFieldOf("autonomy_production", Map.of()).forGetter(p -> Map.copyOf(p.production))
    ).apply(i, MarsProgress::new));
    public static final SavedDataType<MarsProgress> TYPE = new SavedDataType<>(
            SpaceFabric.id("mars_expedition"), MarsProgress::new, CODEC, DataFixTypes.LEVEL);
    private final Map<String, Entry> players = new LinkedHashMap<>();

    private final Map<String, Settlement> settlements = new LinkedHashMap<>();
    private final Map<String, Boolean> autonomy = new LinkedHashMap<>();
    private final Map<String, AutonomyRefuge> autonomyRefuges = new LinkedHashMap<>();
    private final Map<String, Production> production = new LinkedHashMap<>();
    public enum AutonomyStatus { LOCKED, AVAILABLE, ACCEPTED }
    public AutonomyStatus autonomyStatus(UUID player) {
        if (!entry(player).complete() || settlement(player).stage() != 5) return AutonomyStatus.LOCKED;
        return Boolean.TRUE.equals(autonomy.get(player.toString())) ? AutonomyStatus.ACCEPTED : AutonomyStatus.AVAILABLE;
    }
    public boolean acceptAutonomy(UUID player) {
        if (autonomyStatus(player) != AutonomyStatus.AVAILABLE) return false;
        autonomy.put(player.toString(), true); setDirty(); return true;
    }
    public Optional<AutonomyRefuge> autonomyRefuge(UUID player) { return Optional.ofNullable(autonomyRefuges.get(player.toString())); }
    public boolean bindAutonomyRefuge(UUID player, BlockPos pos, UUID controller) {
        if (autonomyStatus(player) != AutonomyStatus.ACCEPTED) return false;
        var binding = new AutonomyRefuge(pos.immutable(), controller);
        if (autonomyRefuge(player).filter(binding::equals).isPresent()) return false;
        autonomyRefuges.put(player.toString(), binding); production.remove(player.toString()); setDirty(); return true;
    }
    public Production production(UUID player) { return production.getOrDefault(player.toString(), Production.EMPTY); }
    public boolean commissionProduction(UUID player, BlockPos pos, UUID identity, UUID ticket, String kind, int baseline) {
        if (autonomyStatus(player) != AutonomyStatus.ACCEPTED || autonomyRefuge(player).isEmpty()
                || production(player).ticket().isPresent() || productionBit(kind)==0 || baseline<0) return false;
        production.put(player.toString(), new Production(production(player).flags(), Optional.of(pos.immutable()), Optional.of(identity), Optional.of(ticket), kind, baseline));
        setDirty(); return true;
    }
    public static int productionBit(String kind) { return switch(kind) {case "agua" -> 1; case "oxigeno" -> 2; case "solar" -> 4; default -> 0;}; }
    public boolean cancelProduction(UUID player) {
        var old=production(player); if (old.ticket().isEmpty()) return false;
        production.put(player.toString(),new Production(old.flags(),Optional.empty(),Optional.empty(),Optional.empty(),"",0)); setDirty(); return true;
    }
    public boolean recordProduction(UUID player, BlockPos pos, UUID identity, UUID ticket, String kind, int batches) {
        var old=production(player);
        if (autonomyStatus(player)!=AutonomyStatus.ACCEPTED || autonomyRefuge(player).isEmpty()
                || !old.machine().filter(pos::equals).isPresent() || !old.identity().filter(identity::equals).isPresent()
                || !old.ticket().filter(ticket::equals).isPresent() || !old.kind().equals(kind) || batches<=old.baseline()) return false;
        production.put(player.toString(),new Production(old.flags()|productionBit(kind),Optional.empty(),Optional.empty(),Optional.empty(),"",0)); setDirty(); return true;
    }
    public MarsProgress() { }
    private MarsProgress(Map<String, Entry> saved, Map<String, Settlement> savedSettlements, Map<String, Boolean> savedAutonomy, Map<String, AutonomyRefuge> savedRefuges, Map<String, Production> savedProduction) {
        players.putAll(saved); settlements.putAll(savedSettlements); autonomy.putAll(savedAutonomy); autonomyRefuges.putAll(savedRefuges); production.putAll(savedProduction);
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

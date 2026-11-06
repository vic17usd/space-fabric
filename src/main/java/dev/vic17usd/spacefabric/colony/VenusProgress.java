package dev.vic17usd.spacefabric.colony;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** Informational milestones, per player and world. Never gates existing recipes or grants items. */
public final class VenusProgress extends SavedData {
    public enum Step {
        ARRIVAL, SHELTER, POWER, COOLING, AUTOMATION, FOOD, EXPEDITION;
        public int bit() { return 1 << ordinal(); }
        public String key() { return name().toLowerCase(Locale.ROOT); }
    }

    private record Entry(String player, int flags) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("player").forGetter(Entry::player),
                Codec.intRange(0, 127).fieldOf("flags").forGetter(Entry::flags)
        ).apply(i, Entry::new));
    }

    public static final Codec<VenusProgress> CODEC = Entry.CODEC.listOf().fieldOf("players")
            .xmap(VenusProgress::new, p -> p.flags.entrySet().stream()
                    .map(e -> new Entry(e.getKey(), e.getValue())).toList()).codec();
    public static final SavedDataType<VenusProgress> TYPE = new SavedDataType<>(
            SpaceFabric.id("venus_progress"), VenusProgress::new, CODEC, DataFixTypes.LEVEL);
    private final Map<String, Integer> flags = new LinkedHashMap<>();

    public VenusProgress() { }
    private VenusProgress(List<Entry> entries) {
        entries.forEach(e -> flags.merge(e.player(), e.flags(), (a, b) -> a | b));
    }
    public static VenusProgress get(ServerLevel level) {
        return level.getServer().getDataStorage().computeIfAbsent(TYPE);
    }
    public boolean reached(UUID player, Step step) {
        return (flags.getOrDefault(player.toString(), 0) & step.bit()) != 0;
    }
    public boolean observe(UUID player, Step step) {
        if (reached(player, step)) return false;
        flags.merge(player.toString(), step.bit(), (a, b) -> a | b);
        setDirty();
        return true;
    }
    public Optional<Step> next(UUID player) {
        return Arrays.stream(Step.values()).filter(s -> !reached(player, s)).findFirst();
    }
}

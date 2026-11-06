package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.phys.*;

/** Read-only discovery and explicit server-side research transactions. Never requests a missing chunk. */
public final class MarsSettlementExpedition {
    public static final String STRUCTURE = "martian_haven_settlement";
    private record Search(long time, int cursor) { }
    private static final Map<UUID, Search> SEARCHES = new HashMap<>();
    public record Site(BlockPos anchor, BoundingBox bounds) { }
    private MarsSettlementExpedition() { }
    public static Component text(String key, Object... args) { return Component.translatable("mars.settlement." + key, args); }
    public static void clear() { SEARCHES.clear(); }
    private static boolean allowed(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator() && !player.isPassenger()
                && PlanetDimensions.find(player.level()).orElse(null) == Planet.MARS
                && MarsProgress.get(player.level()).entry(player.getUUID()).complete();
    }
    private static Structure structure(ServerLevel level) { return level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(SpaceFabric.id(STRUCTURE)); }
    private static Optional<Site> pieceSite(StructurePiece piece) {
        if (piece instanceof MarsSurfacePiece m) return Optional.of(new Site(m.getLocatorPosition().immutable(), m.templateBounds()));
        // Older Haven starts used vanilla jigsaw pieces. Their boxes describe the imported building,
        // unlike the very large preparation box on a current MarsSurfacePiece.
        if (piece instanceof PoolElementStructurePiece) {
            var box = piece.getBoundingBox();
            return Optional.of(new Site(new BlockPos(box.getCenter().getX(), box.minY() + 1, box.getCenter().getZ()), box));
        }
        return Optional.empty();
    }
    public static Optional<Site> site(StructureStart start, BlockPos pos) {
        if (start == null || !start.isValid()) return Optional.empty();
        return start.getPieces().stream().map(MarsSettlementExpedition::pieceSite).flatMap(Optional::stream)
                .filter(site -> site.bounds().isInside(pos)).findFirst();
    }
    /** Native starts/references, with template bounds rather than the large terrain-preparation box. */
    public static List<StructureStart> loadedStarts(ServerLevel level, BlockPos pos) {
        var chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        var type = structure(level);
        if (chunk == null || type == null) return List.of();
        var starts = new ArrayList<StructureStart>();
        var own = chunk.getStartForStructure(type); if (own != null) starts.add(own);
        int checked = 0;
        for (long ref : chunk.getReferencesForStructure(type)) {
            if (++checked > 64) break;
            var origin = level.getChunkSource().getChunkNow(ChunkPos.getX(ref), ChunkPos.getZ(ref));
            if (origin != null) { var start = origin.getStartForStructure(type); if (start != null) starts.add(start); }
        }
        return starts;
    }
    public static Optional<Site> at(ServerLevel level, BlockPos pos) {
        return loadedStarts(level, pos).stream().map(start -> site(start, pos)).flatMap(Optional::stream).findFirst();
    }
    public static void observe(ServerPlayer player) {
        if (!allowed(player)) return;
        var progress = MarsProgress.get(player.level());
        if (progress.settlement(player.getUUID()).stage() != 0) return;
        at(player.level(), player.blockPosition()).ifPresent(site -> {
            if (progress.visitSettlement(player.getUUID(), site.anchor())) player.sendSystemMessage(text("visited"));
        });
    }
    private static BlockPos lookingAt(ServerPlayer player) {
        var hit = player.pick(5, 1, false);
        if (!(hit instanceof BlockHitResult block) || hit.getType() != HitResult.Type.BLOCK) return null;
        var pos = block.getBlockPos();
        return player.mayBuild() && player.level().mayInteract(player, pos)
                && player.level().getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null ? pos : null;
    }
    public static int investigate(ServerPlayer player) {
        if (!allowed(player)) return message(player, "locked");
        observe(player);
        var pos = lookingAt(player); if (pos == null) return message(player, "look_point");
        var found = at(player.level(), pos); var progress = MarsProgress.get(player.level()); var old = progress.settlement(player.getUUID());
        if (found.isEmpty() || !old.site().filter(found.get().anchor()::equals).isPresent()
                || !found.get().bounds().isInside(player.blockPosition())
                || !player.level().getBlockState(pos).isCollisionShapeFullBlock(player.level(), pos)) return message(player, "look_point");
        boolean changed = old.stage() == 1 ? progress.inspectSettlement(player.getUUID(), found.get().anchor(), pos)
                : progress.relocatePoint(player.getUUID(), found.get().anchor(), pos);
        if (changed) player.sendSystemMessage(text("point", pos.toShortString()));
        send(player); return 1;
    }
    public static int analyze(ServerPlayer player) {
        if (!allowed(player)) return message(player, "locked");
        var pos = lookingAt(player); var progress = MarsProgress.get(player.level());
        if (progress.settlement(player.getUUID()).stage() != 2) return message(player, "bind_order");
        if (pos == null || !(player.level().getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine)
                || machine.kind() != ColonyContent.Machine.ANALYZER) return message(player, "look_analyzer");
        var old = machine.marsCommission();
        if (old != null && commissioned(progress, old, pos)) return message(player, "busy");
        if (machine.progress() != 0 || !machine.getItem(0).isEmpty() || !machine.getItem(1).isEmpty() || !machine.getItem(2).isEmpty()
                || machine.completedBatches() >= Integer.MAX_VALUE - 1 || machine.maintenance().failed()) return message(player, "empty_analyzer");
        var ticket = UUID.randomUUID();
        if (progress.selectAnalyzer(player.getUUID(), pos, machine.completedBatches(), ticket)) {
            machine.marsCommission(new ColonyMachineBlockEntity.MarsCommission(player.getUUID(), ticket));
            player.sendSystemMessage(text("bound", pos.toShortString()));
        }
        send(player); return 1;
    }
    public static boolean commissioned(MarsProgress progress, ColonyMachineBlockEntity.MarsCommission job, BlockPos pos) {
        var state = progress.settlement(job.player());
        return state.stage() == 3 && state.analyzer().filter(pos::equals).isPresent() && state.ticket().filter(job.ticket()::equals).isPresent();
    }
    public static boolean martian(ColonyRecipes.Formula formula) {
        return formula.machine() == ColonyContent.Machine.ANALYZER && formula.outputs().stream().anyMatch(p -> p.item() == ColonyContent.MARS_REPORT);
    }
    public static void processed(ServerLevel level, ColonyMachineBlockEntity machine, ColonyRecipes.Formula formula) {
        var job = machine.marsCommission();
        if (job == null || PlanetDimensions.find(level).orElse(null) != Planet.MARS || !martian(formula)) return;
        var progress = MarsProgress.get(level);
        if (creditCycle(progress, machine, formula)) {
            var owner = level.getServer().getPlayerList().getPlayer(job.player());
            if (owner != null) { owner.sendSystemMessage(text("analyzed")); send(owner); }
        }
    }
    /** Shared by the native server completion hook and processor integration checks. */
    public static boolean creditCycle(MarsProgress progress, ColonyMachineBlockEntity machine, ColonyRecipes.Formula formula) {
        var job = machine.marsCommission();
        if (job == null || !martian(formula) || !machine.lastCompletedRecipe().equals(formula.id())
                || !commissioned(progress, job, machine.getBlockPos())) return false;
        if (!progress.recordAnalysis(job.player(), machine.getBlockPos(), job.ticket(), machine.completedBatches(), true)) return false;
        machine.marsCommission(null); return true;
    }
    public static int cancel(ServerPlayer player) {
        if (!allowed(player)) return message(player, "locked");
        if (MarsProgress.get(player.level()).cancelAnalysis(player.getUUID())) player.sendSystemMessage(text("cancelled"));
        send(player); return 1;
    }
    /** A single slot is reserved before marking the reward; no drops, input payment or callbacks. */
    public static int rewardSlot(List<ItemStack> inventory, ItemStack reward) {
        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.get(i);
            if (stack.isEmpty() || ItemStack.isSameItemSameComponents(stack, reward) && stack.getCount() + reward.getCount() <= stack.getMaxStackSize()) return i;
        }
        return -1;
    }
    public static boolean grant(MarsProgress progress, UUID player, BlockPos site, net.minecraft.world.Container inventory) {
        var reward = new ItemStack(ColonyContent.CHARGED_POWER_CELL, 2);
        var stacks = new ArrayList<ItemStack>(); for (int i = 0; i < inventory.getContainerSize(); i++) stacks.add(inventory.getItem(i));
        int slot = rewardSlot(stacks, reward);
        if (slot < 0 || !progress.completeSettlement(player, site)) return false;
        var current = inventory.getItem(slot);
        if (current.isEmpty()) inventory.setItem(slot, reward); else current.grow(reward.getCount());
        inventory.setChanged(); return true;
    }
    public static int deliver(ServerPlayer player) {
        if (!allowed(player)) return message(player, "locked");
        var progress = MarsProgress.get(player.level()); var entry = progress.settlement(player.getUUID());
        if (entry.stage() != 4) return message(player, "delivery_order");
        var pos = lookingAt(player);
        if (pos == null || !entry.point().filter(pos::equals).isPresent()) return message(player, "return_point");
        var found = at(player.level(), pos);
        if (found.isEmpty() || !entry.site().filter(found.get().anchor()::equals).isPresent()
                || !found.get().bounds().isInside(player.blockPosition())
                || !player.level().getBlockState(pos).isCollisionShapeFullBlock(player.level(), pos)) return message(player, "look_point");
        // Only the 36 ordinary inventory slots. Equipment/offhand are never used as payment/reward slots.
        var inventory = new net.minecraft.world.SimpleContainer(36);
        for (int i = 0; i < 36; i++) inventory.setItem(i, player.getInventory().getItem(i).copy());
        if (!grant(progress, player.getUUID(), found.get().anchor(), inventory)) return message(player, "inventory_full");
        for (int i = 0; i < 36; i++) player.getInventory().setItem(i, inventory.getItem(i));
        player.getInventory().setChanged(); player.containerMenu.broadcastChanges();
        player.sendSystemMessage(text("completed")); player.sendSystemMessage(text("next")); send(player); return 1;
    }
    public static int message(ServerPlayer player, String key) { player.sendSystemMessage(text(key)); send(player); return 0; }
    public static void send(ServerPlayer player) { MarsSettlementNetwork.send(player, false); }
    public static void pages(ServerPlayer player, boolean detailed) {
        send(player); var e = MarsProgress.get(player.level()).settlement(player.getUUID());
        player.sendSystemMessage(text("title")); player.sendSystemMessage(text("stage_" + e.stage()));
        if (detailed) for (int i = 0; i <= 5; i++) player.sendSystemMessage(text("help_" + i));
        else player.sendSystemMessage(Component.literal("/marte expedicion · /marte texto"));
        e.site().ifPresent(p -> player.sendSystemMessage(text("site", p.toShortString())));
        e.point().ifPresent(p -> player.sendSystemMessage(text("point", p.toShortString())));
        e.analyzer().ifPresent(p -> player.sendSystemMessage(text("bound", p.toShortString())));
    }
    public static int search(ServerPlayer player) {
        if (!allowed(player)) return message(player, "locked");
        var previous = SEARCHES.get(player.getUUID()); long now = player.level().getGameTime();
        if (previous != null && now - previous.time() < 100) return message(player, "search_wait");
        int cursor = previous == null ? 0 : previous.cursor() + 1;
        SEARCHES.put(player.getUUID(), new Search(now, cursor));
        var found = new HashMap<BlockPos, Site>(); var center = player.chunkPosition();
        for (int x = -8; x <= 8; x++) for (int z = -8; z <= 8; z++) {
            var pos = new BlockPos((center.x() + x) * 16, player.getBlockY(), (center.z() + z) * 16);
            for (var start : loadedStarts(player.level(), pos)) {
                if (!start.isValid()) continue;
                for (var piece : start.getPieces()) pieceSite(piece).ifPresent(site -> found.put(site.anchor(), site));
            }
        }
        var nearest = found.values().stream().min(Comparator.comparingDouble(s -> s.anchor().distSqr(player.blockPosition())));
        if (nearest.isPresent()) { player.sendSystemMessage(text("found", nearest.get().anchor().toShortString())); return 1; }
        // Seed/placement math is a navigation hint, never proof of a generated village or mission progress.
        var set = player.level().registryAccess().lookupOrThrow(Registries.STRUCTURE_SET).getValue(SpaceFabric.id(STRUCTURE));
        if (set != null && set.placement() instanceof RandomSpreadStructurePlacement placement && player.level().structureManager().shouldGenerateStructures()) {
            var candidates = new ArrayList<ChunkPos>();
            for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) {
                var candidate = placement.getPotentialStructureChunk(player.level().getSeed(), center.x() + x * placement.spacing(), center.z() + z * placement.spacing());
                if (player.level().getWorldBorder().isWithinBounds(candidate.getWorldPosition())) candidates.add(candidate);
            }
            candidates.sort(Comparator.comparingDouble(c -> c.getWorldPosition().distSqr(player.blockPosition().atY(0))));
            if (!candidates.isEmpty()) { var p = candidates.get(Math.floorMod(cursor, candidates.size())).getWorldPosition(); player.sendSystemMessage(text("potential", p.getX(), p.getZ())); return 1; }
        }
        return message(player, "not_found");
    }
}

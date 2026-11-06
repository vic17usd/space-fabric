package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.transport.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/** Read-only snapshot of actual habitat interfaces and the bounded native transport graphs. */
final class MarsReserveNetwork {
    record Snapshot(long oxygen, long energy, int ports, int tanks, boolean incomplete, boolean blocked) { }
    private MarsReserveNetwork() { }
    static Snapshot inspect(ServerLevel level, ColonyMachineBlockEntity controller) {
        var world = TransportService.world(level);
        var oxygenUsed = new HashMap<BlockPos, Long>(); var energyUsed = new HashMap<BlockPos, Long>();
        var positions = new HashSet<BlockPos>(); long oxygenUnits = 0, energyUnits = 0;
        boolean incomplete = false, blocked = false; int ports = 0;
        for (var direction : Direction.values()) {
            var pos = controller.getBlockPos().relative(direction);
            if (!world.loaded(pos)) { incomplete = true; continue; }
            if (!(level.getBlockEntity(pos) instanceof MachineInterfaceBlockEntity port) || port.isRemoved()
                    || port.target() != controller) continue;
            ports++;
            if (!port.enabled()) { blocked = true; continue; }
            var face = port.getBlockState().getValue(MachineInterfaceBlock.FACING);
            for (var side : Direction.values()) if (side != face && !world.loaded(pos.relative(side))) incomplete = true;
            var graphs = TransportService.connections(world, pos, face);
            boolean oxygenCarrier = carrier(port.getItem(0), ColonyContent.EMPTY_CANISTER)
                    && singleInput(controller.getItem(0), ColonyContent.OXYGEN_CANISTER)
                    && returnRoom(controller.getItem(6), ColonyContent.EMPTY_CANISTER);
            boolean powerCarrier = carrier(port.getItem(1), ColonyContent.EMPTY_POWER_CELL)
                    && singleInput(controller.getItem(1), ColonyContent.CHARGED_POWER_CELL)
                    && returnRoom(controller.getItem(5), ColonyContent.EMPTY_POWER_CELL);
            if (!oxygenCarrier || !powerCarrier) blocked = true;
            var oxygen = new LinkedHashMap<BlockPos, Long>(); var energy = new LinkedHashMap<BlockPos, Long>();
            for (var graph : graphs) {
                var problem = graph.problem();
                if (problem == TransportGraph.Problem.UNLOADED || problem == TransportGraph.Problem.TOO_LARGE) incomplete = true;
                if (problem == TransportGraph.Problem.MIXED) blocked = true;
                if (oxygenCarrier && graph.accepts(TransportResource.OXYGEN)) add(oxygen, graph, TransportResource.OXYGEN);
                if (powerCarrier && graph.accepts(TransportResource.POWER)) add(energy, graph, TransportResource.POWER);
            }
            positions.addAll(oxygen.keySet()); positions.addAll(energy.keySet());
            oxygenUnits += allocate(oxygen, oxygenUsed, TransportResource.OXYGEN);
            energyUnits += allocate(energy, energyUsed, TransportResource.POWER);
        }
        return new Snapshot(oxygenUnits, energyUnits, ports, positions.size(), incomplete, blocked);
    }
    private static boolean carrier(ItemStack stack, net.minecraft.world.item.Item item) { return stack.is(item) && !stack.isEmpty(); }
    private static boolean singleInput(ItemStack stack, net.minecraft.world.item.Item item) { return stack.isEmpty() || stack.is(item) && stack.getCount() == 1; }
    private static boolean returnRoom(ItemStack stack, net.minecraft.world.item.Item item) { return stack.isEmpty() || stack.is(item) && stack.getCount() < stack.getMaxStackSize(); }
    static void add(Map<BlockPos,Long> stores, TransportGraph graph, TransportResource resource) {
        for (var endpoint : graph.stores()) stores.putIfAbsent(endpoint.position(), (long)endpoint.buffer().available(resource));
    }
    static long allocate(Map<BlockPos,Long> stores, Map<BlockPos,Long> used, TransportResource resource) {
        long raw = stores.entrySet().stream().mapToLong(e -> Math.max(0, e.getValue() - used.getOrDefault(e.getKey(), 0L))).sum();
        long complete = raw / resource.quantum, remaining = complete * resource.quantum;
        for (var entry : stores.entrySet()) {
            long amount = Math.min(remaining, Math.max(0, entry.getValue() - used.getOrDefault(entry.getKey(), 0L)));
            if (amount > 0) used.merge(entry.getKey(), amount, Long::sum);
            remaining -= amount;
            if (remaining == 0) break;
        }
        return complete * LifeSupport.CELL;
    }
}

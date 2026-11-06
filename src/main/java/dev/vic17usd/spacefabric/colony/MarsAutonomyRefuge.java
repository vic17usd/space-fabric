package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Server-only selection and read-only inspection of the controller's existing scanner cache. */
final class MarsAutonomyRefuge {
    private MarsAutonomyRefuge() { }

    static int select(ServerPlayer player) {
        var progress = MarsProgress.get(player.level());
        if (progress.autonomyStatus(player.getUUID()) != MarsProgress.AutonomyStatus.ACCEPTED)
            return message(player, "accept_first");
        if (PlanetDimensions.find(player.level()).orElse(null) != Planet.MARS || !player.isAlive()
                || player.isSpectator() || player.isPassenger()) return message(player, "on_mars");
        var hit = player.pick(5, 1, false);
        if (!(hit instanceof BlockHitResult block) || hit.getType() != HitResult.Type.BLOCK)
            return message(player, "look");
        var pos = block.getBlockPos();
        var level = player.level();
        if (!player.mayBuild() || !level.mayInteract(player, pos) || player.distanceToSqr(pos.getCenter()) > 36)
            return message(player, "permission");
        var chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null || !(chunk.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine)
                || machine.isRemoved() || machine.kind() != ColonyContent.Machine.HABITAT) return message(player, "look");
        machine.persistControllerIdentity();
        boolean changed = progress.bindAutonomyRefuge(player.getUUID(), pos, machine.controllerIdentity());
        player.sendSystemMessage(Component.translatable(changed ? "mars.autonomy.refuge.selected" : "mars.autonomy.refuge.same", pos.toShortString()));
        show(player, progress);
        return 1;
    }
    private static int message(ServerPlayer player, String key) {
        player.sendSystemMessage(Component.translatable("mars.autonomy.refuge." + key)); return 0;
    }
    static void show(ServerPlayer player, MarsProgress progress) {
        var binding = progress.autonomyRefuge(player.getUUID());
        if (binding.isEmpty()) { message(player, "none"); return; }
        var selected = binding.get(); var pos = selected.pos();
        player.sendSystemMessage(Component.translatable("mars.autonomy.refuge.coordinates", pos.toShortString()));
        ServerLevel level = player.level().getServer().getLevel(PlanetDimensions.key(Planet.MARS));
        if (level == null) { message(player, "unloaded"); return; }
        var chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) { message(player, "unloaded"); return; }
        if (!(chunk.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine) || machine.isRemoved()
                || machine.kind() != ColonyContent.Machine.HABITAT) { message(player, "missing"); return; }
        if (!machine.controllerIdentity().equals(selected.controller())) { message(player, "replaced"); return; }
        if (machine.maintenance().failed()) player.sendSystemMessage(Component.translatable("mars.autonomy.refuge.damaged"));
        var world = HabitatSystem.world(level);
        var status = inspect(machine.scannedRoom(), world, pos.above());
        if (status != null) { message(player, status); return; }
        var room = machine.scannedRoom();
        int occupants = ColonyMachineBlockEntity.occupants(level, room);
        player.sendSystemMessage(Component.translatable("mars.autonomy.refuge.sealed", room.air().size(), occupants));
        var required = MarsReserveBudget.requirement(room.air().size(), occupants, false);
        var oxygenInput = machine.getItem(0); var powerInput = machine.getItem(1); var waterInput = machine.getItem(2);
        var stock = MarsReserveBudget.controllerStock(machine.support().oxygen(), machine.support().energy(),
                oxygenInput.is(ColonyContent.OXYGEN_CANISTER) ? oxygenInput.getCount() : 0,
                powerInput.is(ColonyContent.CHARGED_POWER_CELL) ? powerInput.getCount() : 0,
                waterInput.is(ColonyContent.WATER_CANISTER) ? waterInput.getCount() : 0,
                returnCapacity(machine.getItem(6), ColonyContent.EMPTY_CANISTER),
                returnCapacity(machine.getItem(5), ColonyContent.EMPTY_POWER_CELL));
        player.sendSystemMessage(Component.translatable("mars.autonomy.reserves.controller", stock.oxygen(), required.oxygen(), stock.energy(), required.energy(), stock.water(), required.water()));
        player.sendSystemMessage(Component.translatable("mars.autonomy.reserves.pending"));
    }
    private static int returnCapacity(net.minecraft.world.item.ItemStack stack, net.minecraft.world.item.Item item) {
        return stack.isEmpty() ? item.getDefaultInstance().getMaxStackSize()
                : stack.is(item) ? stack.getMaxStackSize() - stack.getCount() : 0;
    }
    /** Null means the cached room is still sealed. Never loads chunks or changes the controller. */
    static String inspect(SealScanner.Room room, SealScanner.World world, BlockPos vent) {
        var cell = world.cell(vent);
        if (cell == SealScanner.Cell.UNLOADED) return "unloaded";
        if (cell != SealScanner.Cell.OPEN) return "blocked_vent";
        if (room == null) return "waiting";
        if (!room.sealed()) return room.status().name().toLowerCase(java.util.Locale.ROOT);
        if (room.air().stream().anyMatch(p -> world.cell(p) == SealScanner.Cell.UNLOADED)
                || room.boundary().keySet().stream().anyMatch(p -> world.cell(p) == SealScanner.Cell.UNLOADED)) return "unloaded";
        return room.unchanged(world) ? null : "leak";
    }
}

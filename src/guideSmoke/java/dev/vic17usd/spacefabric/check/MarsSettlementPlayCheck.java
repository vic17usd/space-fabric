package dev.vic17usd.spacefabric.check;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.phys.Vec3;

/** Opt-in destructive fixture for a disposable world, excluded from production.
 * Seeds stage I explicitly, generates a village normally, then exercises real stage-II actions/processor ticks. */
public final class MarsSettlementPlayCheck {
    private static UUID owner;
    private static BlockPos point;
    private static long deadline;
    public static boolean enabled() { return Boolean.getBoolean("spacefabric.marsSettlementCheck"); }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (owner == null) return;
            var player = server.getPlayerList().getPlayer(owner); if (player == null) return;
            if (player.level().getGameTime() > deadline) { owner = null; throw new AssertionError("Mars settlement analyzer deadline expired"); }
            var progress = MarsProgress.get(player.level());
            if (progress.settlement(owner).stage() != 4) return;
            var id = owner; owner = null;
            aim(player, point);
            for (int i = 0; i < 36; i++) player.getInventory().setItem(i, new ItemStack(net.minecraft.world.item.Items.COBBLESTONE, 64));
            MarsSettlementExpedition.deliver(player);
            require(progress.settlement(id).stage() == 4, "Full inventory must preserve delivery");
            player.getInventory().setItem(35, ItemStack.EMPTY);
            MarsSettlementExpedition.deliver(player);
            require(progress.settlement(id).stage() == 5, "Real delivery must complete expedition");
            require(player.getInventory().getItem(35).is(ColonyContent.CHARGED_POWER_CELL) && player.getInventory().getItem(35).getCount() == 2, "Reward must be two batteries");
            MarsSettlementExpedition.deliver(player);
            require(player.getInventory().getItem(35).getCount() == 2, "Repeated delivery must not duplicate reward");
            System.out.println("MARS_SETTLEMENT_NATIVE_OK: natural village, look interaction, personal commission, real processor, full inventory, delivery and duplicate rejection");
            MarsSettlementNetwork.send(player, true);
        });
    }
    public static void start(MinecraftServer server, UUID id) {
        var player = server.getPlayerList().getPlayer(id); if (player == null) throw new AssertionError("No test player");
        var mars = server.getLevel(PlanetDimensions.key(Planet.MARS)); require(mars != null, "Mars missing");
        var set = mars.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET).getValue(SpaceFabric.id(MarsSettlementExpedition.STRUCTURE));
        var type = mars.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(SpaceFabric.id(MarsSettlementExpedition.STRUCTURE));
        var placement = (RandomSpreadStructurePlacement)set.placement();
        MarsSurfacePiece village = null;
        // This explicit test fixture generates terrain. The production search never calls getChunk.
        for (int i = 0; i < 16 && village == null; i++) {
            var candidate = placement.getPotentialStructureChunk(mars.getSeed(), (i % 4 - 2) * placement.spacing(), (i / 4 - 2) * placement.spacing());
            var chunk = mars.getChunk(candidate.x(), candidate.z()); var start = chunk.getStartForStructure(type);
            if (start != null && start.isValid()) for (var piece : start.getPieces()) if (piece instanceof MarsSurfacePiece m) { village = m; break; }
        }
        require(village != null, "No generated village among test candidates");
        var box = village.templateBounds(); point = null;
        // Inspect a small central patch in an actual generated building, without placing a survey block.
        for (int y = box.minY(); y < box.minY() + 6 && point == null; y++)
            for (int dx = -8; dx <= 8 && point == null; dx++) for (int dz = -8; dz <= 8; dz++) {
                var p = new BlockPos(box.getCenter().getX() + dx, y, box.getCenter().getZ() + dz);
                if (mars.getBlockState(p).isCollisionShapeFullBlock(mars, p) && mars.getBlockState(p.above()).isAir()
                        && mars.getBlockState(p.above(2)).isAir()) { point = p; break; }
            }
        require(point != null, "No clear survey point");
        player.setGameMode(GameType.CREATIVE);
        player.teleportTo(mars, point.getX() + .5, point.getY() + 3, point.getZ() + .5, Set.of(), 0, 90, false);
        var progress = MarsProgress.get(mars);
        progress.land(id); progress.select(id, point);
        progress.observe(id, point, new MarsExpeditionChecks.Observation(true, true, true, true, 600, 1500, 2000));
        require(progress.entry(id).complete(), "Stage-I fixture setup failed");
        aim(player, point); MarsSettlementExpedition.observe(player); MarsSettlementExpedition.investigate(player);
        require(progress.settlement(id).stage() == 2, "Village survey action failed");
        point = progress.settlement(id).point().orElseThrow();
        var machinePos = point.above(90);
        require(mars.getBlockState(machinePos).isAir(), "Test analyzer location must be empty");
        mars.setBlockAndUpdate(machinePos, ColonyContent.block(ColonyContent.Machine.ANALYZER).defaultBlockState());
        aim(player, machinePos); MarsSettlementExpedition.analyze(player);
        require(progress.settlement(id).stage() == 3, "Explicit commission failed");
        var machine = (ColonyMachineBlockEntity)mars.getBlockEntity(machinePos);
        var formula = ColonyRecipes.ALL.stream().filter(MarsSettlementExpedition::martian).findFirst().orElseThrow();
        for (var part : formula.inputs()) machine.setItem(part.slot(), part.stack());
        // Leave this player in the loaded chunk. Native server ticks finish the actual recipe and invoke its hook.
        owner = id; deadline = mars.getGameTime() + 600;
        System.out.println("MARS_SETTLEMENT_NATIVE_RUNNING: " + point.toShortString());
    }
    private static void aim(ServerPlayer player, BlockPos target) {
        var level = player.level();
        player.teleportTo(level, target.getX() + .5, target.getY() + 1.01, target.getZ() + .5, Set.of(), 0, 90, false);
        var direction = target.getCenter().subtract(player.getEyePosition());
        player.setYRot((float)Math.toDegrees(-Math.atan2(direction.x, direction.z)));
        player.setXRot((float)Math.toDegrees(-Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))));
    }
}

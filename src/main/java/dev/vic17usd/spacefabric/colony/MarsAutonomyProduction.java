package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.phys.*;
import java.util.UUID;

/** One explicit, exclusive commissioning ticket per player, fulfilled only by a committed machine cycle. */
public final class MarsAutonomyProduction {
    private MarsAutonomyProduction() { }
    private static int message(ServerPlayer player, String key) {
        player.sendSystemMessage(Component.translatable("mars.production."+key)); return 0;
    }
    public static boolean commissioned(MarsProgress progress, ColonyMachineBlockEntity.MarsCommission job, net.minecraft.core.BlockPos pos) {
        var state=progress.production(job.player());
        return state.machine().filter(pos::equals).isPresent() && state.ticket().filter(job.ticket()::equals).isPresent();
    }
    static int select(ServerPlayer player, String kind) {
        var level=player.level(); var progress=MarsProgress.get(level);
        if (progress.autonomyStatus(player.getUUID())!=MarsProgress.AutonomyStatus.ACCEPTED || progress.autonomyRefuge(player.getUUID()).isEmpty()) return message(player,"locked");
        if (PlanetDimensions.find(level).orElse(null)!=Planet.MARS || !player.isAlive() || player.isSpectator() || player.isPassenger()) return message(player,"look");
        var hit=player.pick(5,1,false);
        if (!(hit instanceof BlockHitResult block) || hit.getType()!=HitResult.Type.BLOCK) return message(player,"look");
        var pos=block.getBlockPos();
        if (!player.mayBuild() || !level.mayInteract(player,pos)) return message(player,"look");
        var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
        if (chunk==null || !(chunk.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine) || machine.isRemoved()
                || machine.kind()!=(kind.equals("solar")?ColonyContent.Machine.SOLAR:ColonyContent.Machine.ELECTROLYZER)) return message(player,"look");
        var state=progress.production(player.getUUID());
        if (state.ticket().isPresent()) return message(player,"active");
        if ((state.flags()&MarsProgress.productionBit(kind))!=0) return message(player,"done");
        var old=machine.marsCommission();
        if (old!=null && (MarsSettlementExpedition.commissioned(progress,old,pos) || commissioned(progress,old,pos))) return message(player,"busy");
        if (machine.progress()!=0 || !machine.getItem(0).isEmpty() || !machine.getItem(1).isEmpty() || !machine.getItem(2).isEmpty()
                || machine.maintenance().failed() || machine.completedBatches()>=Integer.MAX_VALUE-1) return message(player,"empty");
        var ticket=UUID.randomUUID();
        if (!progress.commissionProduction(player.getUUID(),pos,machine.controllerIdentity(),ticket,kind,machine.completedBatches())) return message(player,"locked");
        machine.persistControllerIdentity();
        machine.marsCommission(new ColonyMachineBlockEntity.MarsCommission(player.getUUID(),ticket));
        player.sendSystemMessage(Component.translatable("mars.production.bound",kind,pos.toShortString()));
        return 1;
    }
    static int cancel(ServerPlayer player) {
        MarsProgress.get(player.level()).cancelProduction(player.getUUID());
        player.sendSystemMessage(Component.translatable("mars.production.cancelled")); return 1;
    }
    static void show(ServerPlayer player, MarsProgress progress) {
        var state=progress.production(player.getUUID());
        player.sendSystemMessage(Component.translatable("mars.production.status",(state.flags()&1)!=0?"✓":"○",(state.flags()&2)!=0?"✓":"○",(state.flags()&4)!=0?"✓":"○"));
        state.machine().ifPresent(pos->player.sendSystemMessage(Component.translatable("mars.production.bound",state.kind(),pos.toShortString())));
        player.sendSystemMessage(Component.translatable("mars.production.help"));
    }
    public static String kind(ColonyRecipes.Formula formula) {
        if (formula.machine()==ColonyContent.Machine.SOLAR && formula.id().equals("solar")) return "solar";
        if (formula.machine()!=ColonyContent.Machine.ELECTROLYZER) return "";
        if (formula.id().equals("electrolysis")) return "oxigeno";
        return ColonyRecipes.ALL.contains(formula) && formula.id().startsWith("melt_")?"agua":"";
    }
    public static void processed(ServerLevel level, ColonyMachineBlockEntity machine, ColonyRecipes.Formula formula) {
        if (PlanetDimensions.find(level).orElse(null)!=Planet.MARS) return;
        var job=machine.marsCommission();
        if (creditCycle(MarsProgress.get(level),machine,formula) && job!=null) {
            var player=level.getServer().getPlayerList().getPlayer(job.player());
            if (player!=null) { message(player,"credited"); show(player,MarsProgress.get(level)); }
        }
    }
    public static boolean creditCycle(MarsProgress progress, ColonyMachineBlockEntity machine, ColonyRecipes.Formula formula) {
        var job=machine.marsCommission(); var kind=kind(formula);
        if (job==null || kind.isEmpty() || !machine.lastCompletedRecipe().equals(formula.id()) || !commissioned(progress,job,machine.getBlockPos())) return false;
        if (!progress.recordProduction(job.player(),machine.getBlockPos(),machine.controllerIdentity(),job.ticket(),kind,machine.completedBatches())) return false;
        machine.marsCommission(null); return true;
    }
}

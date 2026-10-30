package dev.vic17usd.spacefabric.mining;

import dev.vic17usd.spacefabric.colony.OperationsNavigator;
import dev.vic17usd.spacefabric.planet.*;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.item.ItemStack;

/** Incremental read-only scans. Budget is shared across players, no chunk generation or /locate. */
public final class ProspectingSystem {
    public static final int RADIUS=4,DEPTH=64,BUDGET=512;
    private static final Map<ServerLevel,LinkedHashMap<UUID,Scan>> JOBS=new IdentityHashMap<>();
    private static final class Scan {
        final UUID player;final ItemStack device;final BlockPos origin;
        final Map<net.minecraft.world.level.block.Block,BlockPos> nearest=new LinkedHashMap<>();
        final Map<net.minecraft.world.level.block.Block,Integer> counts=new HashMap<>();
        int cursor,unloaded;
        Scan(ServerPlayer player,ItemStack device,BlockPos origin){this.player=player.getUUID();this.device=device;this.origin=origin.immutable();}
    }
    private ProspectingSystem() { }
    public static Component text(String key,Object... args){return Component.translatable("mining.space_fabric."+key,args);}
    public static void start(ServerPlayer player,ItemStack device,BlockPos origin){
        var level=player.level();
        if(PlanetDimensions.find(level).orElse(null)!=Planet.VENUS){player.sendSystemMessage(text("venus_only"));return;}
        var jobs=JOBS.computeIfAbsent(level,k->new LinkedHashMap<>());
        if(jobs.containsKey(player.getUUID())||player.getCooldowns().isOnCooldown(device))return;
        // Two scans per world at a time keeps memory and main-thread work bounded.
        if(jobs.size()>=2){player.sendSystemMessage(text("busy"));return;}
        if(!player.getAbilities().instabuild){
            var cells=new ArrayList<>(player.getInventory().getNonEquipmentItems());cells.add(player.getOffhandItem());
            var cell=cells.stream().filter(s->s.is(dev.vic17usd.spacefabric.colony.ColonyContent.CHARGED_POWER_CELL)).findFirst().orElse(null);
            if(cell==null){player.sendSystemMessage(text("scan_power"));return;}
            cell.shrink(1);var empty=new ItemStack(dev.vic17usd.spacefabric.colony.ColonyContent.EMPTY_POWER_CELL);
            if(!player.getInventory().add(empty))player.drop(empty,false);
        }
        jobs.put(player.getUUID(),new Scan(player,device,origin));player.getCooldowns().addCooldown(device,200);
        player.sendSystemMessage(text("scan_start"));
    }
    private static boolean holds(ServerPlayer player,ItemStack device){return player.getOffhandItem()==device||player.getInventory().getNonEquipmentItems().stream().anyMatch(s->s==device);}
    private static void tick(ServerLevel level){
        var jobs=JOBS.get(level);if(jobs==null||jobs.isEmpty())return;
        int remaining=BUDGET;
        // Each job receives an equal slice; unfinished work resumes on the next tick.
        int slice=BUDGET/jobs.size();
        for(var iterator=jobs.values().iterator();iterator.hasNext();){
            var job=iterator.next();var player=level.getServer().getPlayerList().getPlayer(job.player);
            if(player==null||player.level()!=level||!player.isAlive()||!holds(player,job.device)){iterator.remove();continue;}
            int work=Math.min(slice,remaining);
            while(work-->0&&job.cursor<BoreGeometry.count(RADIUS,DEPTH)){
                int i=job.cursor++;remaining--;
                var pos=job.origin.offset(BoreGeometry.dx(i,RADIUS),-BoreGeometry.down(i,RADIUS),BoreGeometry.dz(i,RADIUS));
                if(pos.getY()<level.getMinY())continue;
                if(!level.getWorldBorder().isWithinBounds(pos)||level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null){job.unloaded++;continue;}
                var block=level.getBlockState(pos).getBlock();if(!MiningDrill.mineral(block))continue;
                job.counts.merge(block,1,Integer::sum);
                var old=job.nearest.get(block);if(old==null||pos.distSqr(job.origin)<old.distSqr(job.origin))job.nearest.put(block,pos);
            }
            if(job.cursor>=BoreGeometry.count(RADIUS,DEPTH)){
                player.sendSystemMessage(text(job.nearest.isEmpty()?"scan_empty":"scan_done",job.unloaded));
                for(var entry:job.nearest.entrySet()){
                    var name=entry.getKey().getName();var pos=entry.getValue();
                    OperationsNavigator.remember(job.device,new OperationsNavigator.Point(level.dimension().identifier().toString(),pos,name.getString()));
                    player.sendSystemMessage(text("scan_found",name,job.counts.get(entry.getKey()),pos.toShortString(),job.origin.getY()-pos.getY()));
                }
                player.getInventory().setChanged();player.containerMenu.broadcastChanges();iterator.remove();
            }
        }
    }
    public static void initialize(){ServerTickEvents.END_LEVEL_TICK.register(ProspectingSystem::tick);ServerLifecycleEvents.SERVER_STOPPED.register(server->JOBS.clear());}
}

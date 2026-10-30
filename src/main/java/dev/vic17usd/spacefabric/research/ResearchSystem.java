package dev.vic17usd.spacefabric.research;

import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.planet.*;
import java.util.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.item.ItemStack;

/** A manual server-authoritative campaign: no auto-claims, new terrain searches or client proofs. */
public final class ResearchSystem {
    public record Evidence(int mission,boolean consoleReady,boolean stationReady,boolean collectorReady,boolean cloudAltitude,boolean sameLab) { }
    private ResearchSystem() { }
    public static Component text(String key,Object... args){return Component.translatable("research.space_fabric."+key,args);}
    public static boolean ready(int stage,Evidence e){
        if(!e.consoleReady)return false;
        return switch(stage){case 0->e.mission==2;case 1->e.mission==1&&e.stationReady;case 2->e.mission==3&&e.collectorReady&&e.cloudAltitude;
            case 3,4->e.mission==1&&e.stationReady&&e.sameLab;default->false;};
    }
    public static boolean healthy(ColonyMachineBlockEntity m){return m.maintenance().integrity()>=7000&&!m.thermal().tripped();}
    /** All components must belong to the same live room, not merely be close to its wall. */
    public static boolean stationReady(ColonyMachineBlockEntity console,List<ColonyMachineBlockEntity> loaded){
        for(var controller:loaded){
            if(controller.kind()!=ColonyContent.Machine.HABITAT||!healthy(controller)||!controller.operating()||!controller.sheltersMachine(console.getBlockPos()))continue;
            boolean cooling=false,analysis=false,food=false;
            for(var machine:loaded){
                if(!healthy(machine)||!controller.sheltersMachine(machine.getBlockPos()))continue;
                if(machine.kind()==ColonyContent.Machine.EXCHANGER&&machine.getBlockPos().distManhattan(controller.getBlockPos())==1&&machine.coolingCharge()>0)cooling=true;
                if(machine.kind()==ColonyContent.Machine.ANALYZER&&machine.completedBatches()>0&&machine.lastCompletedRecipe().startsWith("venus_"))analysis=true;
                if(machine.kind()==ColonyContent.Machine.GREENHOUSE&&machine.completedBatches()>0&&machine.lastCompletedRecipe().equals("hydroponic_wheat"))food=true;
            }
            if(cooling&&analysis&&food)return true;
        }
        return false;
    }
    public static List<Component> pages(ServerPlayer player){
        var progress=ResearchProgress.get(player.level());int stage=progress.stage(player.getUUID());
        var pages=new ArrayList<Component>();pages.add(text("intro"));pages.add(text("current",stage,4,text("objective_"+stage)));
        progress.laboratory(player.getUUID()).ifPresent(pos->pages.add(text("bound",pos.toShortString())));
        for(int i=0;i<4;i++)pages.add(text("page",i<stage?"✓":"○",text("objective_"+i),text("detail_"+i)));
        pages.add(text("manufacture"));return List.copyOf(pages);
    }
    public static void interact(ServerPlayer player,BlockPos pos){
        var level=player.level();
        if(!player.isAlive()||player.isSpectator()||!player.mayBuild()||player.distanceToSqr(pos.getCenter())>64||!level.mayInteract(player,pos)
                ||PlanetDimensions.find(level).orElse(null)!=Planet.VENUS||level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null)return;
        var progress=ResearchProgress.get(level);int stage=progress.stage(player.getUUID());
        if(!(level.getBlockEntity(pos) instanceof ColonyMachineBlockEntity console)||console.kind()!=ColonyContent.Machine.SURVEY){player.sendSystemMessage(text("wrong_site"));return;}
        var loaded=ThermalSystem.loaded(level).stream().filter(m->m.getBlockPos().distSqr(pos)<=144).toList();
        boolean collector=loaded.stream().anyMatch(m->m.kind()==ColonyContent.Machine.ATMOSPHERIC_COLLECTOR&&m.getBlockPos().distManhattan(pos)==1&&healthy(m)&&m.completedBatches()>0&&m.lastCompletedRecipe().equals("venus_atmosphere"));
        var evidence=new Evidence(console.naturalMission(),healthy(console),(stage==1||stage>=3)&&stationReady(console,loaded),collector,
                pos.getY()>=280&&pos.getY()<=320&&player.getY()>=280&&player.getY()<=320,progress.laboratory(player.getUUID()).map(pos::equals).orElse(false));
        if(!ready(stage,evidence)){player.sendSystemMessage(text("objective_"+stage));player.sendSystemMessage(text("detail_"+Math.min(stage,3)));return;}
        var inventory=player.getInventory().getNonEquipmentItems();var costs=ResearchCosts.forStage(stage);
        if(!ResearchCosts.affordable(inventory,costs)){
            player.sendSystemMessage(text("missing"));for(var cost:costs)player.sendSystemMessage(text("cost",cost.count(),new ItemStack(cost.item()).getHoverName()));return;
        }
        // No callbacks between the proof, claim and payment. Each progression reward can be granted once.
        if(stage<4&&!progress.advance(player.getUUID(),stage,pos))return;
        ResearchCosts.pay(inventory,costs);
        player.getInventory().placeItemBackInInventory(new ItemStack(ColonyContent.EMPTY_POWER_CELL));
        if(stage>=3){
            player.getInventory().placeItemBackInInventory(new ItemStack(ResearchContent.ROVER_MODULE));
            player.getInventory().placeItemBackInInventory(new ItemStack(ResearchContent.SCANNER_LENS));
            if(stage==3)player.getInventory().placeItemBackInInventory(new ItemStack(ColonyContent.EMPTY_CANISTER));
        }
        player.getInventory().setChanged();player.containerMenu.broadcastChanges();ResearchNetwork.send(player);
        player.sendSystemMessage(text(stage==4?"manufactured":"advanced",Math.min(4,stage+1)));
    }
    public static void initialize(){
        ServerTickEvents.END_LEVEL_TICK.register(level->{if(level.getGameTime()%20==0&&PlanetDimensions.find(level).orElse(null)==Planet.VENUS)for(var player:level.players())if(player.isAlive()&&!player.isSpectator())ResearchNetwork.send(player);});
        CommandRegistrationCallback.EVENT.register((dispatcher,context,selection)->dispatcher.register(Commands.literal("venus").then(Commands.literal("investigacion").executes(c->{pages(c.getSource().getPlayerOrException()).forEach(c.getSource().getPlayerOrException()::sendSystemMessage);return 1;}))));
    }
}

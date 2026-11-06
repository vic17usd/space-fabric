package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.industry.IndustryContent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.item.ItemStack;

public final class ExpeditionSystem {
    private ExpeditionSystem() { }
    public static ColonyContent.Machine role(int mission){return mission==1?ColonyContent.Machine.GREENHOUSE:mission==2?ColonyContent.Machine.RECLAIMER:ColonyContent.Machine.ATMOSPHERIC_COLLECTOR;}
    public static boolean ready(ColonyMachineBlockEntity machine,int mission) {
        return machine!=null&&machine.kind()==role(mission)&&machine.maintenance().integrity()>=7000&&machine.completedBatches()>0&&!machine.thermal().tripped();
    }
    public static void interact(ServerPlayer player,ColonyMachineBlockEntity console) {
        if(!(console.getLevel() instanceof ServerLevel level)||player.level()!=level||!player.isAlive()||player.isSpectator()||!player.mayBuild()||player.distanceToSqr(console.getBlockPos().getCenter())>64||!level.mayInteract(player,console.getBlockPos())||PlanetDimensions.find(level).orElse(null)!=Planet.VENUS)return;
        if(console.maintenance().failed())return;
        int mission=console.naturalMission();
        if(mission<1||mission>3){player.sendSystemMessage(Component.translatable("expedition.space_fabric.no_site"));return;}
        var ledger=ExpeditionLedger.get(level);
        if(ledger.claimed(player.getUUID(),mission)){player.sendSystemMessage(Component.translatable("expedition.space_fabric.claimed"));return;}
        boolean ready=false;
        for(var face:Direction.values()) {
            var pos=console.getBlockPos().relative(face);
            if(level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null&&level.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine&&ready(machine,mission))ready=true;
        }
        if(!ready||!console.getItem(0).is(ColonyContent.VENUS_REPORT)||!console.getItem(1).is(ColonyContent.CHARGED_POWER_CELL)||!console.getItem(2).is(ColonyContent.DATA_CARD)){
            player.sendSystemMessage(Component.translatable("expedition.space_fabric.objective",mission,ColonyContent.block(role(mission)).getName()));return;
        }
        // Commit the claim before granting items. Inputs are checked again on every server interaction.
        if(!ledger.claim(player.getUUID(),mission))return;
        for(int i=0;i<3;i++)console.getItem(i).shrink(1);console.setChanged();
        player.getInventory().placeItemBackInInventory(new ItemStack(ColonyContent.EMPTY_POWER_CELL));
        player.getInventory().placeItemBackInInventory(new ItemStack(IndustryContent.COMPOSITE_PLATE,mission*2));
        player.getInventory().placeItemBackInInventory(new ItemStack(dev.vic17usd.spacefabric.maintenance.MaintenanceContent.FILTER,2));
        if(mission==3)player.getInventory().placeItemBackInInventory(new ItemStack(ExpansionContent.AEROSTAT));
        player.sendSystemMessage(Component.translatable("expedition.space_fabric.success",mission));
    }
    public static void initialize(){CommandRegistrationCallback.EVENT.register((dispatcher,context,selection)->dispatcher.register(Commands.literal("expediciones").executes(c->{
        c.getSource().sendSuccess(()->Component.translatable("expedition.space_fabric.guide"),false);return 1;
    })));}
}

package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.space.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.phys.*;

/** Server-authoritative first Mercury mission; no world edits or inventory rewards. */
public final class MercuryExpedition {
    private MercuryExpedition() { }
    public static boolean landing(Planet current,Planet origin,Planet destination,int phase,boolean touchdown) {
        return current==Planet.MERCURY && destination==Planet.MERCURY && origin!=null && origin!=Planet.MERCURY
                && phase==RocketJourney.DESCENDING && touchdown;
    }
    public static void landed(RocketEntity rocket) {
        if (!(rocket.level() instanceof ServerLevel level) || !(rocket.getFirstPassenger() instanceof ServerPlayer player)
                || !player.isAlive() || player.isSpectator() || rocket.hull()<=0 || rocket.isRemoved()) return;
        if (landing(PlanetDimensions.find(level).orElse(null),TravelDestination.at(rocket.originIndex()).planet(),
                TravelDestination.at(rocket.destinationIndex()).planet(),rocket.flightState(),rocket.verticalCollision&&rocket.onGround())
                && MercuryProgress.get(level).land(player.getUUID())) message(player,"welcome");
    }
    private static int message(ServerPlayer p,String key){p.sendSystemMessage(Component.translatable("mercury.expedition."+key));return 1;}
    private static int select(ServerPlayer p) {
        var level=p.level();var progress=MercuryProgress.get(level);var entry=progress.entry(p.getUUID());
        if(!entry.arrived() || entry.complete())return message(p,entry.complete()?"complete":"arrival");
        if(PlanetDimensions.find(level).orElse(null)!=Planet.MERCURY || !p.isAlive() || p.isSpectator() || p.isPassenger() || !p.mayBuild())return message(p,"look");
        var hit=p.pick(5,1,false);
        if(!(hit instanceof BlockHitResult block) || hit.getType()!=HitResult.Type.BLOCK)return message(p,"look");
        var pos=block.getBlockPos(); var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
        if(!level.mayInteract(p,pos) || chunk==null || !(chunk.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine)
                || machine.isRemoved() || machine.kind()!=ColonyContent.Machine.HABITAT) return message(p,"look");
        machine.persistControllerIdentity();
        if(progress.select(p.getUUID(),pos,machine.controllerIdentity()))p.sendSystemMessage(Component.translatable("mercury.expedition.selected",pos.toShortString()));
        else message(p,"same");
        return 1;
    }
    private static boolean ready(ServerPlayer player, MercuryProgress.Refuge refuge) {
        var level=player.level();var pos=refuge.pos();
        if(PlanetDimensions.find(level).orElse(null)!=Planet.MERCURY||!player.isAlive()||player.isSpectator()||player.isPassenger())return false;
        var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
        if(chunk==null || !(chunk.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine) || machine.isRemoved()
                || machine.kind()!=ColonyContent.Machine.HABITAT || !machine.controllerIdentity().equals(refuge.identity()))return false;
        var head=BlockPos.containing(player.getEyePosition());
        var room=machine.scannedRoom();
        return machine.operating() && machine.support().pressurized() && room!=null && room.air().contains(head)
                && MarsAutonomyRefuge.inspect(room,HabitatSystem.world(level),pos.above())==null
                && machine.support().oxygen()>=MarsExpeditionChecks.OXYGEN && machine.support().energy()>=MarsExpeditionChecks.ENERGY;
    }
    private static int status(ServerPlayer p,boolean guide) {
        var entry=MercuryProgress.get(p.level()).entry(p.getUUID());
        if(!entry.arrived())return message(p,"arrival");
        if(entry.complete())return message(p,"complete");
        message(p,"objective");
        if(entry.refuge().isPresent()) {
            var ref=entry.refuge().get(); p.sendSystemMessage(Component.translatable("mercury.expedition.bound",ref.pos().toShortString()));
            message(p,ready(p,ref)?"ready":"unready");
        } else message(p,"select");
        if(guide) {message(p,"thermal");message(p,"build");message(p,"supply");message(p,"recipes");message(p,"solar");}
        return 1;
    }
    /** Advancement award is a persistent, idempotent reward; reconcile after reconnect/restart. */
    private static void reconcile(ServerPlayer player) {
        if (!MercuryProgress.get(player.level()).entry(player.getUUID()).complete()) return;
        var advancement=player.level().getServer().getAdvancements().get(dev.vic17usd.spacefabric.SpaceFabric.id("mercury_expedition/refuge"));
        if (advancement!=null) player.getAdvancements().award(advancement,"verified");
    }
    public static void initialize() {
        ServerTickEvents.END_LEVEL_TICK.register(level->{
            if(level.getGameTime()%20!=0 || PlanetDimensions.find(level).orElse(null)!=Planet.MERCURY)return;
            for(var player:level.players()) {
                var progress=MercuryProgress.get(level);var entry=progress.entry(player.getUUID());
                if(entry.arrived()&&!entry.complete()&&entry.refuge().isPresent()&&ready(player,entry.refuge().get())
                        &&progress.observe(player.getUUID(),entry.refuge().get(),true))message(player,"completed_now");
                reconcile(player);
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher,registry,selection)->dispatcher.register(Commands.literal("mercurio")
                .executes(c->status(c.getSource().getPlayerOrException(),false))
                .then(Commands.literal("guia").executes(c->status(c.getSource().getPlayerOrException(),true)))
                .then(Commands.literal("refugio").executes(c->select(c.getSource().getPlayerOrException())))));
    }
}

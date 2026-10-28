package dev.vic17usd.spacefabric.maintenance;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Atmosphere reaches roofed equipment too. Only verified sealed shielding blocks it. */
public final class MachineExposure {
    public static final TagKey<Block> SOURCES=TagKey.create(Registries.BLOCK,SpaceFabric.id("corrosive_sources"));
    private MachineExposure() { }
    public static boolean enclosed(SealScanner.Room room,BlockPos machine,SealScanner.World world) {
        if(room==null||!room.sealed()||!room.unchanged(world))return false;
        boolean attached=room.boundary().containsKey(machine);
        for(var direction:Direction.values()) {
            var next=machine.relative(direction);var cell=world.cell(next);
            if(cell==SealScanner.Cell.UNLOADED)return false;
            if(room.air().contains(next)&&cell==SealScanner.Cell.OPEN){attached=true;continue;}
            if(cell==SealScanner.Cell.RATED)continue;
            if(cell==SealScanner.Cell.SOLID)continue;
            return false;
        }
        return attached;
    }
    public static MachineWear.Risk classify(boolean venus,boolean source,boolean shielded,boolean haze) {
        return !venus?MachineWear.Risk.NONE:source?MachineWear.Risk.SOURCE:shielded?MachineWear.Risk.NONE:haze?MachineWear.Risk.HAZE:MachineWear.Risk.ATMOSPHERE;
    }
    public static MachineWear.Risk at(ServerLevel level,BlockPos pos) {
        if(PlanetDimensions.find(level).orElse(null)!=Planet.VENUS)return MachineWear.Risk.NONE;
        for(var direction:Direction.values()) {
            var next=pos.relative(direction);
            if(level.getChunkSource().getChunkNow(next.getX()>>4,next.getZ()>>4)!=null&&level.getBlockState(next).is(SOURCES))return MachineWear.Risk.SOURCE;
        }
        if(HabitatSystem.sheltersMachine(level,pos))return MachineWear.Risk.NONE;
        return classify(true,false,false,level.getRainLevel(1)>.1F);
    }
    public static boolean tick(ServerLevel level,BlockEntity machine,MachineWear wear) {
        if(wear.needsSample(level.getGameTime()))wear.sample(at(level,machine.getBlockPos()),level.getGameTime());
        var before=wear.state();var stage=wear.stage();boolean changed=wear.tick();var after=wear.state();
        String notice=wear.stage().ordinal()>stage.ordinal()?"stage_"+wear.stage().name().toLowerCase(java.util.Locale.ROOT):
                before.filter()>240&&after.filter()<=240?"filter_low":before.filter()>0&&after.filter()==0?"filter_spent":
                before.coating()>960&&after.coating()<=960?"coating_low":before.coating()>0&&after.coating()==0?"coating_spent":null;
        if(notice!=null)for(var player:level.players())if(player.distanceToSqr(machine.getBlockPos().getX()+.5,machine.getBlockPos().getY()+.5,machine.getBlockPos().getZ()+.5)<=256)
            player.sendSystemMessage(Component.translatable("maintenance.space_fabric.notice",machine.getBlockState().getBlock().getName(),machine.getBlockPos().toShortString(),Component.translatable("maintenance.space_fabric."+notice)));
        return changed;
    }
}

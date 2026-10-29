package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.planet.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.*;
import net.minecraft.tags.FluidTags;

/** Local, non-destructive vent bursts. Checks only sampled loaded positions near players. */
public final class VenusActivity {
    private VenusActivity() { }
    public static void initialize(){ServerTickEvents.END_LEVEL_TICK.register(level->{
        if(level.getGameTime()%100!=0||PlanetDimensions.find(level).orElse(null)!=Planet.VENUS)return;
        for(var player:level.players()){
            if(player.getY()>260)continue;
            if(level.getGameTime()%400==0&&level.canSeeSky(player.blockPosition())&&!dev.vic17usd.spacefabric.colony.HabitatSystem.protects(player)){
                boolean fumaroles=level.getBiome(player.blockPosition()).is(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME,dev.vic17usd.spacefabric.SpaceFabric.id("venus_fumaroles")));
                level.playSound(null,player.blockPosition(),fumaroles?SoundEvents.LAVA_AMBIENT:SoundEvents.FIRE_AMBIENT,SoundSource.AMBIENT,.12F,.6F);
            }
            for(int i=0;i<8;i++){
                var pos=player.blockPosition().offset(level.getRandom().nextInt(17)-8,-1,level.getRandom().nextInt(17)-8);
                if(level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null)continue;
                if(!level.getBlockState(pos.above()).isAir())continue;
                if(!level.getFluidState(pos).is(FluidTags.LAVA)&&!level.getBlockState(pos).is(dev.vic17usd.spacefabric.maintenance.MachineExposure.SOURCES))continue;
                level.sendParticles(player,ParticleTypes.LARGE_SMOKE,false,false,pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,8,.2,.5,.2,.03);
                level.playSound(null,pos,SoundEvents.LAVA_POP,SoundSource.AMBIENT,.35F,.7F);break;
            }
        }
    });}
}

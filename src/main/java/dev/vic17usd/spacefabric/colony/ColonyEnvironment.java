package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.Heightmap;

/** Server-derived environmental gates. The collector must sample outdoor gas, never room air. */
public record ColonyEnvironment(boolean venus,boolean intakeOpen,boolean directSun,boolean lavaBelow,boolean regulated,boolean lit,LifeSupport recovery) {
    public ColonyEnvironment(boolean venus,boolean intakeOpen,boolean directSun,boolean lavaBelow){this(venus,intakeOpen,directSun,lavaBelow,false,false,null);}
    public static ColonyEnvironment read(ServerLevel level,BlockPos pos,ColonyContent.Machine kind) {
        boolean venus=PlanetDimensions.find(level).orElse(null)==Planet.VENUS;
        boolean intake=false,sun=false,heat=false;
        if(kind==ColonyContent.Machine.ATMOSPHERIC_COLLECTOR) {
            // WORLD_SURFACE includes glass and leaves, unlike canSeeSky alone.
            intake=level.getBlockState(pos.above()).isAir()&&level.getFluidState(pos.above()).isEmpty()
                    &&level.getHeight(Heightmap.Types.WORLD_SURFACE,pos.getX(),pos.getZ())==pos.getY()+1;
        }
        if(kind==ColonyContent.Machine.SOLAR) {
            long phase=Math.floorMod(level.getDefaultClockTime(),24000);
            sun=!venus&&phase<12000&&level.canSeeSky(pos.above())&&!level.isRainingAt(pos.above());
        }
        if(kind==ColonyContent.Machine.GEOTHERMAL||kind==ColonyContent.Machine.CERAMIC_KILN)
            heat=level.getFluidState(pos.below()).is(FluidTags.LAVA);
        var controller=kind==ColonyContent.Machine.GREENHOUSE||kind==ColonyContent.Machine.RECLAIMER?HabitatSystem.machineController(level,pos):null;
        return new ColonyEnvironment(venus,intake,sun,heat,controller!=null&&controller.operating(),kind==ColonyContent.Machine.GREENHOUSE&&level.getMaxLocalRawBrightness(pos.above())>=9,controller==null?null:controller.support());
    }
    public String blocker(ColonyContent.Machine kind) {
        return switch(kind) {
            case GREENHOUSE -> !regulated?"greenhouse_room":!lit?"greenhouse_light":"";
            case RECLAIMER -> !regulated?"greenhouse_room":recovery==null||recovery.respired()<4800?"recovery_wait":"";
            case ATMOSPHERIC_COLLECTOR -> !venus?"collector_environment":!intakeOpen?"collector_intake":"";
            case SOLAR -> directSun?"":"sunlight";
            case GEOTHERMAL,CERAMIC_KILN -> lavaBelow?"":"heat";
            default -> "";
        };
    }
}

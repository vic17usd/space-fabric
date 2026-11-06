package dev.vic17usd.spacefabric.entity.venus;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.server.level.ServerLevel;

/** Passive fauna approaches vent habitat without replacing its higher-priority avoidance AI. */
public final class VenusHabitatGoal extends Goal {
    private final VenusMob mob;private BlockPos target;
    public VenusHabitatGoal(VenusMob mob){this.mob=mob;setFlags(EnumSet.of(Flag.MOVE));}
    @Override public boolean canUse(){
        if(!(mob.level() instanceof ServerLevel level)||mob.tickCount%100!=0||mob.getTarget()!=null)return false;
        for(int dx=-6;dx<=6;dx+=3)for(int dz=-6;dz<=6;dz+=3){
            var pos=mob.blockPosition().offset(dx,-1,dz);
            if(level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null)continue;
            if(level.getBlockState(pos).is(dev.vic17usd.spacefabric.maintenance.MachineExposure.SOURCES)&&level.getBlockState(pos.above()).isAir()){target=pos.above();return true;}
        }
        return false;
    }
    @Override public void start(){mob.getNavigation().moveTo(target.getX()+.5,target.getY(),target.getZ()+.5,.6);}
    @Override public boolean canContinueToUse(){return mob.getTarget()==null&&!mob.getNavigation().isDone();}
    @Override public void stop(){mob.getNavigation().stop();}
}

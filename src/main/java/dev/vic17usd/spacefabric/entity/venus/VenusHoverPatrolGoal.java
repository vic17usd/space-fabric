package dev.vic17usd.spacefabric.entity.venus;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;

/** Airborne wandering keeps fliers above the surface instead of selecting buried land paths. */
public final class VenusHoverPatrolGoal extends Goal {
    private final VenusMob mob;
    private int cooldown;
    public VenusHoverPatrolGoal(VenusMob mob){this.mob=mob;setFlags(EnumSet.of(Flag.MOVE));}
    @Override public boolean canUse(){return mob.getTarget()==null&&mob.getNavigation().isDone()&&--cooldown<=0;}
    @Override public boolean canContinueToUse(){return mob.getTarget()==null&&!mob.getNavigation().isDone();}
    @Override public void start() {
        var random=mob.getRandom();double angle=random.nextDouble()*Math.PI*2,radius=3+random.nextDouble()*6;
        double x=mob.getX()+Math.cos(angle)*radius,z=mob.getZ()+Math.sin(angle)*radius;
        int surface=mob.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(int)Math.floor(x),(int)Math.floor(z));
        double y=Math.min(mob.level().getMaxY()-8,surface+4+random.nextInt(7));
        mob.getNavigation().moveTo(x,y,z,.75);
    }
    @Override public void stop(){cooldown=30+mob.getRandom().nextInt(30);}
}

package dev.vic17usd.spacefabric.colony;

import java.util.*;

/** Expiring HUD snapshot and ordered remaining objectives, independent of rendering. */
public final class VenusMissionTracker {
    private int flags=-1,age;
    public void accept(int flags){new VenusGuideNetwork.State(flags);this.flags=flags;age=0;}
    public void clear(){flags=-1;age=0;}
    public void tick(){if(age<=60)age++;}
    public boolean ready(){return flags>=0&&age<=60;}
    public List<VenusProgress.Step> remaining(){
        if(!ready())return List.of();
        return Arrays.stream(VenusProgress.Step.values()).filter(s->s!=VenusProgress.Step.ARRIVAL&&(flags&s.bit())==0).toList();
    }
    public int completed(){return ready()?6-remaining().size():0;}
}

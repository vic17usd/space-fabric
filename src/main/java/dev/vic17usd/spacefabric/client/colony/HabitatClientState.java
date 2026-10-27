package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.colony.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

/** Short-lived visual state only; it never modifies player protection or resource budgets. */
public final class HabitatClientState {
    public static final int TTL=40;
    private HabitatNetwork.State state;
    private SealScanner.Room proof;
    private int age=TTL+1;
    private long checkedTick=Long.MIN_VALUE;
    private boolean validProof;
    public void clear(){state=null;proof=null;age=TTL+1;checkedTick=Long.MIN_VALUE;}
    public void tick(){if(age<=TTL)age++;}
    public void accept(HabitatNetwork.State incoming) {
        if(incoming.anchor().isEmpty()){clear();return;}
        if(state==null||!state.dimension().equals(incoming.dimension())||!state.anchor().equals(incoming.anchor()))proof=null;
        state=incoming;if(incoming.geometryUpdate())proof=incoming.room().orElse(null);age=0;checkedTick=Long.MIN_VALUE;
    }
    public HabitatReadings readings(Identifier dimension) {return current(dimension)?state.readings():null;}
    public boolean current(Identifier dimension){return state!=null&&age<=TTL&&state.dimension().equals(dimension);}
    public boolean clearInterior(Identifier dimension,BlockPos playerHead,BlockPos camera,SealScanner.World world,long tick) {
        if(checkedTick!=tick){validProof=proof!=null&&proof.unchanged(world);checkedTick=tick;}
        return current(dimension)&&state.readings().regulated()&&proof!=null&&proof.air().contains(playerHead)
                &&proof.air().contains(camera)&&validProof;
    }
}

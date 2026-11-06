package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.*;
import java.util.*;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;

/** Exercise real client state and native FogData without graphics or a Minecraft instance. */
public final class HabitatViewCheck {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    public static void main(String[] args) {
        var venus=SpaceFabric.id("venus");var mars=SpaceFabric.id("mars");var head=BlockPos.ZERO;var anchor=head.below();
        var cells=new HashMap<BlockPos,SealScanner.Cell>();cells.put(head,SealScanner.Cell.OPEN);cells.put(anchor,SealScanner.Cell.RATED);
        var room=new SealScanner.Room(SealScanner.Status.SEALED,Set.of(head),Map.of(anchor,SealScanner.Cell.RATED));
        var safe=new HabitatReadings(HabitatReadings.Condition.PRESSURIZED,7000,7000,7000,1,true);
        var view=new HabitatClientState();var full=new HabitatNetwork.State(venus,Optional.of(anchor),safe,true,Optional.of(room));
        view.accept(full);
        check(view.clearInterior(venus,head,head,cells::get,0),"Regulated rooms should be clear");
        check(!view.clearInterior(venus,head,head.above(),cells::get,0),"Third-person cameras outside retain fog");
        check(!view.clearInterior(venus,head.above(),head,cells::get,0),"Players outside cannot borrow a clear view");
        check(!view.clearInterior(mars,head,head,cells::get,0),"Snapshots cannot cross dimensions");
        cells.put(anchor,SealScanner.Cell.OPEN);
        check(!view.clearInterior(venus,head,head,cells::get,1),"Local breaches revoke proof");
        cells.put(anchor,SealScanner.Cell.UNLOADED);
        check(!view.clearInterior(venus,head,head,cells::get,2),"Unloaded hulls cannot clear fog");
        cells.put(anchor,SealScanner.Cell.RATED);view.accept(full);
        view.accept(new HabitatNetwork.State(venus,Optional.of(anchor),safe,false,Optional.empty()));
        check(view.clearInterior(venus,head,head,cells::get,3),"Small updates retain geometry");
        for(var condition:HabitatReadings.Condition.values()) {
            if(condition==HabitatReadings.Condition.PRESSURIZED)continue;
            view.accept(new HabitatNetwork.State(venus,Optional.of(anchor),new HabitatReadings(condition,7000,7000,7000,1,true),false,Optional.empty()));
            check(!view.clearInterior(venus,head,head,cells::get,4),"Fault must retain fog: "+condition);
        }
        view.accept(full);view.accept(new HabitatNetwork.State(venus,Optional.of(anchor.above()),safe,false,Optional.empty()));
        check(!view.clearInterior(venus,head,head,cells::get,5),"Changing controller cannot reuse proof");
        view.accept(full);for(int i=0;i<=HabitatClientState.TTL;i++)view.tick();
        check(view.readings(venus)==null&&!view.clearInterior(venus,head,head,cells::get,6),"Stale telemetry expires");
        view.accept(full);view.accept(HabitatNetwork.State.absent(venus));check(view.readings(venus)==null,"Leaving removes HUD");
        view.accept(full);view.clear();check(!view.current(venus),"Death/disconnect clears state");
        var fog=new FogData();fog.environmentalStart=0;fog.environmentalEnd=8;fog.renderDistanceStart=96;fog.renderDistanceEnd=128;fog.skyEnd=28;fog.cloudEnd=56;
        HabitatFog.apply(fog,8);
        check(fog.environmentalStart==128&&fog.environmentalEnd==144,"Indoor fog starts beyond normal render range");
        check(fog.renderDistanceStart==96&&fog.renderDistanceEnd==128&&fog.skyEnd==28&&fog.cloudEnd==56,"Preserve render distance and sky/cloud limits");
        check(fog.color.isFinite(),"Finite fog color");
        System.out.println("OK: habitat camera/room containment, breach, faults, expiry, lifecycle and native indoor FogData verified without GPU.");
    }
}

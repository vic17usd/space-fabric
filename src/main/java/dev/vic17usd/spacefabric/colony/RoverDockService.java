package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.rover.*;
import dev.vic17usd.spacefabric.industry.IndustryContent;
import dev.vic17usd.spacefabric.recovery.RecoveryContent;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;

/** Every supply conversion preflights the complete unit and returned physical container. */
public final class RoverDockService {
    private RoverDockService() { }
    public static boolean tick(ServerLevel level,ColonyMachineBlockEntity dock) {
        var vehicles=level.getEntitiesOfClass(RoverEntity.class,new AABB(dock.getBlockPos()).inflate(6),
            r->!r.isRemoved()&&!r.isVehicle()&&r.onGround()&&r.getDeltaMovement().horizontalDistanceSqr()<.0001);
        vehicles.sort(Comparator.comparingDouble(r->r.distanceToSqr(dock.getBlockPos().getCenter())));
        for(var rover:vehicles)if(service(dock,rover))return true;
        return false;
    }
    public static boolean service(ColonyMachineBlockEntity dock,RoverEntity rover) {
        if(dock.kind()!=ColonyContent.Machine.DOCK||!dock.dockEnabled()||dock.maintenance().failed()||dock.thermal().tripped()||rover.isRemoved()||rover.isVehicle())return false;
        boolean changed=false;
        for(int slot=0;slot<3;slot++) {
            var source=dock.getItem(slot);if(!ColonyRecipes.accepts(ColonyContent.Machine.DOCK,slot,source))continue;
            var kind=slot==0?RoverSystems.Supply.POWER:slot==1?RoverSystems.Supply.OXYGEN:source.is(RecoveryContent.CHILLED_COOLANT)?RoverSystems.Supply.CHILLED:RoverSystems.Supply.WATER;
            var next=rover.systems().supply(kind);if(next.equals(rover.systems()))continue;
            int output=slot==0?3:4;var empty=slot==0?ColonyContent.EMPTY_POWER_CELL:ColonyContent.EMPTY_CANISTER;
            var old=dock.getItem(output);if(!old.isEmpty()&&(!old.is(empty)||old.getCount()>=old.getMaxStackSize()))continue;
            source.shrink(1);if(old.isEmpty())dock.setItem(output,new ItemStack(empty));else old.grow(1);
            rover.restoreSystems(next);changed=true;
        }
        // Slot 8 is reserved for repair stock; slots 5..7 receive cargo without changing its components.
        for(int slot=8;slot<9;slot++)if(dock.getItem(slot).is(IndustryContent.COMPOSITE_PLATE)&&rover.systems().hull()<100){
            rover.restoreSystems(rover.systems().supply(RoverSystems.Supply.REPAIR));dock.getItem(slot).shrink(1);changed=true;break;
        }
        for(int slot=3;slot<12;slot++) {
            var source=rover.inventory().getItem(slot);if(source.isEmpty())continue;
            for(int dest=5;dest<8&&!source.isEmpty();dest++) {
                var old=dock.getItem(dest);if(!old.isEmpty()&&!ItemStack.isSameItemSameComponents(old,source))continue;
                int count=Math.min(source.getCount(),source.getMaxStackSize()-(old.isEmpty()?0:old.getCount()));if(count<=0)continue;
                if(old.isEmpty())dock.setItem(dest,source.copyWithCount(count));else old.grow(count);
                source.shrink(count);changed=true;
            }
        }
        if(changed){dock.markWorked();dock.setChanged();rover.inventory().setChanged();}return changed;
    }
}

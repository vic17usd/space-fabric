package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/** Server-only thermal accounting. It observes loaded machines and never requests chunks. */
public final class ThermalSystem {
    public static final int CHARGE=4800, RATE=120;
    private static final Map<ServerLevel,Map<BlockPos,ColonyMachineBlockEntity>> ACTIVE=new IdentityHashMap<>();
    private ThermalSystem() { }
    public static List<ColonyMachineBlockEntity> loaded(ServerLevel level){var map=ACTIVE.get(level);return map==null?List.of():map.values().stream().filter(m->!m.isRemoved()&&level.getChunkSource().getChunkNow(m.getBlockPos().getX()>>4,m.getBlockPos().getZ()>>4)!=null).toList();}
    static void register(ServerLevel level,ColonyMachineBlockEntity machine){ACTIVE.computeIfAbsent(level,k->new HashMap<>()).put(machine.getBlockPos(),machine);}
    static void remove(ColonyMachineBlockEntity machine){if(machine.getLevel() instanceof ServerLevel level){var map=ACTIVE.get(level);if(map!=null)map.remove(machine.getBlockPos(),machine);}}
    public static int generation(ColonyContent.Machine kind) {
        return switch(kind){case HABITAT,SOLAR->0;case GEOTHERMAL->30;case DRILL->55;case CERAMIC_KILN->40;
            case ELECTROLYZER,GAS_SEPARATOR->14;case COOLER->12;case EXCHANGER->6;default->8;};
    }
    /** Contains only machines touching the validated room, with a deterministic owner for overlaps. */
    static Map<ColonyMachineBlockEntity,ColonyMachineBlockEntity> owners(List<ColonyMachineBlockEntity> machines,ServerLevel level) {
        var owners=new IdentityHashMap<ColonyMachineBlockEntity,ColonyMachineBlockEntity>();
        var at=new HashMap<BlockPos,ColonyMachineBlockEntity>();machines.forEach(m->at.put(m.getBlockPos(),m));
        for(var controller:machines) {
            if(controller.kind()!=ColonyContent.Machine.HABITAT||!controller.validate(level))continue;
            for(var pos:controller.enclosure().boundary().keySet()) {
                var machine=at.get(pos);
                if(machine!=null&&machine.kind()!=ColonyContent.Machine.HABITAT)owners.putIfAbsent(machine,controller);
            }
        }
        return owners;
    }
    private static final class Load {
        int ambient,generated,cooling;
        Load(int ambient,int cooling){this.ambient=ambient;this.cooling=cooling;}
        int demand(ThermalBudget budget){return (int)Math.clamp((long)budget.heat()+ambient+generated-cooling,0,Integer.MAX_VALUE);}
    }
    /** Same accounting used by the server and native inventory tests. Owners must be loaded machines. */
    public static void balance(List<ColonyMachineBlockEntity> machines,Map<ColonyMachineBlockEntity,ColonyMachineBlockEntity> ownership,boolean venus,long cycle) {
        var loads=new LinkedHashMap<ColonyMachineBlockEntity,Load>();
        var at=new HashMap<BlockPos,ColonyMachineBlockEntity>();
        var present=Collections.newSetFromMap(new IdentityHashMap<ColonyMachineBlockEntity,Boolean>());present.addAll(machines);
        for(var machine:machines) {
            at.put(machine.getBlockPos(),machine);
            var owner=ownership.getOrDefault(machine,machine);
            if(!present.contains(owner))owner=machine;
            machine.thermalOwner(owner==machine?null:owner);
            final var target=owner;
            var load=loads.computeIfAbsent(target,k->{
                int volume=target.enclosure()==null?0:target.enclosure().air().size();
                boolean habitat=target.kind()==ColonyContent.Machine.HABITAT;
                return new Load(venus?(habitat?8+(volume+15)/16:4):0,
                    venus?(habitat?(target.passiveCooling()?16:0):8):40);
            });
            if(machine.takeWorked()&&venus)load.generated+=generation(machine.kind());
        }
        if(venus)for(var exchanger:machines) {
            if(exchanger.kind()!=ColonyContent.Machine.EXCHANGER||exchanger.maintenance().failed())continue;
            var targets=new ArrayList<ColonyMachineBlockEntity>();
            for(var direction:Direction.values()) {
                var neighbor=at.get(exchanger.getBlockPos().relative(direction));
                if(neighbor==null||neighbor.kind()==ColonyContent.Machine.EXCHANGER)continue;
                var target=ownership.getOrDefault(neighbor,neighbor);
                if(loads.containsKey(target)&&!targets.contains(target))targets.add(target);
            }
            if(targets.isEmpty())continue;
            Collections.rotate(targets,-Math.floorMod(cycle,targets.size()));
            int remaining=RATE;
            for(var target:targets) {
                var load=loads.get(target);
                int supplied=exchanger.extractCooling(Math.min(remaining,load.demand(target.localThermal())));
                load.cooling+=supplied;remaining-=supplied;
            }
        }
        for(var entry:loads.entrySet()) {
            var budget=entry.getKey().localThermal();var load=entry.getValue();
            budget.tick(load.ambient,load.generated,load.cooling);entry.getKey().setChanged();
        }
    }
    private static int stage(ThermalBudget budget){return budget.tripped()?2:budget.warning()?1:0;}
    private static void tick(ServerLevel level) {
        if(level.getGameTime()%20!=0)return;
        var active=ACTIVE.get(level);if(active==null)return;
        active.entrySet().removeIf(e->e.getValue().isRemoved()||level.getChunkSource().getChunkNow(e.getKey().getX()>>4,e.getKey().getZ()>>4)==null);
        var machines=new ArrayList<>(active.values());machines.sort(Comparator.comparingLong(m->m.getBlockPos().asLong()));
        var before=new IdentityHashMap<ColonyMachineBlockEntity,Integer>();machines.forEach(m->before.put(m,stage(m.localThermal())));
        balance(machines,owners(machines,level),PlanetDimensions.find(level).orElse(null)==Planet.VENUS,level.getGameTime()/20);
        for(var machine:machines) {
            int old=before.get(machine),now=stage(machine.localThermal());
            if(old==now)continue;
            for(var player:level.players())if(player.distanceToSqr(machine.getBlockPos().getX()+.5,machine.getBlockPos().getY()+.5,machine.getBlockPos().getZ()+.5)<=1024)
                player.sendSystemMessage(Component.translatable("thermal.space_fabric.notice",machine.getBlockState().getBlock().getName(),machine.getBlockPos().toShortString(),
                    Component.translatable("thermal.space_fabric."+(now==2?"trip":now==1?"warning":"normal"))));
        }
    }
    public static void initialize(){ServerTickEvents.END_LEVEL_TICK.register(ThermalSystem::tick);ServerLifecycleEvents.SERVER_STOPPED.register(server->ACTIVE.clear());}
}

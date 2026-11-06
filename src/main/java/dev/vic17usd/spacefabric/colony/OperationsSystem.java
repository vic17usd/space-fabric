package dev.vic17usd.spacefabric.colony;

import java.util.*;
import dev.vic17usd.spacefabric.planet.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.InteractionHand;

public final class OperationsSystem {
    private OperationsSystem() { }
    public static int autonomy(int oxygen,int energy,int water,int occupants,boolean venus){return Math.min(occupants<=0?Integer.MAX_VALUE:oxygen/occupants,Math.min(energy/(venus?15:5),venus?water/5:Integer.MAX_VALUE));}
    public static List<Component> dashboard(ServerLevel level,ColonyMachineBlockEntity controller){
        var r=controller.readings();var units=ThermalSystem.loaded(level).stream().filter(m->m==controller||controller.enclosure()!=null&&controller.enclosure().boundary().containsKey(m.getBlockPos())).toList();
        int occupants=controller.enclosure()==null?0:(int)level.players().stream().filter(p->!p.isSpectator()&&p.isAlive()&&controller.enclosure().air().contains(BlockPos.containing(p.getEyePosition()))).count();
        var pages=new ArrayList<Component>();pages.add(Component.translatable("operations.space_fabric.condition",Component.translatable("gui.space_fabric.habitat."+r.condition().name().toLowerCase(java.util.Locale.ROOT)),units.stream().filter(m->m.maintenance().integrity()<=7500||m.thermal().tripped()).count()));pages.add(Component.translatable("operations.space_fabric.dashboard",r.oxygen(),r.energy(),r.coolant(),controller.support().respired(),r.heat(),r.regulated()?autonomy(r.oxygen(),r.energy(),r.coolant(),occupants,r.venus()):0,occupants));
        pages.add(Component.translatable("operations.space_fabric.consumption",occupants,r.venus()?15:5,r.venus()?5:0,units.size()));
        for(var m:units.stream().sorted(Comparator.comparingLong(m->m.getBlockPos().asLong())).limit(32).toList())pages.add(Component.translatable("operations.space_fabric.machine",m.getBlockState().getBlock().getName(),m.getBlockPos().toShortString(),m.maintenance().integrity()/100,m.maintenance().state().filter(),m.completedBatches(),m.progress()));
        return List.copyOf(pages);
    }
    private static int count(ServerPlayer player,Item item){int n=0;for(var stack:player.getInventory().getNonEquipmentItems())if(stack.is(item))n+=stack.getCount();return n;}
    private static void consume(ServerPlayer player,Item item){for(var stack:player.getInventory().getNonEquipmentItems())if(stack.is(item)){stack.shrink(1);return;}}
    private static ItemStack token(ServerPlayer player,Item item){var stack=new ItemStack(item);CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.putString("SurveyOwner",player.getUUID().toString()));return stack;}
    public static void project(ServerPlayer player,BlockPos pos){
        var level=player.level();if(!player.isAlive()||player.isSpectator()||!player.mayBuild()||player.distanceToSqr(pos.getCenter())>64||!level.mayInteract(player,pos)||PlanetDimensions.find(level).orElse(null)!=Planet.VENUS)return;
        if(level.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine){
            if(machine.kind()==ColonyContent.Machine.SURVEY&&machine.naturalMission()==3)sample(player);
            if(machine.kind()==ColonyContent.Machine.SURVEY&&machine.naturalMission()==2&&count(player,ColonyContent.DATA_CARD)>0){consume(player,ColonyContent.DATA_CARD);player.getInventory().placeItemBackInInventory(token(player,ExpansionContent.BLACK_BOX));}
            if(machine.kind()==ColonyContent.Machine.HABITAT&&machine.operating()){
                for(var spec:List.of(Map.entry(ExpansionContent.BLACK_BOX,4),Map.entry(ExpansionContent.AIR_SAMPLE,5)))for(var stack:player.getInventory().getNonEquipmentItems()){
                    if(!stack.is(spec.getKey()))continue;
                    var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
                    if(!tag.getStringOr("SurveyOwner","").equals(player.getUUID().toString())||!ExpeditionLedger.get(level).claim(player.getUUID(),spec.getValue()))continue;
                    stack.shrink(1);player.getInventory().placeItemBackInInventory(new ItemStack(dev.vic17usd.spacefabric.industry.IndustryContent.COMPOSITE_PLATE,4));
                    if(spec.getValue()==5)player.getInventory().placeItemBackInInventory(new ItemStack(ColonyContent.EMPTY_CANISTER));
                    player.sendSystemMessage(Component.translatable("operations.space_fabric.delivered"));break;
                }
            }
        }
    }
    private static void tick(ServerLevel level){
        if(level.getGameTime()%20!=0)return;
        for(var player:level.players()){
            var stack=player.getMainHandItem();if(!stack.is(ExpansionContent.NAVIGATOR))continue;
            for(var machine:ThermalSystem.loaded(level))if(machine.naturalMission()>0&&machine.getBlockPos().distSqr(player.blockPosition())<=1024&&OperationsNavigator.points(stack).stream().noneMatch(p->p.dimension().equals(level.dimension().identifier().toString())&&p.pos().equals(machine.getBlockPos())))OperationsNavigator.discover(stack,new OperationsNavigator.Point(level.dimension().identifier().toString(),machine.getBlockPos(),machine.getBlockState().getBlock().getName().getString()+" "+machine.naturalMission()));
            var points=OperationsNavigator.points(stack);if(!points.isEmpty()&&player.getVehicle()==null)player.sendOverlayMessage(OperationsNavigator.hud(OperationsNavigator.selected(stack).orElse(points.getFirst()),level.dimension().identifier().toString(),player.blockPosition()));
        }
    }
    public static boolean sample(ServerPlayer player){
        if(PlanetDimensions.find(player.level()).orElse(null)!=Planet.VENUS||player.getY()<280||player.getY()>320||count(player,ColonyContent.EMPTY_CANISTER)<1||count(player,ColonyContent.CHARGED_POWER_CELL)<1)return false;
        consume(player,ColonyContent.EMPTY_CANISTER);consume(player,ColonyContent.CHARGED_POWER_CELL);player.getInventory().placeItemBackInInventory(token(player,ExpansionContent.AIR_SAMPLE));player.getInventory().placeItemBackInInventory(new ItemStack(ColonyContent.EMPTY_POWER_CELL));return true;
    }
    public static void initialize(){ServerTickEvents.END_LEVEL_TICK.register(OperationsSystem::tick);}
}

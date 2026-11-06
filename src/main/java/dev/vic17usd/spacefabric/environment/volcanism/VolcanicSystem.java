package dev.vic17usd.spacefabric.environment.volcanism;

import dev.vic17usd.spacefabric.colony.HabitatSystem;
import dev.vic17usd.spacefabric.equipment.SuitProtection;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.space.RocketCabin;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/** Surface-only local events. Budgets are shared per level and no terrain is ever written. */
public final class VolcanicSystem {
    public static final int SCAN_COLUMNS=48;
    private static final List<int[]> OFFSETS=new ArrayList<>();
    private static final Map<ServerLevel,State> LEVELS=new IdentityHashMap<>();
    private static final class State {
        final VolcanicField field=new VolcanicField();
        final Map<UUID,Integer> cursors=new HashMap<>();int turn;
    }
    static{for(int x=-16;x<=16;x++)for(int z=-16;z<=16;z++)OFFSETS.add(new int[]{x,z});OFFSETS.sort(Comparator.comparingInt(p->p[0]*p[0]+p[1]*p[1]));}
    private VolcanicSystem() { }
    public static Component text(String key,Object... args){return Component.translatable("volcanism.space_fabric."+key,args);}
    private static VolcanicTerrain.View view(ServerLevel level){return new VolcanicTerrain.View(){
        public boolean loaded(BlockPos pos){return level.getWorldBorder().isWithinBounds(pos)&&level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null;}
        public BlockState state(BlockPos pos){return level.getBlockState(pos);}
        public net.minecraft.world.level.BlockGetter blocks(){return level;}
    };}
    private static void discover(ServerLevel level,State state,List<ServerPlayer> players,VolcanicTerrain.View world){
        if(players.isEmpty())return;
        for(int work=0;work<SCAN_COLUMNS;work++){
            var player=players.get(Math.floorMod(state.turn++,players.size()));
            int cursor=state.cursors.getOrDefault(player.getUUID(),0);var offset=OFFSETS.get(cursor);state.cursors.put(player.getUUID(),(cursor+1)%OFFSETS.size());
            int x=player.getBlockX()+offset[0],z=player.getBlockZ()+offset[1];
            var chunk=level.getChunkSource().getChunkNow(x>>4,z>>4);if(chunk==null)continue;
            int y=chunk.getHeight(Heightmap.Types.WORLD_SURFACE,x&15,z&15);
            if(y<level.getMinY()||y>260||Math.abs(y+1-player.getY())>24)continue;
            var pos=new BlockPos(x,y,z);var kind=VolcanicTerrain.source(world,pos);
            if(kind!=null)state.field.discover(pos,kind,level.getSeed());
        }
    }
    private static void particles(ServerLevel level,List<ServerPlayer> players,BlockPos pos,VolcanicCycle cycle,long time){
        boolean gas=cycle.kind()==VolcanicCycle.Kind.GAS;
        var phase=cycle.phase();
        if(phase==VolcanicCycle.Phase.QUIET&&time%40!=0||phase==VolcanicCycle.Phase.RECOVERY&&time%20!=0)return;
        boolean active=phase==VolcanicCycle.Phase.ACTIVE,warning=phase==VolcanicCycle.Phase.WARNING;
        double y=pos.getY()+1.1;
        for(var player:players){
            if(player.distanceToSqr(pos.getCenter())>1024)continue;
            level.sendParticles(player,active?ParticleTypes.CAMPFIRE_COSY_SMOKE:ParticleTypes.SMOKE,false,false,pos.getX()+.5,y+(active?1.5:0),pos.getZ()+.5,active?6:warning?3:1,active?.55:.2,active?1.5:.25,active?.55:.2,active?.05:.015);
            if(gas&&(active||warning))level.sendParticles(player,new DustParticleOptions(0xB9A851,active?1.8F:1.0F),false,false,pos.getX()+.5,y+(active?2:0),pos.getZ()+.5,active?4:1,active?1.2:.25,active?1.7:.4,active?1.2:.25,.03);
            if(!gas&&active){
                level.sendParticles(player,ParticleTypes.LAVA,false,false,pos.getX()+.5,y+.4,pos.getZ()+.5,5,.65,.4,.65,.1);
                level.sendParticles(player,ParticleTypes.FLAME,false,false,pos.getX()+.5,y+2.5,pos.getZ()+.5,8,2,1.8,2,.08);
                level.sendParticles(player,ParticleTypes.ASH,false,false,pos.getX()+.5,y+4,pos.getZ()+.5,4,3,1,3,.025);
            }
        }
        if((active||warning)&&time%20==0)level.playSound(null,pos,gas?SoundEvents.FIRE_EXTINGUISH:active?SoundEvents.LAVA_POP:SoundEvents.LAVA_AMBIENT,SoundSource.AMBIENT,active?.55F:.3F,gas?.55F:active?.6F:.4F);
    }
    private static void exposure(ServerLevel level,List<ServerPlayer> players,State state,VolcanicTerrain.View world){
        for(var player:players){
            double nearest=144;Map.Entry<BlockPos,VolcanicCycle> notice=null;
            var combined=VolcanicRules.NONE;boolean eruption=false;
            boolean immune=player.isCreative()||player.isSpectator();
            boolean sheltered=RocketCabin.protects(player)||HabitatSystem.protects(player);
            var suit=SuitProtection.tier(player);
            for(var entry:state.field.sources().entrySet()){
                var pos=entry.getKey();var cycle=entry.getValue();
                double distance=player.distanceToSqr(pos.getCenter());
                if(cycle.alert()&&distance<nearest){nearest=distance;notice=entry;}
                if(cycle.phase()!=VolcanicCycle.Phase.ACTIVE||sheltered||immune)continue;
                if(!VolcanicRules.contains(cycle.kind(),player.getX()-pos.getX()-.5,player.getY()-pos.getY(),player.getZ()-pos.getZ()-.5))continue;
                if(!VolcanicTerrain.clearRay(world,new Vec3(pos.getX()+.5,pos.getY()+1.1,pos.getZ()+.5),player.getEyePosition()))continue;
                combined=VolcanicRules.merge(combined,VolcanicRules.impact(cycle.kind(),suit,false,false));eruption|=cycle.kind()==VolcanicCycle.Kind.ERUPTION;
            }
            if(notice!=null&&!sheltered){
                var c=notice.getValue();player.sendOverlayMessage(text((c.kind()==VolcanicCycle.Kind.GAS?"gas_":"eruption_")+(c.phase()==VolcanicCycle.Phase.WARNING?"warning":"active"),(c.remaining()+19)/20,(int)Math.sqrt(nearest)));
            }
            if(VolcanicConfig.settings().damage()&&!combined.equals(VolcanicRules.NONE))VolcanicEffects.apply(level,player,combined,eruption);
        }
    }
    private static void tick(ServerLevel level){
        if(!VolcanicConfig.settings().enabled()||PlanetDimensions.find(level).orElse(null)!=Planet.VENUS)return;
        var players=level.players().stream().filter(p->p.isAlive()&&!p.isSpectator()&&p.getY()<=260).toList();
        if(players.isEmpty()){LEVELS.remove(level);return;}
        var state=LEVELS.computeIfAbsent(level,k->new State());var world=view(level);long time=level.getGameTime();
        state.cursors.keySet().removeIf(id->players.stream().noneMatch(p->p.getUUID().equals(id)));
        if(time%5==0)discover(level,state,players,world);
        state.field.tick(pos->players.stream().anyMatch(p->p.distanceToSqr(pos.getCenter())<=2304)&&VolcanicTerrain.source(world,pos)==state.field.sources().get(pos).kind());
        if(time%5==0)state.field.sources().forEach((pos,cycle)->particles(level,players,pos,cycle,time));
        if(time%20==0)exposure(level,players,state,world);
    }
    public static void initialize(){VolcanicConfig.initialize();ServerTickEvents.END_LEVEL_TICK.register(VolcanicSystem::tick);ServerLifecycleEvents.SERVER_STOPPED.register(server->LEVELS.clear());}
}

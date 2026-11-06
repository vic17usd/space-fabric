package dev.vic17usd.spacefabric.mars;

import com.mojang.serialization.Codec;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.worldgen.PlanetBiomes;
import java.util.*;
import java.util.function.*;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.LevelChunk;

/** One-time physical CO2 skin, applied after generation and to already explored chunks. */
public final class MarsIceCover {
    public static final int INTERIOR=1,WEST=2,EAST=4,NORTH=8,SOUTH=16;
    private static final AttachmentType<Integer> DONE=AttachmentRegistry.<Integer>builder()
        .initializer(()->0).persistent(Codec.intRange(0,31)).buildAndRegister(SpaceFabric.id("mars_ice_cover_v1"));
    private static final Map<ServerLevel,LinkedHashSet<ChunkPos>> PENDING=new WeakHashMap<>();
    private MarsIceCover() { }
    public static int pending(int completed,int available){return available&~completed&31;}
    private static int side(BlockPos p,Direction d,ChunkPos chunk){
        if(d==Direction.WEST&&p.getX()==chunk.getMinBlockX())return WEST;
        if(d==Direction.EAST&&p.getX()==chunk.getMaxBlockX())return EAST;
        if(d==Direction.NORTH&&p.getZ()==chunk.getMinBlockZ())return NORTH;
        if(d==Direction.SOUTH&&p.getZ()==chunk.getMaxBlockZ())return SOUTH;
        return INTERIOR;
    }
    public static int coat(BlockGetter level,ChunkPos chunk,int minY,int maxY,int mask,Predicate<BlockPos> eligible,Consumer<BlockPos> replace){
        if(mask==0)return 0;
        var changed=new ArrayList<BlockPos>();var p=new BlockPos.MutableBlockPos();
        for(int x=chunk.getMinBlockX();x<=chunk.getMaxBlockX();x++)for(int z=chunk.getMinBlockZ();z<=chunk.getMaxBlockZ();z++)for(int y=minY;y<maxY;y++){
            p.set(x,y,z);
            if(!level.getBlockState(p).is(MarsPolarContent.ICE)||!eligible.test(p))continue;
            for(var direction:Direction.values()){
                if((mask&side(p,direction,chunk))==0)continue;
                var next=p.relative(direction);var neighbour=level.getBlockState(next);
                if(!neighbour.isCollisionShapeFullBlock(level,next)){changed.add(p.immutable());break;}
            }
        }
        changed.forEach(replace);return changed.size();
    }
    private static void enqueue(ServerLevel level,LevelChunk chunk){
        if(PlanetDimensions.find(level).orElse(null)!=Planet.MARS)return;
        var queue=PENDING.computeIfAbsent(level,k->new LinkedHashSet<>());var p=chunk.getPos();
        queue.add(p);queue.add(new ChunkPos(p.x()-1,p.z()));queue.add(new ChunkPos(p.x()+1,p.z()));queue.add(new ChunkPos(p.x(),p.z()-1));queue.add(new ChunkPos(p.x(),p.z()+1));
    }
    private static void apply(ServerLevel level,LevelChunk chunk){
        var p=chunk.getPos();int available=INTERIOR;
        if(level.getChunkSource().getChunkNow(p.x()-1,p.z())!=null)available|=WEST;
        if(level.getChunkSource().getChunkNow(p.x()+1,p.z())!=null)available|=EAST;
        if(level.getChunkSource().getChunkNow(p.x(),p.z()-1)!=null)available|=NORTH;
        if(level.getChunkSource().getChunkNow(p.x(),p.z()+1)!=null)available|=SOUTH;
        var attachments=(AttachmentTarget)chunk;int done=attachments.getAttachedOrElse(DONE,0),work=pending(done,available);
        if(work==0)return;
        var sections=chunk.getSections();
        for(int i=0;i<sections.length;i++)if(sections[i].maybeHas(s->s.is(MarsPolarContent.ICE))){
            int min=level.getMinY()+i*16;
            coat(level,p,min,min+16,work,pos->level.getBiome(pos).is(PlanetBiomes.MARS_POLAR),pos->level.setBlock(pos,MarsPolarContent.CO2.defaultBlockState(),2));
        }
        attachments.setAttached(DONE,done|available);chunk.markUnsaved();
    }
    public static void initialize(){
        ServerChunkEvents.CHUNK_LOAD.register((level,chunk,newlyGenerated)->enqueue(level,chunk));
        ServerTickEvents.END_LEVEL_TICK.register(level->{
            var queue=PENDING.get(level);if(queue==null)return;
            for(int work=0;work<2&&!queue.isEmpty();work++){
                var iterator=queue.iterator();var pos=iterator.next();iterator.remove();
                var chunk=level.getChunkSource().getChunkNow(pos.x(),pos.z());
                if(chunk!=null)apply(level,chunk);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server->PENDING.clear());
    }
}

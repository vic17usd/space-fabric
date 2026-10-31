package dev.vic17usd.spacefabric.expedition;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.worldgen.structure.VenusSurfacePiece;
import java.util.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

/** Loaded-site evidence and a global one-candidate-per-second destination survey. */
public final class ColossusExpedition {
    public static final List<String> SITES=List.of("rustfall_foundry","venusian_ash_outpost","venus_sulfurfire_nest","venus_colossus_bunker");
    private record Search(ChunkPos origin,int cursor){}
    private static final LinkedHashMap<UUID,Search> SEARCHES=new LinkedHashMap<>();
    private static boolean inFlight;
    private static int epoch;
    private ColossusExpedition(){}
    public static Component text(String key,Object... args){return Component.translatable("colossus.quest."+key,args);}
    public static List<Component> pages(ServerPlayer player){
        var e=ColossusProgress.get(player.level()).entry(player.getUUID());
        var result=new ArrayList<Component>();result.add(text("intro"));result.add(text("objective_"+e.stage()));
        for(int i=0;i<5;i++)result.add(text("page",i<e.stage()?"✓":"○",text("detail_"+i)));
        e.target().ifPresent(pos->result.add(text("coordinates",pos.toShortString())));
        if(e.stage()==3&&e.target().isEmpty())result.add(text("survey"));
        return List.copyOf(result);
    }
    /** Never requests a missing chunk. Plot pieces cover empty sky and are excluded as proof. */
    private static Optional<StructureStart> siteStart(ServerLevel level,BlockPos pos,String name){
        var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
        if(chunk==null)return Optional.empty();
        var structure=level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(SpaceFabric.id(name));
        if(structure==null)return Optional.empty();
        var candidates=new ArrayList<StructureStart>();var own=chunk.getStartForStructure(structure);if(own!=null)candidates.add(own);
        for(long reference:chunk.getReferencesForStructure(structure)){
            var origin=level.getChunkSource().getChunkNow(ChunkPos.getX(reference),ChunkPos.getZ(reference));
            if(origin!=null){var start=origin.getStartForStructure(structure);if(start!=null)candidates.add(start);}
        }
        return candidates.stream().filter(start->start.isValid()&&start.getPieces().stream().anyMatch(piece->
            !(piece instanceof VenusSurfacePiece v&&v.isPlot())&&piece.getBoundingBox().isInside(pos))).findFirst();
    }
    public static boolean atSite(ServerLevel level,BlockPos pos,String name){return siteStart(level,pos,name).isPresent();}
    /** Shift-use the existing diary on a solid block of a natural expedition site. */
    public static boolean interact(ServerPlayer player,BlockPos pos){
        var level=player.level();
        if(!player.isAlive()||player.isSpectator()||!player.mayBuild()||!player.onGround()||player.distanceToSqr(pos.getCenter())>64
            ||PlanetDimensions.find(level).orElse(null)!=Planet.VENUS||!level.mayInteract(player,pos)
            ||level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null)return false;
        if(!level.getBlockState(pos).isCollisionShapeFullBlock(level,pos))return false;
        var progress=ColossusProgress.get(level);var e=progress.entry(player.getUUID());
        for(int i=0;i<SITES.size();i++)if(atSite(level,pos,SITES.get(i))){
            if(e.stage()==i&&(i!=3||e.target().filter(t->player.distanceToSqr(t.getCenter())<=32*32&&Math.abs(player.getY()-t.getY())<=8&&siteStart(level,pos,SITES.get(3)).orElseThrow().getBoundingBox().isInside(t)).isPresent())&&progress.advance(player.getUUID(),i)){
                player.sendSystemMessage(text("recovered",text("objective_"+(i+1))));
                if(i==2)queue(player);
            }else player.sendSystemMessage(text("objective_"+e.stage()));
            QuestNetwork.send(player);return true;
        }
        return false;
    }
    public static void defeated(ServerPlayer player,BlockPos boss){
        if(PlanetDimensions.find(player.level()).orElse(null)!=Planet.VENUS||!atSite(player.level(),boss,SITES.get(3)))return;
        var progress=ColossusProgress.get(player.level());
        if(progress.entry(player.getUUID()).target().filter(t->siteStart(player.level(),boss,SITES.get(3)).orElseThrow().getBoundingBox().isInside(t)).isEmpty())return;
        if(progress.advance(player.getUUID(),4)){
            player.sendSystemMessage(text("objective_5"));QuestNetwork.send(player);
        }
    }
    private static void queue(ServerPlayer player){
        var e=ColossusProgress.get(player.level()).entry(player.getUUID());
        if(e.stage()==3&&e.target().isEmpty())SEARCHES.putIfAbsent(player.getUUID(),new Search(player.chunkPosition(),0));
    }
    /** 81 region offsets, ordered outward. A retry is explicit and cannot add parallel jobs. */
    public record Offset(int x,int z){}
    public static List<Offset> offsets(){
        var list=new ArrayList<Offset>();
        for(int radius=0;radius<=4;radius++)for(int x=-radius;x<=radius;x++)for(int z=-radius;z<=radius;z++)
            if(Math.max(Math.abs(x),Math.abs(z))==radius)list.add(new Offset(x,z));
        return List.copyOf(list);
    }
    private static void survey(ServerLevel level){
        if(inFlight||SEARCHES.isEmpty())return;
        var iterator=SEARCHES.entrySet().iterator();var job=iterator.next();iterator.remove();
        var player=level.getServer().getPlayerList().getPlayer(job.getKey());
        if(player==null||player.level()!=level)return;
        var progress=ColossusProgress.get(level);var e=progress.entry(player.getUUID());
        if(e.stage()!=3||e.target().isPresent())return;
        var set=level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET).getValue(SpaceFabric.id(SITES.get(3)));
        if(!level.structureManager().shouldGenerateStructures()||set==null||!(set.placement() instanceof RandomSpreadStructurePlacement placement)){
            player.sendSystemMessage(text("unavailable"));return;
        }
        var offset=offsets().get(job.getValue().cursor());var origin=job.getValue().origin();
        var candidate=placement.getPotentialStructureChunk(level.getSeed(),origin.x()+offset.x()*placement.spacing(),origin.z()+offset.z()*placement.spacing());
        var cache=level.getChunkSource();var generator=cache.getGenerator();
        var structure=level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(SpaceFabric.id(SITES.get(3)));
        // The bunker GenerationStub surveys nine heights. No templates or chunks are opened here.
        var found=placement.isStructureChunk(cache.getGeneratorState(),candidate.x(),candidate.z())?
            structure.findValidGenerationPoint(new Structure.GenerationContext(level.registryAccess(),generator,generator.getBiomeSource(),cache.randomState(),null,
                level.getSeed(),candidate,level,structure.biomes()::contains)):Optional.<Structure.GenerationStub>empty();
        if(found.isPresent()&&level.getWorldBorder().isWithinBounds(found.get().position())){
            // Verify the actual start asynchronously: old explored chunks may predate this structure.
            // One pending chunk at STRUCTURE_STARTS globally; never join a future on the tick thread.
            inFlight=true;int generation=epoch;
            cache.getChunkFuture(candidate.x(),candidate.z(),net.minecraft.world.level.chunk.status.ChunkStatus.STRUCTURE_STARTS,true)
                .whenComplete((result,error)->level.getServer().execute(()->{
                    if(generation!=epoch)return;inFlight=false;
                    var chunk=error==null&&result!=null?result.orElse(null):null;
                    var start=chunk==null?null:chunk.getStartForStructure(structure);
                    // Prefer a fresh, undecorated site: a full old chunk may have an already defeated boss.
                    if(start!=null&&start.isValid()&&!chunk.getPersistedStatus().isOrAfter(net.minecraft.world.level.chunk.status.ChunkStatus.FULL)){
                        var box=start.getBoundingBox();var target=new BlockPos(box.getCenter().getX(),box.minY()+5,box.getCenter().getZ());
                        if(progress.reveal(player.getUUID(),target)){
                            player.sendSystemMessage(text("coordinates",target.toShortString()));QuestNetwork.send(player);
                        }
                    }else retry(player,job.getValue());
                }));
        }else retry(player,job.getValue());
    }
    private static void retry(ServerPlayer player,Search search){
        if(search.cursor()+1<offsets().size())SEARCHES.put(player.getUUID(),new Search(search.origin(),search.cursor()+1));
        else player.sendSystemMessage(text("not_found"));
    }

    public static void initialize(){
        QuestNetwork.initialize();
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{SEARCHES.clear();inFlight=false;epoch++;});
        ServerTickEvents.END_LEVEL_TICK.register(level->{
            if(level.getGameTime()%20!=0||PlanetDimensions.find(level).orElse(null)!=Planet.VENUS)return;
            survey(level);for(var player:level.players())QuestNetwork.send(player);
        });
        CommandRegistrationCallback.EVENT.register((dispatcher,context,selection)->dispatcher.register(Commands.literal("venus").then(Commands.literal("coloso").executes(c->{
            var player=c.getSource().getPlayerOrException();pages(player).forEach(player::sendSystemMessage);
            if(PlanetDimensions.find(player.level()).orElse(null)==Planet.VENUS)queue(player);return 1;
        }))));
    }
}

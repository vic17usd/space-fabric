package dev.vic17usd.spacefabric.client.map;

import com.google.gson.*;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.map.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelResource;

public final class AtlasClient {
    private static final LinkedHashMap<String,AtlasData> MAPS=new LinkedHashMap<>(4,.75F,true);
    private static final LinkedHashMap<String,AtlasData> CAVES=new LinkedHashMap<>(4,.75F,true);
    private static boolean underground;
    private static int caveBand;
    private static String layerDimension="";
    private static final List<int[]> OFFSETS=new ArrayList<>();
    private static final AtlasCanvas MINI=new AtlasCanvas("atlas_mini",76);
    private static UUID world;
    private static Path root;
    private static boolean mini=true,failed;
    private static final Set<String> WARNED=new HashSet<>();
    private static int cursor;
    private static long tick;
    private static KeyMapping openKey;
    static {for(int x=-8;x<=8;x++)for(int z=-8;z<=8;z++)OFFSETS.add(new int[]{x,z});OFFSETS.sort(Comparator.comparingInt(p->p[0]*p[0]+p[1]*p[1]));}
    private AtlasClient() { }
    public static Component text(String key,Object... args){return Component.translatable("atlas.space_fabric."+key,args);}
    public static String dimension(){return Minecraft.getInstance().level.dimension().identifier().toString();}
    public static long ticks(){return tick;}
    public static boolean mapKey(net.minecraft.client.input.KeyEvent event){return openKey!=null&&openKey.matches(event);}
    public static Component label(String dimension){
        if(dimension.equals("minecraft:overworld"))return text("overworld");
        if(dimension.startsWith("space_fabric:"))return Component.translatable("destination.space_fabric."+dimension.substring(13));
        return Component.literal(dimension);
    }
    public static AtlasData map(String dimension) throws IOException {
        if(root==null)throw new IOException("Atlas identity pending");
        var data=MAPS.get(dimension);
        if(data==null){
            if(MAPS.size()>=2){var oldest=MAPS.entrySet().iterator().next();oldest.getValue().flush(Integer.MAX_VALUE);MAPS.remove(oldest.getKey());}
            data=new AtlasData(root,dimension);MAPS.put(dimension,data);
        }
        return data;
    }
    public static boolean underground(){return underground;}
    public static AtlasData terrain(AtlasData surface) throws IOException {
        if(!underground||!surface.dimension().equals(dimension()))return surface;
        String key=surface.dimension()+"|"+caveBand;
        var data=CAVES.get(key);
        if(data==null) {
            if(CAVES.size()>=4){var oldest=CAVES.entrySet().iterator().next();oldest.getValue().flush(Integer.MAX_VALUE);CAVES.remove(oldest.getKey());}
            data=new AtlasData(root.resolve("caves").resolve(Integer.toString(caveBand)),surface.dimension());CAVES.put(key,data);
        }
        return data;
    }
    public static List<String> visited() throws IOException {return root==null?List.of():AtlasData.visited(root).stream().filter(d->!d.equals("space_fabric:earth")).toList();}
    public static void error(Exception failure){SpaceFabric.LOGGER.warn("Planet atlas operation failed",failure);var c=Minecraft.getInstance();if(c.player!=null)c.player.sendSystemMessage(text("error"));}
    public static void report(AtlasData data){if(data.error()!=null&&WARNED.add(data.dimension()))error(data.error());}
    private static void close(){
        for(var data:MAPS.values())try{data.flush(Integer.MAX_VALUE);}catch(IOException failure){error(failure);}
        for(var data:CAVES.values())try{data.flush(Integer.MAX_VALUE);}catch(IOException failure){error(failure);}
        CAVES.clear();underground=false;layerDimension="";MAPS.clear();WARNED.clear();root=null;world=null;failed=false;cursor=0;MINI.close();
    }
    private static void start(Minecraft c) throws IOException {
        String scope=c.getSingleplayerServer()!=null?"local:"+c.getSingleplayerServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize():
                c.getCurrentServer()!=null?"server:"+c.getCurrentServer().ip.toLowerCase(Locale.ROOT):"connection";
        root=c.gameDirectory.toPath().resolve("config/space-fabric/maps").resolve(AtlasData.sessionKey(world+"|"+scope,c.player.getUUID()));
        var settings=root.resolve("settings.json");mini=true;
        if(Files.isRegularFile(settings)&&Files.size(settings)<4096)try{mini=JsonParser.parseString(Files.readString(settings)).getAsJsonObject().get("minimap").getAsBoolean();}catch(RuntimeException ignored) { }
    }
    private static void sample(Minecraft c,AtlasData data) throws IOException {
        data.position(c.player.getBlockX(),c.player.getBlockY(),c.player.getBlockZ());
        var feet=c.player.blockPosition();
        int roof=c.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,feet.getX(),feet.getZ());
        boolean cave=roof>feet.getY()+3&&!c.level.canSeeSky(feet);
        int band=CaveSlice.band(feet.getY());
        if(cave!=underground||(cave&&band!=caveBand)||!layerDimension.equals(dimension()))cursor=0;
        underground=cave;caveBand=band;layerDimension=dimension();
        var caveData=cave?terrain(data):null;
        if(caveData!=null)caveData.position(feet.getX(),feet.getY(),feet.getZ());
        for(int work=0;work<2;work++) {
            var offset=OFFSETS.get(cursor);cursor=(cursor+1)%OFFSETS.size();int cx=(c.player.getBlockX()>>4)+offset[0],cz=(c.player.getBlockZ()>>4)+offset[1];
            var chunk=c.level.getChunkSource().getChunk(cx,cz,ChunkStatus.FULL,false);if(chunk==null)continue;
            for(int x=2;x<16;x+=4)for(int z=2;z<16;z+=4) {
                int wx=cx*16+x,wz=cz*16+z,top=chunk.getHeight(Heightmap.Types.WORLD_SURFACE,x,z);
                int color=0xFF14202B;
                if(top>=c.level.getMinY()) {
                    var pos=new BlockPos(wx,top,wz);var state=chunk.getBlockState(pos);int rgb=state.getMapColor(c.level,pos).col;
                    for(int down=1;rgb==0&&down<=8&&top-down>=c.level.getMinY();down++){var p=pos.below(down);rgb=chunk.getBlockState(p).getMapColor(c.level,p).col;}
                    if(rgb==0)rgb=0x697680;
                    int north=z>=4?chunk.getHeight(Heightmap.Types.WORLD_SURFACE,x,z-4):top;
                    double shade=.9+Math.clamp((top-north)*.035,-.18,.1);
                    color=0xFF000000|((int)(((rgb>>16)&255)*shade)<<16)|((int)(((rgb>>8)&255)*shade)<<8)|(int)((rgb&255)*shade);
                }
                data.record(wx,wz,Math.clamp(top+1,-2048,2047),color);
                if(caveData!=null) {
                    var p=new BlockPos.MutableBlockPos(wx,caveBand,wz);
                    int floor=CaveSlice.floor(feet.getY(),c.level.getMinY(),c.level.getMaxY()-1,y->{
                        p.setY(y);var block=chunk.getBlockState(p);
                        return !block.isAir()&&!block.getCollisionShape(c.level,p).isEmpty();
                    });
                    int caveColor=0xFF252936;
                    if(floor!=Integer.MIN_VALUE) {
                        p.setY(floor);int rgb=chunk.getBlockState(p).getMapColor(c.level,p).col;
                        if(rgb==0)rgb=0x75818A;
                        caveColor=0xFF000000|rgb;
                        p.setY(floor+1);
                        if(!chunk.getBlockState(p).getFluidState().isEmpty()){int fluid=chunk.getBlockState(p).getMapColor(c.level,p).col;caveColor=0xFF000000|(fluid==0?0x346DA6:fluid);}
                    }
                    caveData.record(wx,wz,floor==Integer.MIN_VALUE?caveBand:floor+1,caveColor);
                }
            }
        }
    }
    public static void initialize(){
        openKey=dev.vic17usd.spacefabric.client.ModKeyBindings.ATLAS;
        var mark=dev.vic17usd.spacefabric.client.ModKeyBindings.MARKER;
        var toggle=dev.vic17usd.spacefabric.client.ModKeyBindings.MINIMAP;
        ClientPlayNetworking.registerGlobalReceiver(AtlasNetwork.Identity.TYPE,(p,context)->world=p.world());
        ClientPlayConnectionEvents.DISCONNECT.register((handler,c)->close());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(c->close());
        ClientTickEvents.END_CLIENT_TICK.register(c->{
            if(c.level==null||c.player==null)return;
            tick++;
            try {
                if(world!=null&&!failed){if(root==null)start(c);for(var data:MAPS.values())data.beginTick();for(var data:CAVES.values())data.beginTick();var data=map(dimension());if(!c.isPaused())sample(c,data);report(data);if(tick%20==0){for(var cached:MAPS.values())cached.flush(2);for(var cached:CAVES.values())cached.flush(2);}}
            }catch(IOException failure){failed=true;error(failure);}
            while(openKey.consumeClick())if(c.screen==null){if(root!=null&&!failed)c.setScreen(new AtlasScreen());else c.player.sendSystemMessage(text("waiting"));}
            while(mark.consumeClick())if(c.screen==null&&root!=null&&!failed)try{c.setScreen(new WaypointScreen(null,map(dimension()),null,c.player.getBlockX(),c.player.getBlockY(),c.player.getBlockZ()));}catch(IOException failure){error(failure);}
            while(toggle.consumeClick())if(c.screen==null&&root!=null){mini=!mini;var j=new JsonObject();j.addProperty("minimap",mini);try{AtlasData.writeJson(root.resolve("settings.json"),j);}catch(IOException failure){error(failure);}}
        });
        HudElementRegistry.addLast(SpaceFabric.id("planet_minimap"),(gui,delta)->{
            var c=Minecraft.getInstance();if(!mini||root==null||failed||c.level==null||c.player==null||!c.player.isAlive()||c.screen!=null||c.options.hideGui)return;
            if(dev.vic17usd.spacefabric.sphere.SphereDemo.active(c.level))return;
            if(c.player.getVehicle() instanceof dev.vic17usd.spacefabric.space.RocketEntity rocket&&rocket.isFlying())return;
            try {
                int x=gui.guiWidth()-84,y=24;
                MINI.draw(gui,map(dimension()),x,y,76,c.player.getX(),c.player.getZ(),2,false);
                gui.text(c.font,c.font.split(underground?text("cave",c.player.getBlockY()):label(dimension()),76).getFirst(),x,y-12,0xFFE5F4FA);
                gui.centeredText(c.font,c.font.plainSubstrByWidth(c.player.getBlockX()+", "+c.player.getBlockZ(),76),x+38,y+83,0xFFE5F4FA);
            }catch(IOException failure){error(failure);}
        });
    }
}

package dev.vic17usd.spacefabric.client.map;

import com.google.gson.*;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.map.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelResource;
import org.lwjgl.glfw.GLFW;

public final class AtlasClient {
    private static final LinkedHashMap<String,AtlasData> MAPS=new LinkedHashMap<>(4,.75F,true);
    private static final List<int[]> OFFSETS=new ArrayList<>();
    private static final AtlasCanvas MINI=new AtlasCanvas("atlas_mini",80);
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
    public static List<String> visited() throws IOException {return root==null?List.of():AtlasData.visited(root);}
    public static void error(Exception failure){SpaceFabric.LOGGER.warn("Planet atlas operation failed",failure);var c=Minecraft.getInstance();if(c.player!=null)c.player.sendSystemMessage(text("error"));}
    public static void report(AtlasData data){if(data.error()!=null&&WARNED.add(data.dimension()))error(data.error());}
    private static void close(){
        for(var data:MAPS.values())try{data.flush(Integer.MAX_VALUE);}catch(IOException failure){error(failure);}
        MAPS.clear();WARNED.clear();root=null;world=null;failed=false;cursor=0;MINI.close();
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
            }
        }
    }
    public static void initialize(){
        openKey=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.space_fabric.atlas",GLFW.GLFW_KEY_M,KeyMapping.Category.INVENTORY));
        var mark=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.space_fabric.map_marker",GLFW.GLFW_KEY_B,KeyMapping.Category.INVENTORY));
        var toggle=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.space_fabric.minimap",GLFW.GLFW_KEY_N,KeyMapping.Category.INVENTORY));
        ClientPlayNetworking.registerGlobalReceiver(AtlasNetwork.Identity.TYPE,(p,context)->world=p.world());
        ClientPlayConnectionEvents.DISCONNECT.register((handler,c)->close());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(c->close());
        ClientTickEvents.END_CLIENT_TICK.register(c->{
            if(c.level==null||c.player==null)return;
            tick++;
            try {
                if(world!=null&&!failed){if(root==null)start(c);for(var data:MAPS.values())data.beginTick();var data=map(dimension());if(!c.isPaused())sample(c,data);report(data);if(tick%20==0)for(var cached:MAPS.values())cached.flush(2);}
            }catch(IOException failure){failed=true;error(failure);}
            while(openKey.consumeClick())if(c.screen==null){if(root!=null&&!failed)c.setScreen(new AtlasScreen());else c.player.sendSystemMessage(text("waiting"));}
            while(mark.consumeClick())if(c.screen==null&&root!=null&&!failed)try{c.setScreen(new WaypointScreen(null,map(dimension()),null,c.player.getBlockX(),c.player.getBlockY(),c.player.getBlockZ()));}catch(IOException failure){error(failure);}
            while(toggle.consumeClick())if(c.screen==null&&root!=null){mini=!mini;var j=new JsonObject();j.addProperty("minimap",mini);try{AtlasData.writeJson(root.resolve("settings.json"),j);}catch(IOException failure){error(failure);}}
        });
        HudElementRegistry.addLast(SpaceFabric.id("planet_minimap"),(gui,delta)->{
            var c=Minecraft.getInstance();if(!mini||root==null||failed||c.level==null||c.player==null||!c.player.isAlive()||c.screen!=null||c.options.hideGui)return;
            if(c.player.getVehicle() instanceof dev.vic17usd.spacefabric.space.RocketEntity rocket&&rocket.isFlying())return;
            try {
                int x=gui.guiWidth()-88,y=gui.guiHeight()-112;
                MINI.draw(gui,map(dimension()),x,y,80,c.player.getX(),c.player.getZ(),2,false);
                gui.text(c.font,c.font.split(label(dimension()),80).getFirst(),x,y-12,0xFFE5F4FA);
                gui.fill(x,y+67,x+80,y+80,0xB0121D28);
                gui.text(c.font,c.font.plainSubstrByWidth(c.player.getBlockX()+", "+c.player.getBlockZ(),76),x+2,y+70,0xFFE5F4FA);
            }catch(IOException failure){error(failure);}
        });
    }
}

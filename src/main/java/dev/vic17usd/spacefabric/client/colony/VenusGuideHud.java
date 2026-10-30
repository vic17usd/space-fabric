package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.planet.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Text only: no background rectangles, textures or dependency on a held navigator. */
public final class VenusGuideHud {
    private static final VenusMissionTracker STATE=new VenusMissionTracker();
    private static int bottom;
    private VenusGuideHud() { }
    private static boolean visible(){
        var c=Minecraft.getInstance();
        return c.level!=null&&c.player!=null&&c.player.isAlive()&&!c.player.isSpectator()
                &&!(c.player.getVehicle() instanceof dev.vic17usd.spacefabric.space.RocketEntity rocket&&rocket.isFlying())
                &&PlanetDimensions.find(c.level).orElse(null)==Planet.VENUS&&STATE.ready();
    }
    public static int bottom(){return visible()?bottom:0;}
    public static void initialize(){
        ClientPlayNetworking.registerGlobalReceiver(VenusGuideNetwork.State.TYPE,(payload,context)->STATE.accept(payload.flags()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{STATE.clear();bottom=0;});
        ClientTickEvents.END_CLIENT_TICK.register(c->{
            if(c.level==null||c.player==null||!c.player.isAlive()||PlanetDimensions.find(c.level).orElse(null)!=Planet.VENUS){STATE.clear();bottom=0;}
            else if(!c.isPaused())STATE.tick();
        });
        HudElementRegistry.addLast(SpaceFabric.id("venus_missions"),(gui,delta)->draw(gui));
    }
    private static int lines(GuiGraphicsExtractor gui,Component text,int y,int width,int color,int limit){
        var font=Minecraft.getInstance().font;
        for(var line:font.split(text,width).stream().limit(limit).toList()){
            gui.text(font,line,8,y,color);y+=font.lineHeight+2;
        }
        return y;
    }
    private static void draw(GuiGraphicsExtractor gui){
        var c=Minecraft.getInstance();bottom=0;
        if(!visible()||c.options.hideGui||c.screen!=null)return;
        int width=Math.min(230,c.getWindow().getGuiScaledWidth()-16),y=8;
        y=lines(gui,Component.translatable("venus.guide.hud_title",STATE.completed()),y,width,0xFFFFD477,1);
        var remaining=STATE.remaining();
        if(remaining.isEmpty()){
            bottom=lines(gui,Component.translatable("venus.guide.hud_done"),y+3,width,0xFF8DE9BE,2);return;
        }
        var step=remaining.getFirst();
        y=lines(gui,Component.translatable("venus.guide.hud_now",Component.translatable("venus.guide."+step.key())),y+3,width,0xFFFFFFFF,2);
        y=lines(gui,Component.translatable("venus.guide.hud_"+step.key()),y,width,0xFFD6E5EA,3);
        if(remaining.size()>1)y=lines(gui,Component.translatable("venus.guide.hud_next",Component.translatable("venus.guide."+remaining.get(1).key())),y+3,width,0xFF9FB3C0,2);
        bottom=lines(gui,Component.translatable("venus.guide.hud_help"),y+3,width,0xFF9FB3C0,1);
    }
}

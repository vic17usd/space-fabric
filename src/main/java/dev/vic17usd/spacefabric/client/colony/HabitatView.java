package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.colony.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;

/** HUD and indoor fog share the same expiring, server-issued room snapshot. */
public final class HabitatView {
    private static final HabitatClientState STATE=new HabitatClientState();
    private static long tick;
    private HabitatView() { }
    public static boolean clearInterior(Camera camera) {
        var client=Minecraft.getInstance();
        if(client.level==null||client.player==null||!client.player.isAlive()||client.getCameraEntity()!=client.player
                ||client.player.isSpectator()||client.player.hasEffect(MobEffects.BLINDNESS)||client.player.hasEffect(MobEffects.DARKNESS))return false;
        var level=client.level;
        return STATE.clearInterior(level.dimension().identifier(),BlockPos.containing(client.player.getEyePosition()),BlockPos.containing(camera.position()),pos->{
            if(pos.getY()<level.getMinY()||pos.getY()>=level.getMaxY()||!level.hasChunk(pos.getX()>>4,pos.getZ()>>4))return SealScanner.Cell.UNLOADED;
            return HabitatSystem.classify(level.getBlockState(pos),level,pos);
        },tick);
    }
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(HabitatNetwork.State.TYPE,(payload,context)->STATE.accept(payload));
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->STATE.clear());
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            if(client.level==null||client.player==null||!client.player.isAlive()){STATE.clear();return;}
            if(!client.isPaused()){STATE.tick();tick++;}
        });
        HudElementRegistry.addLast(SpaceFabric.id("habitat"),(gui,delta)->draw(gui));
    }
    private static void draw(GuiGraphicsExtractor gui) {
        var client=Minecraft.getInstance();
        if(client.level==null||client.player==null||!client.player.isAlive()||client.screen!=null||client.options.hideGui)return;
        var r=STATE.readings(client.level.dimension().identifier());if(r==null)return;
        int width=Math.min(206,client.getWindow().getGuiScaledWidth()-12),x=client.getWindow().getGuiScaledWidth()-width-6,y=6;
        gui.fill(x,y,x+width,y+(r.venus()?100:85),0xBD101E2B);
        gui.fill(x,y,x+2,y+(r.venus()?100:85),HabitatPresentation.stateColor(r));
        gui.text(client.font,Component.translatable("gui.space_fabric.habitat.title"),x+7,y+5,0xFFE5F5FF);
        gui.text(client.font,HabitatPresentation.warning(r),x+7,y+17,HabitatPresentation.stateColor(r));
        gui.text(client.font,HabitatPresentation.pressure(r),x+7,y+29,0xFFE5F5FF);
        gui.text(client.font,HabitatPresentation.temperature(r),x+113,y+29,0xFFE5F5FF);
        reserve(gui,x,y+42,width,"oxygen",r.oxygen(),0xFF65E5BE);
        reserve(gui,x,y+60,width,"energy",r.energy(),0xFFFFCD69);
        if(r.venus())reserve(gui,x,y+78,width,"coolant",r.coolant(),0xFF6EC9FF);
    }
    private static void reserve(GuiGraphicsExtractor gui,int x,int y,int width,String name,int amount,int color) {
        var font=Minecraft.getInstance().font;
        gui.text(font,Component.translatable("gui.space_fabric.habitat.reserve_"+name,amount,LifeSupport.CAPACITY),x+7,y,0xFFB5D6E5);
        gui.fill(x+7,y+10,x+width-7,y+13,0xFF07121C);
        gui.fill(x+7,y+10,x+7+(int)((width-14)*HabitatReadings.fraction(amount)),y+13,color);
    }
}

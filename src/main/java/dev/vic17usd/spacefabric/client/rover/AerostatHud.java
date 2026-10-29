package dev.vic17usd.spacefabric.client.rover;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.rover.RoverEntity;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class AerostatHud {
    private static RoverEntity previous;private static double y,vertical;
    private AerostatHud() { }
    public static void initialize(){net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(c->{
        var rover=c.player!=null&&c.player.getVehicle() instanceof RoverEntity r&&r.aerial()?r:null;
        vertical=rover!=null&&rover==previous?(rover.getY()-y)*20:0;y=rover==null?0:rover.getY();previous=rover;
    });HudElementRegistry.addLast(SpaceFabric.id("aerostat_flight"),(gui,delta)->{
        var client=Minecraft.getInstance();if(client.options.hideGui||client.player==null||!(client.player.getVehicle() instanceof RoverEntity rover)||!rover.aerial())return;
        var state=rover.systems();int color=state.energy()<2400||state.oxygen()<1200||state.coolant()<1200||state.hull()<25?0xFFFF8070:0xFF85E5D2;
        gui.text(client.font,Component.translatable("operations.space_fabric.flight",(int)rover.getY(),String.format(java.util.Locale.ROOT,"%.1f",vertical),rover.onGround()?"✓":"—"),8,42,color);
        gui.text(client.font,Component.translatable("operations.space_fabric.flight_reserves",state.energy(),state.oxygen(),state.coolant()),8,54,color);
        var device=client.player.getMainHandItem().is(dev.vic17usd.spacefabric.colony.ExpansionContent.NAVIGATOR)?client.player.getMainHandItem():client.player.getOffhandItem();
        if(device.is(dev.vic17usd.spacefabric.colony.ExpansionContent.NAVIGATOR))dev.vic17usd.spacefabric.colony.OperationsNavigator.selected(device).ifPresent(p->gui.text(client.font,dev.vic17usd.spacefabric.colony.OperationsNavigator.hud(p,client.level.dimension().identifier().toString(),client.player.blockPosition()),8,66,color));
    });}
}

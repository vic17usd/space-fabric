package dev.vic17usd.spacefabric.client.rocket;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.space.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Pilot-only presentation; no client packet can advance or complete the flight. */
public final class RocketFlightView {
    private RocketFlightView() { }
    public static RocketEntity rocket() {
        var client=Minecraft.getInstance();
        return client.player!=null&&client.getCameraEntity()==client.player
            &&client.player.getVehicle() instanceof RocketEntity rocket?rocket:null;
    }
    public static boolean inSpace(){var rocket=rocket();return rocket!=null&&rocket.isCruising();}
    public static void initialize() {
        HudElementRegistry.addLast(SpaceFabric.id("rocket_journey"),(gui,delta)->{
            var rocket=rocket();if(rocket==null||!rocket.isFlying())return;
            var client=Minecraft.getInstance();float elapsed=rocket.journeyTicks()+delta.getGameTimeDeltaPartialTick(false);
            // Keep the space view visible while chunks load; do not leave the pilot on a black screen.
            float fade=rocket.isFindingLanding()?0:RocketJourney.blackout(rocket.flightState(),elapsed,rocket.journeyDuration(),rocket.getY(),rocket.level().getMaxY()-8);
            int w=client.getWindow().getGuiScaledWidth(),h=client.getWindow().getGuiScaledHeight();
            if(fade>0)gui.fill(0,0,w,h,((int)(fade*255)<<24));
            String phase=rocket.isFindingLanding()?"landing_search":rocket.isCruising()?"cruise":rocket.flightState()==RocketJourney.DESCENDING?"descent":"ascent";
            var from=TravelDestination.at(rocket.originIndex());var to=TravelDestination.at(rocket.destinationIndex());
            gui.centeredText(client.font,Component.translatable("gui.space_fabric.journey_route",from.label(),to.label()),w/2,12,0xFFE4F3FF);
            gui.centeredText(client.font,Component.translatable("gui.space_fabric.journey_"+phase),w/2,25,0xFF72E0C9);
            if(rocket.isCruising()) {
                int width=Math.min(180,w-24),left=(w-width)/2;
                gui.fill(left,40,left+width,44,0xFF253747);
                gui.fill(left,40,left+(int)(width*RocketJourney.progress(elapsed,rocket.journeyDuration())),44,0xFF72E0C9);
                int seconds=Math.max(0,(int)Math.ceil((rocket.journeyDuration()-elapsed)/20));
                if(!rocket.isFindingLanding())gui.centeredText(client.font,Component.translatable("gui.space_fabric.journey_eta",seconds),w/2,49,0xFFE4F3FF);
            }
            gui.centeredText(client.font,Component.translatable("gui.space_fabric.journey_harness"),w/2,h-45,0xFFBDCADA);
        });
    }
}

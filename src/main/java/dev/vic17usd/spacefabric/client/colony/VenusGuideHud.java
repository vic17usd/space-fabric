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

/** Compact quest tracker: mission heading, fine rule and a cyan objective ring on a clear background. */
public final class VenusGuideHud {
    private static final VenusMissionTracker STATE=new VenusMissionTracker();
    private static int bottom,researchStage=-1,researchAge;
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
        ClientPlayNetworking.registerGlobalReceiver(dev.vic17usd.spacefabric.research.ResearchNetwork.State.TYPE,(payload,context)->{researchStage=payload.stage();researchAge=0;});
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{STATE.clear();bottom=0;researchStage=-1;});
        ClientTickEvents.END_CLIENT_TICK.register(c->{
            if(c.level==null||c.player==null||!c.player.isAlive()||PlanetDimensions.find(c.level).orElse(null)!=Planet.VENUS){STATE.clear();bottom=0;researchStage=-1;}
            else if(!c.isPaused()){STATE.tick();if(researchAge<=60)researchAge++;}
        });
        HudElementRegistry.addLast(SpaceFabric.id("venus_missions"),(gui,delta)->draw(gui));
    }
    private static void ring(GuiGraphicsExtractor gui,int x,int y,boolean complete){
        // A hollow, nine-pixel circle; the centre remains transparent.
        for(int row=0;row<9;row++)for(int col=0;col<9;col++){
            int distance=(col-4)*(col-4)+(row-4)*(row-4);
            if(distance>=10&&distance<=20)gui.fill(x+col,y+row,x+col+1,y+row+1,complete?0xFF8DE9BE:0xFF65D9ED);
        }
        if(complete)gui.fill(x+3,y+3,x+6,y+6,0xFF8DE9BE);
    }
    private static void draw(GuiGraphicsExtractor gui){
        var c=Minecraft.getInstance();bottom=0;
        if(!visible()||c.options.hideGui||c.screen!=null)return;
        var remaining=STATE.remaining();
        boolean complete=remaining.isEmpty();
        boolean research=complete&&researchStage>=0&&researchAge<=60;
        String step=research?"research_"+researchStage:complete?"complete":remaining.getFirst().key();
        if(research)complete=researchStage==4;
        int width=Math.min(214,c.getWindow().getGuiScaledWidth()-16);
        var title=Component.translatable("venus.guide.tracker_title_"+step);
        gui.text(c.font,c.font.split(title,width).getFirst(),8,8,0xFFF2F3F4);
        gui.horizontalLine(8,8+Math.min(width,Math.max(100,c.font.width(title))),20,0x807F919B);
        ring(gui,8,25,complete);
        // The smaller objective text matches the reference while preserving Minecraft's GUI scaling.
        float scale=.85F;
        var pose=gui.pose();pose.pushMatrix();
        try {
            pose.translate(22,26);pose.scale(scale,scale);
            var objective=Component.translatable("venus.guide.tracker_objective_"+step);
            int row=0;
            for(var line:c.font.split(objective,(int)((width-14)/scale)).stream().limit(2).toList()){
                gui.text(c.font,line,0,row,0xFFE0E7EB);row+=c.font.lineHeight+2;
            }
            bottom=26+(int)Math.ceil(row*scale)+3;
        } finally {
            pose.popMatrix();
        }
    }
}

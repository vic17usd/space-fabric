package dev.vic17usd.spacefabric.client.map;

import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.map.AtlasData;
import java.io.IOException;
import java.util.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.*;
import net.minecraft.network.chat.Component;

/** Explored surface only. Right-click creates a personal marker, never a teleport. */
public final class AtlasScreen extends Screen {
    private final AtlasCanvas canvas=new AtlasCanvas("atlas_large",256);
    private String dimension;
    private AtlasData data;
    private double centerX,centerZ;
    private int zoom=2,page,side,left,top,rows,panel;
    private boolean dragging;
    private static final int[] ZOOM={1,2,4,8,16};
    public AtlasScreen(){super(AtlasClient.text("title"));dimension=AtlasClient.dimension();var p=net.minecraft.client.Minecraft.getInstance().player;centerX=p.getX();centerZ=p.getZ();}
    private Button button(Component label,int x,int y,int width,Runnable action){return addRenderableWidget(Button.builder(label,b->action.run()).bounds(x,y,width,18).build());}
    @Override protected void init(){
        try{data=AtlasClient.map(dimension);}catch(IOException failure){AtlasClient.error(failure);onClose();return;}
        side=Math.max(64,Math.min(384,Math.min(width-180,height-96)));left=Math.max(8,(width-172-side)/2);top=32;
        panel=width-160;rows=Math.max(1,(height-174)/20);
        button(AtlasClient.label(dimension),panel,28,152,this::planet);
        button(AtlasClient.text("new"),panel,50,72,()->{
            var p=minecraft.player;minecraft.setScreen(new WaypointScreen(this,data,null,p.getBlockX(),p.getBlockY(),p.getBlockZ()));
        }).active=dimension.equals(AtlasClient.dimension());
        button(AtlasClient.text("import"),panel+76,50,76,this::importNavigator);
        var points=data.waypoints();page=Math.clamp(page,0,Math.max(0,(points.size()-1)/rows));
        for(int row=0;row<rows&&page*rows+row<points.size();row++) {
            var point=points.get(page*rows+row);boolean selected=data.selected().map(p->p.id().equals(point.id())).orElse(false);
            var label=Component.literal((selected?"> ":"")+font.plainSubstrByWidth(point.name(),130)).withStyle(s->s.withColor(point.visible()?point.color():0x7E8C99));
            button(label,panel,78+row*20,152,()->{
                try{data.select(point.id());centerX=point.x();centerZ=point.z();rebuildWidgets();}catch(IOException failure){AtlasClient.error(failure);}
            });
        }
        button(Component.literal("<"),panel,height-92,36,()->{page--;rebuildWidgets();}).active=page>0;
        button(Component.literal(">"),panel+116,height-92,36,()->{page++;rebuildWidgets();}).active=(page+1)*rows<points.size();
        button(AtlasClient.text("edit"),panel,height-68,152,()->data.selected().ifPresent(p->minecraft.setScreen(new WaypointScreen(this,data,p,p.x(),p.y(),p.z())))).active=data.selected().isPresent();
        button(AtlasClient.text("close"),panel,height-26,152,this::onClose);
        button(Component.literal("+"),left,height-26,24,()->zoom(-1,left+side/2.,top+side/2.));
        button(Component.literal("−"),left+28,height-26,24,()->zoom(1,left+side/2.,top+side/2.));
        button(AtlasClient.text("center"),left+56,height-26,Math.min(100,side-56),this::center);
    }
    private void center(){if(dimension.equals(AtlasClient.dimension())){centerX=minecraft.player.getX();centerZ=minecraft.player.getZ();}else{centerX=data.lastX();centerZ=data.lastZ();}}
    private void planet(){
        try {
            var visited=AtlasClient.visited();if(visited.isEmpty())return;
            String next=visited.get((visited.indexOf(dimension)+1)%visited.size());
            var nextData=AtlasClient.map(next); // Commit the view only after its own files load successfully.
            dimension=next;data=nextData;page=0;center();rebuildWidgets();
        }
        catch(IOException failure){AtlasClient.error(failure);}
    }
    private void importNavigator(){
        try {
            int count=0;
            var inventory=new ArrayList<>(minecraft.player.getInventory().getNonEquipmentItems());inventory.add(minecraft.player.getOffhandItem());
            for(var stack:inventory)if(stack.is(ExpansionContent.NAVIGATOR)) {
                for(var p:OperationsNavigator.points(stack))if(p.dimension().equals(dimension)&&data.waypoints().size()<AtlasData.MAX_WAYPOINTS
                        &&data.waypoints().stream().noneMatch(w->w.x()==p.pos().getX()&&w.y()==p.pos().getY()&&w.z()==p.pos().getZ())) {
                    try{data.put(new AtlasData.Waypoint(UUID.randomUUID(),p.label(),p.pos().getX(),p.pos().getY(),p.pos().getZ(),0x65D9ED,true));count++;}
                    catch(IllegalArgumentException invalidPoint){/* Ignore invalid legacy points; preserve all valid locations. */}
                }
            }
            minecraft.player.sendSystemMessage(AtlasClient.text("imported",count));rebuildWidgets();
        }catch(IOException failure){AtlasClient.error(failure);}
    }
    private boolean inside(double x,double y){return x>=left&&x<left+side&&y>=top&&y<top+side;}
    private static double bound(double coordinate){return Math.clamp(coordinate,-29_999_984,29_999_984);}
    private void zoom(int delta,double x,double y){int next=Math.clamp(zoom+delta,0,ZOOM.length-1);double difference=ZOOM[zoom]-ZOOM[next];centerX=bound(centerX+(x-left-side/2.)*difference);centerZ=bound(centerZ+(y-top-side/2.)*difference);zoom=next;}
    @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(inside(x,y)){zoom(vertical>0?-1:1,x,y);return true;}return super.mouseScrolled(x,y,horizontal,vertical);}
    @Override public boolean mouseClicked(MouseButtonEvent event,boolean twice){
        if(super.mouseClicked(event,twice))return true;
        if(inside(event.x(),event.y())) {
            if(event.button()==1){int x=(int)bound(Math.floor(centerX+(event.x()-left-side/2.)*ZOOM[zoom])),z=(int)bound(Math.floor(centerZ+(event.y()-top-side/2.)*ZOOM[zoom]));minecraft.setScreen(new WaypointScreen(this,data,null,x,data.height(x,z,data.lastY()),z));}
            else if(event.button()==0)dragging=true;
            return true;
        }
        return false;
    }
    @Override public boolean mouseDragged(MouseButtonEvent event,double dx,double dy){if(dragging&&event.button()==0){centerX=bound(centerX-dx*ZOOM[zoom]);centerZ=bound(centerZ-dy*ZOOM[zoom]);return true;}return super.mouseDragged(event,dx,dy);}
    @Override public boolean mouseReleased(MouseButtonEvent event){dragging=false;return super.mouseReleased(event);}
    @Override public boolean keyPressed(KeyEvent event){if(AtlasClient.mapKey(event)){onClose();return true;}return super.keyPressed(event);}
    @Override public boolean isPauseScreen(){return false;}
    @Override public void removed(){canvas.close();}
    @Override public void extractRenderState(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta){
        gui.fill(0,0,width,height,0xE6121D29);gui.text(font,title,8,8,0xFFE6F5FA);
        if(data==null)return;
        canvas.draw(gui,data,left,top,side,centerX,centerZ,ZOOM[zoom],true);
        AtlasClient.report(data);
        gui.text(font,AtlasClient.text("markers",data.waypoints().size()),panel,12,0xFFB9D5DF);
        gui.centeredText(font,(page+1)+" / "+Math.max(1,(data.waypoints().size()+rows-1)/rows),panel+76,height-86,0xFFAAC3D0);
        String coordinates=(int)centerX+", "+(int)centerZ+" · "+ZOOM[zoom]+" b/px";
        if(inside(mouseX,mouseY))coordinates=(int)(centerX+(mouseX-left-side/2.)*ZOOM[zoom])+", "+(int)(centerZ+(mouseY-top-side/2.)*ZOOM[zoom]);
        gui.text(font,coordinates,left,top+side+6,0xFFD3E6EE);
        gui.text(font,font.plainSubstrByWidth(AtlasClient.text("controls").getString(),side),left,top+side+18,0xFF9CB7C5);
        data.selected().ifPresent(p->{
            String info=p.x()+", "+p.y()+", "+p.z();
            if(dimension.equals(AtlasClient.dimension()))info+=" · "+(int)Math.sqrt(minecraft.player.distanceToSqr(p.x(),p.y(),p.z()))+" m";
            gui.text(font,font.plainSubstrByWidth(info,152),panel,height-44,0xFFA8DCD0);
        });
        super.extractRenderState(gui,mouseX,mouseY,delta);
    }
}

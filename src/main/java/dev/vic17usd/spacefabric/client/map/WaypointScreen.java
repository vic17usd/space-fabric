package dev.vic17usd.spacefabric.client.map;

import dev.vic17usd.spacefabric.map.AtlasData;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Editing never moves the player or reveals terrain. Cancel leaves the atlas unchanged. */
final class WaypointScreen extends Screen {
    private static final int[] COLORS={0x65D9ED,0xF5BE62,0x8DE9BE,0xEC858F,0xC8A2F2,0xF2F4F5};
    private final Screen previous;private final AtlasData data;private final AtlasData.Waypoint original;
    private final UUID id;private final int px,py,pz;
    private int color;private boolean visible;
    private EditBox name,x,y,z;private Button save;private Component error;
    WaypointScreen(Screen previous,AtlasData data,AtlasData.Waypoint original,int x,int y,int z){
        super(AtlasClient.text(original==null?"new_marker":"edit_marker"));this.previous=previous;this.data=data;this.original=original;
        id=original==null?UUID.randomUUID():original.id();px=x;py=y;pz=z;color=original==null?COLORS[0]:original.color();visible=original==null||original.visible();
    }
    private EditBox field(int x,int y,int width,String label,String value,int length){var box=addRenderableWidget(new EditBox(font,x,y,width,18,AtlasClient.text(label)));box.setMaxLength(length);box.setValue(value);box.setResponder(v->validate());return box;}
    @Override protected void init(){
        int left=width/2-120,top=height/2-95;
        String n=name==null?(original==null?AtlasClient.text("default_name").getString():original.name()):name.getValue();
        String sx=x==null?Integer.toString(px):x.getValue(),sy=y==null?Integer.toString(py):y.getValue(),sz=z==null?Integer.toString(pz):z.getValue();
        name=field(left,top+27,240,"name",n,48);
        x=field(left,top+66,76,"x",sx,12);y=field(left+82,top+66,76,"y",sy,12);z=field(left+164,top+66,76,"z",sz,12);
        addRenderableWidget(Button.builder(AtlasClient.text("color").copy().append(Component.literal(" ■").withStyle(s->s.withColor(color))),b->{int i=0;while(i<COLORS.length&&COLORS[i]!=color)i++;color=COLORS[(i+1)%COLORS.length];b.setMessage(AtlasClient.text("color").copy().append(Component.literal(" ■").withStyle(s->s.withColor(color))));}).bounds(left,top+96,116,18).build());
        addRenderableWidget(Button.builder(AtlasClient.text(visible?"visible":"hidden"),b->{visible=!visible;b.setMessage(AtlasClient.text(visible?"visible":"hidden"));}).bounds(left+124,top+96,116,18).build());
        save=addRenderableWidget(Button.builder(AtlasClient.text("save"),b->{try{data.put(point());onClose();}catch(IOException failure){error=AtlasClient.text("save_failed");AtlasClient.error(failure);}}).bounds(left,top+128,76,20).build());
        addRenderableWidget(Button.builder(AtlasClient.text("delete"),b->{try{data.delete(id);onClose();}catch(IOException failure){error=AtlasClient.text("save_failed");AtlasClient.error(failure);}}).bounds(left+82,top+128,76,20).build()).active=original!=null;
        addRenderableWidget(Button.builder(AtlasClient.text("cancel"),b->onClose()).bounds(left+164,top+128,76,20).build());
        validate();setInitialFocus(name);
    }
    private AtlasData.Waypoint point(){return new AtlasData.Waypoint(id,name.getValue(),Integer.parseInt(x.getValue()),Integer.parseInt(y.getValue()),Integer.parseInt(z.getValue()),color,visible);}
    private void validate(){
        if(save==null||name==null||x==null||y==null||z==null)return;
        try{point();save.active=original!=null||data.waypoints().size()<AtlasData.MAX_WAYPOINTS;error=save.active?null:AtlasClient.text("limit");}
        catch(IllegalArgumentException invalid){save.active=false;error=AtlasClient.text("invalid");}
    }
    @Override public boolean isPauseScreen(){return false;}
    @Override public void onClose(){minecraft.setScreen(previous);}
    @Override public void extractRenderState(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta){
        int left=width/2-120,top=height/2-95;
        gui.fill(left-12,top-10,left+252,top+183,0xF0152433);gui.text(font,title,left,top,0xFFE6F5FA);
        gui.text(font,AtlasClient.text("name"),left,top+15,0xFFAAC6D4);
        gui.text(font,"X",left,top+54,0xFFAAC6D4);gui.text(font,"Y",left+82,top+54,0xFFAAC6D4);gui.text(font,"Z",left+164,top+54,0xFFAAC6D4);
        if(error!=null)gui.text(font,error,left,top+158,0xFFFF9999);
        super.extractRenderState(gui,mouseX,mouseY,delta);
    }
}

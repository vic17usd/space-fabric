package dev.vic17usd.spacefabric.client.map;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.map.AtlasData;
import dev.vic17usd.spacefabric.map.AtlasStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

/** Two bounded textures, updated at most once per client tick. Unexplored cells remain dark. */
final class AtlasCanvas {
    private final Identifier id;private final int resolution;
    private DynamicTexture texture;private AtlasData previous;
    private long painted=-100,revision;
    private double centerX=Double.NaN,centerZ,scale;
    private int size;
    AtlasCanvas(String name,int resolution){id=SpaceFabric.id(name);this.resolution=resolution;}
    void close(){if(texture!=null){Minecraft.getInstance().getTextureManager().release(id);texture=null;}previous=null;painted=-100;}
    void draw(GuiGraphicsExtractor gui,AtlasData data,int left,int top,int size,double x,double z,double scale,boolean labels){
        var c=Minecraft.getInstance();
        if(texture==null){texture=new DynamicTexture(id.toString(),resolution,resolution,false);c.getTextureManager().register(id,texture);}
        long now=AtlasClient.ticks();
        boolean changed=previous!=data||Math.abs(centerX-x)>=scale/2||Math.abs(centerZ-z)>=scale/2||this.scale!=scale||this.size!=size||revision!=data.revision();
        if(previous!=data||painted!=now&&(changed||data.deferredReads()||now-painted>=5)) {
            data.beginView();
            var pixels=texture.getPixels();double span=size*scale;
            for(int py=0;py<resolution;py++)for(int px=0;px<resolution;px++) {
                int wx=(int)Math.floor(x-span/2+(px+.5)*span/resolution),wz=(int)Math.floor(z-span/2+(py+.5)*span/resolution);
                int color=data.color(wx,wz);if(color==0)color=0xFF111C28;
                pixels.setPixel(px,py,labels?color:AtlasStyle.roundPixel(px,py,resolution,color));
            }
            texture.upload();previous=data;centerX=x;centerZ=z;this.scale=scale;this.size=size;revision=data.revision();painted=now;
        }
        gui.blit(RenderPipelines.GUI_TEXTURED,id,left,top,0,0,size,size,resolution,resolution,resolution,resolution);
        if(labels)gui.outline(left-1,top-1,size+2,size+2,0xFFAA915A);
        gui.enableScissor(left,top,left+size,top+size);
        var target=data.selected().map(AtlasData.Waypoint::id).orElse(null);
        for(var p:data.waypoints())if(p.visible()) {
            int px=left+size/2+(int)Math.round((p.x()-x)/scale),py=top+size/2+(int)Math.round((p.z()-z)/scale);
            boolean selected=p.id().equals(target);
            if(!labels){
                var edge=AtlasStyle.edge(px-left-size/2.,py-top-size/2.,size/2.-14);
                if(!selected&&!AtlasStyle.inside(px-left-size/2.,py-top-size/2.,size/2.-14))continue;
                px=left+size/2+(int)Math.round(edge.x());py=top+size/2+(int)Math.round(edge.y());
            }
            if(px<left||px>=left+size||py<top||py>=top+size)continue;
            symbol(gui,px,py,selected?0xFFFFD968:p.color()|0xFF000000,selected,false,0,left,top,size,!labels);
            if(labels)gui.text(c.font,p.name(),px+8,py-3,selected?0xFFFFDF8C:0xFFFFFFFF);
        }
        if(c.player!=null&&data.dimension().equals(AtlasClient.dimension())) {
            int px=left+size/2+(int)Math.round((c.player.getX()-x)/scale),py=top+size/2+(int)Math.round((c.player.getZ()-z)/scale);
            symbol(gui,px,py,0xFFEAF7FF,false,true,Math.toRadians(c.player.getYRot()),left,top,size,!labels);
        }
        gui.disableScissor();
        if(labels)gui.text(c.font,"N",left+size/2-3,top+2,0xFFFFDF8C);
        else {
            // External compass ornaments leave the terrain and the world outside the circle visible.
            chevron(gui,left+size/2,top-2,0);
            chevron(gui,left+size/2,top+size+2,2);
            chevron(gui,left-2,top+size/2,3);
            chevron(gui,left+size+2,top+size/2,1);
            gui.text(c.font,"N",left+size/2-3,top+7,0xFFFFE3A0);
        }
    }
    private static boolean shape(int x,int y,boolean selected,boolean player,double yaw){
        return player?AtlasStyle.player(x,y,yaw):AtlasStyle.marker(x,y,selected);
    }
    private static void symbol(GuiGraphicsExtractor gui,int px,int py,int color,boolean selected,boolean player,double yaw,int left,int top,int size,boolean round){
        // Scanlines keep draw calls bounded. A dark halo separates symbols from pale ice or lava.
        for(int pass=0;pass<2;pass++)for(int y=-8;y<=8;y++) {
            int start=-99;
            for(int x=-9;x<=9;x++){
                boolean on=shape(x,y,selected,player,yaw);
                if(pass==0)on|=shape(x-1,y,selected,player,yaw)||shape(x+1,y,selected,player,yaw)||shape(x,y-1,selected,player,yaw)||shape(x,y+1,selected,player,yaw);
                on&=!round||AtlasStyle.inside(px+x-left-size/2.,py+y-top-size/2.,size/2.-7);
                if(on&&start==-99)start=x;
                if((!on||x==9)&&start!=-99){int end=on?x+1:x;gui.fill(px+start,py+y,px+end,py+y+1,pass==0?0xFF172026:color);start=-99;}
            }
        }
    }
    private static void chevron(GuiGraphicsExtractor gui,int cx,int cy,int direction){
        for(int x=-5;x<=5;x++)for(int stroke=0;stroke<2;stroke++){
            int y=Math.abs(x)/2+stroke,dx=x,dy=y;
            if(direction==1){dx=-y;dy=x;}else if(direction==2){dx=x;dy=-y;}else if(direction==3){dx=y;dy=x;}
            gui.fill(cx+dx,cy+dy,cx+dx+1,cy+dy+1,stroke==0?0xFFE3C46B:0xFF6D5735);
        }
    }

}

package dev.vic17usd.spacefabric.client.map;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.map.AtlasData;
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
                int color=data.color(wx,wz);pixels.setPixel(px,py,color==0?0xFF111C28:color);
            }
            texture.upload();previous=data;centerX=x;centerZ=z;this.scale=scale;this.size=size;revision=data.revision();painted=now;
        }
        gui.blit(RenderPipelines.GUI_TEXTURED,id,left,top,0,0,size,size,resolution,resolution,resolution,resolution);
        gui.outline(left-1,top-1,size+2,size+2,0xFF7595A7);
        gui.enableScissor(left,top,left+size,top+size);
        var target=data.selected().map(AtlasData.Waypoint::id).orElse(null);
        for(var p:data.waypoints())if(p.visible()) {
            int px=left+size/2+(int)Math.round((p.x()-x)/scale),py=top+size/2+(int)Math.round((p.z()-z)/scale);
            boolean selected=p.id().equals(target);
            if(!labels&&selected){px=Math.clamp(px,left+3,left+size-4);py=Math.clamp(py,top+3,top+size-17);}
            if(px<left||px>=left+size||py<top||py>=top+size)continue;
            if(selected)gui.outline(px-3,py-3,7,7,0xFFFFFFFF);
            gui.fill(px-2,py-2,px+3,py+3,p.color()|0xFF000000);
            if(labels&&px>=left&&px<left+size&&py>=top&&py<top+size)gui.text(c.font,p.name(),px+5,py-3,0xFFFFFFFF);
        }
        if(c.player!=null&&data.dimension().equals(AtlasClient.dimension())) {
            int px=left+size/2+(int)Math.round((c.player.getX()-x)/scale),py=top+size/2+(int)Math.round((c.player.getZ()-z)/scale);
            gui.fill(px-2,py-2,px+3,py+3,0xFFFFFFFF);
            double yaw=Math.toRadians(c.player.getYRot());
            for(int i=3;i<=7;i++){int dx=(int)Math.round(-Math.sin(yaw)*i),dz=(int)Math.round(Math.cos(yaw)*i);gui.fill(px+dx,py+dz,px+dx+1,py+dz+1,0xFFFFD66A);}
        }
        gui.disableScissor();
        gui.text(c.font,"N",left+size/2-3,top+2,0xFFFFFFFF);
    }
}

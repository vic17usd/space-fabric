package dev.vic17usd.spacefabric.client.map;

import dev.vic17usd.spacefabric.map.AtlasStyle;
import java.util.*;
import org.joml.Matrix3x2f;

/** Immutable silhouette: rotation transforms its quads without rerasterizing the arrow. */
final class PlayerMarkerGeometry {
    record Span(int pass,int x0,int x1,int y) { }
    static final List<Span> SPANS=build();
    private PlayerMarkerGeometry() { }
    static Matrix3x2f pose(int x,int y,double yaw){return new Matrix3x2f().translate(x,y).rotate((float)yaw).scale(.8F);}
    private static boolean arrow(int x,int y){return AtlasStyle.player(x,y,0);}
    private static List<Span> build(){
        var result=new ArrayList<Span>();
        for(int pass=0;pass<2;pass++)for(int y=-8;y<=8;y++){
            int start=-99;
            for(int x=-9;x<=9;x++){
                boolean on=arrow(x,y);
                if(pass==0)on|=arrow(x-1,y)||arrow(x+1,y)||arrow(x,y-1)||arrow(x,y+1);
                if(on&&start==-99)start=x;
                if((!on||x==9)&&start!=-99){result.add(new Span(pass,start,on?x+1:x,y));start=-99;}
            }
        }
        return List.copyOf(result);
    }
}

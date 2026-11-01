package dev.vic17usd.spacefabric.client.map;

import dev.vic17usd.spacefabric.map.AtlasStyle;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerMarkerGeometryTest {
    @Test void actualMarkerSpansHaveNoHolesAndTheirHaloCoversTheWholeArrow(){
        for(int y=-8;y<=8;y++)for(int x=-9;x<=9;x++){
            int px=x,py=y;
            long fill=PlayerMarkerGeometry.SPANS.stream().filter(s->s.pass()==1&&s.y()==py&&s.x0()<=px&&s.x1()>px).count();
            long halo=PlayerMarkerGeometry.SPANS.stream().filter(s->s.pass()==0&&s.y()==py&&s.x0()<=px&&s.x1()>px).count();
            assertEquals(AtlasStyle.player(x,y,0)?1:0,fill);assertTrue(halo<=1);assertTrue(halo>=fill);
        }
    }
    @Test void everyYawPreservesTheActualQuadsDimensionsAndMinecraftHeading(){
        for(int degrees=-720;degrees<=720;degrees++){
            var pose=PlayerMarkerGeometry.pose(38,38,Math.toRadians(degrees));
            for(var span:PlayerMarkerGeometry.SPANS){
                var a=pose.transformPosition(span.x0(),span.y(),new Vector2f());
                var b=pose.transformPosition(span.x1(),span.y(),new Vector2f());
                var c=pose.transformPosition(span.x0(),span.y()+1,new Vector2f());
                assertEquals((span.x1()-span.x0())*.8,a.distance(b),.00002);
                assertEquals(.8,a.distance(c),.00002);
                assertEquals(0,new Vector2f(b).sub(a).dot(new Vector2f(c).sub(a)),.0001);
            }
        }
        assertTrue(PlayerMarkerGeometry.pose(0,0,0).transformPosition(0,7,new Vector2f()).y>0);
        assertTrue(PlayerMarkerGeometry.pose(0,0,Math.PI/2).transformPosition(0,7,new Vector2f()).x<0);
    }
}

package dev.vic17usd.spacefabric.map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AtlasStyleTest {
    @Test void mapCornersAreTransparentAndCenterKeepsTerrainColor() {
        int size=104,color=0xFFBA6230;
        for(int x:new int[]{0,103})for(int y:new int[]{0,103})assertEquals(0,AtlasStyle.roundPixel(x,y,size,color));
        assertEquals(color,AtlasStyle.roundPixel(52,52,size,color));
        int opaque=0;
        for(int y=0;y<size;y++)for(int x=0;x<size;x++){
            int pixel=AtlasStyle.roundPixel(x,y,size,color);
            if(pixel!=0){opaque++;assertTrue(AtlasStyle.inside(x+.5-52,y+.5-52,51));assertEquals(255,pixel>>>24);}
        }
        assertTrue(opaque>8000&&opaque<8400);
        assertNotEquals(color,AtlasStyle.roundPixel(52,2,size,color));
    }
    @Test void distantDestinationPreservesBearingAtCircularEdge() {
        for(int x:new int[]{-1000,0,1000})for(int y:new int[]{-1000,0,1000}){
            var p=AtlasStyle.edge(x,y,38);
            assertTrue(AtlasStyle.inside(p.x(),p.y(),38.00001));
            assertEquals(0,x*p.y()-y*p.x(),.00001);
            assertTrue(x*p.x()+y*p.y()>=0);
            if(x!=0||y!=0)assertEquals(38,Math.hypot(p.x(),p.y()),.00001);
        }
        assertEquals(new AtlasStyle.Point(2,3),AtlasStyle.edge(2,3,38));
    }
    @Test void arrowUsesMinecraftYawAndSymbolsHaveBoundedFootprints() {
        assertTrue(AtlasStyle.player(0,5,0));assertFalse(AtlasStyle.player(0,-5,0));
        assertTrue(AtlasStyle.player(-5,0,Math.PI/2));assertFalse(AtlasStyle.player(5,0,Math.PI/2));
        assertTrue(AtlasStyle.player(0,-5,Math.PI));
        assertTrue(AtlasStyle.marker(0,-4,true));assertFalse(AtlasStyle.marker(0,-4,false));
        assertTrue(AtlasStyle.marker(0,0,false));
        for(int x=-12;x<=12;x++)for(int y=-12;y<=12;y++)if(Math.abs(x)>7||Math.abs(y)>7){
            assertFalse(AtlasStyle.marker(x,y,true));assertFalse(AtlasStyle.marker(x,y,false));
            assertFalse(AtlasStyle.player(x,y,.7));
        }
    }
}

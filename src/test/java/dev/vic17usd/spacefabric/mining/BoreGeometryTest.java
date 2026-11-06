package dev.vic17usd.spacefabric.mining;

import java.util.HashSet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoreGeometryTest {
    @Test void drillVisitsEveryCellExactlyOnceAndStaysWithinFiniteVolume(){check(1,96);}
    @Test void scannerVisitsEveryCellExactlyOnceAndStaysWithinFiniteVolume(){check(4,64);}
    private void check(int radius,int depth){
        var seen=new HashSet<String>();int previous=-1;
        for(int i=0;i<BoreGeometry.count(radius,depth);i++){
            int x=BoreGeometry.dx(i,radius),z=BoreGeometry.dz(i,radius),y=BoreGeometry.down(i,radius);
            assertTrue(Math.abs(x)<=radius&&Math.abs(z)<=radius&&y>=0&&y<depth);assertTrue(y>=previous);previous=y;
            assertTrue(seen.add(x+","+y+","+z));
        }
        assertEquals((2*radius+1)*(2*radius+1)*depth,seen.size());
    }
}

package dev.vic17usd.spacefabric.venus.regions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegionReliefTest {
    @Test void reliefIsBoundedAndHasBothPlateausAndDeepFissures(){
        boolean raised=false,cut=false;
        for(int x=-512;x<=512;x+=3)for(int z=-512;z<=512;z+=7){
            int t=RegionRelief.tessera(x,z);assertTrue(t>=0&&t<=10);
            int r=RegionRelief.rift(x,z,42);assertTrue(r>=-14&&r<=6);raised|=t>=8;cut|=r<=-10;
        }
        assertTrue(raised);assertTrue(cut);
    }
    @Test void tesseraHeightsAreContinuousAcrossChunkEdgesAndNegativeCoordinates(){
        for(int x=-256;x<=256;x++)for(int z:new int[]{-129,-128,-17,-16,-1,0,15,16,127,128})
            assertTrue(Math.abs(RegionRelief.tessera(x,z)-RegionRelief.tessera(x+1,z))<=1);
    }
    @Test void riftLayoutDependsOnSeedAndIsRepeatable(){
        int different=0;
        for(int x=-128;x<128;x+=4)for(int z=-128;z<128;z+=4){
            assertEquals(RegionRelief.rift(x,z,42),RegionRelief.rift(x,z,42));if(RegionRelief.rift(x,z,42)!=RegionRelief.rift(x,z,971))different++;
        }
        assertTrue(different>100);
    }
}

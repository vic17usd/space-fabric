package dev.vic17usd.spacefabric.research;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResearchRulesTest {
    private ResearchSystem.Evidence e(int mission,boolean console,boolean station,boolean collector,boolean altitude,boolean same){return new ResearchSystem.Evidence(mission,console,station,collector,altitude,same);}
    @Test void archivesAndRestorationCannotBeSkippedByVisitingTheWrongSite(){
        assertTrue(ResearchSystem.ready(0,e(2,true,false,false,false,false)));
        assertFalse(ResearchSystem.ready(0,e(1,true,true,true,true,true)));
        assertFalse(ResearchSystem.ready(0,e(2,false,true,true,true,true)));
        assertTrue(ResearchSystem.ready(1,e(1,true,true,false,false,false)));
        assertFalse(ResearchSystem.ready(1,e(1,true,false,true,true,true)));
        assertFalse(ResearchSystem.ready(1,e(2,true,true,true,true,true)));
    }
    @Test void aerialSurveyNeedsTheNaturalCloudStationAltitudeAndCompletedCollector(){
        assertTrue(ResearchSystem.ready(2,e(3,true,false,true,true,false)));
        assertFalse(ResearchSystem.ready(2,e(3,true,false,false,true,false)));
        assertFalse(ResearchSystem.ready(2,e(3,true,false,true,false,false)));
        assertFalse(ResearchSystem.ready(2,e(0,true,true,true,true,true)));
    }
    @Test void calibrationAndManufacturingRequireTheBoundOperatingLaboratory(){
        for(int stage:new int[]{3,4}){
            assertTrue(ResearchSystem.ready(stage,e(1,true,true,false,false,true)));
            assertFalse(ResearchSystem.ready(stage,e(1,true,true,false,false,false)));
            assertFalse(ResearchSystem.ready(stage,e(1,true,false,true,true,true)));
            assertFalse(ResearchSystem.ready(stage,e(1,false,true,true,true,true)));
            assertFalse(ResearchSystem.ready(stage,e(3,true,true,true,true,true)));
        }
        assertFalse(ResearchSystem.ready(5,e(1,true,true,true,true,true)));
    }
}

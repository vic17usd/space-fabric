package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.planet.Planet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RocketVariantTest {
    @Test void everyMissionSupportsItsDestinationAndReturnHome() {
        for(var variant:RocketVariant.values()) {
            assertTrue(variant.protects(variant.mission,240),variant.path);
            assertTrue(variant.permits(variant.mission,false),variant.path);
            assertTrue(variant.permits(Planet.EARTH,false),variant.path);
            assertTrue(variant.permits(Planet.EARTH,true),variant.path);
        }
    }
    @Test void capabilitiesPermitCompatibleCrossPlanetTripsButRejectHostileRoutes() {
        assertTrue(RocketVariant.MERCURY.permits(Planet.MOON,false));
        assertTrue(RocketVariant.JUPITER.permits(Planet.MARS,false));
        assertFalse(RocketVariant.MOON.permits(Planet.MERCURY,false));
        assertTrue(RocketVariant.MARS.permits(Planet.VENUS,false));
        assertFalse(RocketVariant.JUPITER.permits(Planet.NEPTUNE,false));
        assertTrue(RocketVariant.NEPTUNE.permits(Planet.VENUS,false));
        assertFalse(RocketVariant.EARTH.permits(Planet.MARS,false));
        assertFalse(RocketVariant.BASE.permits(Planet.VENUS,false));
        assertTrue(RocketVariant.MERCURY.permits(Planet.MOON,true));
    }
    @Test void everyTierIncludesAllEarlierDestinationsAndRejectsLaterOnes() {
        for(var variant:RocketVariant.values())for(var planet:Planet.values())
            assertEquals(RocketRules.tier(planet)<=variant.rank(),variant.permits(planet,false),variant+":"+planet);
    }
    @Test void noHullSurvivesUnlimitedGasGiantDepth() {
        for(var variant:RocketVariant.values()) for(var planet:new Planet[]{Planet.JUPITER,Planet.SATURN,Planet.URANUS,Planet.NEPTUNE})
            assertFalse(variant.protects(planet,-1000),variant.path+":"+planet);
    }
    @Test void malformedNetworkAndSaveIdsFallBackSafely() {
        assertEquals(RocketVariant.BASE,RocketVariant.byId(-1));
        assertEquals(RocketVariant.BASE,RocketVariant.byId(Integer.MAX_VALUE));
        assertEquals(RocketVariant.BASE,RocketVariant.byName("fake"));
        for(var variant:RocketVariant.values()) assertSame(variant,RocketVariant.byName(variant.path));
    }
}

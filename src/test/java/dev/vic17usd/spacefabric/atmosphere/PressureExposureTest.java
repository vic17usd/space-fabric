package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.equipment.SuitTier;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.space.RocketCabin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class PressureExposureTest {
    @Test void realReferencePressuresUseKpaAndGasGiantsUseTheOneBarLevel() {
        assertEquals(0.636,AtmosphereProfile.of(Planet.MARS).pressureAt(80));
        assertEquals(9200,AtmosphereProfile.of(Planet.VENUS).pressureAt(80));
        assertEquals(101.325,AtmosphereProfile.of(Planet.EARTH).pressureAt(80));
        assertTrue(AtmosphereProfile.of(Planet.MOON).pressureAt(80)<1e-10);
        for(var p:new Planet[]{Planet.JUPITER,Planet.SATURN,Planet.URANUS,Planet.NEPTUNE}) {
            var profile=AtmosphereProfile.of(p);
            assertEquals(100,profile.pressureAt(240));
            assertTrue(profile.minTemperatureAt(160)>profile.minTemperatureAt(240));
            assertTrue(profile.pressureAt(160)>profile.pressureAt(240));
            assertTrue(profile.pressureAt(240)>profile.pressureAt(320));
            assertTrue(Double.isFinite(profile.pressureAt(-1e20)));
            assertFalse(RocketCabin.pressureSafe(p,-1000));
        }
        assertTrue(RocketCabin.pressureSafe(Planet.VENUS,64));
    }
    @ParameterizedTest @EnumSource(Planet.class)
    void barePlayerDiesQuicklyOnEveryNonEarthPlanet(Planet planet) {
        var danger=PressureExposure.danger(planet,240,SuitTier.NONE);
        if(planet==Planet.EARTH){assertTrue(danger.isEmpty());return;}
        assertTrue(danger.isPresent());
        int expected=planet==Planet.VENUS?3:34;
        assertEquals(expected,PressureExposure.lethalTicks(danger.get(),72));
        double dose=0;
        for(int tick=1; tick<=expected; tick++) {
            dose=PressureExposure.advance(dose,danger.get(),72);
            if(tick<expected) assertTrue(dose<1);
        }
        assertEquals(1,dose);
    }
    @Test void wrongPressureAndTemperatureSuitStillFail() {
        for(var p:new Planet[]{Planet.MERCURY,Planet.MOON,Planet.MARS})
            assertTrue(PressureExposure.danger(p,80,SuitTier.VACUUM).isEmpty());
        assertEquals(Exposure.Hazard.CRUSHING_PRESSURE,PressureExposure.danger(Planet.VENUS,80,SuitTier.VACUUM).orElseThrow().hazard());
        assertTrue(PressureExposure.danger(Planet.VENUS,80,SuitTier.VENUS).isEmpty());
        assertEquals(Exposure.Hazard.THERMAL_EXPOSURE,PressureExposure.danger(Planet.NEPTUNE,240,SuitTier.VENUS).orElseThrow().hazard());
        assertTrue(PressureExposure.danger(Planet.NEPTUNE,240,SuitTier.CRYO).isEmpty());
        assertTrue(PressureExposure.danger(Planet.JUPITER,100,SuitTier.ABYSSAL).isEmpty());
        assertEquals(Exposure.Hazard.CRUSHING_PRESSURE,PressureExposure.danger(Planet.JUPITER,0,SuitTier.ABYSSAL).orElseThrow().hazard());
    }
    @Test void changingHazardsDoesNotResetAccumulatedDangerAndTheScaleCanBeConfigured() {
        var mars=PressureExposure.danger(Planet.MARS,80,SuitTier.NONE).orElseThrow();
        var venus=PressureExposure.danger(Planet.VENUS,80,SuitTier.NONE).orElseThrow();
        double dose=0;for(int i=0;i<17;i++)dose=PressureExposure.advance(dose,mars,72);
        assertEquals(0.5,dose,1e-8);
        dose=PressureExposure.advance(dose,venus,72);dose=PressureExposure.advance(dose,venus,72);
        assertEquals(1,dose);
        assertEquals(200,PressureExposure.lethalTicks(mars,12));
    }
}

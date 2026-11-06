package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.atmosphere.*;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.equipment.SuitTier;
import dev.vic17usd.spacefabric.environment.PlanetEnvironment;
import dev.vic17usd.spacefabric.planet.Planet;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MercuryHabitatTest {
    @Test void onlyExistingDestinationsAndMercuryAreSupported() {
        for (var planet : Planet.values()) assertEquals(planet==Planet.EARTH || planet==Planet.MARS || planet==Planet.VENUS || planet==Planet.MERCURY, HabitatSystem.supports(planet));
    }
    @Test void breathablePressureDoesNotInventThermalProtection() {
        assertEquals(Exposure.Hazard.DECOMPRESSION, PressureExposure.danger(Planet.MERCURY,64,SuitTier.NONE).orElseThrow().hazard());
        assertEquals(Exposure.Hazard.THERMAL_EXPOSURE, PressureExposure.mercuryShelterDanger(64,SuitTier.NONE).orElseThrow().hazard());
        assertTrue(PressureExposure.mercuryShelterDanger(64,SuitTier.VACUUM).isEmpty());
        assertEquals(Exposure.Hazard.THERMAL_EXPOSURE, PressureExposure.mercuryShelterDanger(64,SuitTier.CRYO).orElseThrow().hazard());
    }
    @Test void mercuryDaylightUsesNativeSlowClockAndSkyConditions() {
        assertEquals(1.0/175.94,PlanetEnvironment.of(Planet.MERCURY).clockRate(),0.000001);
        assertTrue(ColonyEnvironment.solarAvailable(false,11999,true,false));
        assertFalse(ColonyEnvironment.solarAvailable(false,12000,true,false));
        assertTrue(ColonyEnvironment.solarAvailable(false,24000,true,false));
        assertFalse(ColonyEnvironment.solarAvailable(false,0,false,false));
        assertFalse(ColonyEnvironment.solarAvailable(false,0,true,true));
        assertFalse(ColonyEnvironment.solarAvailable(true,0,true,false));
    }
    @Test void anUnloadedIntakeIsNotReportedAsBlocked() {
        assertEquals(SealScanner.Status.UNLOADED,SealScanner.scan(p->SealScanner.Cell.UNLOADED,BlockPos.ZERO,false).status());
        assertEquals(SealScanner.Status.BLOCKED_VENT,SealScanner.scan(p->SealScanner.Cell.SOLID,BlockPos.ZERO,false).status());
    }
}

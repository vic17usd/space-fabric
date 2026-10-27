package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.planet.Atmosphere;
import dev.vic17usd.spacefabric.planet.Planet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExposureTest {
    @Test
    void earthIsSafeWithoutSuit() {
        assertTrue(Exposure.hazard(Planet.EARTH.atmosphere(), false, false).isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = Planet.class, names = "EARTH", mode = EnumSource.Mode.EXCLUDE)
    void everyOtherPlanetIsHostileWithoutSuit(Planet planet) {
        assertTrue(Exposure.hazard(planet.atmosphere(), false, false).isPresent());
    }

    @ParameterizedTest
    @EnumSource(Planet.class)
    void legacyFullyProtectedFlagSuppressesCoarseHazards(Planet planet) {
        assertTrue(Exposure.hazard(planet.atmosphere(), true, false).isEmpty());
    }

    @ParameterizedTest
    @EnumSource(Planet.class)
    void creativeAndSpectatorAreExempt(Planet planet) {
        assertTrue(Exposure.hazard(planet.atmosphere(), false, true).isEmpty());
    }

    @Test
    void pressureTakesPrecedenceOverHypoxia() {
        assertEquals(Optional.of(Exposure.Hazard.DECOMPRESSION),
                Exposure.hazard(Planet.MARS.atmosphere(), false, false));
        assertEquals(Optional.of(Exposure.Hazard.CRUSHING_PRESSURE),
                Exposure.hazard(Planet.VENUS.atmosphere(), false, false));
    }

    @Test
    void unbreathableAirAtNormalPressureStillHurts() {
        assertEquals(Optional.of(Exposure.Hazard.HYPOXIA), Exposure.hazard(
                new Atmosphere(false, Atmosphere.Pressure.NORMAL), false, false));
    }
}

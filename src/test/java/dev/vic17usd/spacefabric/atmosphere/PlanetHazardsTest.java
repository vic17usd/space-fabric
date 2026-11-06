package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.planet.Planet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanetHazardsTest {
    @ParameterizedTest
    @EnumSource(value = Planet.class, names = {"JUPITER", "SATURN", "URANUS", "NEPTUNE"})
    void gasHasAnExplicitOrbitalBoundary(Planet planet) {
        assertTrue(PlanetHazards.isDenseGas(planet, 159.99));
        assertFalse(PlanetHazards.isDenseGas(planet, 160.0));
        assertFalse(PlanetHazards.isDenseGas(planet, 240.0));
    }

    @ParameterizedTest
    @EnumSource(value = Planet.class, names = {"JUPITER", "SATURN", "URANUS", "NEPTUNE"},
            mode = EnumSource.Mode.EXCLUDE)
    void rockyWorldsDoNotInheritTheGasHazard(Planet planet) {
        assertFalse(PlanetHazards.isDenseGas(planet, -50));
    }

    @Test
    void acidRequiresRainAndExposedUnprotectedPlayer() {
        assertTrue(PlanetHazards.acidExposure(Planet.VENUS, true, false, false));
        assertFalse(PlanetHazards.acidExposure(Planet.VENUS, false, false, false));
        assertFalse(PlanetHazards.acidExposure(Planet.VENUS, true, true, false));
        assertFalse(PlanetHazards.acidExposure(Planet.VENUS, true, false, true));
        assertFalse(PlanetHazards.acidExposure(Planet.MARS, true, false, false));
    }
}

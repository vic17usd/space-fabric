package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.planet.Planet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class EnvironmentRulesTest {
    @ParameterizedTest
    @EnumSource(Planet.class)
    void clocksRepresentSolarDaysAndKeepValidVisualRanges(Planet planet) {
        var profile = PlanetEnvironment.of(planet);
        assertTrue(profile.clockRate() > 0);
        double period = 24000.0 / profile.clockRate();
        assertEquals(24000.0 * profile.solarDayEarthDays(), period, period * 0.000001);
        assertTrue(profile.fogEnd() > profile.fogStart());
        assertTrue(profile.solarLight() > 0 && profile.solarLight() <= 1);
        assertTrue(profile.stars() >= 0 && profile.stars() <= 1);
    }

    @Test
    void dustAndAcidFogInterpolateWithoutOvershooting() {
        assertEquals(112, PlanetEnvironment.stormFogEnd(Planet.MARS, -1));
        assertEquals(12, PlanetEnvironment.stormFogEnd(Planet.MARS, 1));
        assertEquals(62, PlanetEnvironment.stormFogEnd(Planet.MARS, 0.5F));
        assertEquals(8, PlanetEnvironment.stormFogEnd(Planet.VENUS, 2));
    }

    @Test
    void acidProgressesResetsAndKeepsWearCadenceAfterThreeMinutes() {
        assertEquals(0, AcidExposure.nextSeconds(60, false));
        assertEquals(0, AcidExposure.stage(14));
        assertEquals(1, AcidExposure.stage(15));
        assertEquals(2, AcidExposure.stage(45));
        assertEquals(1, AcidExposure.armorDamage(5));
        assertEquals(2, AcidExposure.armorDamage(15));
        assertEquals(3, AcidExposure.armorDamage(45));
        int elapsed = 180;
        int hits = 0;
        for (int i = 0; i < 20; i++) {
            elapsed = AcidExposure.nextSeconds(elapsed, true);
            if (AcidExposure.armorDamage(elapsed) > 0) hits++;
        }
        assertEquals(4, hits);
    }

    @Test
    void stormWindowsAreLongerThanTheWeatherTransition() {
        for (var planet : new Planet[] {Planet.VENUS, Planet.MARS, Planet.EARTH}) {
            var schedule = StormSchedule.of(planet);
            assertTrue(schedule.clearMin() >= 1200 && schedule.clearMax() >= schedule.clearMin());
            assertTrue(schedule.stormMin() >= 1200 && schedule.stormMax() >= schedule.stormMin());
        }
    }
}

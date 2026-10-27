package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.planet.Planet;

/** Un día terrestre equivale a 24 000 ticks. Se modela el día solar, no solo la rotación sidérea. */
public record PlanetEnvironment(double solarDayEarthDays, boolean retrograde, float solarLight,
                               float fogStart, float fogEnd, float stars, boolean opaqueSky) {
    public static PlanetEnvironment of(Planet planet) {
        return switch (planet) {
            case MERCURY -> new PlanetEnvironment(175.94, false, 1.0F, 1024, 4096, 0.8F, false);
            case VENUS -> new PlanetEnvironment(116.75, true, 0.45F, 0, 28, 0, true);
            case EARTH -> new PlanetEnvironment(1.0, false, 1.0F, 0, 256, 0.5F, false);
            case MOON -> new PlanetEnvironment(29.5306, false, 1.0F, 1024, 4096, 0.8F, false);
            case MARS -> new PlanetEnvironment(1.02749, false, 0.8F, 0, 112, 0.08F, false);
            case JUPITER -> new PlanetEnvironment(0.41354, false, 0.35F, 0, 32, 0, true);
            case SATURN -> new PlanetEnvironment(0.444, false, 0.25F, 0, 40, 0, true);
            case URANUS -> new PlanetEnvironment(0.71833, true, 0.16F, 0, 36, 0, true);
            case NEPTUNE -> new PlanetEnvironment(0.67125, false, 0.12F, 0, 28, 0, true);
        };
    }

    public float clockRate() { return (float) (1.0 / solarDayEarthDays); }
    public double realMinutesPerGameDay() { return 20.0 * solarDayEarthDays; }
    public static boolean airless(Planet planet) { return planet == Planet.MOON || planet == Planet.MERCURY; }

    public static float stormFogEnd(Planet planet, float intensity) {
        float amount = Math.clamp(intensity, 0, 1);
        float clear = of(planet).fogEnd;
        float storm = planet == Planet.MARS ? 12 : planet == Planet.VENUS ? 8 : clear;
        return clear + (storm - clear) * amount;
    }
}

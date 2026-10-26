package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.planet.Planet;

/** Intervalos en ticks de simulación; la hora solar no acelera el clima. */
public record StormSchedule(int clearMin, int clearMax, int stormMin, int stormMax) {
    public static StormSchedule of(Planet planet) {
        return switch (planet) {
            case VENUS -> new StormSchedule(1200, 4800, 1200, 3600);
            case MARS -> new StormSchedule(8000, 24000, 2400, 6000);
            default -> new StormSchedule(12000, 168000, 12000, 24000);
        };
    }
}

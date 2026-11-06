package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.planet.Planet;

public final class PlanetHazards {
    public static final int DENSE_GAS_TOP_Y = 160;
    private PlanetHazards() { }

    public static boolean isGasGiant(Planet planet) {
        return switch (planet) {
            case JUPITER, SATURN, URANUS, NEPTUNE -> true;
            default -> false;
        };
    }

    public static boolean isDenseGas(Planet planet, double y) {
        return isGasGiant(planet) && y < DENSE_GAS_TOP_Y;
    }

    public static boolean acidExposure(Planet planet, boolean rainingAtHead, boolean sealed, boolean immune) {
        return planet == Planet.VENUS && rainingAtHead && !sealed && !immune;
    }
}

package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.planet.Planet;

/** Parámetros jugables y operaciones puras compartidas por servidor, GUI y pruebas. */
public final class RocketRules {
    public static final int TANK_CAPACITY = 3000;
    public static final int FUEL_PER_CELL = 1000;
    public static final int IDLE = 0;
    public static final int ASCENDING = 1;
    public static final int TRANSFERRING = 2;
    private RocketRules() { }

    public static int tier(Planet planet) {
        return switch (planet) {
            case EARTH, MOON -> 0;
            case VENUS -> 1;
            case MERCURY, MARS -> 2;
            case JUPITER -> 4;
            case SATURN -> 5;
            case URANUS -> 6;
            case NEPTUNE -> 7;
        };
    }

    public static int cost(TravelDestination from, TravelDestination to) {
        return from.world().equals(to.world()) ? 0 : 200 + Math.abs(tier(from.planet()) - tier(to.planet())) * 100;
    }

    public static boolean canLaunch(boolean pilot, int state, int fuel, int cost, boolean differentWorld) {
        return pilot && state == IDLE && differentWorld && cost > 0 && fuel >= cost;
    }

    public static boolean fitsCell(int fuel) { return fitsCell(fuel,TANK_CAPACITY); }
    public static boolean fitsCell(int fuel,int capacity) { return fuel >= 0 && fuel <= capacity - FUEL_PER_CELL; }
    public static double ascentSpeed(int ticks) { return Math.min(1.2, 0.08 + Math.max(0, ticks) * 0.015); }
}

package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.equipment.SuitTier;
import dev.vic17usd.spacefabric.planet.Planet;
import java.util.Optional;

/** Presiones científicas; límites del traje y tiempos de muerte son reglas explícitas de juego. */
public final class PressureExposure {
    public record Danger(Exposure.Hazard hazard,double referenceSeconds) { }
    private PressureExposure() { }
    public static Optional<Danger> danger(Planet planet,double y,SuitTier suit) {
        if (planet==Planet.EARTH) return Optional.empty();
        var profile=AtmosphereProfile.of(planet);double p=profile.pressureAt(y);
        if (p>suit.maxKpa) return Optional.of(new Danger(Exposure.Hazard.CRUSHING_PRESSURE,10));
        if (suit==SuitTier.NONE && p<6.3) return Optional.of(new Danger(Exposure.Hazard.DECOMPRESSION,120));
        if (!suit.oxygen && profile.oxygenFraction()*p<16) return Optional.of(new Danger(Exposure.Hazard.HYPOXIA,120));
        if (profile.minTemperatureAt(y)<suit.minCelsius || profile.maxTemperatureAt(y)>suit.maxCelsius
                || planet==Planet.VENUS && !suit.acidResistant)
            return Optional.of(new Danger(Exposure.Hazard.THERMAL_EXPOSURE,planet==Planet.VENUS?10:60));
        return Optional.empty();
    }
    public static int lethalTicks(Danger danger,double realSecondsPerGameSecond) {
        return Math.max(1,(int)Math.ceil(danger.referenceSeconds()*20/realSecondsPerGameSecond));
    }
    public static double advance(double dose,Danger danger,double scale) {
        double next=Math.max(0,dose)+1.0/lethalTicks(danger,scale);
        return next>=1-1e-9 ? 1 : next;
    }
}

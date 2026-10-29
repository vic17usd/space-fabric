package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.planet.Planet;

/** Valores de referencia aproximados. En gigantes, 1 bar a Y=240; no existe suelo sólido. */
public record AtmosphereProfile(double referencePressureKpa, double oxygenFraction, double minCelsius,
        double maxCelsius, String composition, double scaleHeightKm,boolean venusLayers) {
    public AtmosphereProfile(double p,double o,double min,double max,String composition,double height){this(p,o,min,max,composition,height,false);}
    public static AtmosphereProfile of(Planet planet) {
        return switch (planet) {
            case EARTH -> new AtmosphereProfile(101.325,0.2095,-89,57,"N₂ 78%; O₂ 20,95%",0);
            case MARS -> new AtmosphereProfile(0.636,0.0013,-125,20,"CO₂ ~95%; N₂/Ar; O₂ ~0,13%",0);
            case VENUS -> new AtmosphereProfile(9200,0,464,464,"CO₂ 96,5%; N₂ 3,5%",0,true);
            case MERCURY -> new AtmosphereProfile(1e-12,0,-180,430,"Exosfera extremadamente tenue",0);
            case MOON -> new AtmosphereProfile(3e-13,0,-173,127,"Exosfera extremadamente tenue",0);
            case JUPITER -> new AtmosphereProfile(100,0,-110,-110,"H₂ ~90%; He ~10%",27);
            case SATURN -> new AtmosphereProfile(100,0,-140,-140,"H₂ ~96%; He ~3%",59.5);
            case URANUS -> new AtmosphereProfile(100,0,-195,-195,"H₂/He; CH₄ ~2%",27.7);
            case NEPTUNE -> new AtmosphereProfile(100,0,-200,-200,"H₂/He; CH₄ ~1–2%",19.1);
        };
    }
    /** Aproximación adiabática de H₂/He bajo el nivel de 1 bar; no es un modelo de núcleo planetario. */
    public double minTemperatureAt(double y) { return venusLayers?VenusLayers.temperature(y):scaleHeightKm==0 ? minCelsius : deepTemperature(y,minCelsius); }
    public double maxTemperatureAt(double y) { return venusLayers?VenusLayers.temperature(y):scaleHeightKm==0 ? maxCelsius : deepTemperature(y,maxCelsius); }
    private double deepTemperature(double y,double referenceC) {
        return (referenceC+273.15)*Math.pow(Math.max(1,pressureAt(y)/referencePressureKpa),2.0/7.0)-273.15;
    }
    public double pressureAt(double y) {
        if(venusLayers)return VenusLayers.pressure(y);
        if (scaleHeightKm==0) return referencePressureKpa;
        // Aproximación barométrica isoterma de jugabilidad: un bloque vertical representa 1 km.
        return referencePressureKpa*Math.exp(Math.clamp((240-y)/scaleHeightKm,-20,20));
    }
}

package dev.vic17usd.spacefabric.space;

import net.minecraft.world.entity.Entity;

/** La cabina cerrada protege al ocupante; desembarcar no convierte el planeta en habitable. */
public final class RocketCabin {
    private RocketCabin() { }
    /** Compatibilidad con 0.16: referencia de 100 bar, no la certificación de una variante.
     * La protección efectiva se consulta mediante protects(Entity). */
    public static boolean pressureSafe(dev.vic17usd.spacefabric.planet.Planet planet,double y) {
        return dev.vic17usd.spacefabric.atmosphere.AtmosphereProfile.of(planet).pressureAt(y)<=10000;
    }
    public static boolean protects(Entity entity) {
        return entity.getVehicle() instanceof RocketEntity rocket && !rocket.isRemoved() && rocket.hull()>0
                && dev.vic17usd.spacefabric.planet.PlanetDimensions.find(rocket.level())
                .map(planet -> rocket.variant().protects(planet,rocket.getY())).orElse(true);
    }
}

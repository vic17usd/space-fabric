package dev.vic17usd.spacefabric.space;

import net.minecraft.world.entity.Entity;

/** La cabina cerrada protege al ocupante; desembarcar no convierte el planeta en habitable. */
public final class RocketCabin {
    private RocketCabin() { }
    /** Límite ficticio de la cabina: 100 bar. Una cabina no elimina la presión a cualquier profundidad. */
    public static boolean pressureSafe(dev.vic17usd.spacefabric.planet.Planet planet,double y) {
        return dev.vic17usd.spacefabric.atmosphere.AtmosphereProfile.of(planet).pressureAt(y)<=10000;
    }
    public static boolean protects(Entity entity) {
        return entity.getVehicle() instanceof RocketEntity rocket && !rocket.isRemoved()
                && dev.vic17usd.spacefabric.planet.PlanetDimensions.find(rocket.level())
                .map(planet -> pressureSafe(planet,rocket.getY())).orElse(true);
    }
}

package dev.vic17usd.spacefabric.space;

import net.minecraft.world.entity.Entity;

/** La cabina cerrada protege al ocupante; desembarcar no convierte el planeta en habitable. */
public final class RocketCabin {
    private RocketCabin() { }
    public static boolean protects(Entity entity) {
        return entity.getVehicle() instanceof RocketEntity rocket && !rocket.isRemoved();
    }
}

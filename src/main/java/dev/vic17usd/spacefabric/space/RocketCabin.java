package dev.vic17usd.spacefabric.space;

import net.minecraft.world.entity.Entity;

/** La cabina cerrada protege al ocupante; desembarcar no convierte el planeta en habitable. */
public final class RocketCabin {
    private static final net.fabricmc.fabric.api.attachment.v1.AttachmentType<Integer> TRANSFER_SEAL=
        net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.createDefaulted(dev.vic17usd.spacefabric.SpaceFabric.id("rocket_transfer_seal"),()->0);
    private RocketCabin() { }
    public static void initialize() { }
    /** Covers the brief native dismount/remount while a dimension transfer is acknowledged. */
    public static void prepareTransfer(Entity passenger) {
        if(passenger instanceof net.minecraft.server.level.ServerPlayer player)
            ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)player).setAttached(TRANSFER_SEAL,player.tickCount+60);
    }
    /** Compatibilidad con 0.16: referencia de 100 bar, no la certificación de una variante.
     * La protección efectiva se consulta mediante protects(Entity). */
    public static boolean pressureSafe(dev.vic17usd.spacefabric.planet.Planet planet,double y) {
        return dev.vic17usd.spacefabric.atmosphere.AtmosphereProfile.of(planet).pressureAt(y)<=10000;
    }
    public static boolean protects(Entity entity) {
        if(entity instanceof net.minecraft.server.level.ServerPlayer player
            &&((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)player).getAttachedOrCreate(TRANSFER_SEAL)>player.tickCount)return true;
        return entity.getVehicle() instanceof RocketEntity rocket && !rocket.isRemoved() && rocket.hull()>0
                && dev.vic17usd.spacefabric.planet.PlanetDimensions.find(rocket.level())
                .map(planet -> rocket.variant().protects(planet,rocket.getY())).orElse(true);
    }
}

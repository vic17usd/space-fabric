package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.equipment.SuitProtection;
import dev.vic17usd.spacefabric.planet.Planet;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

public final class VenusAcidSystem {
    // No persistente, sin copia al morir ni sincronización: pertenece a esta exposición del jugador.
    private static final AttachmentType<Integer> EXPOSURE = AttachmentRegistry.createDefaulted(
            SpaceFabric.id("acid_exposure"), () -> 0);
    private static final EquipmentSlot[] ARMOR = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private VenusAcidSystem() { }

    public static void initialize() {
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, from, to) ->
                ((AttachmentTarget) player).removeAttached(EXPOSURE));
    }

    /** Se invoca una vez cada 20 ticks desde el sistema atmosférico. */
    public static void tick(ServerLevel level, ServerPlayer player, Planet planet) {
        var target = (AttachmentTarget) player;
        boolean exposed = planet == Planet.VENUS && player.isAlive() && !player.isCreative() && !player.isSpectator()
                && !SuitProtection.resistsAcid(player)
                && !dev.vic17usd.spacefabric.space.RocketCabin.protects(player)
                && !dev.vic17usd.spacefabric.colony.HabitatSystem.protects(player)
                && level.isRainingAt(player.blockPosition().above());
        int seconds = AcidExposure.nextSeconds(target.getAttachedOrCreate(EXPOSURE), exposed);
        if (seconds == 0) { target.removeAttached(EXPOSURE); return; }
        target.setAttached(EXPOSURE, seconds);
        int wear = AcidExposure.armorDamage(seconds);
        if(wear>0)dev.vic17usd.spacefabric.equipment.SuitEquipment.environmentalWear(player,wear);
        if (wear > 0) for (var slot : ARMOR) {
            var stack = player.getItemBySlot(slot);
            if (stack.isDamageableItem()) stack.hurtAndBreak(wear, player, slot);
        }
        if (!SuitProtection.resistsAcid(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, AcidExposure.stage(seconds)));
        }
    }
}

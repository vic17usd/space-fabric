package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.equipment.SuitProtection;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import dev.vic17usd.spacefabric.environment.VenusAcidSystem;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Dosis acumulada por tick: cambiar de planeta sin protección no renueva el tiempo disponible. */
public final class AtmosphereSystem {
    private static final AttachmentType<Double> DOSE=AttachmentRegistry.createDefaulted(
            dev.vic17usd.spacefabric.SpaceFabric.id("atmosphere_dose"),()->0.0);
    private AtmosphereSystem() { }
    public static void initialize() {
        AtmosphereConfig.initialize();
        ServerTickEvents.END_LEVEL_TICK.register(level->{
            for(var player:level.players()) {
                var suit=SuitProtection.tier(player);
                if(suit.oxygen || dev.vic17usd.spacefabric.space.RocketCabin.protects(player)) player.setAirSupply(player.getMaxAirSupply());
                var planet=PlanetDimensions.find(level);
                if(planet.isPresent()) tick(level,player,planet.get(),suit);
                else ((AttachmentTarget)player).removeAttached(DOSE);
            }
        });
    }
    private static void tick(ServerLevel level,ServerPlayer player,Planet planet,dev.vic17usd.spacefabric.equipment.SuitTier suit) {
        var data=(AttachmentTarget)player;
        if(level.getGameTime()%20==0)VenusAcidSystem.tick(level,player,planet);
        if(!player.isAlive()||player.isCreative()||player.isSpectator()||dev.vic17usd.spacefabric.space.RocketCabin.protects(player)) {
            data.removeAttached(DOSE);return;
        }
        var danger=PressureExposure.danger(planet,player.getY(),suit);
        if(danger.isEmpty()){data.removeAttached(DOSE);return;}
        double dose=PressureExposure.advance(data.getAttachedOrCreate(DOSE),danger.get(),AtmosphereConfig.scale());
        data.setAttached(DOSE,dose);
        if(player.tickCount%4==0) player.sendOverlayMessage(Component.translatable("message.space_fabric.exposure",
                planet.path(),String.format(java.util.Locale.ROOT,"%.3g",AtmosphereProfile.of(planet).pressureAt(player.getY())),(int)(dose*100)));
        if(dose>=0.083) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,20,1));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20));
        }
        if(dose>=1 || player.tickCount%5==0) {
            float damage=dose>=1 ? Float.MAX_VALUE : player.getMaxHealth()*2.5F/PressureExposure.lethalTicks(danger.get(),AtmosphereConfig.scale());
            player.hurtServer(level,PlanetDamageTypes.source(level,danger.get().hazard()),damage);
        }
    }
}

package dev.vic17usd.spacefabric.environment.volcanism;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.equipment.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.EquipmentSlot;

public final class VolcanicEffects {
    public static final ResourceKey<DamageType> GAS=ResourceKey.create(Registries.DAMAGE_TYPE,SpaceFabric.id("volcanic_gas")),
        EJECTA=ResourceKey.create(Registries.DAMAGE_TYPE,SpaceFabric.id("volcanic_ejecta"));
    private VolcanicEffects() { }
    public static void apply(ServerLevel level,ServerPlayer player,VolcanicRules.Impact impact,boolean eruption){
        if(impact.wear()>0){
            if(SuitEquipment.get(player).tier()!=SuitTier.NONE)SuitEquipment.environmentalWear(player,impact.wear());
            else for(var slot:new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){
                var stack=player.getItemBySlot(slot);if(stack.isDamageableItem())stack.hurtAndBreak(impact.wear(),player,slot);
            }
        }
        if(impact.damage()>0)player.hurtServer(level,new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(eruption?EJECTA:GAS)),impact.damage());
        if(impact.gas())player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,40));
        if(impact.slow())player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,40));
    }
}

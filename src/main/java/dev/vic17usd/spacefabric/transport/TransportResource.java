package dev.vic17usd.spacefabric.transport;

import dev.vic17usd.spacefabric.colony.ColonyContent;
import dev.vic17usd.spacefabric.industry.IndustryContent;
import dev.vic17usd.spacefabric.recovery.RecoveryContent;
import java.util.*;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;

/** Quantities are transported without containers. One canister/bucket is 1,000 units. */
public enum TransportResource implements StringRepresentable {
    NONE(null,0),WATER(TransportChannel.LIQUID,1000),CHILLED_COOLANT(TransportChannel.LIQUID,1000),
    OXYGEN(TransportChannel.GAS,1000),HYDROGEN(TransportChannel.GAS,1000),VENUS_ATMOSPHERE(TransportChannel.GAS,1000),
    CARBON_DIOXIDE(TransportChannel.GAS,1000),NITROGEN(TransportChannel.GAS,1000),POWER(TransportChannel.ENERGY,2400);
    public static final com.mojang.serialization.Codec<TransportResource> CODEC=StringRepresentable.fromEnum(TransportResource::values);
    public final TransportChannel channel;public final int quantum;
    TransportResource(TransportChannel channel,int quantum){this.channel=channel;this.quantum=quantum;}
    @Override public String getSerializedName(){return name().toLowerCase(Locale.ROOT);}
    public String key(){return "transport.space_fabric.resource_"+getSerializedName();}
    public Item fullItem(){return switch(this){
        case NONE->Items.AIR;case WATER->ColonyContent.WATER_CANISTER;case CHILLED_COOLANT->RecoveryContent.CHILLED_COOLANT;
        case OXYGEN->ColonyContent.OXYGEN_CANISTER;case HYDROGEN->ColonyContent.HYDROGEN_CANISTER;
        case VENUS_ATMOSPHERE->IndustryContent.ATMOSPHERE;case CARBON_DIOXIDE->IndustryContent.CARBON_DIOXIDE;
        case NITROGEN->IndustryContent.NITROGEN;case POWER->ColonyContent.CHARGED_POWER_CELL;};}
    public Item emptyItem(){return this==POWER?ColonyContent.EMPTY_POWER_CELL:ColonyContent.EMPTY_CANISTER;}
    public static TransportResource of(ItemStack stack) {
        if(stack.isEmpty())return NONE;
        if(stack.is(Items.WATER_BUCKET))return WATER;
        for(var resource:values())if(resource!=NONE&&stack.is(resource.fullItem()))return resource;
        return NONE;
    }
    public static List<TransportResource> choices(TransportChannel channel){return channel==TransportChannel.ENERGY?List.of(POWER):Arrays.stream(values()).filter(r->r==NONE||r.channel==channel).toList();}
}

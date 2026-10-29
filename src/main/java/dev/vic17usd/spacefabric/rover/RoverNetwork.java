package dev.vic17usd.spacefabric.rover;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** The client requests only its current vehicle's inventory, never positions or resources. */
public final class RoverNetwork {
    public record Open() implements CustomPacketPayload {
        public static final Type<Open> TYPE=new Type<>(SpaceFabric.id("rover_inventory"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Open> CODEC=StreamCodec.unit(new Open());
        @Override public Type<Open> type(){return TYPE;}
    }
    private RoverNetwork() { }
    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(Open.TYPE,Open.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Open.TYPE,(payload,context)->{
            var player=context.player();
            if(player.isAlive()&&!player.isSpectator()&&player.getVehicle() instanceof RoverEntity rover&&!rover.isRemoved()
                    &&player.containerMenu==player.inventoryMenu)player.openMenu(rover);
        });
    }
}

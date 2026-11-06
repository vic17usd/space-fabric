package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Server-issued progress only. Clients cannot complete or reset objectives through this channel. */
public final class VenusGuideNetwork {
    private VenusGuideNetwork() { }
    public record State(int flags) implements CustomPacketPayload {
        public State { if(flags<0||flags>127)throw new IllegalArgumentException("Invalid Venus progress"); }
        public static final Type<State> TYPE=new Type<>(SpaceFabric.id("venus_guide"));
        public static final StreamCodec<RegistryFriendlyByteBuf,State> CODEC=new StreamCodec<>() {
            public State decode(RegistryFriendlyByteBuf b){return new State(b.readVarInt());}
            public void encode(RegistryFriendlyByteBuf b,State state){b.writeVarInt(state.flags());}
        };
        @Override public Type<State> type(){return TYPE;}
    }
    public static void initialize(){PayloadTypeRegistry.clientboundPlay().register(State.TYPE,State.CODEC);}
    public static void send(ServerPlayer player,VenusProgress progress){
        if(!ServerPlayNetworking.canSend(player,State.TYPE))return;
        int flags=0;
        for(var step:VenusProgress.Step.values())if(progress.reached(player.getUUID(),step))flags|=step.bit();
        ServerPlayNetworking.send(player,new State(flags));
    }
}

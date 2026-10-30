package dev.vic17usd.spacefabric.research;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class ResearchNetwork {
    private ResearchNetwork() { }
    public record State(int stage) implements CustomPacketPayload {
        public State{if(stage<0||stage>4)throw new IllegalArgumentException("Invalid research stage");}
        public static final Type<State> TYPE=new Type<>(SpaceFabric.id("venus_research"));
        public static final StreamCodec<RegistryFriendlyByteBuf,State> CODEC=new StreamCodec<>(){public State decode(RegistryFriendlyByteBuf b){return new State(b.readVarInt());}public void encode(RegistryFriendlyByteBuf b,State s){b.writeVarInt(s.stage);}};
        @Override public Type<State> type(){return TYPE;}
    }
    public static void initialize(){PayloadTypeRegistry.clientboundPlay().register(State.TYPE,State.CODEC);}
    public static void send(ServerPlayer player){if(ServerPlayNetworking.canSend(player,State.TYPE))ServerPlayNetworking.send(player,new State(ResearchProgress.get(player.level()).stage(player.getUUID())));}
}

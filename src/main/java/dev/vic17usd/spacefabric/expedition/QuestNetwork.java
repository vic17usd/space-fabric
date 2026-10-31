package dev.vic17usd.spacefabric.expedition;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Display only; clients cannot submit evidence or progression. */
public final class QuestNetwork {
    public record State(int stage) implements CustomPacketPayload {
        public State{if(stage<0||stage>5)throw new IllegalArgumentException("Invalid expedition stage");}
        public static final Type<State> TYPE=new Type<>(SpaceFabric.id("colossus_quest"));
        public static final StreamCodec<RegistryFriendlyByteBuf,State> CODEC=new StreamCodec<>(){
            public State decode(RegistryFriendlyByteBuf b){return new State(b.readVarInt());}
            public void encode(RegistryFriendlyByteBuf b,State s){b.writeVarInt(s.stage);}
        };
        @Override public Type<State> type(){return TYPE;}
    }
    private QuestNetwork(){}
    public static void initialize(){PayloadTypeRegistry.clientboundPlay().register(State.TYPE,State.CODEC);}
    public static void send(ServerPlayer player){if(ServerPlayNetworking.canSend(player,State.TYPE))ServerPlayNetworking.send(player,new State(ColossusProgress.get(player.level()).entry(player.getUUID()).stage()));}
}

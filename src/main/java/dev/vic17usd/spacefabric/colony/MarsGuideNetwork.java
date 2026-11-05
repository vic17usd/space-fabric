package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** No serverbound channel: a client cannot submit mission evidence or claim a reward. */
public final class MarsGuideNetwork {
    private MarsGuideNetwork() { }
    public record State(int flags, Optional<BlockPos> refuge, boolean inside, int oxygen, int energy, int water)
            implements CustomPacketPayload {
        public static final State EMPTY = new State(0, Optional.empty(), false, 0, 0, 0);
        public State {
            if (flags < 0 || flags > 63 || oxygen < 0 || oxygen > LifeSupport.CAPACITY
                    || energy < 0 || energy > LifeSupport.CAPACITY || water < 0 || water > MarsExpeditionChecks.WATER)
                throw new IllegalArgumentException("Invalid Mars expedition state");
        }
        public static final Type<State> TYPE = new Type<>(SpaceFabric.id("mars_guide"));
        public static final StreamCodec<RegistryFriendlyByteBuf, State> CODEC = new StreamCodec<>() {
            public State decode(RegistryFriendlyByteBuf b) {
                return new State(b.readVarInt(), b.readBoolean() ? Optional.of(b.readBlockPos()) : Optional.empty(),
                        b.readBoolean(), b.readVarInt(), b.readVarInt(), b.readVarInt());
            }
            public void encode(RegistryFriendlyByteBuf b, State state) {
                b.writeVarInt(state.flags());
                b.writeBoolean(state.refuge().isPresent());
                state.refuge().ifPresent(b::writeBlockPos);
                b.writeBoolean(state.inside());
                b.writeVarInt(state.oxygen()); b.writeVarInt(state.energy()); b.writeVarInt(state.water());
            }
        };
        @Override public Type<State> type() { return TYPE; }
        public boolean has(int flag) { return (flags & flag) != 0; }
    }
    public static void initialize() { PayloadTypeRegistry.clientboundPlay().register(State.TYPE, State.CODEC); }
    public static void send(ServerPlayer player, State state) {
        if (ServerPlayNetworking.canSend(player, State.TYPE)) ServerPlayNetworking.send(player, state);
    }
}

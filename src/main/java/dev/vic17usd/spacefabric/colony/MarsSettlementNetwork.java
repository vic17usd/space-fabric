package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Read-only server snapshot; commands validate all mission evidence in the world. */
public final class MarsSettlementNetwork {
    private MarsSettlementNetwork() { }
    public record State(boolean open, boolean unlocked, int stage, Optional<BlockPos> site, Optional<BlockPos> point,
                        Optional<BlockPos> analyzer) implements CustomPacketPayload {
        public static final State EMPTY = new State(false, false, 0, Optional.empty(), Optional.empty(), Optional.empty());
        public State { if (stage < 0 || stage > 5 || !unlocked && stage != 0) throw new IllegalArgumentException("Invalid Mars settlement stage"); }
        public static final Type<State> TYPE = new Type<>(SpaceFabric.id("mars_settlement"));
        public static final StreamCodec<RegistryFriendlyByteBuf, State> CODEC = new StreamCodec<>() {
            private Optional<BlockPos> read(RegistryFriendlyByteBuf b) { return b.readBoolean() ? Optional.of(b.readBlockPos()) : Optional.empty(); }
            private void write(RegistryFriendlyByteBuf b, Optional<BlockPos> p) { b.writeBoolean(p.isPresent()); p.ifPresent(b::writeBlockPos); }
            public State decode(RegistryFriendlyByteBuf b) { return new State(b.readBoolean(), b.readBoolean(), b.readVarInt(), read(b), read(b), read(b)); }
            public void encode(RegistryFriendlyByteBuf b, State s) { b.writeBoolean(s.open); b.writeBoolean(s.unlocked); b.writeVarInt(s.stage); write(b, s.site); write(b, s.point); write(b, s.analyzer); }
        };
        @Override public Type<State> type() { return TYPE; }
    }
    public static void initialize() { PayloadTypeRegistry.clientboundPlay().register(State.TYPE, State.CODEC); }
    public static void send(ServerPlayer player, boolean open) {
        if (!ServerPlayNetworking.canSend(player, State.TYPE)) return;
        var progress = MarsProgress.get(player.level()); var e = progress.settlement(player.getUUID());
        ServerPlayNetworking.send(player, new State(open, progress.entry(player.getUUID()).complete(), e.stage(), e.site(), e.point(), e.analyzer()));
    }
}

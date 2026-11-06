package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.menu.RocketMenu;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** The request contains no entity id: only the server can resolve the sender's actual vehicle. */
public final class RocketCockpitNetwork {
    private RocketCockpitNetwork() { }
    public record Open() implements CustomPacketPayload {
        public static final Type<Open> TYPE = new Type<>(SpaceFabric.id("rocket_cockpit_open"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Open> CODEC = StreamCodec.unit(new Open());
        @Override public Type<Open> type() { return TYPE; }
    }
    public static boolean allowed(boolean alive, boolean spectator, boolean pilot, boolean sameLevel, boolean removed, boolean flying, boolean nearby) {
        return alive && !spectator && pilot && sameLevel && !removed && !flying && nearby;
    }
    private static void open(ServerPlayer player) {
        if (!(player.getVehicle() instanceof RocketEntity rocket)) return;
        if (!allowed(player.isAlive(),player.isSpectator(),rocket.isPilot(player),player.level()==rocket.level(),
                rocket.isRemoved(),rocket.isFlying(),player.distanceToSqr(rocket)<=64)) return;
        if (!(player.containerMenu instanceof RocketMenu)) player.openMenu(rocket);
    }
    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(Open.TYPE, Open.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Open.TYPE, (payload, context) -> open(context.player()));
    }
}

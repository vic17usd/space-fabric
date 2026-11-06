package dev.vic17usd.spacefabric.map;

import com.mojang.serialization.Codec;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** A persistent world identity prevents mixing atlases after a multiplayer world reset. */
public final class AtlasNetwork {
    public static final class WorldIdentity extends SavedData {
        private final UUID id;
        public WorldIdentity(UUID id){this.id=id;}
        public UUID id(){return id;}
        public static final Codec<WorldIdentity> CODEC=Codec.STRING.fieldOf("world").xmap(s->new WorldIdentity(UUID.fromString(s)),v->v.id.toString()).codec();
        public static final SavedDataType<WorldIdentity> TYPE=new SavedDataType<>(SpaceFabric.id("atlas_world"),()->{
            var data=new WorldIdentity(UUID.randomUUID());data.setDirty();return data;
        },CODEC,DataFixTypes.LEVEL);
    }
    public record Identity(UUID world) implements CustomPacketPayload {
        public static final Type<Identity> TYPE=new Type<>(SpaceFabric.id("atlas_identity"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Identity> CODEC=new StreamCodec<>() {
            public Identity decode(RegistryFriendlyByteBuf b){return new Identity(b.readUUID());}
            public void encode(RegistryFriendlyByteBuf b,Identity p){b.writeUUID(p.world());}
        };
        @Override public Type<Identity> type(){return TYPE;}
    }
    private AtlasNetwork() { }
    public static void initialize(){
        PayloadTypeRegistry.clientboundPlay().register(Identity.TYPE,Identity.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{
            var player=handler.getPlayer();
            if(ServerPlayNetworking.canSend(player,Identity.TYPE))ServerPlayNetworking.send(player,
                    new Identity(server.getDataStorage().computeIfAbsent(WorldIdentity.TYPE).id()));
        });
    }
}

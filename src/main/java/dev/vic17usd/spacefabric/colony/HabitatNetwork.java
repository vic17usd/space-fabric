package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Read-only, player-specific telemetry. Room geometry is bounded and sent only when it changes. */
public final class HabitatNetwork {
    public static final int MAX_BOUNDARY=4096;
    private static final Map<UUID,Sent> SENT=new HashMap<>();
    private record Sent(net.minecraft.server.level.ServerPlayer player,Identifier dimension,BlockPos anchor,HabitatReadings readings,SealScanner.Room room,int tick) { }
    private HabitatNetwork() { }
    public record State(Identifier dimension,Optional<BlockPos> anchor,HabitatReadings readings,
                        boolean geometryUpdate,Optional<SealScanner.Room> room) implements CustomPacketPayload {
        public static final Type<State> TYPE=new Type<>(SpaceFabric.id("habitat_status"));
        public static final StreamCodec<RegistryFriendlyByteBuf,State> CODEC=new StreamCodec<>() {
            public State decode(RegistryFriendlyByteBuf b) {
                var dimension=Identifier.parse(b.readUtf(256));
                if(!b.readBoolean())return absent(dimension);
                var anchor=b.readBlockPos();
                int condition=bounded(b,HabitatReadings.Condition.values().length-1);
                var readings=new HabitatReadings(HabitatReadings.Condition.values()[condition],bounded(b,LifeSupport.CAPACITY),bounded(b,LifeSupport.CAPACITY),bounded(b,LifeSupport.CAPACITY),bounded(b,SealScanner.MAX_VOLUME),b.readBoolean());
                boolean update=b.readBoolean();Optional<SealScanner.Room> room=Optional.empty();
                if(update&&b.readBoolean()) {
                    var air=new HashSet<BlockPos>();int count=bounded(b,SealScanner.MAX_VOLUME);
                    for(int i=0;i<count;i++)air.add(b.readBlockPos());
                    var boundary=new HashMap<BlockPos,SealScanner.Cell>();count=bounded(b,MAX_BOUNDARY);
                    for(int i=0;i<count;i++)boundary.put(b.readBlockPos(),b.readBoolean()?SealScanner.Cell.RATED:SealScanner.Cell.SOLID);
                    room=Optional.of(new SealScanner.Room(SealScanner.Status.SEALED,Set.copyOf(air),Map.copyOf(boundary)));
                }
                return new State(dimension,Optional.of(anchor),readings,update,room);
            }
            public void encode(RegistryFriendlyByteBuf b,State state) {
                b.writeUtf(state.dimension.toString(),256);b.writeBoolean(state.anchor.isPresent());
                if(state.anchor.isEmpty())return;
                b.writeBlockPos(state.anchor.get());var r=state.readings;
                b.writeVarInt(r.condition().ordinal());b.writeVarInt(r.oxygen());b.writeVarInt(r.energy());b.writeVarInt(r.coolant());b.writeVarInt(r.volume());b.writeBoolean(r.venus());
                b.writeBoolean(state.geometryUpdate);
                if(!state.geometryUpdate)return;
                b.writeBoolean(state.room.isPresent());if(state.room.isEmpty())return;
                var room=state.room.get();
                if(room.air().size()>SealScanner.MAX_VOLUME||room.boundary().size()>MAX_BOUNDARY)throw new IllegalArgumentException("Unbounded habitat proof");
                b.writeVarInt(room.air().size());room.air().stream().sorted(Comparator.comparingLong(BlockPos::asLong)).forEach(b::writeBlockPos);
                b.writeVarInt(room.boundary().size());room.boundary().entrySet().stream().sorted(Comparator.comparingLong(e->e.getKey().asLong())).forEach(e->{b.writeBlockPos(e.getKey());b.writeBoolean(e.getValue()==SealScanner.Cell.RATED);});
            }
        };
        @Override public Type<State> type(){return TYPE;}
        public static State absent(Identifier dimension) {
            return new State(dimension,Optional.empty(),new HabitatReadings(HabitatReadings.Condition.IDLE,0,0,0,0,false),true,Optional.empty());
        }
    }
    private static int bounded(RegistryFriendlyByteBuf b,int max) {
        int count=b.readVarInt();if(count<0||count>max)throw new IllegalArgumentException("Invalid habitat field");return count;
    }
    public static void initialize() {
        PayloadTypeRegistry.clientboundPlay().register(State.TYPE,State.CODEC);
        ServerTickEvents.END_SERVER_TICK.register(server->{
            for(var player:server.getPlayerList().getPlayers()) {
                if(!ServerPlayNetworking.canSend(player,State.TYPE))continue;
                var previous=SENT.get(player.getUUID());var controller=HabitatSystem.controllerFor(player);
                var dimension=player.level().dimension().identifier();
                if(controller==null) {
                    if(previous!=null){ServerPlayNetworking.send(player,State.absent(dimension));SENT.remove(player.getUUID());}
                    continue;
                }
                var readings=controller.readings();var room=controller.enclosure();var pos=controller.getBlockPos();
                boolean changed=previous==null||previous.player!=player||!previous.dimension.equals(dimension)||!previous.anchor.equals(pos)||previous.room!=room;
                if(changed||!previous.readings.equals(readings)||player.tickCount-previous.tick>=20) {
                    ServerPlayNetworking.send(player,new State(dimension,Optional.of(pos),readings,changed,changed?Optional.ofNullable(room):Optional.empty()));
                    SENT.put(player.getUUID(),new Sent(player,dimension,pos,readings,room,player.tickCount));
                }
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->SENT.remove(handler.getPlayer().getUUID()));
        ServerLifecycleEvents.SERVER_STOPPED.register(server->SENT.clear());
    }
}

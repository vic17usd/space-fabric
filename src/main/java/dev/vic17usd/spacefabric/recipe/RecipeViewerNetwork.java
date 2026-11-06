package dev.vic17usd.spacefabric.recipe;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

/** One recipe per response: no full recipe dump or client-authoritative inventory action. */
public final class RecipeViewerNetwork {
    private static final Map<RecipeManager,RecipeCatalog> CACHE=new IdentityHashMap<>();
    private RecipeViewerNetwork() { }
    public record Request(int sequence,String item,boolean usages,int page) implements CustomPacketPayload {
        public static final Type<Request> TYPE=new Type<>(SpaceFabric.id("recipe_query"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Request> CODEC=new StreamCodec<>() {
            public Request decode(RegistryFriendlyByteBuf b){return new Request(b.readVarInt(),b.readUtf(256),b.readBoolean(),b.readVarInt());}
            public void encode(RegistryFriendlyByteBuf b,Request p){b.writeVarInt(p.sequence);b.writeUtf(p.item,256);b.writeBoolean(p.usages);b.writeVarInt(p.page);}
        };
        @Override public Type<Request> type(){return TYPE;}
    }
    public record Reply(int sequence,int page,int total,Optional<RecipeCard> card) implements CustomPacketPayload {
        public static final Type<Reply> TYPE=new Type<>(SpaceFabric.id("recipe_result"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Reply> CODEC=new StreamCodec<>() {
            public Reply decode(RegistryFriendlyByteBuf b) {
                int sequence=b.readVarInt(),page=b.readVarInt(),total=b.readVarInt();
                if(!b.readBoolean())return new Reply(sequence,page,total,Optional.empty());
                String id=b.readUtf(512),kind=b.readUtf(128);int width=b.readVarInt(),duration=b.readVarInt();float xp=b.readFloat();
                var slots=new ArrayList<List<ItemStack>>();int count=bounded(b,RecipeCard.MAX_SLOTS);
                for(int i=0;i<count;i++)slots.add(readStacks(b));
                return new Reply(sequence,page,total,Optional.of(new RecipeCard(id,kind,width,duration,xp,List.copyOf(slots),readStacks(b),ItemStack.OPTIONAL_STREAM_CODEC.decode(b))));
            }
            public void encode(RegistryFriendlyByteBuf b,Reply p) {
                b.writeVarInt(p.sequence);b.writeVarInt(p.page);b.writeVarInt(p.total);b.writeBoolean(p.card.isPresent());
                if(p.card.isEmpty())return;var c=p.card.get();
                b.writeUtf(c.id(),512);b.writeUtf(c.kind(),128);b.writeVarInt(c.width());b.writeVarInt(c.duration());b.writeFloat(c.experience());
                b.writeVarInt(c.inputs().size());c.inputs().forEach(slot->writeStacks(b,slot));
                writeStacks(b,c.outputs());ItemStack.OPTIONAL_STREAM_CODEC.encode(b,c.station());
            }
        };
        @Override public Type<Reply> type(){return TYPE;}
    }
    private static int bounded(RegistryFriendlyByteBuf b,int max) {
        int n=b.readVarInt();if(n<0||n>max)throw new IllegalArgumentException("Invalid recipe slot count");return n;
    }
    private static List<ItemStack> readStacks(RegistryFriendlyByteBuf b) {
        int size=bounded(b,RecipeCard.MAX_ALTERNATIVES);var stacks=new ArrayList<ItemStack>();
        for(int i=0;i<size;i++)stacks.add(ItemStack.STREAM_CODEC.decode(b));return List.copyOf(stacks);
    }
    private static void writeStacks(RegistryFriendlyByteBuf b,List<ItemStack> stacks) {
        b.writeVarInt(stacks.size());stacks.forEach(s->ItemStack.STREAM_CODEC.encode(b,s));
    }
    public static Reply query(RecipeCatalog catalog,Request request) {
        var id=Identifier.tryParse(request.item());
        var item=id==null?null:BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        var found=item==null?List.<RecipeCard>of():catalog.find(item,request.usages());
        int page=Math.clamp(request.page(),0,Math.max(0,found.size()-1));
        return new Reply(request.sequence(),page,found.size(),found.isEmpty()?Optional.empty():Optional.of(found.get(page)));
    }
    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(Request.TYPE,Request.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(Reply.TYPE,Reply.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Request.TYPE,(request,context)->{
            var player=context.player();var manager=context.server().getRecipeManager();
            var catalog=CACHE.computeIfAbsent(manager,k->RecipeCatalog.build(k.getRecipes(),SlotDisplayContext.fromLevel(player.level())));
            ServerPlayNetworking.send(player,query(catalog,request));
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server,resources,success)->CACHE.clear());
        ServerLifecycleEvents.SERVER_STOPPED.register(server->CACHE.clear());
    }
}

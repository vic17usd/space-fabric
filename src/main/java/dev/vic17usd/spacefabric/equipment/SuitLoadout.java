package dev.vic17usd.spacefabric.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/** Four persistent slots: helmet, chestplate, leggings, boots. Snapshots own their stack copies. */
public record SuitLoadout(List<ItemStack> items) {
    public SuitLoadout {
        if(items.size()!=4||items.stream().anyMatch(s->s.getCount()>1))throw new IllegalArgumentException("Expected four single suit slots");
        items=items.stream().map(ItemStack::copy).toList();
    }
    public static SuitLoadout empty(){return new SuitLoadout(List.of(ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY));}
    public SuitLoadout snapshot(){return new SuitLoadout(items);}
    public SuitTier tier(){return SuitProtection.tier(items.get(0),items.get(1),items.get(2),items.get(3));}
    public static final Codec<SuitLoadout> CODEC=ItemStack.OPTIONAL_CODEC.listOf().flatXmap(
        list->list.size()==4&&list.stream().allMatch(s->s.getCount()<=1)?DataResult.success(new SuitLoadout(list)):DataResult.error(()->"Invalid suit inventory"),
        value->DataResult.success(value.items));
    public static final StreamCodec<RegistryFriendlyByteBuf,SuitLoadout> STREAM_CODEC=new StreamCodec<>() {
        public SuitLoadout decode(RegistryFriendlyByteBuf buffer){
            return new SuitLoadout(java.util.stream.IntStream.range(0,4).mapToObj(i->ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)).toList());
        }
        public void encode(RegistryFriendlyByteBuf buffer,SuitLoadout value){value.items.forEach(s->ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer,s));}
    };
}

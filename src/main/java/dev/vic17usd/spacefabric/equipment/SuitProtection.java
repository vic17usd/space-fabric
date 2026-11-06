package dev.vic17usd.spacefabric.equipment;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Se exige un conjunto completo de la misma categoría, ampliable mediante tags por datapack. */
public final class SuitProtection {
    private static final String[] PARTS={"helmets","chestplates","leggings","boots"};
    private static final java.util.Map<SuitTier,java.util.List<TagKey<Item>>> TAGS=new java.util.EnumMap<>(SuitTier.class);
    static {
        for(var tier:SuitTier.values()) if(tier!=SuitTier.NONE)
            TAGS.put(tier,java.util.Arrays.stream(PARTS).map(part->TagKey.<Item>create(Registries.ITEM,
                    SpaceFabric.id("suits/"+tier.path+"/"+part))).toList());
    }
    private SuitProtection() { }
    public static SuitTier tier(Player player) {
        return tier(SuitEquipment.get(player),player.getItemBySlot(EquipmentSlot.HEAD),player.getItemBySlot(EquipmentSlot.CHEST),
                player.getItemBySlot(EquipmentSlot.LEGS),player.getItemBySlot(EquipmentSlot.FEET));
    }
    public static SuitTier tier(SuitLoadout dedicated,ItemStack head,ItemStack chest,ItemStack legs,ItemStack feet) {
        var selected=dedicated.tier();return selected!=SuitTier.NONE?selected:tier(head,chest,legs,feet);
    }
    public static SuitTier tier(ItemStack head,ItemStack chest,ItemStack legs,ItemStack feet) {
        ItemStack[] stacks={head,chest,legs,feet};
        for (var tier:SuitTier.values()) {
            if(tier==SuitTier.NONE) continue;
            boolean complete=true;
            for(int i=0;i<4;i++) complete &= stacks[i].is(TAGS.get(tier).get(i));
            if(complete)return tier;
        }
        return SuitTier.NONE;
    }
    public static boolean accepts(int slot,ItemStack stack){
        if(slot<0||slot>=4||stack.isEmpty())return false;
        return TAGS.values().stream().anyMatch(tags->stack.is(tags.get(slot)));
    }
    public static int slot(ItemStack stack){for(int i=0;i<4;i++)if(accepts(i,stack))return i;return -1;}
    public static boolean isSealed(Player player){return tier(player)!=SuitTier.NONE;}
    public static boolean resistsAcid(Player player){return tier(player).acidResistant;}
}

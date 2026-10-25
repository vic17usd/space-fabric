package dev.vic17usd.spacefabric.equipment;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

/** Etiquetas reemplazables por datapack: no depende de clases de armadura concretas. */
public final class SuitProtection {
    private static final TagKey<Item> HELMETS = tag("space_suit/helmets");
    private static final TagKey<Item> CHESTPLATES = tag("space_suit/chestplates");
    private static final TagKey<Item> LEGGINGS = tag("space_suit/leggings");
    private static final TagKey<Item> BOOTS = tag("space_suit/boots");

    private SuitProtection() {
    }

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, SpaceFabric.id(path));
    }

    public static boolean isSealed(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(HELMETS)
                && player.getItemBySlot(EquipmentSlot.CHEST).is(CHESTPLATES)
                && player.getItemBySlot(EquipmentSlot.LEGS).is(LEGGINGS)
                && player.getItemBySlot(EquipmentSlot.FEET).is(BOOTS);
    }
}

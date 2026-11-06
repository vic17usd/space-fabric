package dev.vic17usd.spacefabric.item;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAssets;

/** Visual variants preserve the existing iron-based durability, defense and repair behavior. */
public final class SuitMaterials {
    private static final Map<String,ArmorMaterial> MATERIALS=Stream.of("vacuum","venus","cryo","abyssal")
        .collect(Collectors.toUnmodifiableMap(tier->tier,tier->{
            var base=ArmorMaterials.IRON;
            return new ArmorMaterial(base.durability(),base.defense(),base.enchantmentValue(),base.equipSound(),
                    base.toughness(),base.knockbackResistance(),base.repairIngredient(),
                    ResourceKey.create(EquipmentAssets.ROOT_ID,SpaceFabric.id(tier+"_suit")));
        }));
    private SuitMaterials() { }
    public static ArmorMaterial forTier(String tier) {
        var material=MATERIALS.get(tier);
        if(material==null)throw new IllegalArgumentException("Unknown suit tier: "+tier);
        return material;
    }
}

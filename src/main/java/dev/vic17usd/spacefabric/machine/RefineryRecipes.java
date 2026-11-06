package dev.vic17usd.spacefabric.machine;

import dev.vic17usd.spacefabric.item.ModItems;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Shared by the processing engine and recipe viewer, so displayed formulas cannot drift. */
public record RefineryRecipes(List<Item> bases,List<Item> reagents,int duration) {
    public static final List<RefineryRecipes> ALL=List.of(
        new RefineryRecipes(List.of(Items.COAL,Items.CHARCOAL),List.of(Items.BLAZE_POWDER),200),
        new RefineryRecipes(List.of(ModItems.SULFUR_CRYSTAL),List.of(ModItems.ACID_CONDENSATE,ModItems.SULFATE_DUST),160));
    public boolean matches(ItemStack base,ItemStack reagent) {
        return bases.contains(base.getItem())&&reagents.contains(reagent.getItem());
    }
    public static boolean accepts(int slot,ItemStack stack) {
        return switch(slot) {
            case 0 -> ALL.stream().anyMatch(r->r.bases.contains(stack.getItem()));
            case 1 -> ALL.stream().anyMatch(r->r.reagents.contains(stack.getItem()));
            case 2 -> stack.is(ModItems.EMPTY_FUEL_CELL);
            default -> false;
        };
    }
}

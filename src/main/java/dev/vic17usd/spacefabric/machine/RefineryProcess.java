package dev.vic17usd.spacefabric.machine;

import dev.vic17usd.spacefabric.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Motor sin mundo: consume los tres ingredientes de forma atómica al completar el ciclo. */
public final class RefineryProcess {
    private int progress;
    private int recipe;
    public int progress() { return progress; }
    public int duration() { return recipe == 2 ? 160 : 200; }
    public static boolean accepts(int slot, ItemStack stack) {
        return switch (slot) {
            case 0 -> stack.is(Items.COAL) || stack.is(Items.CHARCOAL) || stack.is(ModItems.SULFUR_CRYSTAL);
            case 1 -> stack.is(Items.BLAZE_POWDER) || stack.is(ModItems.ACID_CONDENSATE) || stack.is(ModItems.SULFATE_DUST);
            case 2 -> stack.is(ModItems.EMPTY_FUEL_CELL);
            default -> false;
        };
    }
    private static int match(Container inventory) {
        if (!inventory.getItem(2).is(ModItems.EMPTY_FUEL_CELL)) return 0;
        var base = inventory.getItem(0); var reagent = inventory.getItem(1);
        if ((base.is(Items.COAL) || base.is(Items.CHARCOAL)) && reagent.is(Items.BLAZE_POWDER)) return 1;
        if (base.is(ModItems.SULFUR_CRYSTAL) && (reagent.is(ModItems.ACID_CONDENSATE) || reagent.is(ModItems.SULFATE_DUST))) return 2;
        return 0;
    }
    public boolean tick(Container inventory) {
        int selected = match(inventory);
        boolean changed = selected != recipe;
        if (selected != recipe) { recipe = selected; progress = 0; }
        if (selected == 0) return changed;
        var output = inventory.getItem(3);
        if (!output.isEmpty() && (!output.is(ModItems.ROCKET_FUEL_CELL) || output.getCount() >= output.getMaxStackSize())) return changed;
        if (++progress < duration()) return true;
        for (int i = 0; i < 3; i++) inventory.removeItem(i, 1);
        if (output.isEmpty()) inventory.setItem(3, new ItemStack(ModItems.ROCKET_FUEL_CELL)); else output.grow(1);
        inventory.setChanged(); progress = 0;
        return true;
    }
    public void restore(int progress, int recipe) {
        this.recipe = Math.clamp(recipe, 0, 2);
        this.progress = Math.clamp(progress, 0, duration() - 1);
    }
    public int recipe() { return recipe; }
}

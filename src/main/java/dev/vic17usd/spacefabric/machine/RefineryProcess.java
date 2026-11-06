package dev.vic17usd.spacefabric.machine;

import dev.vic17usd.spacefabric.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/** Motor sin mundo: consume los tres ingredientes de forma atómica al completar el ciclo. */
public final class RefineryProcess {
    private int progress;
    private int recipe;
    public int progress() { return progress; }
    public int duration() { return recipe>0?RefineryRecipes.ALL.get(recipe-1).duration():200; }
    public static boolean accepts(int slot, ItemStack stack) { return RefineryRecipes.accepts(slot,stack); }
    private static int match(Container inventory) {
        if(!inventory.getItem(2).is(ModItems.EMPTY_FUEL_CELL))return 0;
        for(int i=0;i<RefineryRecipes.ALL.size();i++)
            if(RefineryRecipes.ALL.get(i).matches(inventory.getItem(0),inventory.getItem(1)))return i+1;
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

package dev.vic17usd.spacefabric.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.*;
import net.minecraft.util.context.ContextMap;

/** Read-only presentation, resolved against the server's recipes and tags. */
public record RecipeCard(String id, String kind, int width, int duration, float experience,
        List<List<ItemStack>> inputs, List<ItemStack> outputs, ItemStack station) {
    public static final int MAX_SLOTS=9, MAX_ALTERNATIVES=64;
    public static RecipeCard from(String id, String kind, Recipe<?> recipe, RecipeDisplay display, ContextMap context) {
        List<SlotDisplay> slots;
        int width=3, duration=0; float xp=0;
        if(display instanceof ShapedCraftingRecipeDisplay shaped) {slots=shaped.ingredients();width=shaped.width();}
        else if(display instanceof ShapelessCraftingRecipeDisplay shapeless) slots=shapeless.ingredients();
        else if(display instanceof FurnaceRecipeDisplay furnace) {
            slots=List.of(furnace.ingredient(),SlotDisplay.Empty.INSTANCE,SlotDisplay.Empty.INSTANCE,furnace.fuel());
            duration=furnace.duration();xp=furnace.experience();
        } else if(display instanceof StonecutterRecipeDisplay cutter) {slots=List.of(cutter.input());width=1;}
        else if(display instanceof SmithingRecipeDisplay smithing) slots=List.of(smithing.template(),smithing.base(),smithing.addition());
        else slots=recipe.placementInfo().ingredients().stream().map(i->i.display()).toList();
        var ingredients=slots.stream().limit(MAX_SLOTS).map(s->resolve(s,context)).toList();
        return new RecipeCard(id,kind,Math.clamp(width,1,3),duration,xp,ingredients,
                resolve(display.result(),context),display.craftingStation().resolveForFirstStack(context));
    }
    private static List<ItemStack> resolve(SlotDisplay slot,ContextMap context) {
        return slot.resolveForStacks(context).stream().filter(s->!s.isEmpty()).limit(MAX_ALTERNATIVES).map(ItemStack::copy).toList();
    }
}

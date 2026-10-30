package dev.vic17usd.spacefabric.recipe;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.item.ModItems;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Immutable reverse indexes: query cost does not grow with the number of recipes. */
public final class RecipeCatalog {
    private final Map<Item,List<RecipeCard>> recipes=new HashMap<>(), usages=new HashMap<>();
    public RecipeCatalog(Collection<RecipeCard> cards) {
        for(var card:cards) {
            card.outputs().stream().map(ItemStack::getItem).distinct().forEach(item->recipes.computeIfAbsent(item,k->new ArrayList<>()).add(card));
            card.inputs().stream().flatMap(List::stream).map(ItemStack::getItem).distinct()
                    .forEach(item->usages.computeIfAbsent(item,k->new ArrayList<>()).add(card));
        }
    }
    public List<RecipeCard> find(Item item,boolean usage) {return List.copyOf((usage?usages:recipes).getOrDefault(item,List.of()));}
    public static RecipeCatalog build(Collection<RecipeHolder<?>> holders,ContextMap context) {
        var cards=new ArrayList<RecipeCard>();
        holders.stream().sorted(Comparator.comparing(h->h.id().identifier().toString())).forEach(holder->{
            var recipe=holder.value();
            var kind=BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()).getPath();
            int displayIndex=0;
            for(var display:recipe.display()) {
                try {
                    var card=RecipeCard.from(holder.id().identifier()+"/"+displayIndex++,kind,recipe,display,context);
                    if(!card.outputs().isEmpty())cards.add(card);
                } catch(RuntimeException error) {SpaceFabric.LOGGER.warn("Cannot display recipe {}",holder.id().identifier(),error);}
            }
        });
        // The refinery uses a processing engine rather than vanilla recipe JSON.
        for(int i=0;i<dev.vic17usd.spacefabric.machine.RefineryRecipes.ALL.size();i++) {
            var formula=dev.vic17usd.spacefabric.machine.RefineryRecipes.ALL.get(i);
            cards.add(new RecipeCard("space_fabric:refinery/"+i,"refinery",3,formula.duration(),0,
                List.of(formula.bases().stream().map(ItemStack::new).toList(),formula.reagents().stream().map(ItemStack::new).toList(),List.of(new ItemStack(ModItems.EMPTY_FUEL_CELL))),
                List.of(new ItemStack(ModItems.ROCKET_FUEL_CELL)),new ItemStack(ModBlocks.FUEL_REFINERY)));
        }
        for(var formula:dev.vic17usd.spacefabric.colony.ColonyRecipes.ALL) {
            var inputs=new ArrayList<List<ItemStack>>();
            for(int slot=0;slot<3;slot++) {
                int index=slot;inputs.add(formula.inputs().stream().filter(p->p.slot()==index).map(p->p.stack()).toList());
            }
            if(formula.machine()==dev.vic17usd.spacefabric.colony.ColonyContent.Machine.DRILL){
                for(var mineral:List.of(ModItems.VENUSIAN_OLIVINE,ModItems.PYROXENE,ModItems.SULFUR_CRYSTAL))
                    cards.add(new RecipeCard("space_fabric:colony/"+formula.id()+"/"+BuiltInRegistries.ITEM.getKey(mineral).getPath(),"colony",3,formula.duration(),0,
                        List.copyOf(inputs),List.of(new ItemStack(mineral),new ItemStack(dev.vic17usd.spacefabric.colony.ColonyContent.EMPTY_POWER_CELL),new ItemStack(dev.vic17usd.spacefabric.colony.ColonyContent.WATER_CANISTER)),
                        new ItemStack(dev.vic17usd.spacefabric.colony.ColonyContent.DRILL)));
            }
            cards.add(new RecipeCard("space_fabric:colony/"+formula.id(),"colony",3,formula.duration(),0,
                    List.copyOf(inputs),formula.outputs().stream().map(p->p.stack()).toList(),
                    new ItemStack(dev.vic17usd.spacefabric.colony.ColonyContent.block(formula.machine()))));
        }
        cards.add(new RecipeCard("space_fabric:research/modules","colony",3,0,0,
            dev.vic17usd.spacefabric.research.ResearchCosts.forStage(4).stream().map(c->List.of(new ItemStack(c.item(),c.count()))).toList(),
            List.of(new ItemStack(dev.vic17usd.spacefabric.research.ResearchContent.ROVER_MODULE),new ItemStack(dev.vic17usd.spacefabric.research.ResearchContent.SCANNER_LENS),new ItemStack(dev.vic17usd.spacefabric.colony.ColonyContent.EMPTY_POWER_CELL)),
            new ItemStack(dev.vic17usd.spacefabric.colony.ColonyContent.SURVEY)));
        return new RecipeCatalog(cards);
    }
}

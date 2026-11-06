package dev.vic17usd.spacefabric.research;

import dev.vic17usd.spacefabric.colony.ColonyContent;
import dev.vic17usd.spacefabric.industry.IndustryContent;
import dev.vic17usd.spacefabric.item.ModItems;
import java.util.*;
import net.minecraft.world.item.*;

/** Costs paid on the server only after all world evidence has been checked. */
public final class ResearchCosts {
    public record Cost(Item item,int count){public Cost{if(count<1)throw new IllegalArgumentException("Positive cost required");}}
    private static Cost c(Item item,int count){return new Cost(item,count);}
    public static List<Cost> forStage(int stage){return switch(stage){
        case 0->List.of(c(ColonyContent.DATA_CARD,1),c(ColonyContent.CHARGED_POWER_CELL,1));
        case 1->List.of(c(ColonyContent.VENUS_REPORT,1),c(ColonyContent.CHARGED_POWER_CELL,1));
        case 2->List.of(c(ColonyContent.EMPTY_CANISTER,1),c(ColonyContent.CHARGED_POWER_CELL,1));
        case 3->List.of(c(ModItems.VENUSIAN_OLIVINE,2),c(ModItems.PYROXENE,2),c(ModItems.SULFUR_CRYSTAL,2),c(ColonyContent.VENUS_REPORT,1),c(ColonyContent.DATA_CARD,1),c(ColonyContent.CHARGED_POWER_CELL,1));
        case 4->List.of(c(IndustryContent.COMPOSITE_PLATE,4),c(IndustryContent.CERAMIC,2),c(ColonyContent.DATA_CARD,1),c(ColonyContent.CHARGED_POWER_CELL,1));
        default->throw new IllegalArgumentException("Unknown research stage");
    };}
    private ResearchCosts() { }
    private static Map<Item,Integer> totals(List<Cost> costs){var map=new LinkedHashMap<Item,Integer>();costs.forEach(c->map.merge(c.item,c.count,Math::addExact));return map;}
    public static boolean affordable(List<ItemStack> inventory,List<Cost> costs){return totals(costs).entrySet().stream().allMatch(e->inventory.stream().filter(s->s.is(e.getKey())).mapToInt(ItemStack::getCount).sum()>=e.getValue());}
    public static boolean pay(List<ItemStack> inventory,List<Cost> costs){
        if(!affordable(inventory,costs))return false;
        for(var entry:totals(costs).entrySet()){int left=entry.getValue();for(var stack:inventory)if(stack.is(entry.getKey())){int n=Math.min(left,stack.getCount());stack.shrink(n);left-=n;if(left==0)break;}}
        return true;
    }
}

package dev.vic17usd.spacefabric.colony;

import java.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.industry.IndustryContent;

/** One source of truth for processing, automation and the recipe browser. */
public final class ColonyRecipes {
    public record Part(int slot,Item item,int count) {public ItemStack stack(){return new ItemStack(item,count);}}
    public record Formula(String id,ColonyContent.Machine machine,int duration,List<Part> inputs,List<Part> outputs) {
        public boolean accepts(Container inventory) {
            return inputs.stream().allMatch(p->inventory.getItem(p.slot()).is(p.item())&&inventory.getItem(p.slot()).getCount()>=p.count());
        }
        public boolean fits(Container inventory) {
            return outputs.stream().allMatch(p->{var old=inventory.getItem(p.slot());return old.isEmpty()||old.is(p.item())&&old.getCount()+p.count()<=old.getMaxStackSize();});
        }
        public boolean commit(Container inventory) {
            if(!accepts(inventory)||!fits(inventory))return false;
            inputs.forEach(p->inventory.removeItem(p.slot(),p.count()));
            outputs.forEach(p->{var old=inventory.getItem(p.slot());if(old.isEmpty())inventory.setItem(p.slot(),p.stack());else old.grow(p.count());});
            inventory.setChanged();return true;
        }
    }
    public static final List<Formula> ALL=create();
    private ColonyRecipes() { }
    private static Part p(int slot,Item item){return new Part(slot,item,1);}
    private static List<Formula> create() {
        var list=new ArrayList<Formula>();
        for(var ice:List.of(ColonyContent.MARTIAN_ICE.asItem(),Blocks.ICE.asItem(),Blocks.PACKED_ICE.asItem(),Blocks.BLUE_ICE.asItem(),ModItems.CRATER_ICE_SHARD))
            list.add(new Formula("melt_"+list.size(),ColonyContent.Machine.ELECTROLYZER,100,
                    List.of(p(0,ice),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.EMPTY_CANISTER)),
                    List.of(p(3,ColonyContent.WATER_CANISTER),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("electrolysis",ColonyContent.Machine.ELECTROLYZER,200,
                List.of(p(0,ColonyContent.WATER_CANISTER),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.EMPTY_CANISTER)),
                List.of(p(3,ColonyContent.OXYGEN_CANISTER),p(4,ColonyContent.HYDROGEN_CANISTER),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("solar",ColonyContent.Machine.SOLAR,400,List.of(p(0,ColonyContent.EMPTY_POWER_CELL)),List.of(p(4,ColonyContent.CHARGED_POWER_CELL))));
        list.add(new Formula("geothermal",ColonyContent.Machine.GEOTHERMAL,100,List.of(p(0,ColonyContent.EMPTY_POWER_CELL),p(2,ColonyContent.WATER_CANISTER)),
                List.of(p(4,ColonyContent.CHARGED_POWER_CELL),p(6,ColonyContent.WATER_CANISTER))));
        for(var sample:List.of(ModItems.MARTIAN_OLIVINE,ModItems.SULFATE_DUST))
            list.add(new Formula("mars_"+list.size(),ColonyContent.Machine.ANALYZER,200,
                    List.of(p(0,sample),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.DATA_CARD)),
                    List.of(p(3,ColonyContent.MARS_REPORT),p(5,ColonyContent.EMPTY_POWER_CELL))));
        for(var sample:List.of(ModItems.VENUSIAN_OLIVINE,ModItems.PYROXENE,ModItems.SULFUR_CRYSTAL))
            list.add(new Formula("venus_"+list.size(),ColonyContent.Machine.ANALYZER,200,
                    List.of(p(0,sample),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.DATA_CARD)),
                    List.of(p(3,ColonyContent.VENUS_REPORT),p(5,ColonyContent.EMPTY_POWER_CELL))));
        // Preserve the old formula IDs: saved progress and the browser refer to them.
        list.add(new Formula("venus_atmosphere",ColonyContent.Machine.ATMOSPHERIC_COLLECTOR,200,
                List.of(p(0,ColonyContent.EMPTY_CANISTER),p(1,ColonyContent.CHARGED_POWER_CELL)),
                List.of(p(3,IndustryContent.ATMOSPHERE),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("venus_separation",ColonyContent.Machine.GAS_SEPARATOR,200,
                List.of(p(0,IndustryContent.ATMOSPHERE),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.EMPTY_CANISTER)),
                List.of(p(3,IndustryContent.CARBON_DIOXIDE),p(4,IndustryContent.NITROGEN),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("sulfur_refining",ColonyContent.Machine.SULFUR_PROCESSOR,200,
                List.of(new Part(0,ModItems.SULFUR_CRYSTAL,2),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,IndustryContent.NITROGEN)),
                List.of(new Part(3,IndustryContent.REFINED_SULFUR,2),p(5,ColonyContent.EMPTY_POWER_CELL),p(6,ColonyContent.EMPTY_CANISTER))));
        list.add(new Formula("acid_recovery",ColonyContent.Machine.SULFUR_PROCESSOR,200,
                List.of(p(0,ModItems.ACID_CONDENSATE),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.HYDROGEN_CANISTER)),
                List.of(p(3,IndustryContent.REFINED_SULFUR),p(4,ColonyContent.WATER_CANISTER),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("pyroxene_ceramic",ColonyContent.Machine.CERAMIC_KILN,300,
                List.of(p(0,ModItems.PYROXENE),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,IndustryContent.CARBON_DIOXIDE)),
                List.of(new Part(3,IndustryContent.CERAMIC,2),p(5,ColonyContent.EMPTY_POWER_CELL),p(6,ColonyContent.EMPTY_CANISTER))));
        list.add(new Formula("venus_composite",ColonyContent.Machine.CERAMIC_KILN,300,
                List.of(p(0,IndustryContent.CERAMIC),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ModItems.DENSE_IRON_CHUNK)),
                List.of(p(3,IndustryContent.COMPOSITE_PLATE),p(5,ColonyContent.EMPTY_POWER_CELL))));
        // Local construction does not require a prior trip to Mercury. Dense iron remains more efficient.
        list.add(new Formula("venus_composite_iron",ColonyContent.Machine.CERAMIC_KILN,400,
                List.of(p(0,IndustryContent.CERAMIC),p(1,ColonyContent.CHARGED_POWER_CELL),new Part(2,Items.IRON_INGOT,2)),
                List.of(p(3,IndustryContent.COMPOSITE_PLATE),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("carbon_dioxide_electrolysis",ColonyContent.Machine.ELECTROLYZER,200,
                List.of(p(0,IndustryContent.CARBON_DIOXIDE),p(1,ColonyContent.CHARGED_POWER_CELL)),
                List.of(p(3,ColonyContent.OXYGEN_CANISTER),p(4,IndustryContent.CARBON_POWDER),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("chilled_coolant",ColonyContent.Machine.COOLER,200,
                List.of(p(0,ColonyContent.WATER_CANISTER),p(1,ColonyContent.CHARGED_POWER_CELL)),
                List.of(p(3,dev.vic17usd.spacefabric.recovery.RecoveryContent.CHILLED_COOLANT),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("exchanger_charge",ColonyContent.Machine.EXCHANGER,60,
                List.of(p(0,dev.vic17usd.spacefabric.recovery.RecoveryContent.CHILLED_COOLANT),p(1,ColonyContent.CHARGED_POWER_CELL)),
                List.of(p(3,ColonyContent.WATER_CANISTER),p(5,ColonyContent.EMPTY_POWER_CELL))));
        list.add(new Formula("hydroponic_wheat",ColonyContent.Machine.GREENHOUSE,400,
                List.of(p(0,Items.WHEAT_SEEDS),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.WATER_CANISTER)),
                List.of(new Part(3,Items.WHEAT,2),p(4,Items.WHEAT_SEEDS),p(5,ColonyContent.EMPTY_POWER_CELL),p(6,ColonyContent.EMPTY_CANISTER))));
        list.add(new Formula("algal_oxygen",ColonyContent.Machine.GREENHOUSE,400,
                List.of(p(0,IndustryContent.CARBON_DIOXIDE),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,ColonyContent.WATER_CANISTER)),
                List.of(p(3,ColonyContent.OXYGEN_CANISTER),p(5,ColonyContent.EMPTY_POWER_CELL),p(6,ColonyContent.EMPTY_CANISTER))));
        list.add(new Formula("breath_recovery",ColonyContent.Machine.RECLAIMER,200,
                List.of(new Part(0,ColonyContent.EMPTY_CANISTER,2),p(1,ColonyContent.CHARGED_POWER_CELL),p(2,dev.vic17usd.spacefabric.maintenance.MaintenanceContent.FILTER)),
                List.of(p(3,ColonyContent.WATER_CANISTER),p(4,IndustryContent.CARBON_DIOXIDE),p(5,ColonyContent.EMPTY_POWER_CELL),p(6,dev.vic17usd.spacefabric.maintenance.MaintenanceContent.SPENT_FILTER))));
        return List.copyOf(list);
    }
    public static boolean accepts(ColonyContent.Machine machine,int slot,ItemStack stack) {
        if(machine==ColonyContent.Machine.DOCK)return slot==0?stack.is(ColonyContent.CHARGED_POWER_CELL):slot==1?stack.is(ColonyContent.OXYGEN_CANISTER):slot==2?stack.is(dev.vic17usd.spacefabric.recovery.RecoveryContent.CHILLED_COOLANT)||stack.is(ColonyContent.WATER_CANISTER):slot==8&&stack.is(IndustryContent.COMPOSITE_PLATE);
        if(machine==ColonyContent.Machine.SURVEY)return slot==0?stack.is(ColonyContent.VENUS_REPORT):slot==1?stack.is(ColonyContent.CHARGED_POWER_CELL):slot==2&&stack.is(ColonyContent.DATA_CARD);
        if(machine==ColonyContent.Machine.HABITAT)return slot==0?stack.is(ColonyContent.OXYGEN_CANISTER):slot==1?stack.is(ColonyContent.CHARGED_POWER_CELL):slot==2&&stack.is(ColonyContent.WATER_CANISTER);
        return ALL.stream().filter(f->f.machine()==machine).flatMap(f->f.inputs().stream()).anyMatch(p->p.slot()==slot&&stack.is(p.item()));
    }
}

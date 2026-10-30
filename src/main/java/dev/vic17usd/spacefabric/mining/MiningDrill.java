package dev.vic17usd.spacefabric.mining;

import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.recovery.RecoveryContent;
import java.util.*;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;

/** Finite, selective core extraction. No shafts, explosions, arbitrary block mining or free ores. */
public final class MiningDrill {
    public static final int DEPTH=96,DURATION=200,SEARCH_BUDGET=16;
    public interface BoreWorld {
        int minY();
        boolean loaded(BlockPos pos);
        BlockState state(BlockPos pos);
        boolean permitted(BlockPos pos,BlockState expected);
        boolean replace(BlockPos pos,BlockState expected);
    }
    private UUID owner;
    private boolean enabled;
    private int cursor,progress;
    private String target="";
    public UUID owner(){return owner;}
    public void owner(UUID value){if(owner==null)owner=value;}
    public boolean enabled(){return enabled;}
    public void toggle(){enabled=!enabled;}
    public int cursor(){return cursor;}
    public int progress(){return progress;}
    public static Item product(Block block){
        if(block==ModBlocks.VENUSIAN_OLIVINE_ORE)return ModItems.VENUSIAN_OLIVINE;
        if(block==ModBlocks.PYROXENE_ORE)return ModItems.PYROXENE;
        if(block==ModBlocks.SULFUR_DEPOSIT)return ModItems.SULFUR_CRYSTAL;
        return null;
    }
    public static boolean mineral(Block block){return product(block)!=null;}
    private void advance(){cursor++;progress=0;target="";}
    private static boolean fits(ItemStack old,Item item){return old.isEmpty()||old.is(item)&&old.getCount()<old.getMaxStackSize();}
    private static void add(ColonyMachineBlockEntity machine,int slot,Item item){var old=machine.getItem(slot);if(old.isEmpty())machine.setItem(slot,new ItemStack(item));else old.grow(1);}
    /** Also drives native tests with an in-memory world; server adapter enforces ownership/claims. */
    public String step(ColonyMachineBlockEntity machine,BoreWorld world,boolean venus){
        if(!enabled)return "drill_off";
        if(!venus)return "collector_environment";
        if(machine.maintenance().failed())return "maintenance_failure";
        if(machine.thermal().tripped())return "overheating";
        if(!machine.getItem(0).is(MiningContent.BIT)||machine.getItem(0).getDamageValue()>=machine.getItem(0).getMaxDamage()
                ||!machine.getItem(1).is(ColonyContent.CHARGED_POWER_CELL)||!machine.getItem(2).is(RecoveryContent.CHILLED_COOLANT))return "inputs";
        int work=SEARCH_BUDGET;
        while(cursor<BoreGeometry.count(1,DEPTH)&&work-->0){
            var pos=machine.getBlockPos().offset(BoreGeometry.dx(cursor,1),-1-BoreGeometry.down(cursor,1),BoreGeometry.dz(cursor,1));
            if(pos.getY()<world.minY()){cursor=BoreGeometry.count(1,DEPTH);break;}
            if(!world.loaded(pos))return "unloaded";
            var state=world.state(pos);var item=product(state.getBlock());
            if(item==null){advance();continue;}
            String id=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            if(!id.equals(target)){target=id;progress=0;}
            int output=-1;
            for(int slot:new int[]{3,4,7,8})if(fits(machine.getItem(slot),item)){output=slot;break;}
            if(output<0||!fits(machine.getItem(5),ColonyContent.EMPTY_POWER_CELL)||!fits(machine.getItem(6),ColonyContent.WATER_CANISTER))return "output_full";
            if(progress<DURATION)progress++;
            if(progress<DURATION)return "processing";
            if(!world.permitted(pos,state))return "drill_protected";
            // Protection callbacks run third-party code; recheck inventory before any mutation.
            if(!machine.getItem(0).is(MiningContent.BIT)||machine.getItem(0).getDamageValue()>=machine.getItem(0).getMaxDamage()
                    ||!machine.getItem(1).is(ColonyContent.CHARGED_POWER_CELL)||!machine.getItem(2).is(RecoveryContent.CHILLED_COOLANT))return "inputs";
            if(!fits(machine.getItem(output),item)||!fits(machine.getItem(5),ColonyContent.EMPTY_POWER_CELL)||!fits(machine.getItem(6),ColonyContent.WATER_CANISTER))return "output_full";
            // A listener may have changed the block: compare-and-replace is the final authority.
            if(!world.replace(pos,state)){progress=0;return "drill_changed";}
            machine.getItem(1).shrink(1);machine.getItem(2).shrink(1);
            add(machine,output,item);add(machine,5,ColonyContent.EMPTY_POWER_CELL);add(machine,6,ColonyContent.WATER_CANISTER);
            var bit=machine.getItem(0);bit.setDamageValue(bit.getDamageValue()+1);if(bit.getDamageValue()>=bit.getMaxDamage())bit.shrink(1);
            machine.recordDrillBatch();advance();return "processing";
        }
        return cursor>=BoreGeometry.count(1,DEPTH)?"drill_depleted":"drill_search";
    }
    public String tick(ServerLevel level,ColonyMachineBlockEntity machine){
        if(!enabled)return "drill_off";
        if(PlanetDimensions.find(level).orElse(null)!=Planet.VENUS)return "collector_environment";
        var player=owner==null?null:level.getServer().getPlayerList().getPlayer(owner);
        if(player==null||player.level()!=level||!player.mayBuild())return "drill_owner";
        return step(machine,new BoreWorld(){
            public int minY(){return level.getMinY();}
            public boolean loaded(BlockPos pos){return level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null;}
            public BlockState state(BlockPos pos){return level.getBlockState(pos);}
            public boolean permitted(BlockPos pos,BlockState expected){
                return level.getWorldBorder().isWithinBounds(pos)&&level.mayInteract(player,pos)
                    &&!level.getServer().isUnderSpawnProtection(level,pos,player)&&level.getBlockEntity(pos)==null
                    &&PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level,player,pos,expected,null);
            }
            public boolean replace(BlockPos pos,BlockState expected){
                if(!loaded(pos)||!level.getBlockState(pos).equals(expected)||level.getBlockEntity(pos)!=null)return false;
                // Backfill with native basalt: no open shaft, flooding or unintended room breach.
                return level.setBlock(pos,ModBlocks.VULCANIZED_BASALT.defaultBlockState(),Block.UPDATE_CLIENTS);
            }
        },true);
    }
    public void save(ValueOutput output){
        output.putBoolean("DrillEnabled",enabled);output.putInt("DrillCursor",cursor);output.putInt("DrillProgress",progress);output.putString("DrillTarget",target);
        if(owner!=null)output.putString("DrillOwner",owner.toString());
    }
    public void load(ValueInput input){
        enabled=input.getBooleanOr("DrillEnabled",false);cursor=Math.clamp(input.getIntOr("DrillCursor",0),0,BoreGeometry.count(1,DEPTH));
        progress=Math.clamp(input.getIntOr("DrillProgress",0),0,DURATION);target=input.getStringOr("DrillTarget","");
        try{owner=UUID.fromString(input.getStringOr("DrillOwner",""));}catch(IllegalArgumentException ignored){owner=null;enabled=false;}
    }
}

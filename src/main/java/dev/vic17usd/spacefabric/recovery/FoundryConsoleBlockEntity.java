package dev.vic17usd.spacefabric.recovery;

import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.industry.IndustryContent;
import dev.vic17usd.spacefabric.planet.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;

/** Diagnostics never load remote chunks; native structure NBT alone enrolls a reward site. */
public final class FoundryConsoleBlockEntity extends BlockEntity implements MenuProvider {
    public enum Status { MISSING,UNLOADED,REPAIR,FILTER,RUN,READY }
    private boolean naturalSite;
    private BlockPos anchor;
    public FoundryConsoleBlockEntity(BlockPos pos,BlockState state){super(RecoveryContent.CONSOLE_TYPE,pos,state);}
    public static ColonyContent.Machine role(int i){return switch(i){case 0->ColonyContent.Machine.GEOTHERMAL;case 1->ColonyContent.Machine.COOLER;default->ColonyContent.Machine.SULFUR_PROCESSOR;};}
    public static Status classify(int integrity,int filter,int batches){return integrity<7000?Status.REPAIR:filter<=0?Status.FILTER:batches<1?Status.RUN:Status.READY;}
    public static void tick(Level level,BlockPos pos,BlockState state,FoundryConsoleBlockEntity console) {
        if(level instanceof ServerLevel server&&level.getGameTime()%20==0)console.connect(server);
    }
    private boolean venus(){return level instanceof ServerLevel&&PlanetDimensions.find(level).orElse(null)==Planet.VENUS;}
    private void connect(ServerLevel server) {
        if(!venus())return;
        var ledger=FoundryLedger.get(server);
        if(naturalSite) {
            var rotation=switch(getBlockState().getValue(FoundryConsoleBlock.FACING)){case EAST->Rotation.CLOCKWISE_90;case SOUTH->Rotation.CLOCKWISE_180;case WEST->Rotation.COUNTERCLOCKWISE_90;default->Rotation.NONE;};
            var units=new ArrayList<BlockPos>();
            for(int i=1;i<=3;i++)units.add(worldPosition.offset(dev.vic17usd.spacefabric.worldgen.structure.VenusSurfaceStructure.rotate(new BlockPos(i*3,0,0),rotation)));
            ledger.enroll(worldPosition,units);anchor=worldPosition;naturalSite=false;setChanged();
        } else if(anchor==null)ledger.nearby(worldPosition).ifPresent(site->{anchor=site.anchor();setChanged();});
    }
    public Optional<FoundryLedger.Site> site() {
        if(!venus())return Optional.empty();
        connect((ServerLevel)level);
        return anchor==null||anchor.distSqr(worldPosition)>256?Optional.empty():FoundryLedger.get((ServerLevel)level).site(anchor);
    }
    public Status status(FoundryLedger.Site site,int i) {
        var pos=site.machines().get(i);
        if(!level.hasChunkAt(pos))return Status.UNLOADED;
        if(!(level.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine)||machine.kind()!=role(i))return Status.MISSING;
        return classify(machine.maintenance().integrity(),machine.maintenance().state().filter(),machine.completedBatches());
    }
    public ContainerData data(){return new ContainerData(){
        public int getCount(){return 6;}public void set(int i,int value) { }
        public int get(int i) {
            var site=site();if(site.isEmpty())return i<3?Status.MISSING.ordinal():0;
            if(i<3)return status(site.get(),i).ordinal();
            return switch(i){case 3->1;case 4->site.get().activated()?1:0;default->java.util.stream.IntStream.range(0,3).allMatch(n->status(site.get(),n)==Status.READY)?1:0;};
        }
    };}
    public boolean activate(ServerPlayer player) {
        if(!venus()||!player.isAlive()||player.isSpectator()||!player.mayBuild()||player.level()!=level||player.distanceToSqr(worldPosition.getCenter())>64||!level.mayInteract(player,worldPosition))return false;
        var site=site();if(site.isEmpty()||site.get().activated()||java.util.stream.IntStream.range(0,3).anyMatch(i->status(site.get(),i)!=Status.READY))return false;
        // Commit before delivery. Simultaneous clicks, console replacement and full inventory cannot repeat it.
        if(!FoundryLedger.get((ServerLevel)level).activate(site.get().anchor()))return false;
        player.getInventory().placeItemBackInInventory(new ItemStack(RecoveryContent.ROVER));
        player.getInventory().placeItemBackInInventory(new ItemStack(IndustryContent.COMPOSITE_PLATE,4));
        player.sendSystemMessage(Component.translatable("foundry.space_fabric.activated"));return true;
    }
    @Override public Component getDisplayName(){return Component.translatable("block.space_fabric.foundry_console");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new dev.vic17usd.spacefabric.menu.FoundryMenu(id,inventory,this);}
    @Override protected void saveAdditional(ValueOutput output){super.saveAdditional(output);output.putBoolean("NaturalSite",naturalSite);if(anchor!=null)output.store("RecoveryAnchor",BlockPos.CODEC,anchor);}
    @Override protected void loadAdditional(ValueInput input){super.loadAdditional(input);naturalSite=input.getBooleanOr("NaturalSite",false);anchor=input.read("RecoveryAnchor",BlockPos.CODEC).orElse(null);}
}

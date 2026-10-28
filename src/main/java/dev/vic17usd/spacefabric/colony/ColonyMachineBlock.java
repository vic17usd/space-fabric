package dev.vic17usd.spacefabric.colony;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class ColonyMachineBlock extends BaseEntityBlock {
    public static final MapCodec<ColonyMachineBlock> CODEC=simpleCodec(ColonyMachineBlock::new);
    public ColonyMachineBlock(Properties properties){super(properties);}
    @Override protected MapCodec<ColonyMachineBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ColonyMachineBlockEntity(pos,state);}
    @Override protected InteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,net.minecraft.world.InteractionHand hand,BlockHitResult hit) {
        if(dev.vic17usd.spacefabric.maintenance.MaintenanceActions.action(stack)!=null)
            return dev.vic17usd.spacefabric.maintenance.MaintenanceActions.interact(stack,level,pos,player);
        return super.useItemOn(stack,state,level,pos,player,hand,hit);
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,net.minecraft.world.entity.LivingEntity placer,net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);dev.vic17usd.spacefabric.maintenance.MaintenanceActions.restore(level,pos,stack);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine) {
            player.sendOverlayMessage(machine.status());
            if(player.isSecondaryUseActive())dev.vic17usd.spacefabric.maintenance.MaintenanceActions.inspect(machine,player);
            if(!player.isSecondaryUseActive())player.openMenu(machine);
        }
        return InteractionResult.SUCCESS;
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) {
        return level.isClientSide()?null:createTickerHelper(type,ColonyContent.MACHINE_TYPE,ColonyMachineBlockEntity::tick);
    }
}

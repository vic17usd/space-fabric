package dev.vic17usd.spacefabric.transport;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class TransportTankBlock extends BaseEntityBlock {
    private final TransportChannel channel;
    public TransportTankBlock(TransportChannel channel,Properties properties){super(properties);this.channel=channel;}
    public TransportChannel channel(){return channel;}
    public static MapCodec<TransportTankBlock> codec(TransportChannel channel){return simpleCodec(p->new TransportTankBlock(channel,p));}
    @Override protected MapCodec<TransportTankBlock> codec(){return codec(channel);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new TransportTankBlockEntity(pos,state);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(!level.isClientSide()&&level.getBlockEntity(pos) instanceof TransportTankBlockEntity tank)player.openMenu(tank);return InteractionResult.SUCCESS;
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);
        if(!level.isClientSide()&&level.getBlockEntity(pos) instanceof TransportTankBlockEntity tank){tank.buffer().restore(TransportTankItem.stored(stack));tank.setChanged();}
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide()?null:createTickerHelper(type,TransportContent.TANK_TYPE,TransportTankBlockEntity::tick);}
}

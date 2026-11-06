package dev.vic17usd.spacefabric.transport;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public final class MachineInterfaceBlock extends BaseEntityBlock {
    public static final MapCodec<MachineInterfaceBlock> CODEC=simpleCodec(MachineInterfaceBlock::new);
    public static final EnumProperty<Direction> FACING=BlockStateProperties.FACING;
    public static final BooleanProperty POWERED=BlockStateProperties.POWERED;
    public MachineInterfaceBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(POWERED,false));}
    @Override protected MapCodec<MachineInterfaceBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING,POWERED);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getClickedFace().getOpposite()).setValue(POWERED,context.getLevel().hasNeighborSignal(context.getClickedPos()));}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block source,Orientation orientation,boolean moved) {
        if(!level.isClientSide()){boolean powered=level.hasNeighborSignal(pos);if(powered!=state.getValue(POWERED))level.setBlock(pos,state.setValue(POWERED,powered),3);}
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(!level.isClientSide()) {
            if(player.isSecondaryUseActive()) {
                if(!player.mayBuild()||!level.mayInteract(player,pos))return InteractionResult.FAIL;
                var next=state.cycle(FACING);level.setBlock(pos,next,3);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("transport.space_fabric.target_face",net.minecraft.network.chat.Component.translatable("transport.space_fabric.face_"+next.getValue(FACING).getSerializedName())));
            } else if(level.getBlockEntity(pos) instanceof MachineInterfaceBlockEntity port)player.openMenu(port);
        }
        return InteractionResult.SUCCESS;
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MachineInterfaceBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide()?null:createTickerHelper(type,TransportContent.INTERFACE_TYPE,MachineInterfaceBlockEntity::tick);}
}

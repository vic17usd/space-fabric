package dev.vic17usd.spacefabric.transport;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

/** A closed valve stops material flow; the pressure-rated solid casing stays airtight in either state. */
public final class SealedBulkheadBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS=BlockStateProperties.AXIS;
    public static final BooleanProperty ENABLED=BlockStateProperties.ENABLED,POWERED=BlockStateProperties.POWERED;
    private final TransportChannel channel;
    public SealedBulkheadBlock(TransportChannel channel,Properties properties){super(properties);this.channel=channel;registerDefaultState(stateDefinition.any().setValue(AXIS,Direction.Axis.X).setValue(ENABLED,true).setValue(POWERED,false));}
    public TransportChannel channel(){return channel;}
    public static MapCodec<SealedBulkheadBlock> codec(TransportChannel channel){return simpleCodec(p->new SealedBulkheadBlock(channel,p));}
    @Override protected MapCodec<SealedBulkheadBlock> codec(){return codec(channel);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(AXIS,ENABLED,POWERED);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(AXIS,context.getClickedFace().getAxis()).setValue(POWERED,context.getLevel().hasNeighborSignal(context.getClickedPos()));}
    public static boolean open(BlockState state){return state.getValue(ENABLED)&&!state.getValue(POWERED);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block source,Orientation orientation,boolean moved) {
        if(!level.isClientSide()){boolean powered=level.hasNeighborSignal(pos);if(powered!=state.getValue(POWERED))level.setBlock(pos,state.setValue(POWERED,powered),3);}
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(AXIS,rotation.rotate(Direction.get(Direction.AxisDirection.POSITIVE,state.getValue(AXIS))).getAxis());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(!level.isClientSide()) {
            if(!player.mayBuild()||!level.mayInteract(player,pos))return InteractionResult.FAIL;
            var changed=state.cycle(ENABLED);level.setBlock(pos,changed,3);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(open(changed)?"transport.space_fabric.valve_open":"transport.space_fabric.valve_closed"));
        }
        return InteractionResult.SUCCESS;
    }
}

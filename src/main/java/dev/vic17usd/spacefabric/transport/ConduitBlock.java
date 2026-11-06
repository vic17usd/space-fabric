package dev.vic17usd.spacefabric.transport;

import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** Narrow pipes never count as pressure hull. The full sealed bulkhead is a different block. */
public final class ConduitBlock extends Block {
    public static final Map<Direction,BooleanProperty> CONNECTIONS=Map.of(Direction.DOWN,BlockStateProperties.DOWN,Direction.UP,BlockStateProperties.UP,
            Direction.NORTH,BlockStateProperties.NORTH,Direction.SOUTH,BlockStateProperties.SOUTH,Direction.WEST,BlockStateProperties.WEST,Direction.EAST,BlockStateProperties.EAST);
    private final TransportChannel channel;
    public ConduitBlock(TransportChannel channel,Properties properties){super(properties);this.channel=channel;var state=stateDefinition.any();for(var property:CONNECTIONS.values())state=state.setValue(property,false);registerDefaultState(state);}
    public TransportChannel channel(){return channel;}
    public static MapCodec<ConduitBlock> codec(TransportChannel channel){return simpleCodec(p->new ConduitBlock(channel,p));}
    @Override protected MapCodec<ConduitBlock> codec(){return codec(channel);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){CONNECTIONS.values().forEach(builder::add);}
    public static boolean attaches(BlockState state,TransportChannel channel,Direction face) {
        if(state.getBlock() instanceof ConduitBlock pipe)return pipe.channel()==channel;
        if(state.getBlock() instanceof SealedBulkheadBlock valve)return valve.channel()==channel&&state.getValue(SealedBulkheadBlock.AXIS)==face.getAxis();
        if(state.getBlock() instanceof TransportTankBlock tank)return tank.channel()==channel;
        return state.getBlock() instanceof MachineInterfaceBlock&&state.getValue(MachineInterfaceBlock.FACING)!=face;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(!context.getLevel().getFluidState(context.getClickedPos()).isEmpty())return null;
        var state=defaultBlockState();
        for(var direction:Direction.values()) {
            var pos=context.getClickedPos().relative(direction);
            state=state.setValue(CONNECTIONS.get(direction),context.getLevel().hasChunkAt(pos)&&attaches(context.getLevel().getBlockState(pos),channel,direction.getOpposite()));
        }
        return state;
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction direction,BlockPos otherPos,BlockState other,RandomSource random) {
        return state.setValue(CONNECTIONS.get(direction),attaches(other,channel,direction.getOpposite()));
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        var shape=Block.box(5,5,5,11,11,11);
        for(var direction:Direction.values())if(state.getValue(CONNECTIONS.get(direction)))shape=Shapes.or(shape,switch(direction){
            case DOWN->Block.box(5,0,5,11,5,11);case UP->Block.box(5,11,5,11,16,11);case NORTH->Block.box(5,5,0,11,11,5);
            case SOUTH->Block.box(5,5,11,11,11,16);case WEST->Block.box(0,5,5,5,11,11);case EAST->Block.box(11,5,5,16,11,11);});
        return shape;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(level instanceof net.minecraft.server.level.ServerLevel server)player.sendOverlayMessage(TransportService.describe(server,pos,channel));return InteractionResult.SUCCESS;
    }
}

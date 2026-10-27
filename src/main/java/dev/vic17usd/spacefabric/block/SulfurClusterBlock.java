package dev.vic17usd.spacefabric.block;

import com.google.gson.JsonParser;
import com.mojang.serialization.MapCodec;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** One registry ID with 21 pieces; the imported formation keeps its original geometry. */
public final class SulfurClusterBlock extends Block {
    public static final MapCodec<SulfurClusterBlock> CODEC = simpleCodec(SulfurClusterBlock::new);
    public static final IntegerProperty PIECE = IntegerProperty.create("piece", 0, 20);
    public record Piece(BlockPos offset, VoxelShape shape) { }
    public static final List<Piece> PIECES = loadPieces();

    public SulfurClusterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PIECE, 0));
    }
    @Override protected MapCodec<SulfurClusterBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(PIECE); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PIECES.get(state.getValue(PIECE)).shape();
    }
    @Override protected VoxelShape getOcclusionShape(BlockState state) { return Shapes.empty(); }

    /** Check the entire volume first. Never erase terrain, fluids, buildings or neighboring chunks. */
    public static boolean canPlace(LevelReader level, BlockPos anchor) {
        if (anchor.getY() <= level.getMinY() || anchor.getY() + 2 >= level.getMaxY()) return false;
        for (int x=0; x<3; x++) for (int z=0; z<3; z++) {
            var ground=anchor.offset(x,-1,z);
            if (!level.hasChunk(ground.getX()>>4,ground.getZ()>>4)) return false;
            var state=level.getBlockState(ground);
            if (!state.getFluidState().isEmpty() || !state.isCollisionShapeFullBlock(level,ground)) return false;
        }
        // Includes unused cells, so the silhouette cannot overlap a wall or foliage.
        for (int x=0; x<3; x++) for (int y=0; y<3; y++) for (int z=0; z<3; z++)
            if (!level.getBlockState(anchor.offset(x,y,z)).isAir()) return false;
        return true;
    }
    public static void placePieces(LevelAccessor level, BlockPos anchor) {
        for (int i=0; i<PIECES.size(); i++)
            level.setBlock(anchor.offset(PIECES.get(i).offset()),ModBlocks.SULFUR_CLUSTER.defaultBlockState().setValue(PIECE,i),2);
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);
        if (!level.isClientSide()) placePieces(level,pos);
    }
    private static List<Piece> loadPieces() {
        try (var stream=SulfurClusterBlock.class.getResourceAsStream("/data/space_fabric/sulfur_cluster.json")) {
            if (stream==null) throw new IllegalStateException("Missing sulfur outcrop geometry");
            var data=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
            var pieces=new ArrayList<Piece>();
            for (var value:data.getAsJsonArray("blocks")) {
                var part=value.getAsJsonObject();var offset=part.getAsJsonArray("offset");var shape=Shapes.empty();
                for (var valueBox:part.getAsJsonArray("shape_boxes")) {
                    var box=valueBox.getAsJsonObject();
                    shape=Shapes.or(shape,Block.box(box.get("minX").getAsDouble(),box.get("minY").getAsDouble(),box.get("minZ").getAsDouble(),
                            box.get("maxX").getAsDouble(),box.get("maxY").getAsDouble(),box.get("maxZ").getAsDouble()));
                }
                pieces.add(new Piece(new BlockPos(offset.get(0).getAsInt(),offset.get(1).getAsInt(),offset.get(2).getAsInt()),shape));
            }
            if (pieces.size()!=21 || !pieces.getFirst().offset().equals(BlockPos.ZERO)) throw new IllegalStateException("Invalid sulfur outcrop layout");
            return List.copyOf(pieces);
        } catch (java.io.IOException error) { throw new IllegalStateException("Cannot read sulfur outcrop",error); }
    }
}

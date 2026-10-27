package dev.vic17usd.spacefabric.item;

import dev.vic17usd.spacefabric.block.SulfurClusterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Native placement consumes one item only after validating the complete 3×3×3 assembly. */
public final class SulfurClusterItem extends BlockItem {
    public SulfurClusterItem(Block block, Properties properties) { super(block,properties); }
    @Override protected BlockState getPlacementState(BlockPlaceContext context) {
        var level=context.getLevel();var anchor=context.getClickedPos();var player=context.getPlayer();
        if (!SulfurClusterBlock.canPlace(level,anchor)) return null;
        for (int i=0;i<SulfurClusterBlock.PIECES.size();i++) {
            var pos=anchor.offset(SulfurClusterBlock.PIECES.get(i).offset());
            if (player!=null && (!level.mayInteract(player,pos) || !player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand()))) return null;
            var state=getBlock().defaultBlockState().setValue(SulfurClusterBlock.PIECE,i);
            var collision=player==null ? net.minecraft.world.phys.shapes.CollisionContext.empty() : net.minecraft.world.phys.shapes.CollisionContext.of(player);
            if (!level.isUnobstructed(state,pos,collision)) return null;
        }
        return super.getPlacementState(context);
    }
}

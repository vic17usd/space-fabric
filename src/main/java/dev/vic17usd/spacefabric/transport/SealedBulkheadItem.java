package dev.vic17usd.spacefabric.transport;

import dev.vic17usd.spacefabric.colony.ColonyContent;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Replace a certified panel in one block update, with no intermediate opening or lost panel. */
public final class SealedBulkheadItem extends BlockItem {
    public SealedBulkheadItem(Block block,Properties properties){super(block,properties);}
    public BlockState replacement(BlockState original,Direction face,boolean powered) {
        if(!original.is(ColonyContent.PRESSURE_HULL)&&!original.is(ColonyContent.VENUS_COMPOSITE_HULL))return null;
        return getBlock().defaultBlockState().setValue(SealedBulkheadBlock.AXIS,face.getAxis()).setValue(SealedBulkheadBlock.POWERED,powered);
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var player=context.getPlayer();if(player==null||player.isSecondaryUseActive())return super.useOn(context);
        var level=context.getLevel();var pos=context.getClickedPos();var original=level.getBlockState(pos);
        var replacement=replacement(original,context.getClickedFace(),false);
        if(replacement==null)return super.useOn(context);
        if(!(level instanceof ServerLevel server))return InteractionResult.SUCCESS;
        if(!player.mayBuild()||!server.mayInteract(player,pos)||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand())||!server.getWorldBorder().isWithinBounds(pos))return InteractionResult.FAIL;
        replacement=replacement.setValue(SealedBulkheadBlock.POWERED,server.hasNeighborSignal(pos));
        if(!server.setBlock(pos,replacement,3))return InteractionResult.FAIL;
        context.getItemInHand().consume(1,player);
        if(!player.hasInfiniteMaterials())player.getInventory().placeItemBackInInventory(new ItemStack(original.getBlock()));
        return InteractionResult.SUCCESS_SERVER;
    }
}

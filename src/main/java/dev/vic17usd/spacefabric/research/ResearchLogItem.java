package dev.vic17usd.spacefabric.research;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class ResearchLogItem extends WrittenBookItem {
    public ResearchLogItem(Properties p){super(p.component(DataComponents.WRITTEN_BOOK_CONTENT,WrittenBookContent.EMPTY));}
    @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
        if(player instanceof ServerPlayer server){
            var stack=player.getItemInHand(hand);stack.set(DataComponents.WRITTEN_BOOK_CONTENT,new WrittenBookContent(Filterable.passThrough("Proyecto Aurora"),"Space Fabric",0,ResearchSystem.pages(server).stream().map(Filterable::passThrough).toList(),true));server.containerMenu.broadcastChanges();
        }
        return super.use(level,player,hand);
    }
    @Override public InteractionResult useOn(UseOnContext context){
        if(!(context.getPlayer() instanceof ServerPlayer player))return InteractionResult.SUCCESS;
        if(player.isSecondaryUseActive()){ResearchSystem.interact(player,context.getClickedPos());return InteractionResult.SUCCESS_SERVER;}
        return use(context.getLevel(),player,context.getHand());
    }
}

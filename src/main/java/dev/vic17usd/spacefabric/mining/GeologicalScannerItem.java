package dev.vic17usd.spacefabric.mining;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class GeologicalScannerItem extends Item {
    public GeologicalScannerItem(Properties p){super(p);}
    @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
        if(player instanceof ServerPlayer server)ProspectingSystem.start(server,player.getItemInHand(hand),player.blockPosition().below());
        return InteractionResult.SUCCESS;
    }
    @Override public InteractionResult useOn(UseOnContext c){
        if(c.getPlayer() instanceof ServerPlayer server)ProspectingSystem.start(server,c.getItemInHand(),c.getClickedPos());
        return InteractionResult.SUCCESS;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,net.minecraft.world.item.component.TooltipDisplay display,java.util.function.Consumer<net.minecraft.network.chat.Component> lines,TooltipFlag flag){
        super.appendHoverText(stack,context,display,lines,flag);lines.accept(ProspectingSystem.text("scanner_hint"));
    }
}

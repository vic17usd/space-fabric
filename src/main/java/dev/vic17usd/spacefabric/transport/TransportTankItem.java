package dev.vic17usd.spacefabric.transport;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public final class TransportTankItem extends BlockItem {
    public static final String STORED="TransportTank";
    public TransportTankItem(Block block,Properties properties){super(block,properties);}
    public static TransportBuffer.State stored(ItemStack stack) {
        var data=stack.get(DataComponents.CUSTOM_DATA);
        var stored=data==null?TransportBuffer.State.EMPTY:TransportBuffer.State.CODEC.parse(NbtOps.INSTANCE,data.copyTag().getCompoundOrEmpty(STORED)).result().orElse(TransportBuffer.State.EMPTY);
        if(stack.getItem() instanceof BlockItem item&&item.getBlock() instanceof TransportTankBlock tank) {
            var normalized=new TransportBuffer(tank.channel());normalized.restore(stored);return normalized.state();
        }
        return TransportBuffer.State.EMPTY;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,TooltipDisplay display,Consumer<Component> lines,TooltipFlag flag) {
        super.appendHoverText(stack,context,display,lines,flag);var state=stored(stack);
        lines.accept(Component.translatable("transport.space_fabric.stored",Component.translatable(state.resource().key()),state.amount(),((TransportTankBlock)getBlock()).channel().capacity));
    }
}

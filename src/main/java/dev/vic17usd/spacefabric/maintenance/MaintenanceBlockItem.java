package dev.vic17usd.spacefabric.maintenance;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/** Mining preserves only service state; the native container drops its inventory separately. */
public final class MaintenanceBlockItem extends BlockItem {
    public MaintenanceBlockItem(Block block,Properties properties){super(block,properties);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,TooltipDisplay display,Consumer<Component> lines,TooltipFlag flag) {
        super.appendHoverText(stack,context,display,lines,flag);var state=MaintenanceActions.packed(stack);
        lines.accept(Component.translatable("maintenance.space_fabric.item_integrity",(MachineWear.MAX_DAMAGE-state.damage())/100));
        if(state.coating()>0)lines.accept(Component.translatable("maintenance.space_fabric.item_coating",state.coating()));
        if(state.installed())lines.accept(Component.translatable("maintenance.space_fabric.item_filter",state.filter()));
        if(state.reinforced())lines.accept(Component.translatable("maintenance.space_fabric.reinforced"));
        var kind=dev.vic17usd.spacefabric.colony.ColonyContent.kind(getBlock());
        if(kind==dev.vic17usd.spacefabric.colony.ColonyContent.Machine.ATMOSPHERIC_COLLECTOR||kind==dev.vic17usd.spacefabric.colony.ColonyContent.Machine.CERAMIC_KILN)
            lines.accept(Component.translatable("block.space_fabric."+net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(getBlock()).getPath()+".hint"));
    }
}

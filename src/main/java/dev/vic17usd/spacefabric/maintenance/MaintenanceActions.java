package dev.vic17usd.spacefabric.maintenance;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MaintenanceActions {
    public static final String PACKED="SpaceFabricMaintenance";
    private MaintenanceActions() { }
    public static MachineWear.Action action(ItemStack stack) {
        return stack.is(MaintenanceContent.REPAIR_KIT)?MachineWear.Action.REPAIR:stack.is(MaintenanceContent.COATING)?MachineWear.Action.COAT:stack.is(MaintenanceContent.FILTER)?MachineWear.Action.FILTER:null;
    }
    public static InteractionResult interact(ItemStack stack,Level level,BlockPos pos,Player player) {
        var action=action(stack);if(action==null||!(level.getBlockEntity(pos) instanceof Maintainable))return InteractionResult.PASS;
        if(level.isClientSide())return InteractionResult.SUCCESS;
        if(!player.mayBuild()||!level.mayInteract(player,pos))return InteractionResult.FAIL;
        var blockEntity=level.getBlockEntity(pos);var wear=((Maintainable)blockEntity).maintenance();
        boolean spent=action==MachineWear.Action.FILTER&&wear.state().installed();
        boolean failed=wear.failed();
        if(wear.apply(action)) {
            if(failed&&!wear.failed())((Maintainable)blockEntity).resumeAfterRepair();
            stack.consume(1,player);
            if(spent&&!player.hasInfiniteMaterials())player.getInventory().placeItemBackInInventory(new ItemStack(MaintenanceContent.SPENT_FILTER));
            blockEntity.setChanged();
            player.sendSystemMessage(Component.translatable("maintenance.space_fabric.applied_"+action.name().toLowerCase(java.util.Locale.ROOT)));
        } else player.sendOverlayMessage(Component.translatable("maintenance.space_fabric.unneeded_"+action.name().toLowerCase(java.util.Locale.ROOT)));
        inspect(blockEntity,player);return InteractionResult.SUCCESS;
    }
    public static MachineWear.State packed(ItemStack stack) {
        var data=stack.get(DataComponents.CUSTOM_DATA);
        return data==null?MachineWear.State.FRESH:MachineWear.State.CODEC.parse(NbtOps.INSTANCE,data.copyTag().getCompoundOrEmpty(PACKED)).result().orElse(MachineWear.State.FRESH);
    }
    public static void restore(Level level,BlockPos pos,ItemStack stack) {
        if(!level.isClientSide()&&level.getBlockEntity(pos) instanceof Maintainable target) {
            target.maintenance().restore(packed(stack));level.getBlockEntity(pos).setChanged();
        }
    }
    public static void inspect(BlockEntity blockEntity,Player player) {
        var wear=((Maintainable)blockEntity).maintenance();var state=wear.state();
        player.sendSystemMessage(Component.translatable("maintenance.space_fabric.inspect",blockEntity.getBlockState().getBlock().getName(),wear.integrity()/100,
                state.coating(),MachineWear.MAX_COATING,state.filter(),MachineWear.FILTER_CHARGE,Component.translatable("maintenance.space_fabric.risk_"+wear.risk().name().toLowerCase(java.util.Locale.ROOT))));
    }
}

package dev.vic17usd.spacefabric.machine;

import dev.vic17usd.spacefabric.menu.RefineryMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class FuelRefineryBlockEntity extends BaseContainerBlockEntity implements ExtendedMenuProvider<BlockPos>, WorldlyContainer {
    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final RefineryProcess process = new RefineryProcess();
    public final ContainerData data = new ContainerData() {
        @Override public int get(int index) { return index == 0 ? process.progress() : process.duration(); }
        @Override public void set(int index, int value) { }
        @Override public int getCount() { return 2; }
    };
    public FuelRefineryBlockEntity(BlockPos pos, BlockState state) { super(ModMachines.FUEL_REFINERY, pos, state); }
    public static void tick(Level level, BlockPos pos, BlockState state, FuelRefineryBlockEntity refinery) {
        if (refinery.process.tick(refinery)) refinery.setChanged();
    }
    @Override public int getContainerSize() { return 4; }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override protected Component getDefaultName() { return Component.translatable("block.space_fabric.fuel_refinery"); }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return new RefineryMenu(id, inventory, this, data); }
    @Override public BlockPos getScreenOpeningData(ServerPlayer player) { return worldPosition; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return RefineryProcess.accepts(slot, stack); }
    @Override public int[] getSlotsForFace(Direction direction) { return direction == Direction.DOWN ? new int[]{3} : direction == Direction.UP ? new int[]{0} : new int[]{1, 2}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == 3; }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input); items = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        process.restore(input.getIntOr("Progress", 0), input.getIntOr("Recipe", 0));
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output); ContainerHelper.saveAllItems(output, items);
        output.putInt("Progress", process.progress()); output.putInt("Recipe", process.recipe());
    }
}

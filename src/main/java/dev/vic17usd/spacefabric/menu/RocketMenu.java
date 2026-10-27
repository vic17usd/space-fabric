package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.space.RocketEntity;
import dev.vic17usd.spacefabric.space.TravelDestination;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class RocketMenu extends AbstractContainerMenu {
    public static final int LAUNCH = 100, BOARD = 101, DATA_COUNT=12;
    private final RocketEntity rocket;
    private final ContainerData data;
    public RocketMenu(int id, Inventory inventory) { this(id, inventory, null, new SimpleContainer(11), new SimpleContainerData(DATA_COUNT)); }
    public RocketMenu(int id, Inventory inventory, RocketEntity rocket) {
        this(id, inventory, rocket, rocket.inventory(), new ContainerData() {
            @Override public int get(int index) { return switch (index) {
                case 0 -> rocket.fuel(); case 1 -> rocket.destinationIndex(); case 2 -> rocket.flightState();
                case 3 -> rocket.requiredFuel(); case 4 -> rocket.isPilot(inventory.player) ? 1 : 0;
                case 5 -> rocket.level()==null?-1:TravelDestination.indexOf(rocket.level().dimension());
                case 6 -> rocket.variant().ordinal(); case 7 -> rocket.hull();
                case 8 -> dev.vic17usd.spacefabric.space.RocketFlightConfig.exclusive()?1:0;
                case 9 -> rocket.fuelCapacity(); case 10 -> rocket.isVehicle()?1:0; case 11 -> 1; default -> 0;
            }; }
            @Override public void set(int index, int value) { }
            @Override public int getCount() { return DATA_COUNT; }
        });
    }
    private RocketMenu(int id, Inventory inventory, RocketEntity rocket, Container storage, ContainerData data) {
        super(ModMenus.ROCKET, id); this.rocket = rocket; this.data = data;
        addSlot(new Slot(storage, 0, 196, 138) { @Override public boolean mayPlace(ItemStack stack) { return stack.is(ModItems.ROCKET_FUEL_CELL); } });
        addSlot(new Slot(storage, 1, 224, 138) { @Override public boolean mayPlace(ItemStack stack) { return false; } });
        for (int i = 0; i < 9; i++) addSlot(new Slot(storage, i + 2, 8 + i * 18, 138));
        addStandardInventorySlots(inventory, 8, 164); addDataSlots(data);
    }
    public int value(int index) { return data.get(index); }
    public dev.vic17usd.spacefabric.space.RocketNavigation.State state() {
        return new dev.vic17usd.spacefabric.space.RocketNavigation.State(dev.vic17usd.spacefabric.space.RocketVariant.byId(value(6)),
            value(5),value(1),value(2),value(0),value(3),value(9),value(7),value(4)==1,value(10)==1,value(8)==1,value(11)==1);
    }
    @Override public boolean stillValid(Player player) {
        return rocket == null || (player.isAlive()&&!player.isSpectator()&&!rocket.isRemoved() && player.level() == rocket.level() && player.distanceToSqr(rocket) <= 64);
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (rocket == null || !stillValid(player) || !(player instanceof ServerPlayer server)) return false;
        if (button == BOARD) return rocket.navigation(player).board()==dev.vic17usd.spacefabric.space.RocketNavigation.Reason.READY && player.startRiding(rocket);
        if (button == LAUNCH) return rocket.requestLaunch(server);
        return rocket.selectDestination(player, button);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var copy = stack.copy();
        boolean moved = index < 11 ? moveItemStackTo(stack, 11, slots.size(), true)
                : stack.is(ModItems.ROCKET_FUEL_CELL) ? moveItemStackTo(stack, 0, 1, false) : moveItemStackTo(stack, 2, 11, false);
        if (!moved) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack); return copy;
    }
}

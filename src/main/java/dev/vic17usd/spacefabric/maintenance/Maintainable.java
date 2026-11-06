package dev.vic17usd.spacefabric.maintenance;

/** Shared by refinery and colony block entities, without changing their saved inventories. */
public interface Maintainable {
    MachineWear maintenance();
    default void resumeAfterRepair() { }
}

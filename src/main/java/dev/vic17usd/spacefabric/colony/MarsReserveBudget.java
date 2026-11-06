package dev.vic17usd.spacefabric.colony;

/** Pure planning calculation. Storage and network availability must be checked separately. */
public final class MarsReserveBudget {
    public static final int NIGHT_TICKS = 12_000;
    public static final int TICKS_PER_SUPPORT_CYCLE = 20;
    private MarsReserveBudget() { }

    public record Requirement(long oxygen, long energy, int water) { }

    /** Only the controller's internal budgets and inputs, with return-slot capacity checked. */
    public record ControllerStock(long oxygen, long energy, long water) { }
    public static ControllerStock controllerStock(int oxygen, int energy, int oxygenInputs, int energyInputs,
                                                   int waterInputs, int oxygenReturnCapacity, int energyReturnCapacity) {
        if (oxygen < 0 || energy < 0 || oxygenInputs < 0 || energyInputs < 0 || waterInputs < 0
                || oxygenReturnCapacity < 0 || energyReturnCapacity < 0) throw new IllegalArgumentException("negative stock");
        return new ControllerStock(oxygen + (long)Math.min(oxygenInputs, oxygenReturnCapacity) * LifeSupport.CELL,
                energy + (long)Math.min(energyInputs, energyReturnCapacity) * LifeSupport.CELL,
                (long)waterInputs * dev.vic17usd.spacefabric.transport.TransportResource.WATER.quantum);
    }

    /** One complete night, rounded up with a 25% running margin; startup is a separate one-off cost. */
    public static Requirement requirement(int volume, int occupants, boolean alreadyPressurized) {
        if (volume < 1 || volume > SealScanner.MAX_VOLUME || occupants < 0)
            throw new IllegalArgumentException("Invalid habitat volume or occupancy");
        long cycles = (NIGHT_TICKS + TICKS_PER_SUPPORT_CYCLE - 1L) / TICKS_PER_SUPPORT_CYCLE;
        long oxygen = (cycles * occupants * 5L + 3) / 4;
        long energy = (cycles * ColonyBalance.TEMPERATE_POWER_PER_SECOND * 5L + 3) / 4;
        if (!alreadyPressurized) oxygen += 2L * volume;
        return new Requirement(oxygen, energy, MarsExpeditionChecks.WATER);
    }
}

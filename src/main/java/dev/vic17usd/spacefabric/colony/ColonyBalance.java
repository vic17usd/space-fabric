package dev.vic17usd.spacefabric.colony;

/** Shared game-unit costs and a conservative, local life-support packing list. */
public final class ColonyBalance {
    public static final int VENUS_POWER_PER_SECOND = 15;
    public static final int VENUS_COOLANT_PER_SECOND = 5;
    public static final int TEMPERATE_POWER_PER_SECOND = 5;

    public record Supplies(int oxygen, int power, int water) { }

    private ColonyBalance() { }

    /** Includes one initial pressurization. Industry, vehicles, suits and leaks are additional. */
    public static Supplies supplies(int seconds, int occupants, int volume) {
        if (seconds < 1 || seconds > 14_400 || occupants < 1 || occupants > 16
                || volume < 1 || volume > SealScanner.MAX_VOLUME) {
            throw new IllegalArgumentException("Invalid Venus provisioning request");
        }
        return new Supplies(cells((long) seconds * occupants + 2L * volume),
                cells((long) seconds * VENUS_POWER_PER_SECOND),
                cells((long) seconds * VENUS_COOLANT_PER_SECOND));
    }

    private static int cells(long units) {
        return (int) ((units + LifeSupport.CELL - 1) / LifeSupport.CELL);
    }
}

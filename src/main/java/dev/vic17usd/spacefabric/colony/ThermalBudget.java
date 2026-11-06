package dev.vic17usd.spacefabric.colony;

/** Thermal accounting in game units. One step represents one server second. */
public final class ThermalBudget {
    public static final int CAPACITY = 10_000;
    public static final int WARNING = 7_000;
    public static final int TRIP = 9_000;
    public static final int RESET = 5_000;

    private int heat;
    private boolean tripped;

    public int heat() { return heat; }
    public boolean warning() { return heat >= WARNING || tripped; }
    public boolean tripped() { return tripped; }

    /** Restore both values; a saved trip stays active until cooled below RESET. */
    public void restore(int heat, boolean tripped) {
        this.heat = Math.clamp(heat, 0, CAPACITY);
        this.tripped = this.heat >= TRIP || (tripped && this.heat > RESET);
    }

    /** Add ambient and machine heat, then remove available cooling capacity. */
    public void tick(int ambient, int machines, int cooling) {
        long added = (long) Math.max(0, ambient) + Math.max(0, machines);
        long next = (long) heat + added - Math.max(0, cooling);
        heat = (int) Math.clamp(next, 0L, (long) CAPACITY);
        if (heat >= TRIP) tripped = true;
        else if (heat <= RESET) tripped = false;
    }
}

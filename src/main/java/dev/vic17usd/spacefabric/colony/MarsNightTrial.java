package dev.vic17usd.spacefabric.colony;

/** Deterministic trial bookkeeping. The server must supply loaded, live habitat evidence on every tick. */
public final class MarsNightTrial {
    public static final int DAY = 24000, DUSK = 12000, NIGHT = DAY - DUSK;
    public enum Status { WAITING, RUNNING, FAILED, COMPLETE }
    public record Sample(long gameTime, long clockTime, boolean habitatReady) { }
    public record State(Status status, long lastGameTime, long lastClockTime, int elapsedTicks) {
        public static State waiting() { return new State(Status.WAITING, -1, -1, 0); }
    }
    public record Reserves(int oxygen, int energy, int water, boolean fitsController) { }
    private MarsNightTrial() { }

    /** Already pressurized, continuous operation, plus 25% of one night as a reserve margin. */
    public static Reserves reserves(int occupants) {
        if (occupants < 0 || occupants > 16) throw new IllegalArgumentException("occupants");
        int seconds = NIGHT / 20;
        int oxygen = (seconds * occupants * 5 + 3) / 4;
        int energy = (seconds * ColonyBalance.TEMPERATE_POWER_PER_SECOND * 5 + 3) / 4;
        int water = MarsExpeditionChecks.WATER;
        return new Reserves(oxygen, energy, water, oxygen <= LifeSupport.CAPACITY && energy <= LifeSupport.CAPACITY);
    }

    /** Time jumps, unloaded/unprotected rooms and discontinuities invalidate a run. */
    public static State advance(State state, Sample sample) {
        if (state.status() == Status.COMPLETE || state.status() == Status.FAILED) return state;
        long phase = Math.floorMod(sample.clockTime(), DAY);
        if (state.status() == Status.WAITING) {
            if (phase != DUSK || !sample.habitatReady()) return state;
            return new State(Status.RUNNING, sample.gameTime(), sample.clockTime(), 0);
        }
        if (!sample.habitatReady() || sample.gameTime() != state.lastGameTime() + 1
                || sample.clockTime() != state.lastClockTime() + 1) return new State(Status.FAILED, -1, -1, 0);
        int elapsed = state.elapsedTicks() + 1;
        if (elapsed == NIGHT && phase == 0) return new State(Status.COMPLETE, sample.gameTime(), sample.clockTime(), elapsed);
        if (elapsed >= NIGHT || phase < DUSK) return new State(Status.FAILED, -1, -1, 0);
        return new State(Status.RUNNING, sample.gameTime(), sample.clockTime(), elapsed);
    }
}

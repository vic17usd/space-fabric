package dev.vic17usd.spacefabric.client.colony;

/** Latest-request gate shared by the screen and its race/timeout tests. No recipes are synthesized here. */
public final class MarsGuideRecipeRequest {
    public static final long TIMEOUT_MS = 10_000;
    private int sequence;
    private long started;
    private boolean pending;
    public void begin(int sequence, long now, boolean supported) { this.sequence = sequence; started = now; pending = supported; }
    public boolean pending() { return pending; }
    public boolean accept(int sequence, long now) {
        if (!pending || this.sequence != sequence || now - started > TIMEOUT_MS) return false;
        pending = false; return true;
    }
    public boolean expire(long now) {
        if (!pending || now - started <= TIMEOUT_MS) return false;
        pending = false; return true;
    }
}

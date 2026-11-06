package dev.vic17usd.spacefabric.colony;

/** Client display cache, never mission authority. Expiry prevents stale checks after unloading or respawn. */
public final class MarsMissionTracker {
    private MarsGuideNetwork.State state = MarsGuideNetwork.State.EMPTY;
    private int age = 61;
    public void accept(MarsGuideNetwork.State value) { state = value; age = 0; }
    public void tick() { if (age <= 60) age++; }
    public void clear() { state = MarsGuideNetwork.State.EMPTY; age = 61; }
    public boolean available() { return age <= 60; }
    public boolean ready() { return age <= 60 && state.has(MarsProgress.ARRIVAL); }
    public MarsGuideNetwork.State state() { return state; }
}

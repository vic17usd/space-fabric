package dev.vic17usd.spacefabric.environment;

/** Venus surface activity is now handled by the shared, telegraphed volcanic event system. */
public final class VenusActivity {
    private VenusActivity() { }
    public static void initialize(){dev.vic17usd.spacefabric.environment.volcanism.VolcanicSystem.initialize();}
}

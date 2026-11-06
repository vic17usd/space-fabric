package dev.vic17usd.spacefabric.environment;

/** Reglas puras: segundos de simulación bajo lluvia, reinicio al refugiarse y tres grados de corrosión. */
public final class AcidExposure {
    private AcidExposure() { }
    public static int nextSeconds(int previous, boolean exposed) {
        if (!exposed) return 0;
        return previous == Integer.MAX_VALUE ? Integer.MAX_VALUE - 4 : Math.max(0, previous) + 1;
    }
    public static int stage(int seconds) { return seconds >= 45 ? 2 : seconds >= 15 ? 1 : 0; }
    public static int armorDamage(int seconds) { return seconds > 0 && seconds % 5 == 0 ? 1 + stage(seconds) : 0; }
}

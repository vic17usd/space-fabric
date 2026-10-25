package dev.vic17usd.spacefabric.planet;

/** Modelo de jugabilidad global; no simula temperatura, altitud ni composición química. */
public record Atmosphere(boolean breathable, Pressure pressure) {
    public enum Pressure {
        VACUUM, THIN, NORMAL, CRUSHING;

        public boolean requiresPressureSuit() {
            return this != NORMAL;
        }
    }
}

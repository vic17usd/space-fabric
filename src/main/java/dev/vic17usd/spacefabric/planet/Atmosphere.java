package dev.vic17usd.spacefabric.planet;

/** Categorías heredadas. La exposición del jugador utiliza AtmosphereProfile y PressureExposure. */
public record Atmosphere(boolean breathable, Pressure pressure) {
    public enum Pressure {
        VACUUM, THIN, NORMAL, CRUSHING;

        public boolean requiresPressureSuit() {
            return this != NORMAL;
        }
    }
}

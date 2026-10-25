package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.planet.Atmosphere;
import java.util.Optional;

/** Decisión pura, separada del tick y del motor de daño para poder probarla. */
public final class Exposure {
    public enum Hazard {
        DECOMPRESSION, CRUSHING_PRESSURE, HYPOXIA
    }

    private Exposure() {
    }

    public static Optional<Hazard> hazard(Atmosphere atmosphere, boolean sealed, boolean immune) {
        if (sealed || immune) {
            return Optional.empty();
        }
        // Un solo golpe por intervalo: prioriza la presión para evitar daño doble.
        return switch (atmosphere.pressure()) {
            case VACUUM, THIN -> Optional.of(Hazard.DECOMPRESSION);
            case CRUSHING -> Optional.of(Hazard.CRUSHING_PRESSURE);
            case NORMAL -> atmosphere.breathable() ? Optional.empty() : Optional.of(Hazard.HYPOXIA);
        };
    }
}

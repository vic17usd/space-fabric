package dev.vic17usd.spacefabric.atmosphere;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

/** Los JSON registran los tipos dinámicos; Java guarda sus claves y obtiene los holders activos. */
public final class PlanetDamageTypes {
    private static final ResourceKey<DamageType> DECOMPRESSION = key("decompression");
    private static final ResourceKey<DamageType> CRUSHING_PRESSURE = key("crushing_pressure");
    private static final ResourceKey<DamageType> THERMAL_EXPOSURE = key("thermal_exposure");
    private static final ResourceKey<DamageType> HYPOXIA = key("hypoxia");

    private PlanetDamageTypes() {
    }

    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, SpaceFabric.id(path));
    }

    public static DamageSource source(ServerLevel level, Exposure.Hazard hazard) {
        var type = switch (hazard) {
            case DECOMPRESSION -> DECOMPRESSION;
            case CRUSHING_PRESSURE -> CRUSHING_PRESSURE;
            case HYPOXIA -> HYPOXIA;
            case THERMAL_EXPOSURE -> THERMAL_EXPOSURE;
        };
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type));
    }
}

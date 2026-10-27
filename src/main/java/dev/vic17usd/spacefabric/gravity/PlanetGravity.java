package dev.vic17usd.spacefabric.gravity;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Afecta a jugadores y otras entidades vivas con el atributo vanilla de gravedad. */
public final class PlanetGravity {
    private static final Identifier MODIFIER_ID = SpaceFabric.id("planet_gravity");

    private PlanetGravity() {
    }

    public static void initialize() {
        // Incluye conexión, mobs nuevos, carga de chunks y recreación de entidades.
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof LivingEntity living) {
                apply(living);
            }
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(
                (player, origin, destination) -> apply(player));
        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register(
                (original, replacement, origin, destination) -> {
                    if (replacement instanceof LivingEntity living) {
                        apply(living);
                    }
                });
        ServerPlayerEvents.AFTER_RESPAWN.register((previous, player, alive) -> apply(player));
    }

    private static void apply(LivingEntity entity) {
        var gravity = entity.getAttribute(Attributes.GRAVITY);
        if (gravity == null) {
            return;
        }

        double scale = PlanetDimensions.find(entity.level()).map(Planet::gravityScale).orElse(1.0);
        applyToAttribute(gravity, scale);
    }

    /** Comparte la misma operación para todas las entidades vivas, incluidos los colonos marcianos. */
    public static void applyToAttribute(AttributeInstance gravity, double scale) {
        var existing = gravity.getModifier(MODIFIER_ID);
        double amount = scale - 1.0;
        if (existing != null && Double.compare(existing.amount(), amount) == 0) {
            return;
        }

        // Retira solo nuestro modificador; conserva atributos base y cambios de otros mods.
        gravity.removeModifier(MODIFIER_ID);
        if (scale != 1.0) {
            gravity.addTransientModifier(new AttributeModifier(
                    MODIFIER_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }
}

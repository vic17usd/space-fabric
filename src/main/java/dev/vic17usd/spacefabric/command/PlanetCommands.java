package dev.vic17usd.spacefabric.command;

import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Diagnóstico de lectura. El viaje se prueba con /execute in ... run tp ... de vanilla. */
public final class PlanetCommands {
    private PlanetCommands() {
    }

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) ->
                dispatcher.register(Commands.literal("planetinfo").executes(command -> {
                    var player = command.getSource().getPlayerOrException();
                    var planet = PlanetDimensions.find(player.level());
                    String message = planet.map(value -> "Planeta: " + value.path()
                                    + " | gravedad relativa: " + value.gravityScale()
                                    + " | respirable: " + value.atmosphere().breathable()
                                    + " | presión: " + value.atmosphere().pressure())
                            .orElse("Dimensión no planetaria: física y atmósfera vanilla");
                    double gravity = player.getAttributeValue(Attributes.GRAVITY);
                    command.getSource().sendSuccess(
                            () -> Component.literal(message + " | atributo de gravedad: " + gravity), false);
                    return 1;
                })));
    }
}

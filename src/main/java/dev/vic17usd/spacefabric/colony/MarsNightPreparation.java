package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.planet.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** Explicit preparation only: this does not start or advance MarsNightTrial. */
final class MarsNightPreparation {
    private MarsNightPreparation() { }
    static int request(ServerPlayer player) {
        var level = player.level(); var progress = MarsProgress.get(level);
        if (PlanetDimensions.find(level).orElse(null) != Planet.MARS || !player.isAlive()
                || player.isSpectator() || player.isPassenger() || !player.mayBuild()
                || progress.autonomyStatus(player.getUUID()) != MarsProgress.AutonomyStatus.ACCEPTED
                || progress.production(player.getUUID()).flags() != 7 || progress.autonomyRefuge(player.getUUID()).isEmpty())
            return message(player, "requirements", 0);
        var pos = progress.autonomyRefuge(player.getUUID()).orElseThrow().pos();
        if (!level.mayInteract(player, pos)) return message(player, "permission", 0);
        if (!MarsAutonomyRefuge.show(player, progress)) return message(player, "not_ready", 0);
        progress.requestNight(player.getUUID());
        return message(player, "requested", 1);
    }
    private static int message(ServerPlayer player, String key, int result) {
        player.sendSystemMessage(Component.translatable("mars.night." + key)); return result;
    }
}

package dev.vic17usd.spacefabric.mixin;

import dev.vic17usd.spacefabric.environment.PlanetWeather;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.WeatherData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerLevel.class, remap = false)
public abstract class ServerLevelWeatherMixin {
    @Inject(method = "getWeatherData", at = @At("HEAD"), cancellable = true)
    private void spaceFabric$weather(CallbackInfoReturnable<WeatherData> callback) {
        var level = (ServerLevel) (Object) this;
        PlanetDimensions.find(level).ifPresent(planet -> callback.setReturnValue(PlanetWeather.data(level, planet)));
    }

    @Inject(method = "advanceWeatherCycle", at = @At("HEAD"), cancellable = true)
    private void spaceFabric$advance(CallbackInfo callback) {
        var level = (ServerLevel) (Object) this;
        PlanetDimensions.find(level).ifPresent(planet -> {
            PlanetWeather.tick(level, planet);
            callback.cancel();
        });
    }

    @Redirect(method = "advanceWeatherCycle", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void spaceFabric$keepVanillaWeatherOutOfPlanets(PlayerList players, Packet<?> packet) {
        // El Overworld vanilla difunde algunos eventos a TODO el servidor. Los planetas reciben los suyos.
        for (var player : players.getPlayers()) {
            if (PlanetDimensions.find(player.level()).isEmpty()) player.connection.send(packet);
        }
    }
}

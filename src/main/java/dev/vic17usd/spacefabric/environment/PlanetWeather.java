package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.saveddata.WeatherData;

/** WeatherData independiente por destino; nunca modifica ni difunde el clima global del Overworld. */
public final class PlanetWeather {
    private static final Map<Planet, SavedDataType<WeatherData>> TYPES = new EnumMap<>(Planet.class);
    static {
        for (Planet planet : Planet.values()) TYPES.put(planet, new SavedDataType<>(
                SpaceFabric.id("weather_" + planet.path()), () -> initial(planet), WeatherData.CODEC,
                WeatherData.TYPE.dataFixType()));
    }

    private PlanetWeather() { }
    private static WeatherData initial(Planet planet) {
        var data = new WeatherData();
        data.setRainTime(StormSchedule.of(planet).clearMin());
        return data;
    }
    public static WeatherData data(ServerLevel level, Planet planet) {
        return level.getServer().getDataStorage().computeIfAbsent(TYPES.get(planet));
    }

    public static boolean hasStorms(Planet planet) {
        return planet == Planet.VENUS || planet == Planet.MARS || planet == Planet.EARTH;
    }

    public static void tick(ServerLevel level, Planet planet) {
        WeatherData weather = data(level, planet);
        boolean previous = level.isRaining();
        if (!hasStorms(planet)) {
            weather.setRaining(false);
            weather.setThundering(false);
        } else if (level.getGameRules().get(GameRules.ADVANCE_WEATHER)) {
            if (weather.getClearWeatherTime() > 0) {
                weather.setClearWeatherTime(weather.getClearWeatherTime() - 1);
                weather.setRaining(false);
                weather.setThundering(false);
            } else if (weather.getRainTime() > 0) {
                weather.setRainTime(weather.getRainTime() - 1);
            } else {
                boolean storm = !weather.isRaining();
                weather.setRaining(storm);
                weather.setThundering(false);
                var schedule = StormSchedule.of(planet);
                int minimum = storm ? schedule.stormMin() : schedule.clearMin();
                int maximum = storm ? schedule.stormMax() : schedule.clearMax();
                weather.setRainTime(minimum + level.getRandom().nextInt(maximum - minimum + 1));
            }
        }
        level.setRainLevel(approach(level.getRainLevel(1), weather.isRaining() ? 1 : 0));
        level.setThunderLevel(approach(level.getThunderLevel(1), weather.isThundering() && planet != Planet.MARS ? 1 : 0));
        boolean raining = level.isRaining();
        if (raining != previous) send(level, new ClientboundGameEventPacket(raining
                ? ClientboundGameEventPacket.START_RAINING : ClientboundGameEventPacket.STOP_RAINING, 0));
        if (level.getGameTime() % 20 == 0 || raining != previous) {
            send(level, new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, level.getRainLevel(1)));
            send(level, new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, level.getThunderLevel(1)));
        }
    }

    private static float approach(float current, float target) {
        return current < target ? Math.min(target, current + 0.01F) : Math.max(target, current - 0.01F);
    }

    private static void send(ServerLevel level, ClientboundGameEventPacket packet) {
        for (var player : level.players()) player.connection.send(packet);
    }
}

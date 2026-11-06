package dev.vic17usd.spacefabric.atmosphere;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import net.fabricmc.loader.api.FabricLoader;

/** Una escala común para fisiología; independiente de los días solares de cada planeta. */
public final class AtmosphereConfig {
    private static double scale=72;
    private AtmosphereConfig() { }
    public static double scale(){return scale;}
    public static void initialize() {
        var file=FabricLoader.getInstance().getConfigDir().resolve("space-fabric-atmosphere.json");
        try {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.writeString(file,"{\n  \"real_seconds_per_game_second\": 72\n}\n");
            }
            double value=JsonParser.parseString(Files.readString(file)).getAsJsonObject().get("real_seconds_per_game_second").getAsDouble();
            if (!Double.isFinite(value)||value<=0||value>3600) throw new IllegalArgumentException("Escala fuera de (0,3600]");
            scale=value;
        } catch(Exception error) {throw new IllegalStateException("Configuración atmosférica inválida: "+file,error);}
    }
}

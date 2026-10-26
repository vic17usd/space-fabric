package dev.vic17usd.spacefabric.space;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import net.fabricmc.loader.api.FabricLoader;

public final class RocketFlightConfig {
    private static boolean exclusive;
    private RocketFlightConfig() { }
    public static boolean exclusive(){return exclusive;}
    public static void initialize() {
        var file=FabricLoader.getInstance().getConfigDir().resolve("space-fabric-rockets.json");
        try {
            if(!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.writeString(file,"{\n  \"exclusive_destinations\": false\n}\n");
            }
            exclusive=JsonParser.parseString(Files.readString(file)).getAsJsonObject().get("exclusive_destinations").getAsBoolean();
        }catch(Exception error){throw new IllegalStateException("Configuración de cohetes inválida: "+file,error);}
    }
}

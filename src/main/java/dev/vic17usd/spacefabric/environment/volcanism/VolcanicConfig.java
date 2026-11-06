package dev.vic17usd.spacefabric.environment.volcanism;

import com.google.gson.*;
import java.nio.file.Files;
import net.fabricmc.loader.api.FabricLoader;

public final class VolcanicConfig {
    public record Settings(boolean enabled,boolean damage) { }
    private static Settings settings=new Settings(true,true);
    private VolcanicConfig() { }
    public static Settings settings(){return settings;}
    public static Settings parse(String text){
        var object=JsonParser.parseString(text).getAsJsonObject();return new Settings(bool(object,"enabled"),bool(object,"damage"));
    }
    private static boolean bool(JsonObject object,String key){
        if(!object.has(key))return true;
        var value=object.get(key);if(!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isBoolean())throw new IllegalArgumentException("Expected boolean: "+key);return value.getAsBoolean();
    }
    public static void initialize(){
        var file=FabricLoader.getInstance().getConfigDir().resolve("space-fabric-volcanism.json");
        try{if(!Files.exists(file)){Files.createDirectories(file.getParent());Files.writeString(file,"{\n  \"enabled\": true,\n  \"damage\": true\n}\n");}settings=parse(Files.readString(file));}
        catch(Exception failure){throw new IllegalStateException("Invalid volcanic configuration: "+file,failure);}
    }
}

package dev.vic17usd.spacefabric.client.imports;

import com.google.gson.*;
import java.io.StringReader;
import java.nio.file.*;
import java.util.Base64;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.sprite.TextureSlots;

/** Parse the actual native models and verify the supplied asset bytes; no source JAR is run. */
public final class ModBatchCheck {
    private static void checkTextureFiles(Path root, JsonObject model, String target) throws Exception {
        for (var entry : model.getAsJsonObject("textures").entrySet()) {
            String texture = entry.getValue().getAsString();
            if (texture.startsWith("#") || texture.startsWith("minecraft:")) continue;
            if (!texture.startsWith("space_fabric:block/") && !texture.startsWith("space_fabric:item/"))
                throw new AssertionError("Texture outside the supplied block atlas: " + target + " -> " + texture);
            var png = root.resolve("textures/" + texture.split(":", 2)[1] + ".png");
            if (!Files.isRegularFile(png) || javax.imageio.ImageIO.read(png.toFile()) == null)
                throw new AssertionError("Missing or invalid texture PNG: " + target + " -> " + texture);
        }
    }
    public static void main(String[] args) throws Exception {
        var root=Path.of("src/main/resources/assets/space_fabric");var cache=Path.of("tools/import_sources/mod_batch");
        var manifest=JsonParser.parseString(Files.readString(Path.of("third_party/mod_batch/manifest.json"))).getAsJsonObject();
        int blocks=0,items=0,models=0;
        for(var pair:manifest.entrySet()){
            String name=pair.getKey();var value=pair.getValue().getAsJsonObject();
            if(!value.has("target"))continue;String target=value.get("target").getAsString();
            if(name.endsWith(".jar")){
                blocks++;var path=root.resolve("models/block/"+target+".json");var json=JsonParser.parseString(Files.readString(path)).getAsJsonObject();
                var model=CuboidModel.fromStream(new StringReader(Files.readString(path)));
                checkTextureFiles(root,json,target);
                var textures=json.getAsJsonObject("textures");var slots=new TextureSlots.Resolver().addLast(model.textureSlots()).resolve(()->target);
                for(String key:textures.keySet())if(slots.getMaterial("#"+key)==null)throw new AssertionError("Unresolved texture: "+target+" #"+key);
                try(var paths=Files.walk(cache.resolve("blocks").resolve(target).resolve("textures"))){
                    for(var original:paths.filter(p->p.toString().endsWith(".png")).toList()){
                        var copied=root.resolve("textures/block/replacements/"+target).resolve(cache.resolve("blocks").resolve(target).resolve("textures").relativize(original));
                        if(Files.mismatch(original,copied)!=-1)throw new AssertionError("Modified texture: "+target);
                    }
                }
            } else if(name.endsWith(".png")){
                items++;var original=cache.resolve("items").resolve(name);var icon=root.resolve("textures/item/"+target+".png");
                if(Files.mismatch(original,icon)!=-1)throw new AssertionError("Modified item texture: "+name);
                var model=JsonParser.parseString(Files.readString(root.resolve("models/item/"+target+".json"))).getAsJsonObject();
                if(!model.getAsJsonObject("textures").get("layer0").getAsString().equals("space_fabric:item/"+target))throw new AssertionError("Wrong item model: "+target);
            } else if(name.endsWith(".bbmodel")){
                models++;var raw=JsonParser.parseString(Files.readString(cache.resolve("models").resolve(name))).getAsJsonObject();
                var json=JsonParser.parseString(Files.readString(root.resolve("models/block/"+target+".json"))).getAsJsonObject();
                CuboidModel.fromStream(new StringReader(json.toString()));
                checkTextureFiles(root,json,target);
                if(raw.getAsJsonArray("elements").size()!=json.getAsJsonArray("elements").size())throw new AssertionError("Lost model elements: "+target);
                var data=raw.getAsJsonArray("textures").get(0).getAsJsonObject().get("source").getAsString().split(",",2)[1];
                if(!java.util.Arrays.equals(Base64.getDecoder().decode(data),Files.readAllBytes(root.resolve("textures/block/replacements/"+target+"/atlas.png"))))throw new AssertionError("Changed model atlas: "+target);
            }
        }
        if(blocks!=30||items!=21||models!=2)throw new AssertionError("Missing replacements: "+blocks+","+items+","+models);
        System.out.println("OK: 30 native block models, 21 original item textures, two Blockbench models and their original atlases.");
    }
}

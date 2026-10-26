package dev.vic17usd.spacefabric.client.rocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import dev.vic17usd.spacefabric.space.RocketVariant;

/** Native model parsing and face-slot resolution; catches the mixed-atlas failure of 0.17/0.18. */
public final class RocketItemResourcesCheck {
    private static final Path ASSETS=Path.of("src/main/resources/assets/space_fabric");
    private static void validate(JsonObject json) {
        var model=CuboidModel.fromStream(new StringReader(json.toString()));
        var slots=new TextureSlots.Resolver().addLast(model.textureSlots()).resolve(()->"rocket inventory regression");
        var atlases=new HashSet<String>();
        for(var element:json.getAsJsonArray("elements")) {
            for(var face:element.getAsJsonObject().getAsJsonObject("faces").entrySet()) {
                String reference=face.getValue().getAsJsonObject().get("texture").getAsString();
                var material=slots.getMaterial(reference);
                if(material==null)throw new AssertionError("Unresolved native texture slot: "+reference);
                Identifier sprite=material.sprite();
                // The vanilla 26.1 atlases load item/ and block/ into separate directories.
                String atlas=sprite.getPath().startsWith("item/")?"items":sprite.getPath().startsWith("block/")?"blocks":"unsupported";
                if(atlas.equals("unsupported"))throw new AssertionError("Sprite outside a supported atlas: "+sprite);
                atlases.add(atlas);
                if(sprite.getNamespace().equals("space_fabric")&&!Files.isRegularFile(ASSETS.resolve("textures/"+sprite.getPath()+".png")))
                    throw new AssertionError("Missing local sprite: "+sprite);
            }
        }
        // CuboidItemModelWrapper.validateAtlasUsage rejects even valid sprites if their atlases differ.
        if(atlases.size()!=1)throw new IllegalStateException("Mixed sprite atlases: "+atlases);
    }
    public static void main(String[] args) throws Exception {
        for(var variant:RocketVariant.values()) {
            String id=variant==RocketVariant.BASE?"rocket":variant.path+"_rocket";
            var item=JsonParser.parseString(Files.readString(ASSETS.resolve("items/"+id+".json"))).getAsJsonObject();
            if(!item.getAsJsonObject("model").get("model").getAsString().equals("space_fabric:item/"+id))
                throw new AssertionError("Wrong item definition: "+id);
            var json=JsonParser.parseString(Files.readString(ASSETS.resolve("models/item/"+id+".json"))).getAsJsonObject();
            validate(json);
            if(variant!=RocketVariant.BASE) {
                var broken=json.deepCopy();broken.getAsJsonObject("textures").addProperty("accent","minecraft:block/yellow_concrete");
                boolean rejected=false;
                try {validate(broken);}catch(IllegalStateException expected){rejected=true;}
                if(!rejected)throw new AssertionError("Regression check missed the old mixed-atlas failure");
            }
        }
        System.out.println("OK: all ten rocket inventory models parse with native 26.1 classes, resolve every face, and use one atlas; nine old mixed-atlas models rejected.");
    }
}

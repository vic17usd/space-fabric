package dev.vic17usd.spacefabric.client.rocket;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.model.EquipmentClientInfo;

/** Native 26.1 equipment decoding plus UV/alpha checks; no client, window or GPU. */
public final class EquipmentTextureCheck {
    private static final Path ROOT=Path.of("src/main/resources/assets/space_fabric");
    private static void opaque(BufferedImage image,int x,int y,int width,int height) {
        for(int yy=y*4;yy<(y+height)*4;yy++)for(int xx=x*4;xx<(x+width)*4;xx++)
            if((image.getRGB(xx,yy)>>>24)!=255)throw new AssertionError("Hole in equipment UV at "+xx+","+yy);
    }
    public static void main(String[] args) throws Exception {
        var icons=new ArrayList<String>();var hashes=new HashSet<Integer>();
        for(var tier:new String[]{"vacuum","venus","cryo","abyssal"}) {
            for(var part:new String[]{"helmet","chestplate","leggings","boots"})icons.add(tier+"_suit_"+part);
            var equipment=EquipmentClientInfo.CODEC.parse(JsonOps.INSTANCE,JsonParser.parseString(Files.readString(ROOT.resolve("equipment/"+tier+"_suit.json")))).getOrThrow();
            for(var type:new EquipmentClientInfo.LayerType[]{EquipmentClientInfo.LayerType.HUMANOID,EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS}) {
                var layers=equipment.getLayers(type);if(layers.size()!=1)throw new AssertionError("Missing armor layer");
                var location=layers.getFirst().getTextureLocation(type);
                if(!location.getNamespace().equals("space_fabric"))throw new AssertionError("Equipment still uses vanilla texture");
                var image=ImageIO.read(ROOT.resolve(location.getPath()).toFile());
                if(image.getWidth()!=256||image.getHeight()!=128)throw new AssertionError("Invalid 64x32 UV aspect ratio");
                hashes.add(java.util.Arrays.hashCode(image.getRGB(0,0,256,128,null,0,256)));
                if((image.getRGB(250,4)>>>24)!=0)throw new AssertionError("Unused UV padding must stay transparent");
                if(type==EquipmentClientInfo.LayerType.HUMANOID) {
                    opaque(image,0,8,32,8);opaque(image,8,0,16,8); // Full sealed head, visor on its front.
                    opaque(image,16,20,24,12);opaque(image,40,20,16,12); // Torso and arms.
                    opaque(image,0,28,16,4);opaque(image,8,16,4,4); // Boots and soles.
                } else {opaque(image,0,20,16,12);opaque(image,16,28,24,4);}
            }
        }
        for(var planet:new String[]{"mercury","venus","earth","moon","mars","jupiter","saturn","uranus","neptune"})icons.add(planet+"_rocket_kit");
        for(var id:icons) {
            var definition=JsonParser.parseString(Files.readString(ROOT.resolve("items/"+id+".json"))).getAsJsonObject();
            if(!definition.getAsJsonObject("model").get("model").getAsString().equals("space_fabric:item/"+id))throw new AssertionError("Wrong item definition");
            var model=JsonParser.parseString(Files.readString(ROOT.resolve("models/item/"+id+".json"))).getAsJsonObject();
            if(!model.getAsJsonObject("textures").get("layer0").getAsString().equals("space_fabric:item/"+id))throw new AssertionError("Wrong inventory atlas or old vanilla icon");
            var image=ImageIO.read(ROOT.resolve("textures/item/"+id+".png").toFile());
            if(image.getWidth()!=128||image.getHeight()!=128||(image.getRGB(0,0)>>>24)!=0)throw new AssertionError("Invalid icon dimensions/alpha");
        }
        if(hashes.size()!=8)throw new AssertionError("Duplicate suit layers");
        System.out.println("OK: 25 custom icons, four native equipment definitions, eight distinct UV layers; sealed helmet, torso, arm, leg and boot coverage checked.");
    }
}

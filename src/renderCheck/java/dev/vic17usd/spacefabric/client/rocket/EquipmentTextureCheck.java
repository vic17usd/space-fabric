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
    private static void sealed(java.util.List<BufferedImage> images,int x,int y,int width,int height) {
        // Inspect coverage at the finest supplied UV scale, including transparent overlay faces.
        int scale=images.stream().mapToInt(image->image.getWidth()/64).max().orElseThrow();
        for(int yy=y*scale;yy<(y+height)*scale;yy++)for(int xx=x*scale;xx<(x+width)*scale;xx++){
            boolean covered=false;
            for(var image:images)if((image.getRGB(xx*image.getWidth()/(64*scale),yy*image.getHeight()/(32*scale))>>>24)==255)covered=true;
            if(!covered)throw new AssertionError("Hole through all equipment layers at "+xx+","+yy);
        }
    }
    public static void main(String[] args) throws Exception {
        var icons=new ArrayList<String>();var hashes=new HashSet<Integer>();
        for(var tier:new String[]{"vacuum","venus","cryo","abyssal"}) {
            for(var part:new String[]{"helmet","chestplate","leggings","boots"})icons.add(tier+"_suit_"+part);
            var equipment=EquipmentClientInfo.CODEC.parse(JsonOps.INSTANCE,JsonParser.parseString(Files.readString(ROOT.resolve("equipment/"+tier+"_suit.json")))).getOrThrow();
            for(var type:new EquipmentClientInfo.LayerType[]{EquipmentClientInfo.LayerType.HUMANOID,EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS}) {
                var layers=equipment.getLayers(type);if(layers.size()!=2)throw new AssertionError("Missing lining or redesign layer");
                var images=new ArrayList<BufferedImage>();
                for(var layer:layers){
                    var location=layer.getTextureLocation(type);
                    if(!location.getNamespace().equals("space_fabric"))throw new AssertionError("Foreign equipment namespace");
                    var image=ImageIO.read(ROOT.resolve(location.getPath()).toFile());images.add(image);
                    if(image.getWidth()%64!=0||image.getHeight()*2!=image.getWidth())throw new AssertionError("Invalid native 64x32 UV ratio");
                    hashes.add(java.util.Arrays.hashCode(image.getRGB(0,0,image.getWidth(),image.getHeight(),null,0,image.getWidth())));
                    if((image.getRGB(image.getWidth()-6,4)>>>24)!=0)throw new AssertionError("Unused padding lost transparency");
                }
                var overlay=layers.getLast().getTextureLocation(type);
                String folder=type==EquipmentClientInfo.LayerType.HUMANOID?"humanoid":"humanoid_leggings";
                if(!overlay.getPath().equals("textures/entity/equipment/"+folder+"/"+tier+"_suit.png"))throw new AssertionError("Wrong outer design");
                if(Files.mismatch(ROOT.resolve(overlay.getPath()),Path.of("tools/texture_sources/suit_redesigns/"+tier+"/"+folder+".png"))!=-1)
                    throw new AssertionError("Supplied art was modified or assigned to the wrong suit");
                if(type==EquipmentClientInfo.LayerType.HUMANOID) {
                    sealed(images,0,8,32,8);sealed(images,8,0,16,8);
                    sealed(images,16,20,24,12);sealed(images,40,20,16,12);
                    sealed(images,0,28,16,4);sealed(images,8,16,4,4);
                }else{sealed(images,0,20,16,12);sealed(images,16,28,24,4);}

            }
        }
        for(var part:new String[]{"helmet","chestplate","leggings","boots"})icons.add("reinforced_venus_suit_"+part);
        for(var planet:new String[]{"mercury","venus","earth","moon","mars","jupiter","saturn","uranus","neptune"})icons.add(planet+"_rocket_kit");
        for(var id:icons) {
            var definition=JsonParser.parseString(Files.readString(ROOT.resolve("items/"+id+".json"))).getAsJsonObject();
            if(!definition.getAsJsonObject("model").get("model").getAsString().equals("space_fabric:item/"+id))throw new AssertionError("Wrong item definition");
            var model=JsonParser.parseString(Files.readString(ROOT.resolve("models/item/"+id+".json"))).getAsJsonObject();
            if(!model.getAsJsonObject("textures").get("layer0").getAsString().equals("space_fabric:item/"+id))throw new AssertionError("Wrong inventory atlas or old vanilla icon");
            var image=ImageIO.read(ROOT.resolve("textures/item/"+id+".png").toFile());
            if(image.getWidth()!=128||image.getHeight()!=128||(image.getRGB(0,0)>>>24)!=0)throw new AssertionError("Invalid icon dimensions/alpha");
        }
        if(hashes.size()!=16)throw new AssertionError("Duplicate suit designs or linings");
        System.out.println("OK: 29 custom icons, four native equipment definitions, eight unchanged supplied overlays, eight linings; sealed helmet, torso, arm, leg and boot coverage checked.");
    }
}

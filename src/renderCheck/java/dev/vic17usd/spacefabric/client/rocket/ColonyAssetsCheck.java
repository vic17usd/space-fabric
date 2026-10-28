package dev.vic17usd.spacefabric.client.rocket;

import java.io.StringReader;
import java.nio.file.*;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.sprite.TextureSlots;

/** Parse the native 26.1 material object; the old render-layer API no longer applies. */
public final class ColonyAssetsCheck {
    public static void main(String[] args) throws Exception {
        var root=Path.of("src/main/resources/assets/space_fabric");
        var model=CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/pressure_window.json"))));
        var material=new TextureSlots.Resolver().addLast(model.textureSlots()).resolve(()->"habitat glass").getMaterial("#all");
        if(material==null||!material.forceTranslucent())throw new AssertionError("Habitat glass must use native translucent materials");
        var png=ImageIO.read(root.resolve("textures/block/pressure_window.png").toFile());
        if((png.getRGB(32,32)>>>24)>=255)throw new AssertionError("The pressure window is opaque");
        for(String name:new String[]{"pressure_hull","habitat_controller","water_electrolyzer","solar_charger","geothermal_generator","sample_analyzer","martian_ground_ice"})
            CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/"+name+".json"))));
        for(String name:new String[]{"venus_atmospheric_collector","venus_gas_separator","venus_sulfur_processor","venus_ceramic_kiln","venus_composite_hull"}) {
            var industry=CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/"+name+".json"))));
            var texture=new TextureSlots.Resolver().addLast(industry.textureSlots()).resolve(()->name).getMaterial("#all");
            if(texture==null||ImageIO.read(root.resolve("textures/block/"+name+".png").toFile())==null)throw new AssertionError("Missing industry texture: "+name);
        }
        for(String half:new String[]{"bottom","top"})for(String side:new String[]{"left","right"})for(String suffix:new String[]{"","_open"})
            CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/airlock_door_"+half+"_"+side+suffix+".json"))));
        System.out.println("OK: native colony block models, all eight door variants and translucent pressure-window material verified without GPU.");
    }
}

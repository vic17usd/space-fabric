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
        for(String name:new String[]{"venus_core_drill","venus_atmospheric_collector","venus_gas_separator","venus_sulfur_processor","venus_ceramic_kiln","venus_composite_hull","venus_heat_exchanger","venus_greenhouse","venus_life_reclaimer","venus_service_dock","venus_expedition_console","venus_grow_lamp"}) {
            var industry=CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/"+name+".json"))));
            var texture=new TextureSlots.Resolver().addLast(industry.textureSlots()).resolve(()->name).getMaterial("#all");
            if(texture==null||ImageIO.read(root.resolve("textures/block/"+name+".png").toFile())==null)throw new AssertionError("Missing industry texture: "+name);
        }
        for(String name:new String[]{"venus_rift_obsidian","venus_rift_iron","venus_rift_ember_crust","venus_tessera_rock","venus_ember_fissure","venus_sulfur_lantern","venus_basalt_sheet","venus_charred_rock","venus_sulfur_ash"}){
            var imported=CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/"+name+".json"))));
            var slots=new TextureSlots.Resolver().addLast(imported.textureSlots()).resolve(()->name);
            if(slots.getMaterial(name.equals("venus_sulfur_ash")?"#ash":"#all")==null||ImageIO.read(root.resolve("textures/block/"+name+".png").toFile())==null)throw new AssertionError("Invalid imported Venus block: "+name);
        }
        for(String half:new String[]{"bottom","top"})for(String side:new String[]{"left","right"})for(String suffix:new String[]{"","_open"})
            CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/airlock_door_"+half+"_"+side+suffix+".json"))));
        for(var path:Files.list(root.resolve("models/block")).filter(p->{String n=p.getFileName().toString();return n.startsWith("liquid_")||n.startsWith("gas_")||n.startsWith("energy_")||n.equals("machine_interface.json");}).toList())
            CuboidModel.fromStream(new StringReader(Files.readString(path)));
        for(String channel:new String[]{"liquid","gas","energy"}) {
            CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/item/"+channel+"_conduit.json"))));
            if(ImageIO.read(root.resolve("textures/block/"+channel+"_tank.png").toFile())==null)throw new AssertionError("Missing transport tank texture");
            var state=com.google.gson.JsonParser.parseString(Files.readString(root.resolve("blockstates/"+channel+"_conduit.json"))).getAsJsonObject();
            if(state.getAsJsonArray("multipart").size()!=7)throw new AssertionError("A conduit needs six connected arms and a core");
            var valve=com.google.gson.JsonParser.parseString(Files.readString(root.resolve("blockstates/"+channel+"_bulkhead.json"))).getAsJsonObject();
            if(valve.getAsJsonObject("variants").size()!=12)throw new AssertionError("Missing axial valve/redstone states");
        }
        System.out.println("OK: native colony block models, all eight door variants and translucent pressure-window material verified without GPU.");
    }
}

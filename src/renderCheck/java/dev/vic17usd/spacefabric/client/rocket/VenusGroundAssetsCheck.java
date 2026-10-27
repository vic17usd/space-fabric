package dev.vic17usd.spacefabric.client.rocket;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.resources.model.cuboid.CuboidModel;

/** Decode all imported models with the real 26.1 client parser, without a GPU. */
public final class VenusGroundAssetsCheck {
    public static void main(String[] args) throws Exception {
        var root=Path.of("src/main/resources/assets/space_fabric/models");
        for(var name:new String[]{"vulcanized_basalt","venus_hot_rock","sulfur_deposit"})
            CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("block/"+name+".json"))));
        for(int i=0;i<21;i++)
            CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("block/sulfur_cluster_"+i+".json"))));
        CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("item/sulfur_cluster.json"))));
        for(var name:new String[]{"venus_vent_basalt","venus_sulfur_crust","venus_ember_crust","venus_acid_pocket","venus_basalt_substrate","venus_cracked_stone","venus_obsidian_slag","venus_sulfur_crystal_block","venus_acid_pool"})
            CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("block/"+name+".json"))));
        System.out.println("OK: three Venus ground models, 21 original outcrop pieces assembled inventory model and nine new landscape models decoded in 26.1.");
    }
}

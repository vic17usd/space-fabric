package dev.vic17usd.spacefabric.client.rocket;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.vic17usd.spacefabric.space.*;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.StringReader;
import java.util.*;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.resources.model.cuboid.*;
import net.minecraft.core.Direction;

/** Compare actual fleet vertices to native item transforms, including rotated pivots and UVs. */
public final class RocketModulesCheck {
    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();net.minecraft.server.Bootstrap.bootStrap();
        var manifest=JsonParser.parseString(Files.readString(Path.of("tools/rocket_fleet_manifest.json"))).getAsJsonObject();
        var meshes=new HashMap<String,String>();int total=0;
        for(var variant:RocketVariant.values()) {
            var meta=manifest.getAsJsonObject(variant.path);
            var mesh=new RocketFleetModel(variant);var emitted=new ArrayList<float[]>();
            VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,values)->{
                if(!method.getName().equals("addVertex")||values.length!=11)throw new AssertionError(method);
                for(int i:new int[]{0,1,2,4,5,8,9,10})if(!Float.isFinite((float)values[i]))throw new AssertionError("Nonfinite geometry");
                float x=(float)values[0],y=(float)values[1],z=(float)values[2];
                if(Math.max(Math.abs(x),Math.abs(z))>RocketHullGeometry.width(variant)/2||y<-.001||y>RocketHullGeometry.height(variant))throw new AssertionError("Mesh outside collision box: "+variant);
                if((int)values[6]!=123||(int)values[7]!=456)throw new AssertionError("Lost light/overlay");
                if((int)values[3]!=-1)throw new AssertionError("Added tint on original hull: "+variant);
                float length=0;for(int i=8;i<11;i++)length+=(float)values[i]*(float)values[i];
                if(Math.abs(length-1)>.00001)throw new AssertionError("Invalid normal");
                if((float)values[4]<0||(float)values[4]>1||(float)values[5]<0||(float)values[5]>1)throw new AssertionError("UV outside atlas");
                emitted.add(new float[]{x,y,z,(float)values[4],(float)values[5]});return null;
            });
            mesh.renderGeometry(new PoseStack().last(),sink,456,123);
            if(emitted.size()!=meta.get("quads").getAsInt()*4)throw new AssertionError("Missing faces");
            String shape=Arrays.deepToString(emitted.toArray());
            String previous=meshes.putIfAbsent(meta.get("source").getAsString(),shape);
            if(previous!=null&&!previous.equals(shape))throw new AssertionError("Shared original hull was modified: "+variant);
            String name=variant==RocketVariant.BASE?"rocket":variant.path+"_rocket";
            var json=JsonParser.parseString(Files.readString(Path.of("src/main/resources/assets/space_fabric/models/item/"+name+".json"))).getAsJsonObject();
            var parsed=CuboidModel.fromStream(new StringReader(json.toString()));
            var elements=((UnbakedCuboidGeometry)parsed.geometry()).elements();
            if(elements.size()*24!=emitted.size())throw new AssertionError("Item lost geometry");
            var raw=JsonParser.parseString(Files.readString(Path.of("tools/import_sources/"+sourceFile(meta.get("source").getAsString())+".bbmodel"))).getAsJsonObject();
            if(elements.size()!=raw.getAsJsonArray("elements").size()||mesh.faceCount()!=elements.size()*6)
                throw new AssertionError("Geometry added to the original model: "+variant);
            float factor=0;
            var first=raw.getAsJsonArray("elements").get(0).getAsJsonObject();
            for(int axis=0;axis<3;axis++) {
                float span=first.getAsJsonArray("to").get(axis).getAsFloat()-first.getAsJsonArray("from").get(axis).getAsFloat();
                if(span>0){factor=(float)(span*meta.get("scale").getAsDouble())/(elements.getFirst().to().get(axis)-elements.getFirst().from().get(axis));break;}
            }
            int cursor=0;
            for(int e=0;e<meta.get("cubes").getAsInt();e++) {
                var element=elements.get(e);
                for(var direction:new Direction[]{Direction.WEST,Direction.EAST,Direction.UP,Direction.DOWN,Direction.NORTH,Direction.SOUTH}) {
                    var face=element.faces().get(direction);
                    for(int v=0;v<4;v++) {
                        var point=FaceInfo.fromFacing(direction).getVertexInfo(v).select(element.from(),element.to()).div(16);
                        if(element.rotation()!=null){var rotation=element.rotation();point.sub(rotation.origin());rotation.transform().transformPosition(point);point.add(rotation.origin());}
                        point.mul(16);point.set(-(point.x-8)*factor,point.y*factor,-(point.z-8)*factor);
                        var actual=emitted.get(cursor++);
                        if(point.distance(actual[0],actual[1],actual[2])>.00003)throw new AssertionError("Entity/item rotation differs: "+variant+":"+e+":"+direction);
                        if(Math.abs(actual[3]-CuboidFace.getU(face.uvs(),face.rotation(),v))>.000001||Math.abs(actual[4]-CuboidFace.getV(face.uvs(),face.rotation(),v))>.000001)
                            throw new AssertionError("Entity/item UV differs: "+variant);
                    }
                }
            }
            total+=emitted.size();
        }
        if(meshes.size()!=8)throw new AssertionError("Missing supplied rocket model");
        System.out.println("OK: eight unmodified hulls for ten variants, "+total+" vertices; no exterior additions, and native item/entity rotations, UVs and collision bounds agree.");
        var report=new StringBuilder("| Cohete | Nivel | Modelo | Depósito | Tierra → destino | Destino → Tierra |\n|---|---:|---|---:|---:|---:|\n");
        Arrays.stream(RocketVariant.values()).sorted(Comparator.comparingInt(RocketVariant::rank)).forEach(variant->{
            var target=TravelDestination.ALL.stream().filter(d->d.planet()==variant.mission&& !d.id().equals("overworld")).findFirst().orElseThrow();
            report.append("| ").append(variant.path).append(" | ").append(variant.rank()).append(" | ")
                .append(manifest.getAsJsonObject(variant.path).get("source").getAsString()).append(" | ").append(variant.fuelCapacity()).append(" | ")
                .append(RocketRules.cost(TravelDestination.at(0),target)).append(" | ").append(RocketRules.cost(target,TravelDestination.at(0))).append(" |\n");
        });
        Files.createDirectories(Path.of("build/reports"));Files.writeString(Path.of("build/reports/rocket-fleet.md"),report);
    }
    private static String sourceFile(String source){return switch(source){
        case "classic"->"space-rocket";case "dieselpunk"->"dieselpunk-space-rocket";
        case "modern"->"modern-space-rocket";case "venus_lander"->"venus-atmosphere-lander";
        case "heavy"->"two-stage-heavy-lift-orbital-rocket";case "biomechanical"->"biomechanical-exploration-rocket";
        case "vertical_lander"->"venus-vertical-landing-rocket";case "mars_lander"->"mars-landing-rocket";default->throw new IllegalArgumentException(source);};}
}

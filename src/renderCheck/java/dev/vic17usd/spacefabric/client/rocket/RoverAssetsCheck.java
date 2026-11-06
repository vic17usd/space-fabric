package dev.vic17usd.spacefabric.client.rocket;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.vic17usd.spacefabric.client.rover.RoverRenderer;
import java.io.StringReader;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.util.*;
import org.joml.Vector3f;
import net.minecraft.client.resources.model.cuboid.CuboidModel;

/** Exercise native cuboids and actual mesh vertices, UVs, wheel pivots and cockpit rays by CPU. */
public final class RoverAssetsCheck {
    private static final Vector3f CAMERA=new Vector3f(0,1.97F,1.15F);
    public static void main(String[] args) throws Exception {
        var root=Path.of("src/main/resources/assets/space_fabric");
        for(String name:new String[]{"futuristic-control-console","venusian-pressurized-land-vehicle"}) {
            var source=JsonParser.parseString(Files.readString(Path.of("tools/import_sources/"+name+".bbmodel"))).getAsJsonObject();
            byte[] expected=Base64.getDecoder().decode(source.getAsJsonArray("textures").get(0).getAsJsonObject().get("source").getAsString().split(",",2)[1]);
            String target=name.startsWith("futuristic")?"foundry_console":"venus_rover";
            for(String folder:new String[]{name.startsWith("futuristic")?"block":"entity","item"})
                if(!Arrays.equals(expected,Files.readAllBytes(root.resolve("textures/"+folder+"/"+target+".png"))))throw new AssertionError("Changed original atlas");
        }
        CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/block/foundry_console.json"))));
        CuboidModel.fromStream(new StringReader(Files.readString(root.resolve("models/item/venus_rover.json"))));
        var body=new ArrayList<Vector3f>();int count=0;
        for(String group:RoverRenderer.PARTS) {
            var json=JsonParser.parseString(Files.readString(root.resolve("model_mesh/venus_rover_"+group+".json"))).getAsJsonObject();
            var pivot=json.getAsJsonArray("pivot");var mesh=new RocketFleetModel("/assets/space_fabric/model_mesh/venus_rover_"+group+".json");
            for(int angle:new int[]{0,90,213}) {
                var poses=new PoseStack();poses.translate(pivot.get(0).getAsDouble(),pivot.get(1).getAsDouble(),pivot.get(2).getAsDouble());
                if(!group.equals("body")&&!group.equals("glass"))poses.mulPose(Axis.XP.rotationDegrees(angle));
                var actual=new ArrayList<Vector3f>();
                VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,v)->{
                    if(!method.getName().equals("addVertex")||v.length!=11)throw new AssertionError(method);
                    var p=new Vector3f((float)v[0],(float)v[1],(float)v[2]);if(!p.isFinite())throw new AssertionError("Non-finite vertex");
                    if(Math.abs(p.x)>2.021||p.y<-.2||p.y>2.921||Math.abs(p.z)>2.021)throw new AssertionError("Outside rover bounds: "+p);
                    if((int)v[6]!=123||(int)v[7]!=456)throw new AssertionError("Lost light/overlay");
                    if(((int)v[3]>>>24)!=(group.equals("glass")?85:255))throw new AssertionError("Visor/body alpha lost");
                    for(int i:new int[]{4,5})if((float)v[i]<0||(float)v[i]>1)throw new AssertionError("UV outside original atlas");
                    actual.add(p);return null;
                });
                mesh.renderGeometry(poses.last(),sink,456,123);
                if(actual.size()!=mesh.faceCount()*4||actual.isEmpty())throw new AssertionError("Missing mesh part");
                if(group.equals("body")&&angle==0)body.addAll(actual);if(angle==0)count+=actual.size();
            }
        }
        for(int yaw:new int[]{-25,0,25})for(int pitch:new int[]{-10,0,10}) {
            var ray=new Vector3f((float)Math.tan(Math.toRadians(yaw)),(float)Math.tan(Math.toRadians(pitch)),1).normalize();
            if(hit(body,ray))throw new AssertionError("Opaque geometry blocks cockpit: "+ray);
        }
        for(var ray:List.of(new Vector3f(1,0,0),new Vector3f(-1,0,0),new Vector3f(0,1,0),new Vector3f(0,-1,0),new Vector3f(0,0,-1)))
            if(!hit(body,ray))throw new AssertionError("Missing cabin surface: "+ray);
        var airBody=new ArrayList<Vector3f>();
        for(String part:new String[]{"body","glass","lights"}){
            var mesh=new RocketFleetModel("/assets/space_fabric/model_mesh/venus_aerostat_"+part+".json");
            VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,v)->{
                if(!method.getName().equals("addVertex")||v.length!=11)throw new AssertionError(method);
                var point=new Vector3f((float)v[0],(float)v[1],(float)v[2]);
                if(!point.isFinite()||Math.abs(point.x)>2.9||Math.abs(point.z)>2.9||point.y<0||point.y>7.4)throw new AssertionError("Aerostat bounds: "+point);
                if(part.equals("body"))airBody.add(point);return null;
            });
            mesh.renderGeometry(new PoseStack().last(),sink,456,123);
            if(mesh.faceCount()==0)throw new AssertionError("Empty aerostat mesh");
        }
        for(int yaw:new int[]{-25,0,25})for(int pitch:new int[]{-10,0,10})if(hit(airBody,new Vector3f((float)Math.tan(Math.toRadians(yaw)),(float)Math.tan(Math.toRadians(pitch)),1).normalize()))throw new AssertionError("Blocked aerostat visor");
        for(var ray:List.of(new Vector3f(1,0,0),new Vector3f(-1,0,0),new Vector3f(0,1,0),new Vector3f(0,-1,0),new Vector3f(0,0,-1)))if(!hit(airBody,ray))throw new AssertionError("Open aerostat cabin");
        System.out.println("OK: original console/rover atlases, native cuboids, "+count+" mesh vertices, six animated axles and nine unobstructed visor rays verified without GPU.");
    }
    private static boolean hit(List<Vector3f> vertices,Vector3f ray) {
        for(int i=0;i<vertices.size();i+=4)if(triangle(vertices.get(i),vertices.get(i+1),vertices.get(i+2),ray)||triangle(vertices.get(i),vertices.get(i+2),vertices.get(i+3),ray))return true;
        return false;
    }
    private static boolean triangle(Vector3f a,Vector3f b,Vector3f c,Vector3f ray) {
        var e1=new Vector3f(b).sub(a);var e2=new Vector3f(c).sub(a);var p=new Vector3f(ray).cross(e2);float det=e1.dot(p);if(Math.abs(det)<1e-7)return false;
        float inv=1/det;var t=new Vector3f(CAMERA).sub(a);float u=t.dot(p)*inv;if(u<0||u>1)return false;
        var q=t.cross(e1);float v=ray.dot(q)*inv;return v>=0&&u+v<=1&&e2.dot(q)*inv>1e-5;
    }
}

package dev.vic17usd.spacefabric.client.rocket;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.vic17usd.spacefabric.space.RocketVariant;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Arrays;

/** Checks the actual emitter and item bounds against the same equipment geometry without a GPU. */
public final class RocketModulesCheck {
    public static void main(String[] args) throws Exception {
        var meshes=new HashSet<String>();
        for(var variant:RocketVariant.values()) {
            var boxes=RocketModules.boxes(variant);
            if(variant==RocketVariant.BASE) { if(boxes.length!=0)throw new AssertionError("Legacy geometry changed");continue; }
            if(!meshes.add(Arrays.deepToString(boxes)))throw new AssertionError("Duplicate equipment mesh");
            int[] count={0};
            VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,values)->{
                if(!method.getName().equals("addVertex")||values.length!=11)throw new AssertionError(method);
                for(int i:new int[]{0,1,2,4,5,8,9,10})if(!Float.isFinite((float)values[i]))throw new AssertionError("Nonfinite geometry");
                float x=(float)values[0],y=(float)values[1],z=(float)values[2];
                if(Math.abs(x)>.801||Math.abs(z)>.801||y<0||y>3.5)throw new AssertionError("Equipment exceeds rocket clearance");
                if((int)values[3]!=variant.color||(int)values[6]!=123||(int)values[7]!=456)throw new AssertionError("Lost color/light");
                float length=0;for(int i=8;i<11;i++)length+=(float)values[i]*(float)values[i];
                if(Math.abs(length-1)>.00001)throw new AssertionError("Invalid normal");
                if((float)values[4]<0||(float)values[4]>1||(float)values[5]<0||(float)values[5]>1)throw new AssertionError("UV out of atlas");
                count[0]++;return null;
            });
            RocketModules.renderGeometry(variant,new PoseStack().last(),sink,456,123);
            if(count[0]!=boxes.length*24)throw new AssertionError("Missing equipment faces");
            var model=JsonParser.parseString(Files.readString(Path.of("src/main/resources/assets/space_fabric/models/item/"+variant.path+"_rocket.json"))).getAsJsonObject();
            var elements=model.getAsJsonArray("elements");
            if(elements.size()!=48+boxes.length)throw new AssertionError("Item lost base or equipment");
            double scale=16/3.284210526;
            for(int n=0;n<boxes.length;n++) {
                var b=boxes[n];var element=elements.get(48+n).getAsJsonObject();
                double[][] expected={{8-b[3]*scale,b[1]*scale,8-b[5]*scale},{8-b[0]*scale,b[4]*scale,8-b[2]*scale}};
                for(int side=0;side<2;side++)for(int axis=0;axis<3;axis++) {
                    double value=element.getAsJsonArray(side==0?"from":"to").get(axis).getAsDouble();
                    if(Math.abs(value-expected[side][axis])>.00002||value< -16||value>32)throw new AssertionError("Entity/item equipment mismatch");
                }
            }
        }
        System.out.println("OK: nine distinct equipment meshes, native vertices, normals, bounds and matching 3D items.");
    }
}

package dev.vic17usd.spacefabric.client.rocket;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.vic17usd.spacefabric.space.RocketVariant;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

/** Baked Blockbench geometry. Direct vertices retain arbitrary per-face UVs with Sodium. */
public final class RocketFleetModel {
    private record Face(Vector3f normal,int color,float[] vertices) { }
    private final List<Face> faces;
    public final Identifier texture;
    public RocketFleetModel(RocketVariant variant) {
        this("/assets/space_fabric/rocket_mesh/"+variant.path+".json");
    }
    public RocketFleetModel(String path) {
        try(var input=RocketFleetModel.class.getResourceAsStream(path)) {
            if(input==null)throw new IllegalStateException("Missing rocket mesh: "+path);
            var json=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();
            texture=Identifier.parse(json.get("texture").getAsString());
            var result=new ArrayList<Face>();
            for(var entry:json.getAsJsonArray("quads")) {
                var q=entry.getAsJsonArray();if(q.size()!=24)throw new IllegalStateException("Invalid rocket quad");
                float[] vertices=new float[20];for(int i=0;i<20;i++)vertices[i]=q.get(i+4).getAsFloat();
                result.add(new Face(new Vector3f(q.get(0).getAsFloat(),q.get(1).getAsFloat(),q.get(2).getAsFloat()),q.get(3).getAsInt(),vertices));
            }
            faces=List.copyOf(result);
        } catch(java.io.IOException e){throw new IllegalStateException("Cannot read "+path,e);}
    }
    public int faceCount(){return faces.size();}
    public void renderGeometry(Pose pose,VertexConsumer output,int light,int overlay) {
        var normal=new Vector3f();var point=new Vector3f();
        for(var face:faces) {
            pose.transformNormal(face.normal,normal);
            for(int i=0;i<20;i+=5) {
                var v=face.vertices;pose.pose().transformPosition(v[i],v[i+1],v[i+2],point);
                output.addVertex(point.x,point.y,point.z,face.color,v[i+3],v[i+4],overlay,light,normal.x,normal.y,normal.z);
            }
        }
    }
}

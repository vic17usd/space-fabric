package dev.vic17usd.spacefabric.client.rover;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.vic17usd.spacefabric.client.rocket.RocketFleetModel;
import dev.vic17usd.spacefabric.rover.RoverEntity;
import java.io.InputStreamReader;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Direct source UVs, six axle pivots, opaque inner walls and a translucent front visor. */
public final class RoverRenderer extends EntityRenderer<RoverEntity,RoverRenderer.State> {
    public static final String[] PARTS={"body","glass","left_front","left_middle","left_rear","right_front","right_middle","right_rear"};
    private final RocketFleetModel[] parts=new RocketFleetModel[8];
    private final RocketFleetModel landingLights=new RocketFleetModel("/assets/space_fabric/model_mesh/venus_aerostat_lights.json");
    private final RocketFleetModel aerostat=new RocketFleetModel("/assets/space_fabric/model_mesh/venus_aerostat_body.json"),
        aerostatGlass=new RocketFleetModel("/assets/space_fabric/model_mesh/venus_aerostat_glass.json");
    private final double[][] pivots=new double[8][3];
    public static final class State extends EntityRenderState {float yaw,roll;boolean aerial;}
    public RoverRenderer(EntityRendererProvider.Context context) {
        super(context);shadowRadius=1.5F;
        for(int i=0;i<8;i++) {
            String path="/assets/space_fabric/model_mesh/venus_rover_"+PARTS[i]+".json";parts[i]=new RocketFleetModel(path);
            try(var input=RoverRenderer.class.getResourceAsStream(path)) {
                var pivot=JsonParser.parseReader(new InputStreamReader(input,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("pivot");
                for(int axis=0;axis<3;axis++)pivots[i][axis]=pivot.get(axis).getAsDouble();
            } catch(java.io.IOException error){throw new IllegalStateException(path,error);}
        }
    }
    @Override public State createRenderState(){return new State();}
    @Override public void extractRenderState(RoverEntity entity,State state,float delta){super.extractRenderState(entity,state,delta);state.aerial=entity.aerial();state.yaw=entity.getYRot();state.roll=RoverView.wheelRoll(entity,delta);}
    @Override public void submit(State state,PoseStack poses,SubmitNodeCollector collector,CameraRenderState camera) {
        poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(-state.yaw));
        if(state.aerial){
            collector.submitCustomGeometry(poses,RenderTypes.entityCutout(landingLights.texture),(pose,output)->landingLights.renderGeometry(pose,output,15728880,OverlayTexture.NO_OVERLAY));
            collector.submitCustomGeometry(poses,RenderTypes.entityCutout(aerostat.texture),(pose,output)->aerostat.renderGeometry(pose,output,state.lightCoords,OverlayTexture.NO_OVERLAY));
            collector.submitCustomGeometry(poses,RenderTypes.entityTranslucent(aerostatGlass.texture),(pose,output)->aerostatGlass.renderGeometry(pose,output,state.lightCoords,OverlayTexture.NO_OVERLAY));
            poses.popPose();super.submit(state,poses,collector,camera);return;
        }
        // Glass is submitted last so the opaque cabin and wheels can be seen through it.
        for(int pass=0;pass<8;pass++) {
            int i=pass==7?1:pass==0?0:pass+1;var part=parts[i];var pivot=pivots[i];
            poses.pushPose();poses.translate(pivot[0],pivot[1],pivot[2]);if(i>=2)poses.mulPose(Axis.XP.rotationDegrees(state.roll));
            var type=i==1?RenderTypes.entityTranslucent(part.texture):RenderTypes.entityCutout(part.texture);
            collector.submitCustomGeometry(poses,type,(pose,output)->part.renderGeometry(pose,output,state.lightCoords,OverlayTexture.NO_OVERLAY));poses.popPose();
        }
        poses.popPose();super.submit(state,poses,collector,camera);
    }
}

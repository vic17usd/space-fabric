package dev.vic17usd.spacefabric.client.venus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Proxy;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Runs the native imported rigs and animations; no source JAR or graphics context is started. */
public final class VenusExpansionModelCheck {
    public static void main(String[] args) {
        var drone=new CopperAcidstingerDroneEntityModel();
        var boss=new SulfurwingCopperstingerEntityModel();
        for(int action=-1;action<=1;action++)for(int time:new int[]{0,200,800,1500}) {
            var d=new CopperAcidstingerDroneRenderState();d.ageInTicks=40;d.actionIndex=action;d.actionTimeMillis=time;d.horizontalTravelSpeed=.8F;d.horizontalTravelPosition=4;
            drone.setupAnim(d);verify(drone);
            var b=new SulfurwingCopperstingerRenderState();b.ageInTicks=40;b.actionIndex=action==1?-1:action;b.actionTimeMillis=time;b.horizontalTravelSpeed=.8F;b.horizontalTravelPosition=4;
            boss.setupAnim(b);verify(boss);
        }
        System.out.println("OK: two Venus flying rigs render native vertices with finite transforms, original UVs and attack/flight/retreat animations.");
    }
    private static void verify(EntityModel<? extends LivingEntityRenderState> model) {
        int[] count={0};
        VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,values)->{
            if(!method.getName().equals("addVertex")||values.length!=11)throw new AssertionError("Unexpected rig vertex path: "+method);
            for(int i:new int[]{0,1,2,4,5,8,9,10})if(!Float.isFinite((float)values[i]))throw new AssertionError("Nonfinite rig transform");
            if((float)values[4]<0||(float)values[4]>1||(float)values[5]<0||(float)values[5]>1)throw new AssertionError("Rig UV outside original texture");
            if((int)values[6]!=OverlayTexture.NO_OVERLAY||(int)values[7]!=0xF000F0)throw new AssertionError("Rig lost light/overlay");
            count[0]++;return null;
        });
        model.renderToBuffer(new PoseStack(),sink,0xF000F0,OverlayTexture.NO_OVERLAY,-1);
        if(count[0]<24||count[0]%24!=0)throw new AssertionError("Missing imported rig cubes");
    }
}

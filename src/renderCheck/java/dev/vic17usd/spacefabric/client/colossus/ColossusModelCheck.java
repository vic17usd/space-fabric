package dev.vic17usd.spacefabric.client.colossus;

import com.mojang.blaze3d.vertex.*;
import java.lang.reflect.Proxy;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Executes the imported model, all combat animations and the recovery pose without a GPU. */
public final class ColossusModelCheck {
    public static void main(String[] args){
        var model=new SulfurColossusEntityModel();
        for(int action=-1;action<6;action++)for(int millis:new int[]{0,500,1000,2000,3000,4000,5000}){
            var state=new SulfurColossusRenderState();state.ageInTicks=40;
            state.actionIndex=action;state.actionTimeMillis=millis;
            state.horizontalTravelSpeed=millis==500?.8F:0;state.horizontalTravelPosition=4;
            model.setupAnim(state);verify(model);
        }
        var state=new SulfurColossusRenderState();model.setupAnim(state);
        var chest=model.root().createPartLookup().apply("chest_left");float closed=chest.yRot;
        state.coreExposed=true;model.setupAnim(state);float opened=chest.yRot;
        if(Math.abs(opened-closed)<.6F)throw new AssertionError("Core recovery pose is invisible");
        model.setupAnim(state);if(Math.abs(chest.yRot-opened)>.0001F)throw new AssertionError("Core rotations accumulate each frame");
        verify(model);state.coreExposed=false;model.setupAnim(state);
        if(Math.abs(chest.yRot-closed)>.0001F)throw new AssertionError("Core remains open after recovery");
        System.out.println("OK: Sulfur Colossus, 124 cuboids, original UVs, idle/walk, six action animations and visible recovery window.");
    }
    private static void verify(SulfurColossusEntityModel model){
        int[] count={0};
        VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,values)->{
            if(!method.getName().equals("addVertex")||values.length!=11)throw new AssertionError("Unexpected native vertex path: "+method);
            for(int i:new int[]{0,1,2,4,5,8,9,10})if(!Float.isFinite((float)values[i]))throw new AssertionError("Nonfinite transform");
            for(int i:new int[]{4,5})if((float)values[i]<0||(float)values[i]>1)throw new AssertionError("UV outside original atlas");
            if((int)values[6]!=OverlayTexture.NO_OVERLAY||(int)values[7]!=0xF000F0)throw new AssertionError("Light/overlay lost");
            count[0]++;return null;
        });
        model.renderToBuffer(new PoseStack(),sink,0xF000F0,OverlayTexture.NO_OVERLAY,-1);
        if(count[0]!=124*24)throw new AssertionError("Missing imported cuboids: "+count[0]);
    }
}

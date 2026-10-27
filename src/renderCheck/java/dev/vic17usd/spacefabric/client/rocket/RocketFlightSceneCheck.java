package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import dev.vic17usd.spacefabric.planet.Planet;
import java.lang.reflect.Proxy;
import java.util.HashSet;

/** Actual animation emitter, using native vertices without starting a GPU. */
public final class RocketFlightSceneCheck {
    public static void main(String[] args) throws Exception {
        var palettes=new HashSet<String>();
        for(var planet:Planet.values()) {
            var palette=new HashSet<Integer>();
            for(float x=-1;x<=1;x+=.1F)for(float y=-1;y<=1;y+=.1F)palette.add(RocketFlightScene.surfaceColor(planet,x,y,-.8F));
            if(!palettes.add(palette.toString()))throw new AssertionError("Duplicate planetary appearance: "+planet);
            for(float progress:new float[]{0,.25F,.5F,.75F,1}) {
                int[] count={0};
                VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,values)->{
                    if(!method.getName().equals("addVertex")||values.length!=11)throw new AssertionError(method);
                    for(int i:new int[]{0,1,2,4,5,8,9,10})if(!Float.isFinite((float)values[i]))throw new AssertionError("Nonfinite cinematic vertex");
                    if((int)values[6]!=OverlayTexture.NO_OVERLAY||(int)values[7]!=0xF000F0)throw new AssertionError("Cinematic light/overlay mismatch");
                    for(int i=0;i<3;i++)if(Math.abs((float)values[i])>32.01)throw new AssertionError("Geometry outside space backdrop");
                    if((int)values[3]>>>24!=255)throw new AssertionError("Background must occlude original world");
                    count[0]++;return null;
                });
                RocketFlightScene.render(new PoseStack().last(),sink,Planet.EARTH,planet,progress);
                int expected=(6+160+2*16*32+(planet==Planet.SATURN?96*3:0))*4;
                if(count[0]!=expected)throw new AssertionError("Missing space geometry: "+planet);
            }
        }
        System.out.println("OK: all nine animated destinations, star enclosure, orbital spheres/rings and five cruise positions verified without GPU.");
        if(args.length>0&&args[0].equals("preview"))exportPreview();
    }
    private static void exportPreview() throws Exception {
        var frames=new com.google.gson.JsonArray();
        for(int frame=0;frame<=12;frame++) {
            var scene=new com.google.gson.JsonArray();
            VertexConsumer capture=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(proxy,method,values)->{
                var vertex=new com.google.gson.JsonArray();
                for(int i=0;i<6;i++)if(i==3)vertex.add((int)values[i]);else vertex.add((float)values[i]);
                scene.add(vertex);return null;
            });
            RocketFlightScene.render(new PoseStack().last(),capture,Planet.EARTH,Planet.MARS,frame/12F);
            int background=scene.size();
            new RocketCockpitModel().renderGeometry(new PoseStack().last(),capture,0xF000F0,OverlayTexture.NO_OVERLAY);
            var json=new com.google.gson.JsonObject();json.addProperty("backgroundVertices",background);json.add("vertices",scene);frames.add(json);
        }
        java.nio.file.Files.createDirectories(java.nio.file.Path.of("build/reports"));
        java.nio.file.Files.writeString(java.nio.file.Path.of("build/reports/rocket-journey-preview.json"),frames.toString());
    }
}

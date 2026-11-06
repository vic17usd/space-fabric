package dev.vic17usd.spacefabric.client.rocket;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Prueba de emisión real de vértices sin cliente, GPU ni sustitución de ModelPart por optimizadores. */
public final class RocketMeshCheck {
    public static void main(String[] args) throws Exception {
        var source = JsonParser.parseString(Files.readString(Path.of("tools/import_sources/space-rocket.bbmodel"))).getAsJsonObject();
        var actual = new ArrayList<float[]>();
        VertexConsumer sink = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                new Class<?>[]{VertexConsumer.class}, (proxy, method, values) -> {
                    if (!method.getName().equals("addVertex") || values.length != 11)
                        throw new AssertionError("Emisión inesperada: " + method);
                    if ((int)values[3] != -1 || (int)values[6] != 123 || (int)values[7] != 456)
                        throw new AssertionError("Se perdió color, overlay o iluminación");
                    actual.add(new float[]{(float)values[0], (float)values[1], (float)values[2], (float)values[4], (float)values[5]});
                    return null;
                });
        new RocketModel().renderGeometry(new PoseStack().last(), sink, 456, 123, -1);
        var cubes = source.getAsJsonArray("elements");
        if (actual.size() != cubes.size()*24) throw new AssertionError("Número de vértices incorrecto: " + actual.size());
        int index = 0;
        for (var cube : cubes) {
            var faces = cube.getAsJsonObject().getAsJsonObject("faces");
            for (var name : List.of("up", "down", "west", "south", "east", "north")) {
                var uv = faces.getAsJsonObject(name).getAsJsonArray("uv");
                float u1=uv.get(0).getAsFloat()/512, v1=uv.get(1).getAsFloat()/512;
                float u2=uv.get(2).getAsFloat()/512, v2=uv.get(3).getAsFloat()/512;
                for (var expected : new float[][]{{u2,v1},{u1,v1},{u1,v2},{u2,v2}}) {
                    var vertex = actual.get(index++);
                    if (Math.abs(vertex[3]-expected[0])>0.000001 || Math.abs(vertex[4]-expected[1])>0.000001)
                        throw new AssertionError("UV diferente del archivo fuente en vértice " + (index-1));
                    if (vertex[0]<-0.801 || vertex[0]>0.801 || vertex[1]>0.001 || vertex[1]<-3.285 || vertex[2]<-0.801 || vertex[2]>0.801)
                        throw new AssertionError("Geometría fuera del volumen previsto");
                }
            }
        }
        System.out.println("OK: 1152 vértices emitidos; 288 caras con UV originales, escala, color y luz verificados sin GPU.");
    }
}

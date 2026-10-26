package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

/** Prueba de las caras realmente emitidas: interior cerrado y visor abierto sin usar una GPU. */
public final class RocketCockpitCheck {
    public static void main(String[] args) {
        List<Vector3f> vertices=new ArrayList<>();
        VertexConsumer sink=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},
                (proxy,method,values)-> {
                    if (!method.getName().equals("addVertex") || values.length!=11) throw new AssertionError(method);
                    var point=new Vector3f((float)values[0],(float)values[1],(float)values[2]);
                    if (!point.isFinite()) throw new AssertionError("Vértice no finito");
                    if (((int)values[3]>>>24)!=255 || (int)values[6]!=123 || (int)values[7]!=456) throw new AssertionError("Color/luz/overlay perdidos");
                    float u=(float)values[4],v=(float)values[5];
                    if (u<0 || u>1 || v<0 || v>1) throw new AssertionError("UV fuera del atlas");
                    var normal=new Vector3f((float)values[8],(float)values[9],(float)values[10]);
                    if (!normal.isFinite() || Math.abs(normal.length()-1)>0.001) throw new AssertionError("Normal inválida");
                    vertices.add(point);return null;
                });
        new RocketCockpitModel().renderGeometry(new PoseStack().last(),sink,456,123);
        if (vertices.size()<400 || vertices.size()%4!=0) throw new AssertionError("No se ha dibujado la cabina completa");
        for (int yaw:new int[]{-25,0,25}) for (int pitch:new int[]{-15,0,15}) {
            var direction=new Vector3f((float)Math.tan(Math.toRadians(yaw)),(float)Math.tan(Math.toRadians(pitch)),1).normalize();
            if (hit(vertices,direction)) throw new AssertionError("El visor está obstruido: "+yaw+","+pitch);
        }
        for (var direction:List.of(new Vector3f(1,0,0),new Vector3f(-1,0,0),new Vector3f(0,1,0),
                new Vector3f(0,-1,0),new Vector3f(0,0,-1),new Vector3f(0.8F,0,1)))
            if (!hit(vertices,direction.normalize())) throw new AssertionError("Falta una pared, el suelo, techo o el marco: "+direction);
        System.out.println("OK: cabina 3D con "+vertices.size()+" vértices; nueve rayos libres por el visor y seis direcciones opacas comprobadas.");
    }
    private static boolean hit(List<Vector3f> vertices,Vector3f direction) {
        for(int i=0;i<vertices.size();i+=4)
            if (triangle(vertices.get(i),vertices.get(i+1),vertices.get(i+2),direction)
                    ||triangle(vertices.get(i),vertices.get(i+2),vertices.get(i+3),direction)) return true;
        return false;
    }
    private static boolean triangle(Vector3f a,Vector3f b,Vector3f c,Vector3f ray) {
        var e1=new Vector3f(b).sub(a);var e2=new Vector3f(c).sub(a);
        var p=new Vector3f(ray).cross(e2);float det=e1.dot(p);
        if(Math.abs(det)<0.0000001F)return false;
        float inv=1/det;var t=new Vector3f(a).negate();float u=t.dot(p)*inv;
        if(u< -0.00001F || u>1.00001F)return false;
        var q=t.cross(e1);float v=ray.dot(q)*inv;
        return v>=-0.00001F && u+v<=1.00001F && e2.dot(q)*inv>0.00001F;
    }
}

package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.joml.Vector3f;

/** Cabina hueca en metros, con abertura frontal real: no hay caras que crucen el visor. */
public final class RocketCockpitModel {
    public static final float FRONT = 0.40F;
    public static final float WINDOW_RADIUS = 0.265F;
    private static final float SIDE = 0.37F, FLOOR = -0.90F, CEILING = 0.35F, BACK = -0.37F;
    private static final int WALL = 0xFF56616A, DARK = 0xFF28323A, FRAME = 0xFFD5D9D8;
    private record Quad(Vector3f a, Vector3f b, Vector3f c, Vector3f d, Vector3f normal, int color) { }
    private final List<Quad> faces = new ArrayList<>();

    public RocketCockpitModel() {
        // Superficies dirigidas hacia la cámara en el centro del habitáculo.
        quad(WALL, p(-SIDE,FLOOR,FRONT),p(-SIDE,FLOOR,BACK),p(-SIDE,CEILING,BACK),p(-SIDE,CEILING,FRONT));
        quad(WALL, p(SIDE,FLOOR,BACK),p(SIDE,FLOOR,FRONT),p(SIDE,CEILING,FRONT),p(SIDE,CEILING,BACK));
        quad(DARK, p(-SIDE,FLOOR,BACK),p(-SIDE,FLOOR,FRONT),p(SIDE,FLOOR,FRONT),p(SIDE,FLOOR,BACK));
        quad(DARK, p(-SIDE,CEILING,BACK),p(SIDE,CEILING,BACK),p(SIDE,CEILING,FRONT),p(-SIDE,CEILING,FRONT));
        quad(WALL, p(-SIDE,FLOOR,BACK),p(SIDE,FLOOR,BACK),p(SIDE,CEILING,BACK),p(-SIDE,CEILING,BACK));

        // Se incluyen los ángulos de las esquinas para cerrar la pared sin rendijas.
        var angles = new TreeSet<Double>();
        for (int i=0; i<48; i++) angles.add(i*Math.PI*2/48);
        for (float x : new float[]{-SIDE,SIDE}) for (float y : new float[]{FLOOR,CEILING}) {
            double angle = Math.atan2(y,x); angles.add(angle<0 ? angle+Math.PI*2 : angle);
        }
        var ring = new ArrayList<>(angles);
        for (int i=0; i<ring.size(); i++) {
            double a=ring.get(i), b=ring.get((i+1)%ring.size());
            float da=wallDistance(a), db=wallDistance(b);
            quad(WALL, radial(a,0.30F,FRONT),radial(b,0.30F,FRONT),radial(b,db,FRONT),radial(a,da,FRONT));
            // Marco biselado: el centro queda abierto al mundo, sin pantalla ni imagen falsa.
            quad(FRAME, radial(a,WINDOW_RADIUS,0.365F),radial(b,WINDOW_RADIUS,0.365F),radial(b,0.30F,FRONT),radial(a,0.30F,FRONT));
        }
        // Uniones de paneles, respaldo y consola con mandos físicos.
        box(-0.35F,-0.87F,-0.355F,0.35F,-0.78F,-0.30F, DARK);
        box(-0.27F,-0.67F,-0.35F,0.27F,0.15F,-0.32F,0xFF334A55);
        box(-SIDE,-0.30F,0.18F,SIDE,-0.22F,FRONT,DARK);
        box(-0.23F,-0.219F,0.23F,0.04F,-0.21F,0.35F,0xFF42D6C0);
        for (int i=0; i<4; i++) box(0.10F+i*0.054F,-0.215F,0.28F,0.13F+i*0.054F,-0.195F,0.32F,
                i==3 ? 0xFFE95F41 : 0xFFD0B657);
        box(-0.06F,-0.45F,0.12F,0.06F,-0.29F,0.16F,0xFF7E8D92);
        box(-0.13F,-0.30F,0.11F,0.13F,-0.27F,0.17F,0xFF303D43);
        // Fijaciones y franjas laterales perceptibles al girar la cabeza.
        for (float x : new float[]{-0.365F,0.345F}) {
            box(x,-0.65F,-0.25F,x+0.02F,0.25F,-0.22F,FRAME);
            box(x,-0.65F,0.12F,x+0.02F,0.25F,0.15F,FRAME);
        }
    }
    private static Vector3f p(float x,float y,float z) { return new Vector3f(x,y,z); }
    private static Vector3f radial(double angle,float radius,float z) {
        return p((float)Math.cos(angle)*radius,(float)Math.sin(angle)*radius,z);
    }
    private static float wallDistance(double angle) {
        double x=Math.cos(angle), y=Math.sin(angle);
        return (float)Math.min(SIDE/Math.max(1e-9,Math.abs(x)), (y>=0 ? CEILING : -FLOOR)/Math.max(1e-9,Math.abs(y)));
    }
    private void quad(int color,Vector3f a,Vector3f b,Vector3f c,Vector3f d) {
        var normal=new Vector3f(b).sub(a).cross(new Vector3f(c).sub(a)).normalize();
        faces.add(new Quad(a,b,c,d,normal,color));
    }
    private void box(float x,float y,float z,float xx,float yy,float zz,int color) {
        quad(color,p(x,y,z),p(x,y,zz),p(x,yy,zz),p(x,yy,z));
        quad(color,p(xx,y,zz),p(xx,y,z),p(xx,yy,z),p(xx,yy,zz));
        quad(color,p(x,yy,zz),p(xx,yy,zz),p(xx,yy,z),p(x,yy,z));
        quad(color,p(x,y,z),p(xx,y,z),p(xx,y,zz),p(x,y,zz));
        quad(color,p(xx,y,z),p(x,y,z),p(x,yy,z),p(xx,yy,z));
        quad(color,p(x,y,zz),p(xx,y,zz),p(xx,yy,zz),p(x,yy,zz));
    }
    public void renderGeometry(Pose pose, VertexConsumer output, int light, int overlay) {
        var position=new Vector3f(); var normal=new Vector3f();
        // La misma región clara del atlas importado aporta textura a los paneles tintados.
        float[][] uv={{16/512F,48/512F},{36/512F,48/512F},{36/512F,72/512F},{16/512F,72/512F}};
        for (var face:faces) {
            pose.transformNormal(face.normal,normal);
            var points=new Vector3f[]{face.a,face.b,face.c,face.d};
            for (int i=0; i<4; i++) {
                pose.pose().transformPosition(points[i],position);
                output.addVertex(position.x,position.y,position.z,face.color,uv[i][0],uv[i][1],overlay,light,normal.x,normal.y,normal.z);
            }
        }
    }
}

package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.space.RocketJourney;
import java.util.Random;
import org.joml.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Camera-centred cinematic geometry: enclosed star field and rotating, shaded planets.
 * Uses a solid white material with vertex colours, preserving the real 3D cockpit in front.
 * This is a visual transfer, not a new playable dimension or a light-speed simulation.
 */
public final class RocketFlightScene {
    private static final float[][] STARS=new float[160][3];
    static {
        var random=new Random(176219);
        for(var star:STARS){double y=random.nextDouble()*2-1,a=random.nextDouble()*Math.PI*2,r=Math.sqrt(1-y*y);star[0]=(float)(Math.cos(a)*r);star[1]=(float)y;star[2]=(float)(Math.sin(a)*r);}
    }
    private RocketFlightScene() { }
    public static void render(Pose pose,VertexConsumer out,Planet origin,Planet destination,float progress) {
        float p=RocketJourney.smooth(progress);
        // Close enough to occlude the original world's distant terrain/sky, behind every cockpit face.
        float s=32;
        quad(pose,out,0xFF010207,new float[][]{{-s,-s,s},{s,-s,s},{s,s,s},{-s,s,s}});
        quad(pose,out,0xFF010207,new float[][]{{s,-s,-s},{-s,-s,-s},{-s,s,-s},{s,s,-s}});
        quad(pose,out,0xFF010207,new float[][]{{-s,-s,-s},{-s,-s,s},{-s,s,s},{-s,s,-s}});
        quad(pose,out,0xFF010207,new float[][]{{s,-s,s},{s,-s,-s},{s,s,-s},{s,s,s}});
        quad(pose,out,0xFF010207,new float[][]{{-s,s,s},{s,s,s},{s,s,-s},{-s,s,-s}});
        quad(pose,out,0xFF010207,new float[][]{{-s,-s,-s},{s,-s,-s},{s,-s,s},{-s,-s,s}});
        for(int i=0;i<STARS.length;i++) {
            var n=new Vector3f(STARS[i]);var center=new Vector3f(n).mul(27);
            var right=new Vector3f(n).cross(Math.abs(n.y)>.9?new Vector3f(1,0,0):new Vector3f(0,1,0)).normalize().mul(.018F+(i%4)*.009F);
            var up=new Vector3f(right).normalize().cross(n).mul(right.length());
            var a=new Vector3f(center).sub(right).sub(up);var b=new Vector3f(center).add(right).sub(up);
            var c=new Vector3f(center).add(right).add(up);var d=new Vector3f(center).sub(right).add(up);
            quad(pose,out,i%5==0?0xFF93B9E8:0xFFE5E7DF,new float[][]{xyz(a),xyz(b),xyz(c),xyz(d)});
        }
        sphere(pose,out,origin,-7-p*8,-2,15,3.6F*(1-p)+.15F,p*.4F);
        sphere(pose,out,destination,.8F*(1-p),.4F,12,.3F+p*5.3F,p*.4F);
    }
    private static float[] xyz(Vector3f v){return new float[]{v.x,v.y,v.z};}
    private static void sphere(Pose pose,VertexConsumer out,Planet planet,float cx,float cy,float cz,float radius,float rotation) {
        int latitudes=16,longitudes=32;
        for(int y=0;y<latitudes;y++)for(int x=0;x<longitudes;x++) {
            float[][] points=new float[4][3];
            int[][] indices={{x,y},{x+1,y},{x+1,y+1},{x,y+1}};
            for(int i=0;i<4;i++) {
                double lon=indices[i][0]*Math.PI*2/longitudes+rotation,lat=indices[i][1]*Math.PI/latitudes-Math.PI/2;
                points[i]=new float[]{cx+(float)(Math.cos(lat)*Math.sin(lon))*radius,cy+(float)Math.sin(lat)*radius,cz+(float)(Math.cos(lat)*Math.cos(lon))*radius};
            }
            double lon=(x+.5)*Math.PI*2/longitudes+rotation,lat=(y+.5)*Math.PI/latitudes-Math.PI/2;
            float nx=(float)(Math.cos(lat)*Math.sin(lon)),ny=(float)Math.sin(lat),nz=(float)(Math.cos(lat)*Math.cos(lon));
            int base=surfaceColor(planet,nx,ny,nz);
            float light=.12F+.88F*Math.max(0,-nx*.6F+ny*.3F-nz*.74F);
            quad(pose,out,shade(base,light),points);
        }
        if(planet==Planet.SATURN)for(int i=0;i<96;i++) {
            double a=i*Math.PI*2/96,b=(i+1)*Math.PI*2/96;
            for(int band=0;band<3;band++) {
                float inner=radius*(1.35F+band*.25F),outer=inner+radius*.18F;
                quad(pose,out,band==1?0xFFB5A17B:0xFFDCC9A1,new float[][]{
                    ring(cx,cy,cz,inner,a),ring(cx,cy,cz,outer,a),ring(cx,cy,cz,outer,b),ring(cx,cy,cz,inner,b)});
            }
        }
    }
    private static float[] ring(float x,float y,float z,float r,double a){return new float[]{x+(float)Math.cos(a)*r,y+(float)Math.sin(a)*r*.42F,z+(float)Math.sin(a)*r*.91F};}
    public static int surfaceColor(Planet planet,float x,float y,float z) {
        double patch=Math.sin(x*13+Math.sin(z*9))*Math.cos(y*11-z*6);
        int bands=(int)Math.floor((y+1)*18);
        return switch(planet) {
            case EARTH->Math.abs(y)>.86?0xFFE5F3F7:patch>.38?0xFF4A8245:patch<-.85?0xFFD5E2DF:0xFF2268A9;
            case MOON->patch>.45?0xFF707782:0xFFBEC3CC;
            case MERCURY->patch>.15?0xFF736E68:0xFFABA59D;
            case VENUS->bands%3==0?0xFFDDA94E:0xFFE6C877;
            case MARS->Math.abs(y)>.93?0xFFD9C8B0:patch>.25?0xFF9C482A:0xFFD78348;
            case JUPITER->z<-.5&&Math.abs(x-.35)<.22&&Math.abs(y+.25)<.13?0xFFB65935:bands%3==0?0xFFA87656:bands%3==1?0xFFE4CDA9:0xFFC2946C;
            case SATURN->bands%3==0?0xFFB49B6F:0xFFE0CE9C;
            case URANUS->bands%4==0?0xFF76B9C5:0xFFA4DCDD;
            case NEPTUNE->bands%3==0?0xFF24428D:0xFF3F78C4;
        };
    }
    private static int shade(int rgb,float light){return 0xFF000000|((int)(((rgb>>16)&255)*light)<<16)|((int)(((rgb>>8)&255)*light)<<8)|(int)((rgb&255)*light);}
    private static void quad(Pose pose,VertexConsumer out,int color,float[][] points) {
        var transformed=new Vector3f();
        for(var p:points){pose.pose().transformPosition(p[0],p[1],p[2],transformed);out.addVertex(transformed.x,transformed.y,transformed.z,color,0,0,OverlayTexture.NO_OVERLAY,0xF000F0,0,1,0);}
    }
}

package dev.vic17usd.spacefabric.sphere;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Small block sphere with continuous, pole-safe tangent transport. */
public final class SphereGeometry {
    public static final Vec3 CENTER=new Vec3(.5,96.5,.5);
    public static final int RADIUS=24;
    private SphereGeometry() { }
    public static boolean solid(int x,int y,int z){return new Vec3(x+.5,y+.5,z+.5).subtract(CENTER).lengthSqr()<=RADIUS*RADIUS;}
    public static Vec3 tangent(Vec3 forward,Vec3 up){
        var t=forward.subtract(up.scale(forward.dot(up)));
        if(t.lengthSqr()<1e-8)t=(Math.abs(up.y)<.9?new Vec3(0,1,0):new Vec3(0,0,1)).cross(up);
        return t.normalize();
    }
    public static Vec3 rotate(Vec3 v,Vec3 axis,double angle){
        double c=Math.cos(angle),s=Math.sin(angle);
        return v.scale(c).add(axis.cross(v).scale(s)).add(axis.scale(axis.dot(v)*(1-c)));
    }
    public record Step(Vec3 up,Vec3 forward) { }
    public static Step step(Vec3 up,Vec3 forward,double ahead,double right,double distance){
        forward=tangent(forward,up);var movement=forward.scale(ahead).add(forward.cross(up).scale(right));
        if(movement.lengthSqr()<1e-8)return new Step(up,forward);
        var direction=movement.normalize();double angle=distance/RADIUS;
        var axis=up.cross(direction).normalize();var next=rotate(up,axis,angle).normalize();
        return new Step(next,tangent(rotate(forward,axis,angle),next));
    }
    public static double surface(Vec3 up){
        for(double r=RADIUS+2;r>=RADIUS-2;r-=.025){var p=CENTER.add(up.scale(r));if(solid((int)Math.floor(p.x),(int)Math.floor(p.y),(int)Math.floor(p.z)))return r+.12;}
        return RADIUS+.5;
    }
    public static Vec3 look(Vec3 forward,Vec3 up,float pitch){double a=Math.toRadians(pitch);return tangent(forward,up).scale(Math.cos(a)).subtract(up.scale(Math.sin(a))).normalize();}
    public static Quaternionf camera(Vec3 look,Vec3 up){return new Quaternionf().lookAlong(new Vector3f((float)look.x,(float)look.y,(float)look.z),new Vector3f((float)up.x,(float)up.y,(float)up.z)).conjugate();}
}

package dev.vic17usd.spacefabric.sphere;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SphereGeometryTest {
    @Test void aCompleteLapCrossesBothPolesAndReturnsWithoutAFrameFlip(){
        var up=new Vec3(0,1,0);var forward=new Vec3(0,0,1);boolean south=false;
        for(int i=0;i<2000;i++){
            var next=SphereGeometry.step(up,forward,1,0,2*Math.PI*SphereGeometry.RADIUS/2000);
            assertTrue(next.forward().dot(forward)>.999);assertEquals(0,next.up().dot(next.forward()),1e-9);
            up=next.up();forward=next.forward();if(up.y<-.999)south=true;
        }
        assertTrue(south);assertEquals(0,up.distanceTo(new Vec3(0,1,0)),1e-8);assertEquals(0,forward.distanceTo(new Vec3(0,0,1)),1e-8);
    }
    @Test void diagonalInputDoesNotIncreaseWalkingSpeedAndRightHasMinecraftHandedness(){
        var a=SphereGeometry.step(new Vec3(0,1,0),new Vec3(0,0,1),1,0,.16);
        var b=SphereGeometry.step(new Vec3(0,1,0),new Vec3(0,0,1),1,1,.16);
        assertEquals(a.up().y,b.up().y,1e-12);
        var right=SphereGeometry.step(new Vec3(0,1,0),new Vec3(0,0,1),0,1,.16);assertTrue(right.up().x<0);
    }
    @Test void surfaceSamplingPlacesFeetOutsideActualVoxelBlocksOnEverySide(){
        for(int latitude=-90;latitude<=90;latitude+=10)for(int longitude=0;longitude<360;longitude+=10){
            double a=Math.toRadians(latitude),b=Math.toRadians(longitude);var normal=new Vec3(Math.cos(a)*Math.cos(b),Math.sin(a),Math.cos(a)*Math.sin(b));
            double r=SphereGeometry.surface(normal);var foot=SphereGeometry.CENTER.add(normal.scale(r));var inside=SphereGeometry.CENTER.add(normal.scale(r-.14));
            assertFalse(SphereGeometry.solid((int)Math.floor(foot.x),(int)Math.floor(foot.y),(int)Math.floor(foot.z)));
            assertTrue(SphereGeometry.solid((int)Math.floor(inside.x),(int)Math.floor(inside.y),(int)Math.floor(inside.z)));
        }
    }
    @Test void cameraQuaternionMatchesForwardAndUpEvenWhenLookingStraightDown(){
        for(var normal:new Vec3[]{new Vec3(1,0,0),new Vec3(-1,0,0),new Vec3(0,1,0),new Vec3(0,-1,0),new Vec3(0,0,1),new Vec3(0,0,-1)}){
            var forward=SphereGeometry.tangent(new Vec3(0,0,1),normal);
            for(float pitch:new float[]{-90,-45,0,45,90}){
                var look=SphereGeometry.look(forward,normal,pitch);var left=normal.cross(forward).normalize();var up=look.cross(left).normalize();
                var q=SphereGeometry.camera(look,up);var actual=q.transform(new Vector3f(0,0,-1));var actualUp=q.transform(new Vector3f(0,1,0));
                assertEquals(look.x,actual.x,1e-5);assertEquals(look.y,actual.y,1e-5);assertEquals(look.z,actual.z,1e-5);
                assertEquals(up.x,actualUp.x,1e-5);assertEquals(up.y,actualUp.y,1e-5);assertEquals(up.z,actualUp.z,1e-5);
            }
        }
    }
}

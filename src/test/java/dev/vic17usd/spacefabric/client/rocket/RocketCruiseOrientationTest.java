package dev.vic17usd.spacefabric.client.rocket;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RocketCruiseOrientationTest {
    @Test void pointsAtTheActualDestinationDuringCruise() {
        for (float p:new float[]{.25f,.5f,.75f}) {
            var direction=RocketCruiseOrientation.destination(p).add(1,2,-3);
            var axis=new Vector3f(0,1,0).rotate(RocketCruiseOrientation.attitude(p,direction));
            assertTrue(axis.dot(new Vector3f(direction).normalize())>.9999f);
        }
    }
    @Test void easesInAndOutAndHandlesDegenerateDirection() {
        var direction=new Vector3f(3,2,12);
        assertEquals(0,RocketCruiseOrientation.blend(0));assertEquals(0,RocketCruiseOrientation.blend(1));
        assertTrue(RocketCruiseOrientation.blend(.1f)>0 && RocketCruiseOrientation.blend(.1f)<1);
        assertTrue(RocketCruiseOrientation.blend(.9f)>0 && RocketCruiseOrientation.blend(.9f)<1);
        assertEquals(1,new Vector3f(0,1,0).rotate(RocketCruiseOrientation.attitude(0,direction)).y,1e-6);
        assertEquals(1,new Vector3f(0,1,0).rotate(RocketCruiseOrientation.attitude(.5f,new Vector3f())).y,1e-6);
    }
}

package dev.vic17usd.spacefabric.client.rocket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RocketDepartureTest {
    @Test void departureStaysBehindAndRecedesSmoothly() {
        float previousZ=Float.POSITIVE_INFINITY, previousRadius=Float.POSITIVE_INFINITY;
        for(int i=0;i<=100;i++) {
            float progress=i/100F;
            var origin=RocketFlightScene.departurePosition(progress);
            var destination=RocketCruiseOrientation.destination(progress);
            assertEquals(0,origin.x,0.00001); assertTrue(origin.y<0); assertTrue(destination.z>0);
            assertTrue(origin.z<=previousZ);assertTrue(RocketFlightScene.departureRadius(progress)<=previousRadius);
            assertTrue(origin.length()>RocketFlightScene.departureRadius(progress));
            // Saturn has rings extending beyond its body; keep the whole departure
            // inside the 32-block backdrop at every frame.
            float extent=RocketFlightScene.departureRadius(progress)*2.2F;
            assertTrue(Math.abs(origin.y)+extent<32); assertTrue(Math.abs(origin.z)+extent<32);
            previousZ=origin.z; previousRadius=RocketFlightScene.departureRadius(progress);
        }
        assertEquals(8,RocketFlightScene.departurePosition(0).z,0.00001);
        assertEquals(-22,RocketFlightScene.departurePosition(1).z,0.00001);
    }
}

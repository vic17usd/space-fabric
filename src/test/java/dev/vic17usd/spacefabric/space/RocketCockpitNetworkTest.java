package dev.vic17usd.spacefabric.space;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RocketCockpitNetworkTest {
    @Test void serverRejectsInvalidPilotAndActiveFlight() {
        assertTrue(RocketCockpitNetwork.allowed(true,false,true,true,false,false,true));
        assertFalse(RocketCockpitNetwork.allowed(false,false,true,true,false,false,true));
        assertFalse(RocketCockpitNetwork.allowed(true,true,true,true,false,false,true));
        assertFalse(RocketCockpitNetwork.allowed(true,false,false,true,false,false,true));
        assertFalse(RocketCockpitNetwork.allowed(true,false,true,false,false,false,true));
        assertFalse(RocketCockpitNetwork.allowed(true,false,true,true,true,false,true));
        assertFalse(RocketCockpitNetwork.allowed(true,false,true,true,false,true,true));
        assertFalse(RocketCockpitNetwork.allowed(true,false,true,true,false,false,false));
    }
}

package dev.vic17usd.spacefabric.check;

import dev.vic17usd.spacefabric.client.colony.MarsGuideScreen;
import dev.vic17usd.spacefabric.colony.MarsGuideNetwork;
import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/** Development-only entrypoint. Opens the actual screen after joining a disposable test world; never alters that world.
 * Not included in the installable mod. Requires -PmarsGuideSmoke. */
public final class MarsGuideVisualCheck implements ClientModInitializer {
    private boolean opened;
    @Override public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!opened && client.player != null && client.level != null && client.screen == null && client.getOverlay() == null) {
                opened = true;
                client.setScreen(new MarsGuideScreen(new MarsGuideNetwork.State(0, Optional.empty(), false, 0, 0, 0)));
            }
        });
    }
}

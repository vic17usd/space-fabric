package dev.vic17usd.spacefabric.client.rocket;

import dev.vic17usd.spacefabric.space.RocketEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/** Orienta al piloto hacia el visor una sola vez al montar; luego conserva la mirada libre. */
public final class RocketCockpitView {
    private RocketEntity previousRocket;
    private RocketCockpitView() { }
    public static void initialize() {
        var view=new RocketCockpitView();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.getVehicle() instanceof RocketEntity rocket) {
                if (view.previousRocket != rocket) {
                    float yaw=rocket.getYRot();
                    client.player.setYRot(yaw); client.player.setXRot(0);
                    client.player.yRotO=yaw; client.player.xRotO=0;
                }
                view.previousRocket=rocket;
            } else view.previousRocket=null;
        });
    }
}

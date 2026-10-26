package dev.vic17usd.spacefabric.client.rocket;

/** Marca del snapshot visual: no cambia invisibilidad ni estado de la entidad en el servidor. */
public interface RocketPilotRenderState {
    boolean spaceFabric$isInsideRocket();
    void spaceFabric$setInsideRocket(boolean inside);
}

package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.colony.*;
import net.minecraft.network.chat.Component;

/** Shared labels and warning thresholds for the HUD and controller panel. */
public final class HabitatPresentation {
    private HabitatPresentation() { }
    public static Component condition(HabitatReadings r) {
        return Component.translatable("gui.space_fabric.habitat."+r.condition().name().toLowerCase(java.util.Locale.ROOT));
    }
    public static Component pressure(HabitatReadings r) {
        return Component.translatable("gui.space_fabric.habitat.pressure",r.regulated()?String.format(java.util.Locale.ROOT,"%.1f",r.pressureDeciKpa()/10.0):"—");
    }
    public static Component temperature(HabitatReadings r) {
        return Component.translatable("gui.space_fabric.habitat.temperature",r.regulated()?Integer.toString(r.temperatureCelsius()):"—");
    }
    public static int stateColor(HabitatReadings r){return !r.regulated()?0xFFFF8B78:r.lowReserve()!=HabitatReadings.Reserve.NONE?0xFFFFCD69:0xFF65E5BE;}
    public static Component warning(HabitatReadings r) {
        return r.lowReserve()==HabitatReadings.Reserve.NONE?condition(r):Component.translatable("gui.space_fabric.habitat.low_"+r.lowReserve().name().toLowerCase(java.util.Locale.ROOT));
    }
}

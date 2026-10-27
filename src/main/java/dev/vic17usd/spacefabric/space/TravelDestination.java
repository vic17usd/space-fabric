package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/** Lista cerrada del servidor: el cliente nunca envía una clave de dimensión arbitraria. */
public record TravelDestination(String id, ResourceKey<Level> world, Planet planet) {
    public static final List<TravelDestination> ALL;
    static {
        var destinations = new ArrayList<TravelDestination>();
        destinations.add(new TravelDestination("overworld", Level.OVERWORLD, Planet.EARTH));
        for (Planet planet : Planet.values()) destinations.add(new TravelDestination(planet.path(), PlanetDimensions.key(planet), planet));
        ALL = List.copyOf(destinations);
    }

    public static TravelDestination at(int index) {
        if (index < 0 || index >= ALL.size()) throw new IllegalArgumentException("Destino fuera de la lista");
        return ALL.get(index);
    }

    public static int indexOf(ResourceKey<Level> key) {
        for (int i = 0; i < ALL.size(); i++) if (ALL.get(i).world.equals(key)) return i;
        return -1;
    }

    public Component label() { return Component.translatable("destination.space_fabric." + id); }
}

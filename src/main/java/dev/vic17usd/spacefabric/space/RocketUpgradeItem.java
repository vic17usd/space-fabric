package dev.vic17usd.spacefabric.space;

import net.minecraft.world.item.Item;

/** Se instala sobre un cohete vacío y detenido; conserva UUID, combustible e inventario. */
public final class RocketUpgradeItem extends Item {
    private final RocketVariant variant;
    public RocketUpgradeItem(Properties properties,RocketVariant variant){super(properties.stacksTo(16));this.variant=variant;}
    public RocketVariant variant(){return variant;}
}

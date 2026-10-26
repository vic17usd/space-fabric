package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModSpaceEntities {
    public static final EntityType<RocketEntity> ROCKET = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            SpaceFabric.id("rocket"), EntityType.Builder.of(RocketEntity::new, MobCategory.MISC)
                    .sized(1.6F, 3.5F).passengerAttachments(1.15F).clientTrackingRange(10).updateInterval(1)
                    .fireImmune().build(ResourceKey.create(Registries.ENTITY_TYPE, SpaceFabric.id("rocket"))));
    private ModSpaceEntities() { }
    public static void initialize() { RocketFlightConfig.initialize(); }
}

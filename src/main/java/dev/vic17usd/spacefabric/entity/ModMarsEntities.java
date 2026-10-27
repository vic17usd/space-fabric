package dev.vic17usd.spacefabric.entity;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModMarsEntities {
    public static final net.minecraft.sounds.SoundEvent ALIEN_GREETING = Registry.register(BuiltInRegistries.SOUND_EVENT,
            SpaceFabric.id("alien_greeting"), net.minecraft.sounds.SoundEvent.createVariableRangeEvent(SpaceFabric.id("alien_greeting")));
    public static final EntityType<MartianCopperTraderEntity> COPPER_TRADER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, SpaceFabric.id("martian_copper_trader"),
            EntityType.Builder.of(MartianCopperTraderEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).eyeHeight(1.74F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SpaceFabric.id("martian_copper_trader"))));
    static { FabricDefaultAttributeRegistry.register(COPPER_TRADER, MartianCopperTraderEntity.createAttributes()); }
    private ModMarsEntities() { }
    public static void initialize() { }
}

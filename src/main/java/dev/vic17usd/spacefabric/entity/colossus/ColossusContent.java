package dev.vic17usd.spacefabric.entity.colossus;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;

/** Native registrations only: the attached mod is never loaded as a dependency. */
public final class ColossusContent {
    public static final EntityType<SulfurColossusEntity> TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,
        SpaceFabric.id("sulfur_colossus"),EntityType.Builder.of(SulfurColossusEntity::new,MobCategory.MONSTER)
        .sized(SulfurColossusRigProfile.WIDTH,SulfurColossusRigProfile.HEIGHT)
        .eyeHeight(SulfurColossusRigProfile.EYE_HEIGHT).fireImmune().clientTrackingRange(12)
        .build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("sulfur_colossus"))));
    public static final SoundEvent SULFUR_VENT_HISS=Registry.register(BuiltInRegistries.SOUND_EVENT,
        SpaceFabric.id("sulfur_vent_hiss"),SoundEvent.createVariableRangeEvent(SpaceFabric.id("sulfur_vent_hiss")));
    public static final Item COLOSSUS_CORE=Registry.register(BuiltInRegistries.ITEM,SpaceFabric.id("colossus_core"),
        new Item(properties("colossus_core").rarity(Rarity.EPIC).fireResistant()));
    public static final Item SPAWN_EGG=Registry.register(BuiltInRegistries.ITEM,SpaceFabric.id("sulfur_colossus_spawn_egg"),
        new SpawnEggItem(properties("sulfur_colossus_spawn_egg").spawnEgg(TYPE)));
    private static Item.Properties properties(String id){return new Item.Properties().setId(ResourceKey.create(Registries.ITEM,SpaceFabric.id(id)));}
    private ColossusContent() { }
    public static void initialize(){FabricDefaultAttributeRegistry.register(TYPE,SulfurColossusEntity.createAttributes());}
}

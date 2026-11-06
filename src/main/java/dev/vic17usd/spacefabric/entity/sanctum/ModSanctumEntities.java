package dev.vic17usd.spacefabric.entity.sanctum;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModSanctumEntities {
    public static final EntityType<BasaltEmberGolemEntity> GOLEM = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            SpaceFabric.id("basalt_ember_golem"), EntityType.Builder.of(BasaltEmberGolemEntity::new, MobCategory.MONSTER)
                    .sized(BasaltEmberGolemRigProfile.WIDTH, BasaltEmberGolemRigProfile.HEIGHT)
                    .eyeHeight(BasaltEmberGolemRigProfile.EYE_HEIGHT).fireImmune().clientTrackingRange(12)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SpaceFabric.id("basalt_ember_golem"))));
    public static final EntityType<BasaltBosswardenEntity> GUARD = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            SpaceFabric.id("basalt_bosswarden"), EntityType.Builder.of(BasaltBosswardenEntity::new, MobCategory.MONSTER)
                    .sized(BasaltBosswardenRigProfile.WIDTH, BasaltBosswardenRigProfile.HEIGHT)
                    .eyeHeight(BasaltBosswardenRigProfile.EYE_HEIGHT).fireImmune().clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SpaceFabric.id("basalt_bosswarden"))));
    static {
        FabricDefaultAttributeRegistry.register(GOLEM, BasaltEmberGolemEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GUARD, BasaltBosswardenEntity.createAttributes());
    }
    private ModSanctumEntities() { }
    public static void initialize() { }
}

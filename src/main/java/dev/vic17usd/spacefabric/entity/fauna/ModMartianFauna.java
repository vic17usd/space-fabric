package dev.vic17usd.spacefabric.entity.fauna;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.mixin.SpawnPlacementsAccessor;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ModMartianFauna {
    public static final SoundEvent CRAWLER_HUM = Registry.register(BuiltInRegistries.SOUND_EVENT,
            SpaceFabric.id("crystal_crawler_hum"), SoundEvent.createVariableRangeEvent(SpaceFabric.id("crystal_crawler_hum")));
    public static final EntityType<CrystalHumCrawlerEntity> CRAWLER = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            SpaceFabric.id("crystal_hum_crawler"), EntityType.Builder.of(CrystalHumCrawlerEntity::new, MobCategory.CREATURE)
                    .sized(1.05F, 0.91F).eyeHeight(0.35F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SpaceFabric.id("crystal_hum_crawler"))));
    public static final EntityType<GoldenEyeSandwraithEntity> SANDWRAITH = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            SpaceFabric.id("golden_eye_sandwraith"), EntityType.Builder.of(GoldenEyeSandwraithEntity::new, MobCategory.MONSTER)
                    .sized(1.9F, 3.7F).eyeHeight(3.35F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SpaceFabric.id("golden_eye_sandwraith"))));
    public static final EntityType<RedDuneIronmawEntity> IRONMAW = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            SpaceFabric.id("red_dune_ironmaw"), EntityType.Builder.of(RedDuneIronmawEntity::new, MobCategory.MONSTER)
                    .sized(3.7F, 8.7F).eyeHeight(7.7F).clientTrackingRange(12).fireImmune()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SpaceFabric.id("red_dune_ironmaw"))));
    static {
        FabricDefaultAttributeRegistry.register(CRAWLER, CrystalHumCrawlerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SANDWRAITH, GoldenEyeSandwraithEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(IRONMAW, RedDuneIronmawEntity.createAttributes());
    }
    private ModMartianFauna() { }
    public static void initialize() {
        SpawnPlacementsAccessor.spaceFabric$register(CRAWLER, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MartianSpawnRules::peaceful);
        SpawnPlacementsAccessor.spaceFabric$register(SANDWRAITH, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MartianSpawnRules::hostile);
        SpawnPlacementsAccessor.spaceFabric$register(IRONMAW, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MartianSpawnRules::hostile);
    }
}

package dev.vic17usd.spacefabric.entity.venus;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.mixin.SpawnPlacementsAccessor;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.worldgen.PlanetBiomes;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
public final class ModVenusEntities {
public static final Item ACID_BOTTLE=dev.vic17usd.spacefabric.item.ModItems.VENUS_ACID_BOTTLE;
public static final Item SULFUR_CORE=dev.vic17usd.spacefabric.item.ModItems.SULFUR_CORE;
public static final EntityType<AcidstoneSlimeEntity> ACIDSTONE_SLIME=Registry.register(BuiltInRegistries.ENTITY_TYPE,SpaceFabric.id("acidstone_slime"),EntityType.Builder.of(AcidstoneSlimeEntity::new,MobCategory.CREATURE).sized(AcidstoneSlimeRigProfile.WIDTH,AcidstoneSlimeRigProfile.HEIGHT).eyeHeight(AcidstoneSlimeRigProfile.EYE_HEIGHT).fireImmune().clientTrackingRange(12).build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("acidstone_slime"))));
public static final EntityType<ObsidianLavafangEntity> OBSIDIAN_LAVAFANG=Registry.register(BuiltInRegistries.ENTITY_TYPE,SpaceFabric.id("obsidian_lavafang"),EntityType.Builder.of(ObsidianLavafangEntity::new,MobCategory.MONSTER).sized(ObsidianLavafangRigProfile.WIDTH,ObsidianLavafangRigProfile.HEIGHT).eyeHeight(ObsidianLavafangRigProfile.EYE_HEIGHT).fireImmune().clientTrackingRange(12).build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("obsidian_lavafang"))));
public static final EntityType<AshplumeEmberwalkerEntity> ASHPLUME_EMBERWALKER=Registry.register(BuiltInRegistries.ENTITY_TYPE,SpaceFabric.id("ashplume_emberwalker"),EntityType.Builder.of(AshplumeEmberwalkerEntity::new,MobCategory.CREATURE).sized(AshplumeEmberwalkerRigProfile.WIDTH,AshplumeEmberwalkerRigProfile.HEIGHT).eyeHeight(AshplumeEmberwalkerRigProfile.EYE_HEIGHT).fireImmune().clientTrackingRange(12).build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("ashplume_emberwalker"))));
public static final EntityType<SulfurwingCopperHarbingerEntity> SULFURWING_COPPER_HARBINGER=Registry.register(BuiltInRegistries.ENTITY_TYPE,SpaceFabric.id("sulfurwing_copper_harbinger"),EntityType.Builder.of(SulfurwingCopperHarbingerEntity::new,MobCategory.MONSTER).sized(SulfurwingCopperHarbingerRigProfile.WIDTH,SulfurwingCopperHarbingerRigProfile.HEIGHT).eyeHeight(SulfurwingCopperHarbingerRigProfile.EYE_HEIGHT).fireImmune().clientTrackingRange(12).build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("sulfurwing_copper_harbinger"))));
public static void initialize(){FabricDefaultAttributeRegistry.register(ACIDSTONE_SLIME,AcidstoneSlimeEntity.createAttributes()); SpawnPlacementsAccessor.spaceFabric$register(ACIDSTONE_SLIME,SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,ModVenusEntities::surface);FabricDefaultAttributeRegistry.register(OBSIDIAN_LAVAFANG,ObsidianLavafangEntity.createAttributes()); SpawnPlacementsAccessor.spaceFabric$register(OBSIDIAN_LAVAFANG,SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,ModVenusEntities::surface);FabricDefaultAttributeRegistry.register(ASHPLUME_EMBERWALKER,AshplumeEmberwalkerEntity.createAttributes()); SpawnPlacementsAccessor.spaceFabric$register(ASHPLUME_EMBERWALKER,SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,ModVenusEntities::surface);FabricDefaultAttributeRegistry.register(SULFURWING_COPPER_HARBINGER,SulfurwingCopperHarbingerEntity.createAttributes()); SpawnPlacementsAccessor.spaceFabric$register(SULFURWING_COPPER_HARBINGER,SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,ModVenusEntities::surface);}
private static boolean surface(EntityType<? extends Mob> type,ServerLevelAccessor level,EntitySpawnReason reason,BlockPos pos,RandomSource random){
return level.getLevel().dimension().equals(PlanetDimensions.key(Planet.VENUS)) && level.getBiome(pos).is(PlanetBiomes.key(Planet.VENUS)) && level.canSeeSky(pos) && level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,pos.getX(),pos.getZ())==pos.getY() && level.getFluidState(pos).isEmpty() && Mob.checkMobSpawnRules(type,level,reason,pos,random) && (type.getCategory()!=MobCategory.MONSTER || level.getDifficulty()!=net.minecraft.world.Difficulty.PEACEFUL);
}
}

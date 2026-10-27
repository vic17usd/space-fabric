package dev.vic17usd.spacefabric.planet;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import dev.vic17usd.spacefabric.fluid.PlanetFluids;
import dev.vic17usd.spacefabric.worldgen.PlanetFeatures;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.block.HotVolcanicRockBlock;
import dev.vic17usd.spacefabric.block.PlanetRegolithBlock;
import dev.vic17usd.spacefabric.block.MethaneIceBlock;
import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.worldgen.OreGen;
import dev.vic17usd.spacefabric.worldgen.OrbitalMaterialFeature;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagLoader;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.item.crafting.Recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** Usa los codecs reales de 26.1 sin abrir un servidor ni aceptar su EULA. */
class WorldgenCodecTest {
    private static HolderLookup.Provider registries;

    @BeforeAll
    static void initializeRegistries() throws IOException {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        new SpaceFabric().onInitialize();
        var pack = new PathPackResources(
                new PackLocationInfo("space_fabric_test",
                        Component.literal("Planet worldgen test"),
                        PackSource.DEFAULT, Optional.empty()),
                Path.of("src/main/resources"));
        try (var resources = new MultiPackResourceManager(
                PackType.SERVER_DATA, List.of(
                        ServerPacksSource.createVanillaPackSource(), pack))) {
            var builtins = RegistryAccess.fromRegistryOfRegistries(
                    BuiltInRegistries.REGISTRY);
            BuiltInRegistries.REGISTRY.forEach(registry -> {
                if (registry instanceof WritableRegistry<?> writable) {
                    TagLoader.loadTagsForRegistry(resources, writable);
                }
                registry.freeze();
            });
            var loaded = RegistryDataLoader.load(resources,
                    builtins.listRegistries().toList(),
                    RegistryDataLoader.WORLDGEN_REGISTRIES, Runnable::run).join();
            registries = HolderLookup.Provider.create(Stream.concat(
                    builtins.listRegistries(), loaded.listRegistries()));
            var dimensions = RegistryDataLoader.load(resources, registries.listRegistries().toList(),
                    RegistryDataLoader.DIMENSION_REGISTRIES, Runnable::run).join();
            var baked = loaded.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(
                    net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL).value().createWorldDimensions()
                    .bake(dimensions.lookupOrThrow(Registries.LEVEL_STEM)).dimensionsRegistryAccess();
            var serverRegistries = new RegistryAccess.ImmutableRegistryAccess(Stream.concat(
                    Stream.concat(builtins.registries(), loaded.registries()), baked.registries())).freeze();
            BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(serverRegistries)
                    .forEach(net.minecraft.core.component.DataComponentInitializers.PendingComponents::apply);
            // El servidor ejecuta este paso después de combinar datapacks y dimensiones vanilla.
            // En JUnit lo invocamos explícitamente para comprobar las modificaciones reales de Fabric.
            net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl.INSTANCE.finalizeWorldGen(serverRegistries);
            registries = serverRegistries;
            // Incluye biomas, features, ruido, carvers y dimension_type propios.
        }
    }

    @ParameterizedTest
    @EnumSource(Planet.class)
    void dimensionTypeIsAcceptedByMinecraft(Planet planet) throws IOException {
        DimensionType type = decodeType(planet);
        assertEquals(planet.height(), type.height());
        assertEquals(planet.minY(), type.minY());
        JsonElement data = resource("dimension_type/" + planet.path());
        var attributes = data.getAsJsonObject().getAsJsonObject("attributes");
        assertEquals(planet.skyColor(), attributes.get("minecraft:visual/sky_color").getAsString());
        assertEquals(planet.fogColor(), attributes.get("minecraft:visual/fog_color").getAsString());
    }

    @ParameterizedTest
    @EnumSource(Planet.class)
    void dimensionAndGeneratorResolveAgainstRealRegistries(Planet planet) throws IOException {
        LevelStem stem = LevelStem.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),
                resource("dimension/" + planet.path())).getOrThrow();
        assertNotNull(stem.generator());
        assertTrue(stem.generator().getMinY() >= planet.minY());
        assertTrue(stem.generator().getMinY() + stem.generator().getGenDepth()
                <= planet.minY() + planet.height());
        assertEquals("space_fabric:" + planet.path(), stem.type().unwrapKey().orElseThrow().identifier().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"decompression", "crushing_pressure", "hypoxia", "thermal_exposure"})
    void damageTypeIsAcceptedByMinecraft(String path) throws IOException {
        DamageType type = DamageType.DIRECT_CODEC.parse(JsonOps.INSTANCE,
                resource("damage_type/" + path)).getOrThrow();
        assertEquals("space_fabric." + path, type.msgId());
    }

    private static DimensionType decodeType(Planet planet) throws IOException {
        return DimensionType.DIRECT_CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),
                resource("dimension_type/" + planet.path())).getOrThrow();
    }

    @ParameterizedTest
    @EnumSource(value = Planet.class, names = {"JUPITER", "SATURN", "URANUS", "NEPTUNE"})
    void giantsHaveNoSolidLayersButRetainOrbitalDecoration(Planet planet) throws IOException {
        var generator = resource("dimension/" + planet.path()).getAsJsonObject().getAsJsonObject("generator");
        assertEquals("space_fabric:orbital", generator.get("type").getAsString());
        assertTrue(generator.getAsJsonObject("settings").getAsJsonArray("layers").isEmpty());
        assertTrue(generator.getAsJsonObject("settings").get("features").getAsBoolean());
        var stem = LevelStem.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),
                resource("dimension/" + planet.path())).getOrThrow();
        var biome = registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(planet));
        var expected = switch (planet) {
            case JUPITER -> OreGen.JOVIAN_METEORITE;
            case SATURN -> OreGen.SATURN_RING_ICE;
            case URANUS -> OreGen.URANIAN_METHANE_ICE;
            case NEPTUNE -> OreGen.NEPTUNIAN_METHANE_ICE;
            default -> throw new IllegalArgumentException("No es un gigante");
        };
        assertTrue(stem.generator().getBiomeGenerationSettings(biome).features().stream()
                .flatMap(net.minecraft.core.HolderSet::stream).anyMatch(feature -> feature.is(expected)));
    }

    @Test
    void viscousLavaUsesItsOwnBlockAndLavaTag() {
        var fluid = PlanetFluids.VISCOUS_LAVA;
        var state = fluid.defaultFluidState();
        assertEquals(80, fluid.getTickDelay(null));
        assertEquals(PlanetFluids.VISCOUS_LAVA_BLOCK,
                state.createLegacyBlock().getBlock());
        assertTrue(state.is(FluidTags.LAVA));
    }

    static Stream<String> materialNames() { return ModBlocks.all().keySet().stream(); }

    @ParameterizedTest
    @MethodSource("materialNames")
    void blockHasRegisteredItemAndValidLoot(String path) throws IOException {
        var block = ModBlocks.all().get(path);
        var id = SpaceFabric.id(path);
        assertEquals(id, BuiltInRegistries.BLOCK.getKey(block));
        assertEquals(block.asItem(), BuiltInRegistries.ITEM.getValue(id));
        assertEquals(SpaceFabric.id("blocks/" + path), block.getLootTable().orElseThrow().identifier());
        assertNotNull(LootTable.DIRECT_CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),
                resource("loot_table/blocks/" + path)).getOrThrow());
    }

    @Test
    void thermalBootsProtectButOrdinaryBootsDoNot() throws IOException {
        assertTrue(HotVolcanicRockBlock.protectsFromHeat(new ItemStack(ModItems.THERMAL_BOOTS)));
        assertFalse(HotVolcanicRockBlock.protectsFromHeat(new ItemStack(Items.IRON_BOOTS)));
        assertFalse(HotVolcanicRockBlock.protectsFromHeat(ItemStack.EMPTY));
        assertEquals(net.minecraft.world.entity.EquipmentSlot.FEET,
                ModItems.THERMAL_BOOTS.components().get(net.minecraft.core.component.DataComponents.EQUIPPABLE).slot());
        assertNotNull(Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),
                resource("recipe/thermal_boots")).getOrThrow());
    }

    @Test
    void regolithFallsAndMethaneIceIsMoreSlipperyThanBlueIce() {
        assertInstanceOf(PlanetRegolithBlock.class, ModBlocks.MERCURY_REGOLITH);
        assertInstanceOf(PlanetRegolithBlock.class, ModBlocks.LUNAR_REGOLITH);
        assertTrue(net.minecraft.world.level.block.FallingBlock.isFree(Blocks.AIR.defaultBlockState()));
        assertFalse(net.minecraft.world.level.block.FallingBlock.isFree(Blocks.STONE.defaultBlockState()));
        assertInstanceOf(MethaneIceBlock.class, ModBlocks.URANIAN_METHANE_ICE);
        assertTrue(ModBlocks.URANIAN_METHANE_ICE.getFriction() > Blocks.BLUE_ICE.getFriction());
        assertTrue(ModBlocks.NEPTUNIAN_METHANE_ICE.getFriction() > Blocks.BLUE_ICE.getFriction());
    }

    @Test
    void martianOresNeedDiamondToolsAndHaveTheirOwnDrops() {
        assertFalse(new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(ModBlocks.ARES_CRYSTAL_ORE.defaultBlockState()));
        assertTrue(new ItemStack(Items.DIAMOND_PICKAXE).isCorrectToolForDrops(ModBlocks.ARES_CRYSTAL_ORE.defaultBlockState()));
        assertFalse(new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(ModBlocks.PHOBOSITE_ORE.defaultBlockState()));
        assertTrue(new ItemStack(Items.DIAMOND_PICKAXE).isCorrectToolForDrops(ModBlocks.PHOBOSITE_ORE.defaultBlockState()));
    }

    @Test
    void orbitalBandDoesNotCreateAPlanetarySurface() {
        assertFalse(OrbitalMaterialFeature.inOrbitalBand(0, 0));
        assertTrue(OrbitalMaterialFeature.inOrbitalBand(600, 0));
        assertFalse(OrbitalMaterialFeature.inOrbitalBand(800, 0));
        assertFalse(OrbitalMaterialFeature.inOrbitalBand(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test
    void fabricAddsExclusiveMartianOresToTheBiome() {
        var biome = registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS)).value();
        var ores = biome.getGenerationSettings().features().get(
                net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES.ordinal());
        assertTrue(ores.stream().anyMatch(feature -> feature.is(OreGen.ARES_CRYSTAL)));
        assertTrue(ores.stream().anyMatch(feature -> feature.is(OreGen.PHOBOSITE)));
    }

    @ParameterizedTest
    @EnumSource(Planet.class)
    void independentClockAndTimelineDriveActualEnvironmentAttributes(Planet planet) throws IOException {
        var type = decodeType(planet);
        var key = dev.vic17usd.spacefabric.environment.PlanetRotation.key(planet);
        assertEquals(key, type.defaultClock().orElseThrow().unwrapKey().orElseThrow());
        var timeline = registries.lookupOrThrow(Registries.TIMELINE).getOrThrow(
                net.minecraft.resources.ResourceKey.create(Registries.TIMELINE, SpaceFabric.id(planet.path() + "_day")));
        assertEquals(key, timeline.value().clock().unwrapKey().orElseThrow());
        assertEquals(24000, timeline.value().periodTicks().orElseThrow());
        var day = net.minecraft.world.attribute.EnvironmentAttributeSystem.builder().addConstantLayer(type.attributes())
                .addTimelineLayer(timeline, clock -> 6000).build();
        var night = net.minecraft.world.attribute.EnvironmentAttributeSystem.builder().addConstantLayer(type.attributes())
                .addTimelineLayer(timeline, clock -> 18000).build();
        assertTrue(day.getDimensionValue(net.minecraft.world.attribute.EnvironmentAttributes.SKY_LIGHT_FACTOR)
                > night.getDimensionValue(net.minecraft.world.attribute.EnvironmentAttributes.SKY_LIGHT_FACTOR));
        if (dev.vic17usd.spacefabric.environment.PlanetEnvironment.airless(planet)) {
            assertEquals(0, day.getDimensionValue(net.minecraft.world.attribute.EnvironmentAttributes.SKY_COLOR) & 0xFFFFFF);
            assertEquals(0, night.getDimensionValue(net.minecraft.world.attribute.EnvironmentAttributes.SKY_COLOR) & 0xFFFFFF);
            assertTrue(day.getDimensionValue(net.minecraft.world.attribute.EnvironmentAttributes.STAR_BRIGHTNESS) > 0);
        }
    }

    @Test
    void weatherStateSurvivesCodecRoundTrip() {
        var original = new net.minecraft.world.level.saveddata.WeatherData();
        original.setRaining(true);
        original.setRainTime(2400);
        original.setClearWeatherTime(0);
        var codec = net.minecraft.world.level.saveddata.WeatherData.CODEC;
        var restored = codec.parse(JsonOps.INSTANCE, codec.encodeStart(JsonOps.INSTANCE, original).getOrThrow()).getOrThrow();
        assertTrue(restored.isRaining());
        assertEquals(2400, restored.getRainTime());
    }

    @ParameterizedTest
    @EnumSource(value = Planet.class, names = {"URANUS", "NEPTUNE"})
    void frozenCaveActuallyBuildsAnIceShellWithChamberAndEntrance(Planet planet) {
        var world = new VoxelWorld(37, 0);
        var ice = planet == Planet.URANUS ? ModBlocks.URANIAN_METHANE_ICE : ModBlocks.NEPTUNIAN_METHANE_ICE;
        var feature = new dev.vic17usd.spacefabric.worldgen.FrozenCaveFeature();
        boolean placed = feature.place(new net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<>(
                Optional.empty(), world.level, null, net.minecraft.util.RandomSource.create(42),
                new net.minecraft.core.BlockPos(592, -64, 0),
                new net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration(ice.defaultBlockState())));
        assertTrue(placed);
        assertTrue(world.blocks.size() > 1000);
        assertTrue(world.state(new net.minecraft.core.BlockPos(600, 208, 8)).isAir());
        assertTrue(world.state(new net.minecraft.core.BlockPos(600, 203, 8)).is(ice));
        assertTrue(world.state(new net.minecraft.core.BlockPos(600, 213, 8)).is(ice));
        assertTrue(world.state(new net.minecraft.core.BlockPos(600, 211, 14)).isAir());
    }

    @Test
    void frozenCaveDoesNotGenerateOutsideTheOrbitalBand() {
        var world = new VoxelWorld(0, 0);
        assertFalse(new dev.vic17usd.spacefabric.worldgen.FrozenCaveFeature().place(
                new net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<>(Optional.empty(), world.level, null,
                        net.minecraft.util.RandomSource.create(42), new net.minecraft.core.BlockPos(0, -64, 0),
                        new net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration(
                                ModBlocks.URANIAN_METHANE_ICE.defaultBlockState()))));
        assertTrue(world.blocks.isEmpty());
    }

    @Test
    void weatherMixinIsAppliedToTheRealServerClass() {
        var methods = java.util.Arrays.stream(net.minecraft.server.level.ServerLevel.class.getDeclaredMethods())
                .map(java.lang.reflect.Method::getName).toList();
        assertTrue(methods.stream().anyMatch(name -> name.contains("spaceFabric$weather")));
        assertTrue(methods.stream().anyMatch(name -> name.contains("keepVanillaWeatherOutOfPlanets")));
    }

    @Test
    void clientMixinTargetsMatchTheActual26_1DescriptorsWithoutLoadingGraphics() throws IOException {
        assertClientTarget("net/minecraft/client/renderer/entity/EntityRenderDispatcher", "extractEntity",
                "(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;");
        assertClientTarget("net/minecraft/client/renderer/entity/EntityRenderDispatcher", "submit",
                "(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V");
        assertClientTarget("net/minecraft/client/renderer/fog/FogRenderer", "setupFog",
                "(Lnet/minecraft/client/Camera;ILnet/minecraft/client/DeltaTracker;FLnet/minecraft/client/multiplayer/ClientLevel;)Lnet/minecraft/client/renderer/fog/FogData;");
        assertClientTarget("net/minecraft/client/renderer/SkyRenderer", "extractRenderState",
                "(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/state/level/SkyRenderState;)V");
        assertClientTarget("net/minecraft/client/renderer/SkyRenderer", "renderSun", "(FLcom/mojang/blaze3d/vertex/PoseStack;)V");
        assertClientTarget("net/minecraft/client/renderer/SkyRenderer", "renderMoon", "(Lnet/minecraft/world/level/MoonPhase;FLcom/mojang/blaze3d/vertex/PoseStack;)V");
    }

    @Test
    void environmentalFeaturesAreWiredIntoTheRightBiomes() {
        for (var planet : new Planet[] {Planet.MARS, Planet.URANUS, Planet.NEPTUNE}) {
            String name = planet == Planet.MARS ? "mars_crater" : planet.path() + "_frozen_cave";
            var key = net.minecraft.resources.ResourceKey.create(Registries.PLACED_FEATURE, SpaceFabric.id(name));
            var biome = registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                    dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(planet));
            assertTrue(biome.value().getGenerationSettings().features().stream()
                    .flatMap(net.minecraft.core.HolderSet::stream).anyMatch(feature -> feature.is(key)));
        }
    }

    @Test
    void denseIronReallyRequiresNetheriteAndCraterIceDoesNotTickMelt() {
        var state = ModBlocks.DENSE_IRON_ORE.defaultBlockState();
        assertFalse(new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(state));
        assertFalse(new ItemStack(Items.DIAMOND_PICKAXE).isCorrectToolForDrops(state));
        assertTrue(new ItemStack(Items.NETHERITE_PICKAXE).isCorrectToolForDrops(state));
        assertInstanceOf(dev.vic17usd.spacefabric.block.CraterIceBlock.class, ModBlocks.CRATER_ICE);
        assertFalse(ModBlocks.CRATER_ICE.defaultBlockState().isRandomlyTicking());
        assertInstanceOf(PlanetRegolithBlock.class, ModBlocks.IRON_OXIDE_REGOLITH);
    }

    @Test
    void venusianGeologyUsesFunctionalContactHazardClasses() {
        assertInstanceOf(HotVolcanicRockBlock.class, ModBlocks.VULCANIZED_BASALT);
        assertInstanceOf(HotVolcanicRockBlock.class, ModBlocks.SULFUR_DEPOSIT);
        assertInstanceOf(dev.vic17usd.spacefabric.block.CondensedAcidBlock.class, ModBlocks.CONDENSED_SULFURIC_ACID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"dense_iron_chunk", "crater_ice_shard", "venusian_olivine", "pyroxene", "sulfur_crystal",
            "acid_condensate", "iron_oxide_dust", "martian_olivine", "sulfate_dust"})
    void geologyMaterialsAreRegisteredAndHaveNamesAndInventoryModels(String name) throws IOException {
        assertEquals(ModItems.all().get(name), BuiltInRegistries.ITEM.getValue(SpaceFabric.id(name)));
        for (String locale : new String[] {"es_es", "en_us"}) {
            try (var input = WorldgenCodecTest.class.getResourceAsStream("/assets/space_fabric/lang/" + locale + ".json")) {
                assertNotNull(input);
                assertTrue(JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject()
                        .has("item.space_fabric." + name));
            }
        }
        assertNotNull(WorldgenCodecTest.class.getResource("/assets/space_fabric/items/" + name + ".json"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"dense_iron_chunk_smelting", "dense_iron_chunk_blasting", "iron_oxide_dust_smelting", "iron_oxide_dust_blasting"})
    void refiningRecipesActuallyMatchAndProduceIron(String name) throws IOException {
        var recipe = assertInstanceOf(net.minecraft.world.item.crafting.AbstractCookingRecipe.class,
                Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), resource("recipe/" + name)).getOrThrow());
        boolean dense = name.startsWith("dense");
        var input = new net.minecraft.world.item.crafting.SingleRecipeInput(new ItemStack(dense ? ModItems.DENSE_IRON_CHUNK : ModItems.IRON_OXIDE_DUST));
        assertTrue(recipe.matches(input, null));
        assertEquals(dense ? Items.IRON_INGOT : Items.IRON_NUGGET, recipe.assemble(input).getItem());
    }

    @Test
    void ironOxideCanBeCraftedIntoRedPigment() throws IOException {
        var recipe = assertInstanceOf(net.minecraft.world.item.crafting.ShapelessRecipe.class,
                Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), resource("recipe/iron_oxide_red_dye")).getOrThrow());
        var input = net.minecraft.world.item.crafting.CraftingInput.of(1, 1, List.of(new ItemStack(ModItems.IRON_OXIDE_DUST)));
        assertTrue(recipe.matches(input, null));
        assertEquals(Items.RED_DYE, recipe.assemble(input).getItem());
    }

    @Test
    void geologicalDepositsAreRestrictedToTheirSelectedPlanetBiomes() {
        var expected = java.util.Map.of(
                OreGen.DENSE_IRON, Planet.MERCURY, OreGen.CRATER_ICE, Planet.MERCURY,
                OreGen.VENUSIAN_OLIVINE, Planet.VENUS, OreGen.PYROXENE, Planet.VENUS,
                OreGen.SULFUR, Planet.VENUS, OreGen.CONDENSED_ACID, Planet.VENUS,
                OreGen.IRON_OXIDE, Planet.MARS, OreGen.MARTIAN_OLIVINE, Planet.MARS, OreGen.MARTIAN_SULFATE, Planet.MARS);
        for (var entry : expected.entrySet()) for (var planet : Planet.values()) {
            if (planet == Planet.EARTH) continue;
            var biome = registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                    dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(planet));
            boolean present = biome.value().getGenerationSettings().features().stream()
                    .flatMap(net.minecraft.core.HolderSet::stream).anyMatch(feature -> feature.is(entry.getKey()));
            assertEquals(entry.getValue() == planet, present, entry.getKey() + " en " + planet);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-257, 256})
    void craterIceIsGeneratedWithAnOpaqueRoofInBothPolarBands(int chunkZ) {
        var world = new VoxelWorld(0, chunkZ, 60);
        var origin = new net.minecraft.core.BlockPos(0, -64, chunkZ << 4);
        var context = new net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<>(Optional.empty(), world.level, null,
                net.minecraft.util.RandomSource.create(42), origin,
                new net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration(ModBlocks.CRATER_ICE.defaultBlockState()));
        assertTrue(new dev.vic17usd.spacefabric.worldgen.ShadowCraterIceFeature().place(context));
        var ice = world.blocks.entrySet().stream().filter(entry -> entry.getValue().is(ModBlocks.CRATER_ICE)).toList();
        assertFalse(ice.isEmpty());
        for (var entry : ice) {
            assertTrue(world.state(entry.getKey().above(3)).is(ModBlocks.MERCURY_SILICATE));
            assertTrue(world.state(entry.getKey().above()).isAir());
            assertFalse(world.level.canSeeSky(entry.getKey().above()));
        }
    }

    @Test
    void noColdTrapIsPlacedInTheEquatorialBand() {
        var world = new VoxelWorld(0, 0, 60);
        assertFalse(new dev.vic17usd.spacefabric.worldgen.ShadowCraterIceFeature().place(
                new net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<>(Optional.empty(), world.level, null,
                        net.minecraft.util.RandomSource.create(42), new net.minecraft.core.BlockPos(0, -64, 0),
                        new net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration(ModBlocks.CRATER_ICE.defaultBlockState()))));
        assertTrue(world.blocks.isEmpty());
    }

    private static void assertClientTarget(String owner, String name, String descriptor) throws IOException {
        try (var stream = WorldgenCodecTest.class.getResourceAsStream("/" + owner + ".class")) {
            assertNotNull(stream);
            var methods = new java.util.HashSet<String>();
            new org.objectweb.asm.ClassReader(stream).accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                @Override public org.objectweb.asm.MethodVisitor visitMethod(int access, String method, String desc, String signature, String[] exceptions) {
                    methods.add(method + desc);
                    return null;
                }
            }, org.objectweb.asm.ClassReader.SKIP_CODE);
            assertTrue(methods.contains(name + descriptor), owner + "." + name);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"rocket", "fuel_refinery", "empty_fuel_cell", "landing_pad"})
    void spaceEquipmentCanActuallyBeCraftedWithVanillaIngredients(String name) throws IOException {
        var recipe = assertInstanceOf(net.minecraft.world.item.crafting.ShapedRecipe.class,
                Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), resource("recipe/" + name)).getOrThrow());
        var empty = ItemStack.EMPTY;
        java.util.function.Function<net.minecraft.world.item.Item, ItemStack> stack = ItemStack::new;
        List<ItemStack> ingredients = switch (name) {
            case "rocket" -> List.of(empty, stack.apply(Items.IRON_BLOCK), empty,
                    stack.apply(Items.IRON_BLOCK), stack.apply(Items.COPPER_BLOCK), stack.apply(Items.IRON_BLOCK),
                    stack.apply(Items.REDSTONE_BLOCK), stack.apply(Items.FURNACE), stack.apply(Items.REDSTONE_BLOCK));
            case "fuel_refinery" -> List.of(stack.apply(Items.IRON_INGOT), stack.apply(Items.IRON_INGOT), stack.apply(Items.IRON_INGOT),
                    stack.apply(Items.IRON_INGOT), stack.apply(Items.FURNACE), stack.apply(Items.IRON_INGOT),
                    stack.apply(Items.IRON_INGOT), stack.apply(Items.REDSTONE), stack.apply(Items.IRON_INGOT));
            case "empty_fuel_cell" -> List.of(empty, stack.apply(Items.IRON_INGOT), empty,
                    stack.apply(Items.IRON_INGOT), stack.apply(Items.GLASS), stack.apply(Items.IRON_INGOT), empty, stack.apply(Items.IRON_INGOT), empty);
            default -> List.of(stack.apply(Items.IRON_INGOT), stack.apply(Items.IRON_INGOT), stack.apply(Items.IRON_INGOT),
                    stack.apply(Items.STONE), stack.apply(Items.STONE), stack.apply(Items.STONE));
        };
        var input = net.minecraft.world.item.crafting.CraftingInput.of(3, name.equals("landing_pad") ? 2 : 3, ingredients);
        assertTrue(recipe.matches(input, null));
        assertEquals(SpaceFabric.id(name), BuiltInRegistries.ITEM.getKey(recipe.assemble(input).getItem()));
    }

    private static net.minecraft.world.SimpleContainer refineryInput(net.minecraft.world.item.Item base, net.minecraft.world.item.Item reagent) {
        return new net.minecraft.world.SimpleContainer(new ItemStack(base, 2), new ItemStack(reagent, 2),
                new ItemStack(ModItems.EMPTY_FUEL_CELL, 2), ItemStack.EMPTY);
    }

    @Test void refineryConsumesOnlyAtCompletionAndReturnsExactlyOneFilledCell() {
        var process = new dev.vic17usd.spacefabric.machine.RefineryProcess();
        var inventory = refineryInput(Items.COAL, Items.BLAZE_POWDER);
        for (int tick = 0; tick < 199; tick++) process.tick(inventory);
        assertTrue(inventory.getItem(3).isEmpty()); assertEquals(2, inventory.getItem(0).getCount());
        process.tick(inventory);
        for (int slot = 0; slot < 3; slot++) assertEquals(1, inventory.getItem(slot).getCount());
        assertTrue(inventory.getItem(3).is(ModItems.ROCKET_FUEL_CELL)); assertEquals(1, inventory.getItem(3).getCount());
        assertEquals(0, process.progress());
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void planetaryFuelUsesEitherAcidOrSulfate(boolean acid) {
        var process = new dev.vic17usd.spacefabric.machine.RefineryProcess();
        var inventory = refineryInput(ModItems.SULFUR_CRYSTAL, acid ? ModItems.ACID_CONDENSATE : ModItems.SULFATE_DUST);
        for (int tick = 0; tick < 160; tick++) process.tick(inventory);
        assertEquals(1, inventory.getItem(3).getCount()); assertEquals(1, inventory.getItem(0).getCount());
        assertEquals(160, process.duration());
    }

    @ParameterizedTest @ValueSource(booleans = {false, true})
    void refineryPausesWhenOutputIsFullOrContainsAnotherItem(boolean full) {
        var process = new dev.vic17usd.spacefabric.machine.RefineryProcess();
        var inventory = refineryInput(Items.CHARCOAL, Items.BLAZE_POWDER);
        for (int tick = 0; tick < 100; tick++) process.tick(inventory);
        inventory.setItem(3, new ItemStack(full ? ModItems.ROCKET_FUEL_CELL : Items.DIAMOND, full ? 16 : 1));
        for (int tick = 0; tick < 400; tick++) process.tick(inventory);
        assertEquals(100, process.progress()); assertEquals(2, inventory.getItem(2).getCount());
        inventory.setItem(3, ItemStack.EMPTY);
        for (int tick = 0; tick < 100; tick++) process.tick(inventory);
        assertTrue(inventory.getItem(3).is(ModItems.ROCKET_FUEL_CELL));
    }

    @Test void switchingFuelRecipesResetsProgressAndWrongMixturesDoNothing() {
        var process = new dev.vic17usd.spacefabric.machine.RefineryProcess();
        var inventory = refineryInput(Items.COAL, Items.BLAZE_POWDER);
        for (int tick = 0; tick < 199; tick++) process.tick(inventory);
        inventory.setItem(0, new ItemStack(ModItems.SULFUR_CRYSTAL));
        inventory.setItem(1, new ItemStack(ModItems.SULFATE_DUST));
        process.tick(inventory); assertEquals(1, process.progress()); assertTrue(inventory.getItem(3).isEmpty());
        inventory.setItem(1, new ItemStack(Items.BLAZE_POWDER));
        process.tick(inventory); assertEquals(0, process.progress()); assertTrue(inventory.getItem(3).isEmpty());
        assertEquals(1, inventory.getItem(0).getCount());
    }

    @Test void refineryRestoresRealBlockEntityInventoryAndPartialProgressFromNbt() {
        var pos = net.minecraft.core.BlockPos.ZERO;
        var state = ModBlocks.FUEL_REFINERY.defaultBlockState();
        var original = new dev.vic17usd.spacefabric.machine.FuelRefineryBlockEntity(pos, state);
        original.setItem(0, new ItemStack(Items.COAL)); original.setItem(1, new ItemStack(Items.BLAZE_POWDER));
        original.setItem(2, new ItemStack(ModItems.EMPTY_FUEL_CELL));
        for (int tick = 0; tick < 100; tick++) dev.vic17usd.spacefabric.machine.FuelRefineryBlockEntity.tick(null, pos, state, original);
        var restored = assertInstanceOf(dev.vic17usd.spacefabric.machine.FuelRefineryBlockEntity.class,
                net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pos, state, original.saveWithFullMetadata(registries), registries));
        assertEquals(100, restored.data.get(0));
        for (int tick = 0; tick < 100; tick++) dev.vic17usd.spacefabric.machine.FuelRefineryBlockEntity.tick(null, pos, state, restored);
        assertTrue(restored.getItem(3).is(ModItems.ROCKET_FUEL_CELL));
        assertTrue(restored.getItem(0).isEmpty()); assertTrue(restored.getItem(2).isEmpty());
    }

    @Test void refineryRejectsHopperInsertionIntoOutputAndOnlyExtractsFinishedFuel() {
        var refinery = new dev.vic17usd.spacefabric.machine.FuelRefineryBlockEntity(net.minecraft.core.BlockPos.ZERO, ModBlocks.FUEL_REFINERY.defaultBlockState());
        assertFalse(refinery.canPlaceItem(3, new ItemStack(ModItems.ROCKET_FUEL_CELL)));
        assertTrue(refinery.canPlaceItem(2, new ItemStack(ModItems.EMPTY_FUEL_CELL)));
        assertFalse(refinery.canTakeItemThroughFace(0, new ItemStack(Items.COAL), net.minecraft.core.Direction.DOWN));
        assertTrue(refinery.canTakeItemThroughFace(3, new ItemStack(ModItems.ROCKET_FUEL_CELL), net.minecraft.core.Direction.DOWN));
    }

    @ParameterizedTest @ValueSource(ints = {-1, 0, 751, 3000, Integer.MAX_VALUE})
    void packedRocketKeepsFractionalFuelThroughNativeItemSerialization(int fuel) {
        var stack = dev.vic17usd.spacefabric.space.RocketItem.packed(fuel);
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        var serialized = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow();
        var restored = ItemStack.CODEC.parse(ops, serialized).getOrThrow();
        assertEquals(Math.clamp(fuel, 0, 3000), dev.vic17usd.spacefabric.space.RocketItem.storedFuel(restored));
    }

    @Test void rocketCustomNbtRestoresTankDestinationAndCargoWithoutStaleSlots() throws Exception {
        var rocket = new dev.vic17usd.spacefabric.space.RocketEntity(dev.vic17usd.spacefabric.space.ModSpaceEntities.ROCKET, null);
        var write = rocket.getClass().getDeclaredMethod("addAdditionalSaveData", net.minecraft.world.level.storage.ValueOutput.class);
        var read = rocket.getClass().getDeclaredMethod("readAdditionalSaveData", net.minecraft.world.level.storage.ValueInput.class);
        write.setAccessible(true); read.setAccessible(true);
        var input = new net.minecraft.nbt.CompoundTag(); input.putInt("Fuel", 751); input.putInt("Destination", 5);
        read.invoke(rocket, net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, registries, input));
        rocket.inventory().setItem(2, new ItemStack(ModItems.ARES_CRYSTAL, 7));
        var output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, registries);
        write.invoke(rocket, output);
        var restored = new dev.vic17usd.spacefabric.space.RocketEntity(dev.vic17usd.spacefabric.space.ModSpaceEntities.ROCKET, null);
        restored.inventory().setItem(10, new ItemStack(Items.DIAMOND));
        read.invoke(restored, net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, registries, output.buildResult()));
        assertEquals(751, restored.fuel()); assertEquals(5, restored.destinationIndex());
        assertEquals(7, restored.inventory().getItem(2).getCount()); assertTrue(restored.inventory().getItem(2).is(ModItems.ARES_CRYSTAL));
        assertTrue(restored.inventory().getItem(10).isEmpty()); assertEquals(0, restored.flightState());
        assertTrue(restored.isNoGravity()); assertTrue(restored.getControllingPassenger() == null);
        input.putInt("Fuel", Integer.MAX_VALUE); input.putInt("Destination", -5);
        read.invoke(restored, net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, registries, input));
        assertEquals(3000, restored.fuel()); assertEquals(0, restored.destinationIndex()); assertTrue(restored.inventory().isEmpty());
    }

    @Test void breathforgeStructureIsLoadedAndRestrictedToTheMartianBiome() {
        var structure = registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(
                net.minecraft.resources.ResourceKey.create(Registries.STRUCTURE, SpaceFabric.id("martian_breathforge"))).value();
        assertInstanceOf(dev.vic17usd.spacefabric.worldgen.structure.MarsSurfaceStructure.class, structure);
        assertEquals(net.minecraft.world.level.levelgen.GenerationStep.Decoration.SURFACE_STRUCTURES, structure.step());
        assertEquals(1, structure.biomes().size());
        assertTrue(structure.biomes().contains(registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS))));
    }

    @Test void breathforgePoolAndPlacementSetResolveAgainstRealRegistries() {
        var pool = registries.lookupOrThrow(Registries.TEMPLATE_POOL).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.TEMPLATE_POOL, SpaceFabric.id("martian_breathforge/start"))).value();
        assertEquals(1, pool.getTemplates().size());
        var element = pool.getTemplates().getFirst().getFirst();
        assertInstanceOf(net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement.class, element);
        assertEquals(net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection.RIGID, element.getProjection());
        var set = registries.lookupOrThrow(Registries.STRUCTURE_SET).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.STRUCTURE_SET, SpaceFabric.id("martian_breathforge"))).value();
        assertInstanceOf(net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement.class, set.placement());
        assertEquals(1, set.structures().size());
        assertTrue(set.structures().getFirst().structure().is(net.minecraft.resources.ResourceKey.create(
                Registries.STRUCTURE, SpaceFabric.id("martian_breathforge"))));
    }

    @Test void convertedBreathforgeMatchesEveryOriginalBlockAndLoadsWithTheNativeTemplateReader() throws IOException {
        var source = JsonParser.parseString(java.nio.file.Files.readString(Path.of("tools/structure_sources/martian_breathforge.json"))).getAsJsonObject();
        net.minecraft.nbt.CompoundTag nbt;
        try (var input = WorldgenCodecTest.class.getResourceAsStream("/data/space_fabric/structure/martian_breathforge.nbt")) {
            assertNotNull(input);
            nbt = net.minecraft.nbt.NbtIo.readCompressed(input, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
        assertEquals(4786, nbt.getIntOr("DataVersion", 0));
        var blocks = nbt.getListOrEmpty("blocks"); var original = source.getAsJsonArray("blocks");
        assertEquals(30608, blocks.size()); assertEquals(original.size(), blocks.size());
        var palette = nbt.getListOrEmpty("palette");
        assertEquals(22, palette.size());
        for (int i = 0; i < palette.size(); i++) {
            String name = palette.getCompoundOrEmpty(i).getStringOr("Name", "");
            assertEquals(source.getAsJsonArray("palette").get(i).getAsString(), name);
            assertTrue(BuiltInRegistries.BLOCK.containsKey(net.minecraft.resources.Identifier.parse(name)));
        }
        for (int i = 0; i < blocks.size(); i++) {
            var block = blocks.getCompoundOrEmpty(i); var entry = original.get(i).getAsJsonArray();
            var position = block.getListOrEmpty("pos");
            for (int axis = 0; axis < 3; axis++) assertEquals(entry.get(axis).getAsInt(), position.getIntOr(axis, -1));
            assertEquals(entry.get(3).getAsInt(), block.getIntOr("state", -1));
        }
        var template = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate();
        template.load(registries.lookupOrThrow(Registries.BLOCK), nbt);
        assertEquals(new net.minecraft.core.Vec3i(89, 73, 89), template.getSize());
        assertEquals(30608, template.save(new net.minecraft.nbt.CompoundTag()).getListOrEmpty("blocks").size());
        assertEquals(8478, template.filterBlocks(net.minecraft.core.BlockPos.ZERO,
                new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings(), Blocks.RED_SANDSTONE).size());
        assertTrue(template.getJigsaws(net.minecraft.core.BlockPos.ZERO, net.minecraft.world.level.block.Rotation.NONE).isEmpty());
    }

    @Test void martianCratersAreExcavatedBeforeSurfaceBuildings() {
        var features = registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS)).value().getGenerationSettings().features();
        var crater = net.minecraft.resources.ResourceKey.create(Registries.PLACED_FEATURE, SpaceFabric.id("mars_crater"));
        int step = net.minecraft.world.level.levelgen.GenerationStep.Decoration.LOCAL_MODIFICATIONS.ordinal();
        assertTrue(features.get(step).stream().anyMatch(feature -> feature.is(crater)));
        assertFalse(features.get(net.minecraft.world.level.levelgen.GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal())
                .stream().anyMatch(feature -> feature.is(crater)));
    }

    @ParameterizedTest
    @MethodSource("martianStructureSeeds")
    void martianStructuresGenerateFlatPlotsAndKeepTheLegacyZeroDepthRegression(String structureName, long seed,
            @org.junit.jupiter.api.io.TempDir Path temporaryWorlds) throws IOException {
        var access = (RegistryAccess) registries;
        var holder = access.lookupOrThrow(Registries.STRUCTURE).getOrThrow(
                net.minecraft.resources.ResourceKey.create(Registries.STRUCTURE, SpaceFabric.id(structureName)));
        var stem = LevelStem.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), resource("dimension/mars")).getOrThrow();
        var generator = assertInstanceOf(net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator.class, stem.generator());
        var randomState = net.minecraft.world.level.levelgen.RandomState.create(generator.generatorSettings().value(),
                access.lookupOrThrow(Registries.NOISE), seed);
        var pack = new PathPackResources(new PackLocationInfo("breathforge_generation_test",
                Component.literal("Real structure generation"), PackSource.DEFAULT, Optional.empty()), Path.of("src/main/resources"));
        var storage = net.minecraft.world.level.storage.LevelStorageSource.createDefault(temporaryWorlds);
        try (var world = storage.createAccess("structure-test");
                var resources = new MultiPackResourceManager(PackType.SERVER_DATA,
                        List.of(ServerPacksSource.createVanillaPackSource(), pack))) {
            var templates = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager(
                    resources, world, net.minecraft.util.datafix.DataFixers.getDataFixer(), access.lookupOrThrow(Registries.BLOCK));
            assertTrue(templates.get(SpaceFabric.id(structureName)).isPresent());
            var chunk = new net.minecraft.world.level.ChunkPos(2, -3);
            var height = net.minecraft.world.level.LevelHeightAccessor.create(Planet.MARS.minY(), Planet.MARS.height());
            java.util.function.Predicate<net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome>> martianBiome =
                    biome -> biome.is(dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS));
            // Ejecuta Structure.generate y JigsawPlacement; aceptar el codec no basta.
            var valid = holder.value().generate(holder, PlanetDimensions.key(Planet.MARS), access, generator,
                    generator.getBiomeSource(), randomState, templates, seed, chunk, 0, height, martianBiome);
            assertTrue(valid.isValid(), "La estructura debe ser localizable con semilla " + seed);
            assertEquals(1, valid.getPieces().size(), "Sin conectores solo debe generarse el edificio inicial");
            var piece = assertInstanceOf(dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece.class, valid.getPieces().getFirst());
            var box = piece.templateBounds();
            assertTrue(piece.getBoundingBox().minX() >= chunk.getMinBlockX() - 128);
            assertTrue(piece.getBoundingBox().maxX() <= chunk.getMinBlockX() + 127);
            assertTrue(piece.getBoundingBox().minZ() >= chunk.getMinBlockZ() - 128);
            assertTrue(piece.getBoundingBox().maxZ() <= chunk.getMinBlockZ() + 127);
            if (structureName.equals("martian_breathforge")) {
                assertEquals(89, box.getXSpan()); assertEquals(72, box.getYSpan());
            } else {
                assertTrue(box.getXSpan() == 124 || box.getXSpan() == 125);
                assertEquals(81, box.getYSpan()); assertEquals(249, box.getXSpan() + box.getZSpan());
            }
            int sampledHeight = dev.vic17usd.spacefabric.worldgen.structure.SurfacePlot.sampleHighest(piece.getBoundingBox(),
                    (x, z) -> generator.getFirstFreeHeight(x, z, net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, height, randomState));
            assertEquals(sampledHeight - 1, piece.templateOrigin().getY());
            assertEquals(piece.templateOrigin().getY(), piece.surfaceY());
            var pieceContext = new net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext(resources, access, templates);
            var saved = piece.createTag(pieceContext);
            assertTrue(saved.getBooleanOr("FloorAligned", false));
            var restored = assertInstanceOf(dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece.class,
                    dev.vic17usd.spacefabric.worldgen.structure.MarsSurfaceStructures.PIECE.load(pieceContext, saved));
            assertEquals(piece.templateOrigin(), restored.templateOrigin());
            assertEquals(piece.templateBounds(), restored.templateBounds());
            assertEquals(piece.getBoundingBox(), restored.getBoundingBox());
            var legacyTag = saved.copy(); legacyTag.remove("FloorAligned");
            legacyTag.putString("Template", "space_fabric:" + structureName);
            var legacy = (dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece)
                    dev.vic17usd.spacefabric.worldgen.structure.MarsSurfaceStructures.PIECE.load(pieceContext, legacyTag);
            assertEquals(legacy.templateOrigin().getY() - 1, legacy.surfaceY());
            restored.move(16, 3, -16);
            assertEquals(piece.templateOrigin().offset(16, 3, -16), restored.templateOrigin());
            assertEquals(piece.templateBounds().moved(16, 3, -16), restored.templateBounds());
            var previous = resource("worldgen/structure/" + structureName).getAsJsonObject().deepCopy();
            previous.addProperty("type", "minecraft:jigsaw"); previous.addProperty("terrain_adaptation", "beard_thin");
            previous.addProperty("start_pool", "space_fabric:" + structureName + "/start");
            previous.add("start_height", JsonParser.parseString("{\"absolute\":0}"));
            previous.addProperty("project_start_to_heightmap", "WORLD_SURFACE_WG");
            previous.addProperty("max_distance_from_center", 116); previous.addProperty("use_expansion_hack", false);
            previous.remove("template"); previous.remove("padding");
            previous.addProperty("size", 0);
            var broken = net.minecraft.world.level.levelgen.structure.Structure.DIRECT_CODEC.parse(
                    registries.createSerializationContext(JsonOps.INSTANCE), previous).getOrThrow();
            var invalid = broken.generate(holder, PlanetDimensions.key(Planet.MARS), access, generator,
                    generator.getBiomeSource(), randomState, templates, seed, chunk, 0, height, martianBiome);
            assertFalse(invalid.isValid(), "26.1 no añade la pieza inicial con size=0");
            assertTrue(invalid.getPieces().isEmpty());
        }
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> martianStructureSeeds() {
        return Stream.of("martian_breathforge", "martian_haven_settlement").flatMap(name ->
                Stream.of(0L, 42L, 8675309L).map(seed -> org.junit.jupiter.params.provider.Arguments.of(name, seed)));
    }

    @Test void havenTemplatePreservesItsBlocksAndIncludesExactlySixSafeRegisteredResidents() throws IOException {
        var source = JsonParser.parseString(java.nio.file.Files.readString(Path.of("tools/structure_sources/martian_haven_settlement.json"))).getAsJsonObject();
        net.minecraft.nbt.CompoundTag nbt;
        try (var input = WorldgenCodecTest.class.getResourceAsStream("/data/space_fabric/structure/martian_haven_settlement.nbt")) {
            assertNotNull(input); nbt = net.minecraft.nbt.NbtIo.readCompressed(input, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
        var blocks = nbt.getListOrEmpty("blocks"); var original = source.getAsJsonArray("blocks");
        assertEquals(45380, blocks.size()); assertEquals(original.size(), blocks.size());
        var palette = nbt.getListOrEmpty("palette"); assertEquals(36, palette.size());
        var occupied = new java.util.HashSet<net.minecraft.core.BlockPos>();
        for (int i = 0; i < blocks.size(); i++) {
            var block = blocks.getCompoundOrEmpty(i); var entry = original.get(i).getAsJsonArray();
            var position = block.getListOrEmpty("pos");
            for (int axis = 0; axis < 3; axis++) assertEquals(entry.get(axis).getAsInt(), position.getIntOr(axis, -1));
            assertEquals(entry.get(3).getAsInt(), block.getIntOr("state", -1));
            occupied.add(new net.minecraft.core.BlockPos(position.getIntOr(0, -1), position.getIntOr(1, -1), position.getIntOr(2, -1)));
        }
        var template = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate();
        template.load(registries.lookupOrThrow(Registries.BLOCK), nbt);
        assertEquals(new net.minecraft.core.Vec3i(124, 84, 125), template.getSize());
        var residents = template.save(new net.minecraft.nbt.CompoundTag()).getListOrEmpty("entities");
        assertEquals(6, residents.size());
        var positions = new java.util.HashSet<net.minecraft.core.BlockPos>();
        for (int i = 0; i < residents.size(); i++) {
            var resident = residents.getCompoundOrEmpty(i); var data = resident.getCompoundOrEmpty("nbt");
            assertEquals("space_fabric:martian_copper_trader", data.getStringOr("id", ""));
            assertTrue(data.getBooleanOr("PersistenceRequired", false)); assertFalse(data.keySet().contains("UUID"));
            var position = resident.getListOrEmpty("blockPos");
            var point = new net.minecraft.core.BlockPos(position.getIntOr(0, -1), position.getIntOr(1, -1), position.getIntOr(2, -1));
            assertTrue(positions.add(point)); assertTrue(occupied.contains(point.below()));
            for (int dy = 0; dy < 3; dy++) assertFalse(occupied.contains(point.above(dy)));
        }
    }

    @Test void havenRegistryReferencesAreResolvedAndItsOnlyBiomeIsMars() {
        var structure = registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.STRUCTURE, SpaceFabric.id("martian_haven_settlement"))).value();
        assertEquals(1, structure.biomes().size());
        assertTrue(structure.biomes().contains(registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS))));
        assertNotNull(registries.lookupOrThrow(Registries.TEMPLATE_POOL).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.TEMPLATE_POOL, SpaceFabric.id("martian_haven_settlement/start"))));
        assertNotNull(registries.lookupOrThrow(Registries.STRUCTURE_SET).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.STRUCTURE_SET, SpaceFabric.id("martian_haven_settlement"))));
    }

    @Test void copperTraderAndItsUsableSpawnEggHaveNativeAttributeAndEntityRegistrations() {
        var type = dev.vic17usd.spacefabric.entity.ModMarsEntities.COPPER_TRADER;
        assertEquals(SpaceFabric.id("martian_copper_trader"), BuiltInRegistries.ENTITY_TYPE.getKey(type));
        assertTrue(net.minecraft.world.item.SpawnEggItem.spawnsEntity(new ItemStack(ModItems.MARTIAN_COPPER_TRADER_SPAWN_EGG), type));
        var attributes = net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier(type);
        assertNotNull(attributes); assertEquals(24, attributes.getBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH));
        assertEquals(6, attributes.getBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.SAFE_FALL_DISTANCE));
        assertEquals(0.08, attributes.getBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.GRAVITY), 0.00001);
    }

    @ParameterizedTest @ValueSource(strings = {"hypoxia", "decompression", "crushing_pressure"})
    void tradersResistOxygenLossButKeepDeepPressureVulnerability(String path) {
        var type = registries.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.DAMAGE_TYPE, SpaceFabric.id(path)));
        var source = new net.minecraft.world.damagesource.DamageSource(type);
        assertEquals(!path.equals("crushing_pressure"), dev.vic17usd.spacefabric.entity.MartianCopperTraderEntity.resistsAtmosphere(source));
    }

    @Test void tradersKeepNormalCombatDamageAndMartianGravityDoesNotAccumulate() {
        var damage = new net.minecraft.world.damagesource.DamageSource(registries.lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(net.minecraft.world.damagesource.DamageTypes.GENERIC));
        assertFalse(dev.vic17usd.spacefabric.entity.MartianCopperTraderEntity.resistsAtmosphere(damage));
        var gravity = net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier(
                dev.vic17usd.spacefabric.entity.ModMarsEntities.COPPER_TRADER).createInstance(ignored -> {}, net.minecraft.world.entity.ai.attributes.Attributes.GRAVITY);
        dev.vic17usd.spacefabric.gravity.PlanetGravity.applyToAttribute(gravity, Planet.MARS.gravityScale());
        assertEquals(0.0304, gravity.getValue(), 0.00001);
        dev.vic17usd.spacefabric.gravity.PlanetGravity.applyToAttribute(gravity, Planet.MARS.gravityScale());
        assertEquals(0.0304, gravity.getValue(), 0.00001);
        var foreign = SpaceFabric.id("test_foreign_gravity");
        gravity.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(foreign, 0.01,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
        dev.vic17usd.spacefabric.gravity.PlanetGravity.applyToAttribute(gravity, 1.0);
        assertEquals(0.09, gravity.getValue(), 0.00001); assertTrue(gravity.hasModifier(foreign));
    }

    @ParameterizedTest @ValueSource(ints = {0, 1})
    void copperTraderOffersConsumeItemsAndPreserveStockThroughNativeSerialization(int index) {
        var offers = dev.vic17usd.spacefabric.entity.MartianCopperTraderEntity.createTradeOffers();
        assertEquals(2, offers.size()); var offer = offers.get(index);
        var first = offer.getCostA().copy(); var second = offer.getCostB().copy();
        assertTrue(offer.take(first, second)); assertTrue(first.isEmpty()); assertTrue(second.isEmpty()); assertFalse(offer.assemble().isEmpty());
        for (int i = 0; i < offer.getMaxUses(); i++) offer.increaseUses();
        assertTrue(offer.isOutOfStock());
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        var data = net.minecraft.world.item.trading.MerchantOffers.CODEC.encodeStart(ops, offers).getOrThrow();
        var saved = net.minecraft.world.item.trading.MerchantOffers.CODEC.parse(ops, data).getOrThrow().get(index);
        assertTrue(saved.isOutOfStock()); saved.resetUses(); assertFalse(saved.isOutOfStock());
        assertEquals(offer.getResult().getItem(), saved.getResult().getItem());
    }

    @Test void importedTraderResourcesMatchTheUploadedJarBytes() throws Exception {
        var manifest = JsonParser.parseString(java.nio.file.Files.readString(Path.of("third_party/martian_copper_trader/manifest.json")))
                .getAsJsonObject().getAsJsonObject("files");
        for (var file : manifest.entrySet()) {
            var bytes = java.nio.file.Files.readAllBytes(Path.of(file.getKey()));
            var hash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(bytes));
            assertEquals(file.getValue().getAsJsonObject().get("sha256").getAsString(), hash);
        }
    }

    @Test void importedTraderUsesTheTwoOriginalAmethystAndCopperTrades() {
        var offers = dev.vic17usd.spacefabric.entity.MartianCopperTraderEntity.createTradeOffers();
        assertEquals(Items.AMETHYST_SHARD, offers.get(0).getCostA().getItem()); assertEquals(8, offers.get(0).getCostA().getCount());
        assertEquals(Items.COPPER_INGOT, offers.get(0).getCostB().getItem()); assertEquals(4, offers.get(0).getCostB().getCount());
        assertEquals(Items.SPYGLASS, offers.get(0).getResult().getItem());
        assertEquals(16, offers.get(1).getCostA().getCount()); assertEquals(8, offers.get(1).getCostB().getCount());
        assertEquals(Items.RECOVERY_COMPASS, offers.get(1).getResult().getItem());
    }

    @Test void flatPlotsClearUnsampledPeaksAndSupportEveryColumn() {
        var world = new VoxelWorld(0, 0, 20);
        for (int y = 21; y <= 100; y++) world.blocks.put(new net.minecraft.core.BlockPos(7, y, 7), Blocks.STONE.defaultBlockState());
        var plot = new net.minecraft.world.level.levelgen.structure.BoundingBox(2, -60, 2, 13, 447, 13);
        dev.vic17usd.spacefabric.worldgen.structure.SurfacePlot.prepare(world.level, plot, 30, plot);
        for (int x = 2; x <= 13; x++) for (int z = 2; z <= 13; z++) {
            assertTrue(world.state(new net.minecraft.core.BlockPos(x, 29, z)).is(ModBlocks.IRON_OXIDE_REGOLITH));
            for (int y = 21; y < 29; y++) assertFalse(world.state(new net.minecraft.core.BlockPos(x, y, z)).isAir());
            for (int y = 30; y <= 100; y++) assertTrue(world.state(new net.minecraft.core.BlockPos(x, y, z)).isAir());
        }
        assertFalse(world.state(new net.minecraft.core.BlockPos(0, 20, 0)).isAir());
        assertTrue(world.state(new net.minecraft.core.BlockPos(0, 21, 0)).isAir());
    }

    @Test void flatPlotsFillDeepCanyonsAndRespectTheChunkWriteMask() {
        var world = new VoxelWorld(0, 0, -20);
        var plot = new net.minecraft.world.level.levelgen.structure.BoundingBox(-8, -60, -8, 23, 447, 23);
        var clip = new net.minecraft.world.level.levelgen.structure.BoundingBox(0, -64, 0, 15, 447, 15);
        dev.vic17usd.spacefabric.worldgen.structure.SurfacePlot.prepare(world.level, plot, 30, clip);
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++)
            for (int y = -19; y < 30; y++) assertFalse(world.state(new net.minecraft.core.BlockPos(x, y, z)).isAir());
        assertTrue(world.blocks.keySet().stream().allMatch(clip::isInside));
    }

    @Test void surfaceHeightSamplingIsBoundedAndIncludesAllEdges() {
        var calls = new java.util.concurrent.atomic.AtomicInteger();
        var box = new net.minecraft.world.level.levelgen.structure.BoundingBox(-70, -64, -70, 70, 319, 70);
        int highest = dev.vic17usd.spacefabric.worldgen.structure.SurfacePlot.sampleHighest(box, (x, z) -> {
            calls.incrementAndGet(); return x == 70 && z == 70 ? 99 : 40;
        });
        assertEquals(99, highest); assertTrue(calls.get() <= 121);
    }

    @ParameterizedTest @ValueSource(strings = {"martian_breathforge", "martian_haven_settlement"})
    void actualSurfacePiecePlacesItsTemplateAfterRemovingTerrainFromRooms(String name) throws IOException {
        net.minecraft.nbt.CompoundTag nbt;
        try (var input = WorldgenCodecTest.class.getResourceAsStream("/data/space_fabric/structure/" + name + "_surface.nbt")) {
            assertNotNull(input); nbt = net.minecraft.nbt.NbtIo.readCompressed(input, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
        var template = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate();
        template.load(registries.lookupOrThrow(Registries.BLOCK), nbt);
        var world = new VoxelWorld(0, 0, 40);
        var piece = new dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece(SpaceFabric.id(name + "_surface"),
                template, new net.minecraft.core.BlockPos(-40, 30, -40), net.minecraft.world.level.block.Rotation.NONE, 8, -60, 447);
        piece.postProcess(world.level, null, null, net.minecraft.util.RandomSource.create(42),
                new net.minecraft.world.level.levelgen.structure.BoundingBox(0, -64, 0, 15, 447, 15),
                new net.minecraft.world.level.ChunkPos(0, 0), net.minecraft.core.BlockPos.ZERO);
        var expected = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState>();
        var palette = nbt.getListOrEmpty("palette"); var entries = nbt.getListOrEmpty("blocks");
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.getCompoundOrEmpty(i); var pos = entry.getListOrEmpty("pos");
            var state = BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.parse(
                    palette.getCompoundOrEmpty(entry.getIntOr("state", 0)).getStringOr("Name", ""))).defaultBlockState();
            expected.put(new net.minecraft.core.BlockPos(pos.getIntOr(0, -1), pos.getIntOr(1, -1), pos.getIntOr(2, -1)), state);
        }
        int cleared = 0, preserved = 0;
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int dy = 0; dy <= 10; dy++) {
            var intended = expected.getOrDefault(new net.minecraft.core.BlockPos(x + 40, dy, z + 40),
                    dy == 0 ? ModBlocks.IRON_OXIDE_REGOLITH.defaultBlockState() : Blocks.AIR.defaultBlockState());
            assertEquals(intended, world.state(new net.minecraft.core.BlockPos(x, 30 + dy, z)));
            if (intended.isAir()) cleared++; else preserved++;
        }
        assertTrue(cleared > 100); assertTrue(preserved > 100);
    }

    @ParameterizedTest @ValueSource(strings = {"martian_breathforge", "martian_haven_settlement"})
    void preparedTemplatesRemovePedestalsAndPreserveAboveGroundArchitecture(String name) throws IOException {
        var source = JsonParser.parseString(java.nio.file.Files.readString(Path.of("tools/structure_sources/" + name + ".json"))).getAsJsonObject();
        var recipe = JsonParser.parseString(java.nio.file.Files.readString(Path.of("tools/structure_sources/surface_preparation.json")))
                .getAsJsonObject().getAsJsonObject(name);
        int cut = recipe.get("trimmed_layers").getAsInt();
        var removed = new java.util.HashSet<String>(); recipe.getAsJsonArray("terrain_palette_removed_at_ground").forEach(value -> removed.add(value.getAsString()));
        var expected = new java.util.HashMap<net.minecraft.core.BlockPos, Integer>();
        for (var entry : source.getAsJsonArray("blocks")) {
            var block = entry.getAsJsonArray(); int y = block.get(1).getAsInt(), state = block.get(3).getAsInt();
            if (y < cut || y == cut && removed.contains(source.getAsJsonArray("palette").get(state).getAsString())) continue;
            expected.put(new net.minecraft.core.BlockPos(block.get(0).getAsInt(), y - cut, block.get(2).getAsInt()), state);
        }
        net.minecraft.nbt.CompoundTag nbt;
        try (var input = WorldgenCodecTest.class.getResourceAsStream("/data/space_fabric/structure/" + name + "_surface.nbt")) {
            assertNotNull(input); nbt = net.minecraft.nbt.NbtIo.readCompressed(input, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
        var blocks = nbt.getListOrEmpty("blocks"); assertEquals(expected.size(), blocks.size());
        assertTrue(blocks.size() < source.get("block_count").getAsInt());
        assertEquals(recipe.get("retained_blocks").getAsInt(), blocks.size());
        for (int i = 0; i < blocks.size(); i++) {
            var entry = blocks.getCompoundOrEmpty(i); var pos = entry.getListOrEmpty("pos");
            var point = new net.minecraft.core.BlockPos(pos.getIntOr(0, -1), pos.getIntOr(1, -1), pos.getIntOr(2, -1));
            assertEquals(expected.get(point).intValue(), entry.getIntOr("state", -1));
        }
        var entities = nbt.getListOrEmpty("entities");
        assertEquals(name.equals("martian_haven_settlement") ? 6 : 0, entities.size());
        for (int i = 0; i < entities.size(); i++) {
            var pos = entities.getCompoundOrEmpty(i).getListOrEmpty("blockPos");
            var point = new net.minecraft.core.BlockPos(pos.getIntOr(0, -1), pos.getIntOr(1, -1), pos.getIntOr(2, -1));
            assertTrue(point.getY() > 0);
            assertTrue(point.getY() == 1 || expected.containsKey(point.below()));
            assertFalse(expected.containsKey(point)); assertFalse(expected.containsKey(point.above()));
        }
    }

    @ParameterizedTest @ValueSource(strings = {"crystal_hum_crawler", "golden_eye_sandwraith", "red_dune_ironmaw"})
    void faunaRulesBindNativeGroundPlacementAndOnlyTheMarsBiome(String name) {
        net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity> type = switch (name) {
            case "crystal_hum_crawler" -> dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.CRAWLER;
            case "golden_eye_sandwraith" -> dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.SANDWRAITH;
            default -> dev.vic17usd.spacefabric.entity.fauna.ModMartianFauna.IRONMAW;
        };
        assertEquals(type, BuiltInRegistries.ENTITY_TYPE.getValue(SpaceFabric.id(name)));
        assertEquals(net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND, net.minecraft.world.entity.SpawnPlacements.getPlacementType(type));
        assertEquals(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, net.minecraft.world.entity.SpawnPlacements.getHeightmapType(type));
        boolean passive = name.equals("crystal_hum_crawler");
        var category = passive ? net.minecraft.world.entity.MobCategory.CREATURE : net.minecraft.world.entity.MobCategory.MONSTER;
        int weight = passive ? 18 : name.equals("golden_eye_sandwraith") ? 10 : 2;
        int maximum = passive ? 3 : name.equals("golden_eye_sandwraith") ? 2 : 1;
        for (var biome : registries.lookupOrThrow(Registries.BIOME).listElements().toList()) {
            var matches = biome.value().getMobSettings().getMobs(category).unwrap().stream().filter(entry -> entry.value().type() == type).toList();
            assertEquals(biome.is(dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS)) ? 1 : 0, matches.size());
            if (!matches.isEmpty()) { assertEquals(weight, matches.getFirst().weight()); assertEquals(1, matches.getFirst().value().minCount()); assertEquals(maximum, matches.getFirst().value().maxCount()); }
        }
        var attributes = net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier(type);
        assertNotNull(attributes); assertEquals(0.08, attributes.getBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.GRAVITY), 0.00001);
        assertEquals(6, attributes.getBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.SAFE_FALL_DISTANCE));
        assertTrue(net.minecraft.world.item.SpawnEggItem.spawnsEntity(new ItemStack(ModItems.all().get(name + "_spawn_egg")), type));
    }

    @ParameterizedTest @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void martianHabitatRejectsOtherDimensionsRoofsWaterCavesAndFloatingPositions(int invalid) {
        var mars = PlanetDimensions.key(Planet.MARS);
        boolean accepted = dev.vic17usd.spacefabric.entity.fauna.MartianSpawnRules.accepts(
                invalid == 1 ? net.minecraft.world.level.Level.OVERWORLD : mars, invalid != 2,
                invalid == 3 ? Blocks.GRASS_BLOCK.defaultBlockState() : ModBlocks.IRON_OXIDE_REGOLITH.defaultBlockState(),
                invalid != 4, invalid != 5, 80, invalid == 6 ? 81 : invalid == 7 ? 79 : 80);
        assertEquals(invalid == 0, accepted);
    }

    @Test void faunaMaterialsAndImportedAssetsHaveValidResourcesAndOriginalHashes() throws Exception {
        var manifest = JsonParser.parseString(java.nio.file.Files.readString(Path.of("third_party/martian_fauna/manifest.json"))).getAsJsonObject();
        for (var mod : manifest.entrySet()) for (var file : mod.getValue().getAsJsonObject().getAsJsonObject("assets").entrySet()) {
            var hash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(java.nio.file.Files.readAllBytes(Path.of(file.getKey()))));
            assertEquals(file.getValue().getAsJsonObject().get("sha256").getAsString(), hash);
        }
        for (var name : new String[]{"energy_crystal_shard", "condensed_stardust", "vibration_sensor", "crushed_iron_ore"}) {
            assertEquals(ModItems.all().get(name), BuiltInRegistries.ITEM.getValue(SpaceFabric.id(name)));
            assertNotNull(WorldgenCodecTest.class.getResource("/assets/space_fabric/items/" + name + ".json"));
        }
        var settings = net.minecraft.world.level.levelgen.NoiseGeneratorSettings.DIRECT_CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),
                resource("worldgen/noise_settings/mars")).getOrThrow();
        assertFalse(settings.disableMobGeneration());
    }

    @ParameterizedTest @ValueSource(longs = {0L, 42L, 8675309L})
    void sanctumGridHasExactlyOneCandidatePerTwentyThousandBlocksWithoutJitter(long seed) {
        var set = registries.lookupOrThrow(Registries.STRUCTURE_SET).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.STRUCTURE_SET, SpaceFabric.id("soulfire_netherite_sanctum"))).value();
        var placement = assertInstanceOf(net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement.class, set.placement());
        assertEquals(1250, placement.spacing()); assertEquals(1249, placement.separation());
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) {
            for (int offset : new int[]{0, 624, 1249}) {
                var candidate = placement.getPotentialStructureChunk(seed, x * 1250 + offset, z * 1250 + offset);
                assertEquals(new net.minecraft.world.level.ChunkPos(x * 1250, z * 1250), candidate);
                var next = placement.getPotentialStructureChunk(seed, (x + 1) * 1250, z * 1250);
                assertEquals(20000, next.getMinBlockX() - candidate.getMinBlockX());
            }
        }
        var structure = set.structures().getFirst().structure().value();
        assertInstanceOf(dev.vic17usd.spacefabric.worldgen.structure.MarsSanctumStructure.class, structure);
        assertEquals(1, structure.biomes().size());
        assertTrue(structure.biomes().contains(registries.lookupOrThrow(Registries.BIOME).getOrThrow(
                dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS))));
        assertEquals(net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_STRUCTURES, structure.step());
    }

    @ParameterizedTest @ValueSource(strings = {"golem", "guard"})
    void sanctumEntitiesHaveNativeAttributesAndUsableEggsButNoWildSpawns(String role) {
        var type = role.equals("golem") ? dev.vic17usd.spacefabric.entity.sanctum.ModSanctumEntities.GOLEM
                : dev.vic17usd.spacefabric.entity.sanctum.ModSanctumEntities.GUARD;
        var attributes = net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier(type);
        assertEquals(role.equals("golem") ? 1200 : 80, attributes.getBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH));
        double height = role.equals("golem") ? 3.5 : 3.3;
        assertEquals(role.equals("golem") ? 8.1 : 3.0,
                height * attributes.getBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.SCALE), 0.00001);
        var egg = role.equals("golem") ? ModItems.BASALT_EMBER_GOLEM_SPAWN_EGG : ModItems.BASALT_BOSSWARDEN_SPAWN_EGG;
        assertTrue(net.minecraft.world.item.SpawnEggItem.spawnsEntity(new ItemStack(egg), type));
        for (var biome : registries.lookupOrThrow(Registries.BIOME).listElements().toList())
            for (var category : net.minecraft.world.entity.MobCategory.values())
                assertTrue(biome.value().getMobSettings().getMobs(category).unwrap().stream().noneMatch(entry -> entry.value().type() == type));
    }

    private static net.minecraft.nbt.CompoundTag sanctumNbt() throws IOException {
        try (var input = WorldgenCodecTest.class.getResourceAsStream("/data/space_fabric/structure/soulfire_netherite_sanctum.nbt")) {
            assertNotNull(input); return net.minecraft.nbt.NbtIo.readCompressed(input, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
    }

    @Test void sanctumExplicitlyExcavatesItsRoomAndLeavesFullSizeResidentsUnobstructed() throws IOException {
        var nbt = sanctumNbt(); var palette = nbt.getListOrEmpty("palette");
        var blocks = new java.util.HashMap<net.minecraft.core.BlockPos, String>();
        for (var tag : nbt.getListOrEmpty("blocks")) {
            var block = (net.minecraft.nbt.CompoundTag) tag; var pos = block.getListOrEmpty("pos");
            blocks.put(new net.minecraft.core.BlockPos(pos.getIntOr(0, -1), pos.getIntOr(1, -1), pos.getIntOr(2, -1)),
                    palette.getCompoundOrEmpty(block.getIntOr("state", -1)).getStringOr("Name", ""));
        }
        assertEquals(53 * 32 * 53, blocks.size());
        assertTrue(blocks.values().stream().filter("minecraft:air"::equals).count() > 20000);
        var residents = nbt.getListOrEmpty("entities"); assertEquals(5, residents.size());
        int bosses = 0;
        for (var tag : residents) {
            var entry = (net.minecraft.nbt.CompoundTag) tag; var entity = entry.getCompoundOrEmpty("nbt");
            assertTrue(entity.getBooleanOr("PersistenceRequired", false)); assertFalse(entity.keySet().contains("UUID"));
            var pos = entry.getListOrEmpty("blockPos"); int x = pos.getIntOr(0, -1), y = pos.getIntOr(1, -1), z = pos.getIntOr(2, -1);
            boolean boss = entity.getStringOr("id", "").equals("space_fabric:basalt_ember_golem");
            if (boss) { bosses++; assertEquals(new net.minecraft.core.BlockPos(26,9,26), new net.minecraft.core.BlockPos(x,y,z)); }
            else assertEquals("space_fabric:basalt_bosswarden", entity.getStringOr("id", ""));
            for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
                assertFalse(blocks.get(new net.minecraft.core.BlockPos(x+dx,y-1,z+dz)).endsWith(":air"));
                for (int dy = 0; dy < (boss ? 9 : 3); dy++)
                    assertEquals("minecraft:air", blocks.get(new net.minecraft.core.BlockPos(x+dx,y+dy,z+dz)));
            }
        }
        assertEquals(1, bosses);
        for (int x = 0; x < 53; x++) for (int z = 0; z < 53; z++) {
            assertFalse(blocks.get(new net.minecraft.core.BlockPos(x,0,z)).endsWith(":air"));
            assertFalse(blocks.get(new net.minecraft.core.BlockPos(x,31,z)).endsWith(":air"));
        }
    }

    @ParameterizedTest @ValueSource(longs = {0L, 42L, 8675309L})
    void sanctumGeneratesDeepValidStartsAndPersistsItsRotatedLocator(long seed,
            @org.junit.jupiter.api.io.TempDir Path temporaryWorlds) throws IOException {
        var access = (RegistryAccess) registries;
        var holder = access.lookupOrThrow(Registries.STRUCTURE).getOrThrow(net.minecraft.resources.ResourceKey.create(
                Registries.STRUCTURE, SpaceFabric.id("soulfire_netherite_sanctum")));
        var stem = LevelStem.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), resource("dimension/mars")).getOrThrow();
        var generator = (net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator) stem.generator();
        var random = net.minecraft.world.level.levelgen.RandomState.create(generator.generatorSettings().value(), access.lookupOrThrow(Registries.NOISE), seed);
        var pack = new PathPackResources(new PackLocationInfo("sanctum_test", Component.literal("Sanctum generation"), PackSource.DEFAULT, Optional.empty()), Path.of("src/main/resources"));
        var storage = net.minecraft.world.level.storage.LevelStorageSource.createDefault(temporaryWorlds);
        try (var world = storage.createAccess("sanctum-test"); var resources = new MultiPackResourceManager(PackType.SERVER_DATA,
                List.of(ServerPacksSource.createVanillaPackSource(), pack))) {
            var templates = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager(resources, world,
                    net.minecraft.util.datafix.DataFixers.getDataFixer(), access.lookupOrThrow(Registries.BLOCK));
            var height = net.minecraft.world.level.LevelHeightAccessor.create(Planet.MARS.minY(), Planet.MARS.height());
            for (int cell : new int[]{-1,0,1}) {
                var chunk = new net.minecraft.world.level.ChunkPos(cell * 1250, 0);
                var start = holder.value().generate(holder, PlanetDimensions.key(Planet.MARS), access, generator, generator.getBiomeSource(),
                        random, templates, seed, chunk, 0, height, biome -> biome.is(dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.MARS)));
                assertTrue(start.isValid()); assertEquals(1, start.getPieces().size());
                var piece = assertInstanceOf(dev.vic17usd.spacefabric.worldgen.structure.MarsSanctumPiece.class, start.getPieces().getFirst());
                var bounds = piece.getBoundingBox(); assertEquals(-60, bounds.minY()); assertEquals(-13, bounds.maxY());
                assertEquals(53, bounds.getXSpan()); assertEquals(53, bounds.getZSpan());
                assertEquals(new net.minecraft.core.BlockPos(chunk.getMinBlockX()+8, -51, 8), piece.getLocatorPosition());
                var context = new net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext(resources, access, templates);
                var restored = (dev.vic17usd.spacefabric.worldgen.structure.MarsSanctumPiece)
                        dev.vic17usd.spacefabric.worldgen.structure.MarsSurfaceStructures.SANCTUM_PIECE.load(context, piece.createTag(context));
                assertEquals(piece.templateOrigin(), restored.templateOrigin()); assertEquals(bounds, restored.getBoundingBox());
                assertEquals(piece.getLocatorPosition(), restored.getLocatorPosition());
                restored.move(16, 3, -16); assertEquals(piece.getLocatorPosition().offset(16,3,-16), restored.getLocatorPosition());
            }
        }
    }

    @ParameterizedTest @ValueSource(ints = {-64, 40})
    void actualSanctumPieceExcavatesOnlyItsChunkAndKeepsARockCoverOverDeepCanyons(int ground) throws IOException {
        var nbt = sanctumNbt();
        // Los residentes se verifican arriba; esta prueba aísla la escritura de bloques.
        nbt.remove("entities");
        var template = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate();
        template.load(registries.lookupOrThrow(Registries.BLOCK), nbt);
        var world = new VoxelWorld(0,0,ground);
        var piece = new dev.vic17usd.spacefabric.worldgen.structure.MarsSanctumPiece(SpaceFabric.id("soulfire_netherite_sanctum"),
                template, new net.minecraft.core.BlockPos(-18,-60,-18), net.minecraft.world.level.block.Rotation.NONE);
        piece.postProcess(world.level, null, null, net.minecraft.util.RandomSource.create(42),
                new net.minecraft.world.level.levelgen.structure.BoundingBox(0,-64,0,15,319,15),
                new net.minecraft.world.level.ChunkPos(0,0), net.minecraft.core.BlockPos.ZERO);
        assertTrue(world.state(new net.minecraft.core.BlockPos(8,-51,8)).isAir());
        assertEquals(Blocks.OBSIDIAN, world.state(new net.minecraft.core.BlockPos(8,-52,8)).getBlock());
        assertEquals(ground < 40, world.state(new net.minecraft.core.BlockPos(8,40,8)).isAir());
        for (int x=0; x<16; x++) for(int z=0; z<16; z++) for(int y=-28; y<=-13; y++)
            assertFalse(world.state(new net.minecraft.core.BlockPos(x,y,z)).isAir());
        assertTrue(world.blocks.keySet().stream().allMatch(pos -> pos.getY() >= -60 && pos.getY() <= -13));
    }

    @Test void sanctumImportedResourcesMatchTheThreeSourceJars() throws Exception {
        var manifest = JsonParser.parseString(java.nio.file.Files.readString(Path.of("third_party/martian_sanctum/manifest.json"))).getAsJsonObject();
        assertEquals(3, manifest.size());
        for (var source : manifest.entrySet()) for (var asset : source.getValue().getAsJsonObject().getAsJsonObject("assets").entrySet()) {
            var sha = java.security.MessageDigest.getInstance("SHA-256");
            assertEquals(asset.getValue().getAsJsonObject().get("sha256").getAsString(),
                    java.util.HexFormat.of().formatHex(sha.digest(java.nio.file.Files.readAllBytes(Path.of(asset.getKey())))));
        }
    }

    @ParameterizedTest @ValueSource(strings={"acidstone_slime","obsidian_lavafang","ashplume_emberwalker","sulfurwing_copper_harbinger"})
    void venusFaunaHaveAttributesEggsAndVenusOnlyHabitat(String name) {
        var livingType=switch(name){
            case "acidstone_slime" -> dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.ACIDSTONE_SLIME;
            case "obsidian_lavafang" -> dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.OBSIDIAN_LAVAFANG;
            case "ashplume_emberwalker" -> dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.ASHPLUME_EMBERWALKER;
            default -> dev.vic17usd.spacefabric.entity.venus.ModVenusEntities.SULFURWING_COPPER_HARBINGER;
        };
        var gravity=net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier(livingType)
                .createInstance(ignored->{},net.minecraft.world.entity.ai.attributes.Attributes.GRAVITY);
        dev.vic17usd.spacefabric.gravity.PlanetGravity.applyToAttribute(gravity,Planet.VENUS.gravityScale());
        assertEquals(0.072,gravity.getValue(),0.000001);
        var type=BuiltInRegistries.ENTITY_TYPE.getValue(SpaceFabric.id(name));
        assertNotNull(type);
        assertTrue(dev.vic17usd.spacefabric.entity.venus.VenusMob.class.isAssignableFrom(
                switch(name) {
                    case "acidstone_slime" -> dev.vic17usd.spacefabric.entity.venus.AcidstoneSlimeEntity.class;
                    case "obsidian_lavafang" -> dev.vic17usd.spacefabric.entity.venus.ObsidianLavafangEntity.class;
                    case "ashplume_emberwalker" -> dev.vic17usd.spacefabric.entity.venus.AshplumeEmberwalkerEntity.class;
                    default -> dev.vic17usd.spacefabric.entity.venus.SulfurwingCopperHarbingerEntity.class;
                }));
        var egg=BuiltInRegistries.ITEM.getValue(SpaceFabric.id(name+"_spawn_egg"));
        assertTrue(net.minecraft.world.item.SpawnEggItem.spawnsEntity(new ItemStack(egg),type));
        for(var biome:registries.lookupOrThrow(Registries.BIOME).listElements().toList()) {
            boolean found=false;
            for(var category:net.minecraft.world.entity.MobCategory.values())
                found |= biome.value().getMobSettings().getMobs(category).unwrap().stream().anyMatch(entry->entry.value().type()==type);
            assertEquals(biome.is(dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(Planet.VENUS)),found);
        }
    }

    @ParameterizedTest @ValueSource(strings={"vacuum","venus","cryo","abyssal"})
    void ratedSuitsRequireCompleteMatchingEquipmentAndHaveCraftableNativeRecipes(String tier) throws IOException {
        String[] parts={"helmet","chestplate","leggings","boots"};
        var stacks=new ItemStack[4];
        for(int n=0;n<4;n++) {
            String name=tier+"_suit_"+parts[n];
            stacks[n]=new ItemStack(BuiltInRegistries.ITEM.getValue(SpaceFabric.id(name)));
            assertFalse(stacks[n].isEmpty());
            assertTrue(stacks[n].has(net.minecraft.core.component.DataComponents.EQUIPPABLE));
            var equipped=stacks[n].get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
            assertEquals(SpaceFabric.id(tier+"_suit"),equipped.assetId().orElseThrow().identifier());
            var original=new ItemStack(new net.minecraft.world.item.Item[]{Items.IRON_HELMET,Items.IRON_CHESTPLATE,Items.IRON_LEGGINGS,Items.IRON_BOOTS}[n]);
            assertEquals(original.getMaxDamage(),stacks[n].getMaxDamage());
            assertEquals(original.get(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS),stacks[n].get(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS));
            assertEquals(original.get(net.minecraft.core.component.DataComponents.REPAIRABLE),stacks[n].get(net.minecraft.core.component.DataComponents.REPAIRABLE));
            assertEquals(original.get(net.minecraft.core.component.DataComponents.EQUIPPABLE).slot(),equipped.slot());
            assertEquals(2,stacks[n].get(net.minecraft.core.component.DataComponents.LORE).lines().size());
            var recipe=assertInstanceOf(net.minecraft.world.item.crafting.ShapelessRecipe.class,Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("recipe/"+name)).getOrThrow());
            var inputItems=new java.util.ArrayList<ItemStack>();
            for(var ingredient:resource("recipe/"+name).getAsJsonObject().getAsJsonArray("ingredients"))
                inputItems.add(new ItemStack(BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(ingredient.getAsString()))));
            var input=net.minecraft.world.item.crafting.CraftingInput.of(3,3,java.util.stream.IntStream.range(0,9)
                    .mapToObj(index->index<inputItems.size()?inputItems.get(index):ItemStack.EMPTY).toList());
            assertTrue(recipe.matches(input,null)); assertEquals(stacks[n].getItem(),recipe.assemble(input).getItem());
        }
        assertEquals(tier,dev.vic17usd.spacefabric.equipment.SuitProtection.tier(stacks[0],stacks[1],stacks[2],stacks[3]).path);
        assertEquals(dev.vic17usd.spacefabric.equipment.SuitTier.NONE,
                dev.vic17usd.spacefabric.equipment.SuitProtection.tier(stacks[0],stacks[1],stacks[2],ItemStack.EMPTY));
        assertEquals(dev.vic17usd.spacefabric.equipment.SuitTier.NONE,
                dev.vic17usd.spacefabric.equipment.SuitProtection.tier(new ItemStack(Items.IRON_HELMET),new ItemStack(Items.IRON_CHESTPLATE),new ItemStack(Items.IRON_LEGGINGS),new ItemStack(Items.IRON_BOOTS)));
        String other=tier.equals("vacuum")?"venus":"vacuum";
        assertEquals(dev.vic17usd.spacefabric.equipment.SuitTier.NONE,
                dev.vic17usd.spacefabric.equipment.SuitProtection.tier(stacks[0],stacks[1],stacks[2],
                    new ItemStack(BuiltInRegistries.ITEM.getValue(SpaceFabric.id(other+"_suit_boots")))));
    }

    @Test void planetaryExposureDamageBypassesArmorResistanceAndDamageCooldown() {
        for(String id:new String[]{"decompression","crushing_pressure","hypoxia","thermal_exposure"}) {
            var source=new net.minecraft.world.damagesource.DamageSource(registries.lookupOrThrow(Registries.DAMAGE_TYPE)
                    .getOrThrow(net.minecraft.resources.ResourceKey.create(Registries.DAMAGE_TYPE,SpaceFabric.id(id))));
            for(String tag:new String[]{"bypasses_armor","bypasses_shield","bypasses_resistance","bypasses_enchantments","bypasses_cooldown","bypasses_invulnerability"})
                assertTrue(source.is(net.minecraft.tags.TagKey.create(Registries.DAMAGE_TYPE,net.minecraft.resources.Identifier.withDefaultNamespace(tag))),id+" / "+tag);
        }
    }

    private static JsonElement resource(String path) throws IOException {
        String location = "/data/space_fabric/" + path + ".json";
        var stream = WorldgenCodecTest.class.getResourceAsStream(location);
        assertNotNull(stream, location);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        }
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.EnumSource(value=dev.vic17usd.spacefabric.space.RocketVariant.class,names="BASE",mode=org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE)
    void planetaryRocketKitsCraftAndUpgradeWithoutLosingTankOrCargo(dev.vic17usd.spacefabric.space.RocketVariant variant) throws Exception {
        var recipeJson=resource("recipe/"+variant.path+"_rocket_kit").getAsJsonObject();
        var recipe=assertInstanceOf(net.minecraft.world.item.crafting.ShapedRecipe.class,
                Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),recipeJson).getOrThrow());
        var ingredients=net.minecraft.core.NonNullList.withSize(9,ItemStack.EMPTY);
        var pattern=recipeJson.getAsJsonArray("pattern");var key=recipeJson.getAsJsonObject("key");
        for(int row=0;row<3;row++) for(int col=0;col<3;col++)
            ingredients.set(row*3+col,new ItemStack(BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(
                    key.get(String.valueOf(pattern.get(row).getAsString().charAt(col))).getAsString()))));
        var crafting=net.minecraft.world.item.crafting.CraftingInput.of(3,3,ingredients);
        assertTrue(recipe.matches(crafting,null));
        var kit=assertInstanceOf(dev.vic17usd.spacefabric.space.RocketUpgradeItem.class,recipe.assemble(crafting).getItem());
        assertEquals(variant,kit.variant());
        var rocket=new dev.vic17usd.spacefabric.space.RocketEntity(dev.vic17usd.spacefabric.space.ModSpaceEntities.ROCKET,null);
        var read=rocket.getClass().getDeclaredMethod("readAdditionalSaveData",net.minecraft.world.level.storage.ValueInput.class);
        var write=rocket.getClass().getDeclaredMethod("addAdditionalSaveData",net.minecraft.world.level.storage.ValueOutput.class);
        read.setAccessible(true);write.setAccessible(true);
        var input=new net.minecraft.nbt.CompoundTag();input.putInt("Fuel",751);input.putInt("Hull",19);
        read.invoke(rocket,net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,registries,input));
        rocket.inventory().setItem(4,new ItemStack(ModItems.ARES_CRYSTAL,7));
        assertTrue(rocket.install(kit.variant()));
        assertEquals(variant,rocket.variant());assertEquals(751,rocket.fuel());assertEquals(100,rocket.hull());
        assertEquals(7,rocket.inventory().getItem(4).getCount());
        rocket.restoreHull(63);
        var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,registries);
        write.invoke(rocket,output);
        var restored=new dev.vic17usd.spacefabric.space.RocketEntity(dev.vic17usd.spacefabric.space.ModSpaceEntities.ROCKET,null);
        read.invoke(restored,net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,registries,output.buildResult()));
        assertEquals(variant,restored.variant());assertEquals(63,restored.hull());assertEquals(751,restored.fuel());
        assertEquals(rocket.destinationIndex(),restored.destinationIndex());assertEquals(7,restored.inventory().getItem(4).getCount());
        var packed=dev.vic17usd.spacefabric.space.RocketItem.packed(restored.fuel(),restored.variant(),restored.hull());
        var ops=registries.createSerializationContext(JsonOps.INSTANCE);
        var item=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,packed).getOrThrow()).getOrThrow();
        assertEquals(variant,assertInstanceOf(dev.vic17usd.spacefabric.space.RocketItem.class,item.getItem()).variant());
        assertEquals(751,dev.vic17usd.spacefabric.space.RocketItem.storedFuel(item));
        assertEquals(63,item.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().getIntOr("RocketHull",-1));
        restored.restoreHull(-1);assertEquals(0,restored.hull());restored.restoreHull(101);assertEquals(100,restored.hull());
    }

    @ParameterizedTest @EnumSource(dev.vic17usd.spacefabric.earth.EarthMineral.class)
    void terrestrialMaterialsHaveRealRegistrationsLootAndCorrectMiningTiers(dev.vic17usd.spacefabric.earth.EarthMineral mineral) throws IOException {
        for(boolean deep:new boolean[]{false,true}) {
            var block=dev.vic17usd.spacefabric.earth.EarthMaterials.ore(mineral,deep);
            assertEquals(SpaceFabric.id(mineral.oreId(deep)),BuiltInRegistries.BLOCK.getKey(block));
            assertEquals(block.asItem(),BuiltInRegistries.ITEM.getValue(SpaceFabric.id(mineral.oreId(deep))));
            assertNotNull(LootTable.DIRECT_CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("loot_table/blocks/"+mineral.oreId(deep))).getOrThrow());
            assertFalse(new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(block.defaultBlockState()));
            assertFalse(new ItemStack(Items.GOLDEN_PICKAXE).isCorrectToolForDrops(block.defaultBlockState()));
            assertEquals(mineral.tool.equals("stone"),new ItemStack(Items.STONE_PICKAXE).isCorrectToolForDrops(block.defaultBlockState()));
            assertEquals(!mineral.tool.equals("diamond"),new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(block.defaultBlockState()));
            assertTrue(new ItemStack(Items.DIAMOND_PICKAXE).isCorrectToolForDrops(block.defaultBlockState()));
            assertTrue(new ItemStack(Items.NETHERITE_PICKAXE).isCorrectToolForDrops(block.defaultBlockState()));
        }
        var storage=dev.vic17usd.spacefabric.earth.EarthMaterials.storage(mineral);
        assertEquals(SpaceFabric.id(mineral.storageId()),BuiltInRegistries.BLOCK.getKey(storage));
        assertNotNull(LootTable.DIRECT_CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("loot_table/blocks/"+mineral.storageId())).getOrThrow());
        assertEquals(SpaceFabric.id(mineral.productId()),BuiltInRegistries.ITEM.getKey(dev.vic17usd.spacefabric.earth.EarthMaterials.product(mineral)));
    }

    @ParameterizedTest @EnumSource(dev.vic17usd.spacefabric.earth.EarthMineral.class)
    void terrestrialVeinsReplaceBothHostRocksAndAreInjectedIntoTheActualOverworldBiomes(dev.vic17usd.spacefabric.earth.EarthMineral mineral) {
        var key=dev.vic17usd.spacefabric.worldgen.EarthOreGen.key(mineral);
        var placed=registries.lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(key).value();
        var config=assertInstanceOf(net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.class,placed.feature().value().config());
        assertEquals(mineral.veinSize,config.size);assertEquals(2,config.targetStates.size());
        for(int i=0;i<2;i++) {
            var target=config.targetStates.get(i);
            assertTrue(target.state.is(dev.vic17usd.spacefabric.earth.EarthMaterials.ore(mineral,i==1)));
            assertTrue(target.target.test((i==0?net.minecraft.world.level.block.Blocks.STONE:net.minecraft.world.level.block.Blocks.DEEPSLATE).defaultBlockState(),net.minecraft.util.RandomSource.create(42)));
            assertFalse(target.target.test(net.minecraft.world.level.block.Blocks.BEDROCK.defaultBlockState(),net.minecraft.util.RandomSource.create(42)));
        }
        var jungle=registries.lookupOrThrow(Registries.BIOME).getOrThrow(net.minecraft.world.level.biome.Biomes.JUNGLE).value();
        var plains=registries.lookupOrThrow(Registries.BIOME).getOrThrow(net.minecraft.world.level.biome.Biomes.PLAINS).value();
        assertTrue(jungle.getGenerationSettings().features().stream().flatMap(net.minecraft.core.HolderSet::stream).anyMatch(f->f.is(key)));
        assertEquals(mineral!=dev.vic17usd.spacefabric.earth.EarthMineral.BAUXITE,plains.getGenerationSettings().features().stream().flatMap(net.minecraft.core.HolderSet::stream).anyMatch(f->f.is(key)));
        for(var planet:Planet.values()) {
            if(planet==Planet.EARTH)continue; // Uses the same vanilla biomes as the Overworld.
            var biome=registries.lookupOrThrow(Registries.BIOME).getOrThrow(dev.vic17usd.spacefabric.worldgen.PlanetBiomes.key(planet)).value();
            assertFalse(biome.getGenerationSettings().features().stream().flatMap(net.minecraft.core.HolderSet::stream).anyMatch(f->f.is(key)),planet.path());
        }
    }

    @ParameterizedTest @EnumSource(dev.vic17usd.spacefabric.earth.EarthMineral.class)
    void terrestrialRefiningAndReversibleStorageActuallyAssemble(dev.vic17usd.spacefabric.earth.EarthMineral mineral) throws IOException {
        var output=dev.vic17usd.spacefabric.earth.EarthMaterials.product(mineral);
        if(mineral.isMetal()) for(String source:new String[]{mineral.rawId(),mineral.oreId(false),mineral.oreId(true)}) {
            for(String method:new String[]{"smelting","blasting"}) {
                var recipe=assertInstanceOf(net.minecraft.world.item.crafting.AbstractCookingRecipe.class,
                    Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("recipe/"+source+"_"+method)).getOrThrow());
                var input=new net.minecraft.world.item.crafting.SingleRecipeInput(new ItemStack(BuiltInRegistries.ITEM.getValue(SpaceFabric.id(source))));
                assertTrue(recipe.matches(input,null));assertEquals(output,recipe.assemble(input).getItem());
                assertNotNull(net.minecraft.advancements.Advancement.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("advancement/recipes/earth/"+source+"_"+method)).getOrThrow());
            }
        }
        assertNotNull(net.minecraft.advancements.Advancement.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("advancement/recipes/earth/"+mineral.storageId())).getOrThrow());
        assertNotNull(net.minecraft.advancements.Advancement.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("advancement/recipes/earth/"+mineral.productId()+"_from_block")).getOrThrow());
        var compression=assertInstanceOf(net.minecraft.world.item.crafting.ShapedRecipe.class,
            Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("recipe/"+mineral.storageId())).getOrThrow());
        var items=net.minecraft.core.NonNullList.withSize(9,ItemStack.EMPTY);
        for(int i=0;i<9;i++)items.set(i,new ItemStack(output));
        var grid=net.minecraft.world.item.crafting.CraftingInput.of(3,3,items);
        assertTrue(compression.matches(grid,null));assertEquals(dev.vic17usd.spacefabric.earth.EarthMaterials.storage(mineral).asItem(),compression.assemble(grid).getItem());
        var decompression=assertInstanceOf(net.minecraft.world.item.crafting.ShapelessRecipe.class,
            Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("recipe/"+mineral.productId()+"_from_block")).getOrThrow());
        var input=net.minecraft.world.item.crafting.CraftingInput.of(1,1,List.of(new ItemStack(dev.vic17usd.spacefabric.earth.EarthMaterials.storage(mineral))));
        assertTrue(decompression.matches(input,null));assertEquals(9,decompression.assemble(input).getCount());assertEquals(output,decompression.assemble(input).getItem());
    }

    private static dev.vic17usd.spacefabric.recipe.RecipeCatalog viewerCatalog() throws IOException {
        var holders=new java.util.ArrayList<net.minecraft.world.item.crafting.RecipeHolder<?>>();
        try(var paths=java.nio.file.Files.list(Path.of("src/main/resources/data/space_fabric/recipe"))) {
            for(var path:paths.filter(p->p.toString().endsWith(".json")).toList()) {
                var id=SpaceFabric.id(path.getFileName().toString().replace(".json",""));
                var recipe=Recipe.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),resource("recipe/"+id.getPath())).getOrThrow();
                holders.add(new net.minecraft.world.item.crafting.RecipeHolder<>(net.minecraft.resources.ResourceKey.create(Registries.RECIPE,id),recipe));
            }
        }
        var context=new net.minecraft.util.context.ContextMap.Builder()
                .withParameter(net.minecraft.world.item.crafting.display.SlotDisplayContext.REGISTRIES,registries)
                .withParameter(net.minecraft.world.item.crafting.display.SlotDisplayContext.FUEL_VALUES,
                    net.minecraft.world.level.block.entity.FuelValues.vanillaBurnTimes(registries,net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS))
                .create(net.minecraft.world.item.crafting.display.SlotDisplayContext.CONTEXT);
        return dev.vic17usd.spacefabric.recipe.RecipeCatalog.build(holders,context);
    }
    @Test void recipeViewerIndexesActualRecipesAndIngredientUsesIncludingRefinery() throws IOException {
        var catalog=viewerCatalog();
        var rocket=catalog.find(ModItems.ROCKET,false);
        assertEquals(1,rocket.size());assertEquals(3,rocket.getFirst().width());assertEquals(9,rocket.getFirst().inputs().size());
        assertTrue(rocket.getFirst().inputs().get(0).isEmpty());
        assertTrue(rocket.getFirst().inputs().get(1).getFirst().is(Items.IRON_BLOCK));
        assertTrue(catalog.find(ModItems.SULFATE_DUST,true).stream().anyMatch(c->c.kind().equals("refinery")));
        var fuel=catalog.find(ModItems.ROCKET_FUEL_CELL,false);
        assertEquals(2,fuel.size());assertTrue(fuel.stream().allMatch(c->c.station().is(ModBlocks.FUEL_REFINERY.asItem())));
        assertTrue(catalog.find(dev.vic17usd.spacefabric.earth.EarthMaterials.product(dev.vic17usd.spacefabric.earth.EarthMineral.BAUXITE),false).stream().anyMatch(c->c.kind().equals("blasting")));
        assertTrue(catalog.find(ModItems.MARS_ROCKET_KIT,false).stream().anyMatch(c->c.kind().equals("crafting")));
        assertFalse(catalog.find(ModItems.VACUUM_SUIT_HELMET,false).isEmpty());
        var invalid=dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.query(catalog,new dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.Request(7,"not a valid id",false,Integer.MAX_VALUE));
        assertEquals(7,invalid.sequence());assertEquals(0,invalid.total());assertTrue(invalid.card().isEmpty());
        var clamped=dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.query(catalog,new dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.Request(8,"space_fabric:rocket",false,Integer.MAX_VALUE));
        assertEquals(0,clamped.page());assertEquals(1,clamped.total());
    }
    @Test void recipeViewerNativeNetworkRoundTripPreservesSlotsAlternativesAndCounts() throws IOException {
        var catalog=viewerCatalog();
        for(var item:List.of(ModItems.ROCKET,ModItems.ROCKET_FUEL_CELL,ModItems.VACUUM_SUIT_HELMET)) {
            var card=catalog.find(item,false).getFirst();
            var reply=new dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.Reply(31,0,1,Optional.of(card));
            var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),(RegistryAccess)registries);
            try {
                dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.Reply.CODEC.encode(buffer,reply);
                assertTrue(buffer.readableBytes()<1048576);
                var decoded=dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork.Reply.CODEC.decode(buffer);
                var restored=decoded.card().orElseThrow();
                assertEquals(31,decoded.sequence());assertEquals(card.id(),restored.id());assertEquals(card.kind(),restored.kind());
                assertEquals(card.width(),restored.width());assertEquals(card.duration(),restored.duration());
                assertEquals(card.inputs().size(),restored.inputs().size());
                for(int i=0;i<card.inputs().size();i++) {
                    assertEquals(card.inputs().get(i).size(),restored.inputs().get(i).size());
                    for(int j=0;j<card.inputs().get(i).size();j++)assertTrue(ItemStack.matches(card.inputs().get(i).get(j),restored.inputs().get(i).get(j)));
                }
                assertTrue(ItemStack.matches(card.outputs().getFirst(),restored.outputs().getFirst()));
                assertTrue(ItemStack.matches(card.station(),restored.station()));
                assertEquals(0,buffer.readableBytes());
            } finally {buffer.release();}
        }
    }
    @Test void displayedRefineryFormulasAreTheSameOnesTheMachineConsumes() throws IOException {
        var catalog=viewerCatalog();var cards=catalog.find(ModItems.ROCKET_FUEL_CELL,false);
        for(var card:cards)for(var base:card.inputs().get(0))for(var reagent:card.inputs().get(1)) {
            var inventory=new net.minecraft.world.SimpleContainer(4);
            inventory.setItem(0,base.copy());inventory.setItem(1,reagent.copy());inventory.setItem(2,new ItemStack(ModItems.EMPTY_FUEL_CELL));
            var process=new dev.vic17usd.spacefabric.machine.RefineryProcess();
            for(int i=0;i<card.duration();i++)process.tick(inventory);
            assertTrue(inventory.getItem(3).is(ModItems.ROCKET_FUEL_CELL));assertEquals(1,inventory.getItem(3).getCount());
            for(int i=0;i<3;i++)assertTrue(inventory.getItem(i).isEmpty());
        }
    }

    private static dev.vic17usd.spacefabric.equipment.SuitLoadout suitLoadout(String tier) {
        return new dev.vic17usd.spacefabric.equipment.SuitLoadout(java.util.stream.Stream.of("helmet","chestplate","leggings","boots")
                .map(part->new ItemStack(ModItems.all().get(tier+"_suit_"+part))).toList());
    }
    @ParameterizedTest @ValueSource(strings={"vacuum","venus","cryo","abyssal"})
    void separateSuitRetainsProtectionWithCombatArmorAndPersistsThroughNativeCodecs(String tier) {
        var loadout=suitLoadout(tier);loadout.items().get(1).setDamageValue(17);
        var armor=List.of(new ItemStack(Items.NETHERITE_HELMET),new ItemStack(Items.NETHERITE_CHESTPLATE),new ItemStack(Items.NETHERITE_LEGGINGS),new ItemStack(Items.NETHERITE_BOOTS));
        assertEquals(tier,dev.vic17usd.spacefabric.equipment.SuitProtection.tier(loadout,armor.get(0),armor.get(1),armor.get(2),armor.get(3)).path);
        var ops=registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
        var encoded=dev.vic17usd.spacefabric.equipment.SuitLoadout.CODEC.encodeStart(ops,loadout).getOrThrow();
        var restored=dev.vic17usd.spacefabric.equipment.SuitLoadout.CODEC.parse(ops,encoded).getOrThrow();
        assertEquals(tier,restored.tier().path);assertEquals(17,restored.items().get(1).getDamageValue());
        for(int index=0;index<4;index++)assertTrue(ItemStack.matches(loadout.items().get(index),restored.items().get(index)));
        var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),(RegistryAccess)registries);
        try {
            dev.vic17usd.spacefabric.equipment.SuitLoadout.STREAM_CODEC.encode(buffer,loadout);
            var synced=dev.vic17usd.spacefabric.equipment.SuitLoadout.STREAM_CODEC.decode(buffer);
            for(int index=0;index<4;index++)assertTrue(ItemStack.matches(loadout.items().get(index),synced.items().get(index)));
            assertEquals(0,buffer.readableBytes());
        }finally{buffer.release();}
        var partial=loadout.snapshot();partial.items().get(3).shrink(1);
        assertEquals(dev.vic17usd.spacefabric.equipment.SuitTier.NONE,dev.vic17usd.spacefabric.equipment.SuitProtection.tier(partial,armor.get(0),armor.get(1),armor.get(2),armor.get(3)));
        assertEquals(tier,dev.vic17usd.spacefabric.equipment.SuitProtection.tier(partial,loadout.items().get(0),loadout.items().get(1),loadout.items().get(2),loadout.items().get(3)).path);
        assertEquals(1,loadout.items().get(3).getCount()); // Snapshots do not share mutable stacks.
        assertTrue(dev.vic17usd.spacefabric.block.HotVolcanicRockBlock.protectsFromHeat(loadout.items().get(3)));
        assertEquals(dev.vic17usd.spacefabric.SpaceFabric.id("suit_inventory"),BuiltInRegistries.MENU.getKey(dev.vic17usd.spacefabric.menu.ModMenus.SUIT));
        for(var stack:loadout.items())assertInstanceOf(dev.vic17usd.spacefabric.equipment.SuitItem.class,stack.getItem());
    }
    @Test void suitContainerUsesALiveStorageViewRejectsWrongSlotsAndDoesNotDuplicateRemovedItems() {
        var stored=new java.util.concurrent.atomic.AtomicReference<>(dev.vic17usd.spacefabric.equipment.SuitLoadout.empty());
        var container=new dev.vic17usd.spacefabric.equipment.SuitContainer(stored::get,stored::set,p->true);
        var loadout=suitLoadout("venus");
        for(int index=0;index<4;index++) {
            final int part=index;
            assertTrue(container.canPlaceItem(part,loadout.items().get(part)));
            assertFalse(container.canPlaceItem(part,new ItemStack(Items.NETHERITE_HELMET)));
            assertFalse(container.canPlaceItem(part,loadout.items().get((part+1)%4)));
            container.setItem(part,loadout.items().get(part));
        }
        assertEquals(dev.vic17usd.spacefabric.equipment.SuitTier.VENUS,stored.get().tier());
        assertThrows(IllegalArgumentException.class,()->container.setItem(0,new ItemStack(Items.DIAMOND)));
        assertThrows(IllegalArgumentException.class,()->container.setItem(0,new ItemStack(ModItems.VENUS_SUIT_HELMET,2)));
        var taken=container.removeItem(0,64);assertTrue(taken.is(ModItems.VENUS_SUIT_HELMET));assertEquals(1,taken.getCount());
        assertTrue(container.getItem(0).isEmpty());assertTrue(container.removeItem(0,1).isEmpty());
        stored.set(dev.vic17usd.spacefabric.equipment.SuitLoadout.empty()); // Death/respawn clears the actual backing store.
        assertTrue(container.isEmpty());
    }
    @Test void nativeSuitMenuShiftClickRoutesCorrectlyAndKeepsTheOtherInventoryIntact() {
        var stored=new java.util.concurrent.atomic.AtomicReference<>(dev.vic17usd.spacefabric.equipment.SuitLoadout.empty());
        var container=new dev.vic17usd.spacefabric.equipment.SuitContainer(stored::get,stored::set,p->true);
        var inventory=new net.minecraft.world.entity.player.Inventory(null,new net.minecraft.world.entity.EntityEquipment());
        inventory.setItem(9,new ItemStack(ModItems.CRYO_SUIT_HELMET));inventory.setItem(10,new ItemStack(Items.DIAMOND,19));
        var menu=new dev.vic17usd.spacefabric.menu.SuitMenu(1,inventory,container);
        assertEquals(40,menu.slots.size());assertFalse(menu.quickMoveStack(null,4).isEmpty());
        assertTrue(container.getItem(0).is(ModItems.CRYO_SUIT_HELMET));assertTrue(inventory.getItem(9).isEmpty());
        assertTrue(menu.quickMoveStack(null,5).isEmpty());assertEquals(19,inventory.getItem(10).getCount());
        assertFalse(menu.quickMoveStack(null,0).isEmpty());assertTrue(container.getItem(0).isEmpty());
        assertEquals(1,inventory.countItem(ModItems.CRYO_SUIT_HELMET));assertEquals(19,inventory.countItem(Items.DIAMOND));
    }
    @Test void dedicatedSuitDeathPolicyPreservesKeepInventoryAndRespectsVanishing() {
        var original=suitLoadout("abyssal");
        var keep=dev.vic17usd.spacefabric.equipment.SuitEquipment.deathResult(original,true,false);
        assertEquals(dev.vic17usd.spacefabric.equipment.SuitTier.ABYSSAL,keep.remaining().tier());assertTrue(keep.drops().isEmpty());
        assertTrue(dev.vic17usd.spacefabric.equipment.SuitEquipment.deathResult(original,false,true).drops().isEmpty());
        var normal=dev.vic17usd.spacefabric.equipment.SuitEquipment.deathResult(original,false,false);
        assertTrue(normal.remaining().items().stream().allMatch(ItemStack::isEmpty));assertEquals(4,normal.drops().size());
        normal.drops().getFirst().shrink(1);assertFalse(original.items().getFirst().isEmpty());
        original.items().get(0).enchant(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.VANISHING_CURSE),1);
        assertEquals(3,dev.vic17usd.spacefabric.equipment.SuitEquipment.deathResult(original,false,false).drops().size());
    }
    @Test void completeLegacySuitCanMigrateAtomicallyButCombatArmorAndExistingDedicatedItemsCannotBeOverwritten() {
        var worn=suitLoadout("vacuum");var stacks=worn.items();
        var migration=dev.vic17usd.spacefabric.equipment.SuitEquipment.migrationCandidate(dev.vic17usd.spacefabric.equipment.SuitLoadout.empty(),stacks.get(0),stacks.get(1),stacks.get(2),stacks.get(3));
        assertTrue(migration.isPresent());assertEquals(dev.vic17usd.spacefabric.equipment.SuitTier.VACUUM,migration.orElseThrow().tier());
        assertTrue(dev.vic17usd.spacefabric.equipment.SuitEquipment.migrationCandidate(worn,stacks.get(0),stacks.get(1),stacks.get(2),stacks.get(3)).isEmpty());
        assertTrue(dev.vic17usd.spacefabric.equipment.SuitEquipment.migrationCandidate(dev.vic17usd.spacefabric.equipment.SuitLoadout.empty(),new ItemStack(Items.DIAMOND_HELMET),new ItemStack(Items.DIAMOND_CHESTPLATE),new ItemStack(Items.DIAMOND_LEGGINGS),new ItemStack(Items.DIAMOND_BOOTS)).isEmpty());
    }
    @Test void malformedSuitInventoriesCannotLoadAdditionalSlotsOrOverstackEquipment() {
        assertThrows(IllegalArgumentException.class,()->new dev.vic17usd.spacefabric.equipment.SuitLoadout(List.of(ItemStack.EMPTY)));
        assertThrows(IllegalArgumentException.class,()->new dev.vic17usd.spacefabric.equipment.SuitLoadout(List.of(new ItemStack(ModItems.CRYO_SUIT_HELMET,2),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY)));
        assertTrue(dev.vic17usd.spacefabric.equipment.SuitLoadout.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE),new com.google.gson.JsonArray()).error().isPresent());
    }
    @Test void suitClientMixinTargetsExactNativeRendererDescriptors() throws IOException {
        assertClientTarget("net/minecraft/client/renderer/entity/player/AvatarRenderer","<init>","(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V");
        assertClientTarget("net/minecraft/client/renderer/entity/player/AvatarRenderer","extractRenderState","(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V");
    }
}

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
    @ValueSource(strings = {"decompression", "crushing_pressure", "hypoxia"})
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
                assertEquals(89, box.getXSpan()); assertEquals(73, box.getYSpan());
            } else {
                assertTrue(box.getXSpan() == 124 || box.getXSpan() == 125);
                assertEquals(84, box.getYSpan()); assertEquals(249, box.getXSpan() + box.getZSpan());
            }
            int sampledHeight = dev.vic17usd.spacefabric.worldgen.structure.SurfacePlot.sampleHighest(piece.getBoundingBox(),
                    (x, z) -> generator.getFirstFreeHeight(x, z, net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, height, randomState));
            assertTrue(piece.templateOrigin().getY() > sampledHeight);
            var pieceContext = new net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext(resources, access, templates);
            var saved = piece.createTag(pieceContext);
            var restored = assertInstanceOf(dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece.class,
                    dev.vic17usd.spacefabric.worldgen.structure.MarsSurfaceStructures.PIECE.load(pieceContext, saved));
            assertEquals(piece.templateOrigin(), restored.templateOrigin());
            assertEquals(piece.templateBounds(), restored.templateBounds());
            assertEquals(piece.getBoundingBox(), restored.getBoundingBox());
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

    @Test void actualSurfacePiecePlacesItsTemplateAfterRemovingTerrainFromRooms() throws IOException {
        net.minecraft.nbt.CompoundTag nbt;
        try (var input = WorldgenCodecTest.class.getResourceAsStream("/data/space_fabric/structure/martian_breathforge.nbt")) {
            assertNotNull(input); nbt = net.minecraft.nbt.NbtIo.readCompressed(input, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        }
        var template = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate();
        template.load(registries.lookupOrThrow(Registries.BLOCK), nbt);
        var world = new VoxelWorld(0, 0, 40);
        var piece = new dev.vic17usd.spacefabric.worldgen.structure.MarsSurfacePiece(SpaceFabric.id("martian_breathforge"),
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
            var intended = expected.getOrDefault(new net.minecraft.core.BlockPos(x + 40, dy, z + 40), Blocks.AIR.defaultBlockState());
            assertEquals(intended, world.state(new net.minecraft.core.BlockPos(x, 30 + dy, z)));
            if (intended.isAir()) cleared++; else preserved++;
        }
        assertTrue(cleared > 100); assertTrue(preserved > 100);
    }

    private static JsonElement resource(String path) throws IOException {
        String location = "/data/space_fabric/" + path + ".json";
        var stream = WorldgenCodecTest.class.getResourceAsStream(location);
        assertNotNull(stream, location);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        }
    }
}

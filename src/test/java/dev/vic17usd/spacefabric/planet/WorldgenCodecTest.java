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

    private static JsonElement resource(String path) throws IOException {
        String location = "/data/space_fabric/" + path + ".json";
        var stream = WorldgenCodecTest.class.getResourceAsStream(location);
        assertNotNull(stream, location);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        }
    }
}

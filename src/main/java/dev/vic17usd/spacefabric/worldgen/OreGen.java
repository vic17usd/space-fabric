package dev.vic17usd.spacefabric.worldgen;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class OreGen {
    static {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, SpaceFabric.id("orbital"), OrbitalChunkGenerator.CODEC);
        Registry.register(BuiltInRegistries.FEATURE, SpaceFabric.id("orbital_material"), new OrbitalMaterialFeature());
        Registry.register(BuiltInRegistries.FEATURE, SpaceFabric.id("terrestrial_basalt"), new TerrestrialBasaltFeature());
    }

    public static final ResourceKey<PlacedFeature> MERCURY_REGOLITH = placed("mercury_regolith");
    public static final ResourceKey<PlacedFeature> LUNAR_REGOLITH = placed("lunar_regolith");
    public static final ResourceKey<PlacedFeature> VENUS_HOT_ROCK = placed("venus_hot_rock");
    public static final ResourceKey<PlacedFeature> ARES_CRYSTAL = placed("ares_crystal");
    public static final ResourceKey<PlacedFeature> PHOBOSITE = placed("phobosite");
    public static final ResourceKey<PlacedFeature> TERRESTRIAL_BASALT = placed("terrestrial_basalt");
    public static final ResourceKey<PlacedFeature> JOVIAN_METEORITE = placed("jovian_meteorite");
    public static final ResourceKey<PlacedFeature> SATURN_RING_ICE = placed("saturn_ring_ice");
    public static final ResourceKey<PlacedFeature> URANIAN_METHANE_ICE = placed("uranian_methane_ice");
    public static final ResourceKey<PlacedFeature> NEPTUNIAN_METHANE_ICE = placed("neptunian_methane_ice");
    private static boolean initialized;

    private OreGen() { }
    private static ResourceKey<PlacedFeature> placed(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, SpaceFabric.id(path));
    }

    public static void initialize() {
        if (initialized) return;
        initialized = true;
        add(Planet.MERCURY, MERCURY_REGOLITH, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.MOON, LUNAR_REGOLITH, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.VENUS, VENUS_HOT_ROCK, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.MARS, ARES_CRYSTAL, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.MARS, PHOBOSITE, GenerationStep.Decoration.UNDERGROUND_ORES);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES, TERRESTRIAL_BASALT);
        add(Planet.JUPITER, JOVIAN_METEORITE, GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
        add(Planet.SATURN, SATURN_RING_ICE, GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
        add(Planet.URANUS, URANIAN_METHANE_ICE, GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
        add(Planet.NEPTUNE, NEPTUNIAN_METHANE_ICE, GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
    }

    private static void add(Planet planet, ResourceKey<PlacedFeature> feature, GenerationStep.Decoration step) {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(PlanetBiomes.key(planet)), step, feature);
    }
}

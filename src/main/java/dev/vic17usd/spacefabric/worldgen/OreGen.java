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
        Registry.register(BuiltInRegistries.FEATURE, SpaceFabric.id("shadow_crater_ice"), new ShadowCraterIceFeature());
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
    public static final ResourceKey<PlacedFeature> DENSE_IRON = placed("dense_iron");
    public static final ResourceKey<PlacedFeature> CRATER_ICE = placed("crater_ice");
    public static final ResourceKey<PlacedFeature> VENUSIAN_OLIVINE = placed("venusian_olivine");
    public static final ResourceKey<PlacedFeature> PYROXENE = placed("pyroxene");
    public static final ResourceKey<PlacedFeature> SULFUR = placed("sulfur");
    public static final ResourceKey<PlacedFeature> CONDENSED_ACID = placed("condensed_acid");
    public static final ResourceKey<PlacedFeature> IRON_OXIDE = placed("iron_oxide");
    public static final ResourceKey<PlacedFeature> MARTIAN_OLIVINE = placed("martian_olivine");
    public static final ResourceKey<PlacedFeature> MARTIAN_SULFATE = placed("martian_sulfate");
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
        add(Planet.MERCURY, DENSE_IRON, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.MERCURY, CRATER_ICE, GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
        add(Planet.VENUS, VENUSIAN_OLIVINE, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.VENUS, PYROXENE, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.VENUS, SULFUR, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.VENUS, CONDENSED_ACID, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.MARS, IRON_OXIDE, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.MARS, MARTIAN_OLIVINE, GenerationStep.Decoration.UNDERGROUND_ORES);
        add(Planet.MARS, MARTIAN_SULFATE, GenerationStep.Decoration.UNDERGROUND_ORES);
    }

    private static void add(Planet planet, ResourceKey<PlacedFeature> feature, GenerationStep.Decoration step) {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(PlanetBiomes.key(planet)), step, feature);
    }
}

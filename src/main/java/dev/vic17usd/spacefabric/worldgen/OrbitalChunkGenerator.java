package dev.vic17usd.spacefabric.worldgen;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

/** Flat vacío que conserva la decoración del bioma para pequeños depósitos orbitales. */
public final class OrbitalChunkGenerator extends FlatLevelSource {
    public static final MapCodec<OrbitalChunkGenerator> CODEC = FlatLevelGeneratorSettings.CODEC
            .fieldOf("settings").xmap(OrbitalChunkGenerator::new, OrbitalChunkGenerator::settings);

    public OrbitalChunkGenerator(FlatLevelGeneratorSettings settings) { super(new OrbitalSettings(settings)); }
    @Override protected MapCodec<OrbitalChunkGenerator> codec() { return CODEC; }

    private static final class OrbitalSettings extends FlatLevelGeneratorSettings {
        private OrbitalSettings(FlatLevelGeneratorSettings source) {
            super(source.structureOverrides(), source.getBiome(), List.of());
            if (!source.getLayersInfo().isEmpty()) {
                throw new IllegalArgumentException("El generador orbital no admite capas de suelo");
            }
            setDecoration();
        }

        @Override public BiomeGenerationSettings adjustGenerationSettings(Holder<Biome> biome) {
            // El flat vanilla elimina toda decoración en un mundo vacío con bioma personalizado.
            return biome.value().getGenerationSettings();
        }
    }
}

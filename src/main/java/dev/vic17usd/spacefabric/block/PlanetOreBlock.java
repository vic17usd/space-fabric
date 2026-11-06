package dev.vic17usd.spacefabric.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.DropExperienceBlock;

/** Los ítems salen de loot tables; XP usa la implementación vanilla, compatible con Silk Touch. */
public final class PlanetOreBlock extends DropExperienceBlock {
    public static final MapCodec<PlanetOreBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IntProviders.codec(0, 10).fieldOf("experience").forGetter(block -> block.experience),
            propertiesCodec()).apply(instance, PlanetOreBlock::new));
    private final IntProvider experience;

    public PlanetOreBlock(IntProvider experience, Properties properties) {
        super(experience, properties);
        this.experience = experience;
    }

    @Override public MapCodec<PlanetOreBlock> codec() { return CODEC; }
}

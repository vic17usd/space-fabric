package dev.vic17usd.spacefabric.mars;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.*;

/** Geographic north/south caps on an infinite flat world; Y never changes the planetary biome. */
public final class MarsPolarBiomeSource extends BiomeSource {
    public static final MapCodec<MarsPolarBiomeSource> CODEC=RecordCodecBuilder.mapCodec(i->i.group(
        Biome.CODEC.fieldOf("temperate").forGetter(v->v.temperate),Biome.CODEC.fieldOf("polar").forGetter(v->v.polar),
        Codec.intRange(1024,1000000).optionalFieldOf("polar_start",8192).forGetter(v->v.start)).apply(i,MarsPolarBiomeSource::new));
    private final Holder<Biome> temperate,polar;private final int start;
    public MarsPolarBiomeSource(Holder<Biome> temperate,Holder<Biome> polar,int start){this.temperate=temperate;this.polar=polar;this.start=start;}
    @Override protected MapCodec<? extends BiomeSource> codec(){return CODEC;}
    @Override protected Stream<Holder<Biome>> collectPossibleBiomes(){return Stream.of(temperate,polar);}
    /** Smoothly irregular border, continuous across chunk boundaries and symmetric between poles. */
    public static boolean polar(int x,int z,int start){
        double border=start+192*Math.sin(x/1400.0)+96*Math.sin(x/431.0);
        return Math.abs((double)z)>=border;
    }
    @Override public Holder<Biome> getNoiseBiome(int x,int y,int z,Climate.Sampler sampler){return polar(x*4,z*4,start)?polar:temperate;}
}

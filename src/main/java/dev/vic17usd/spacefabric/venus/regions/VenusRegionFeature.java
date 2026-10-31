package dev.vic17usd.spacefabric.venus.regions;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.worldgen.PlanetBiomes;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Per-chunk geology before surface structures; all writes stay in the current chunk. */
public final class VenusRegionFeature extends Feature<NoneFeatureConfiguration> {
    public enum Kind {
        RIFTS("venus_obsidian_riftlands"),TESSERA("venus_sulfur_shardlands"),ASHLANDS("venus_basalt_ashlands");
        public final String path;Kind(String path){this.path=path;}
        public ResourceKey<net.minecraft.world.level.biome.Biome> biome(){return ResourceKey.create(Registries.BIOME,SpaceFabric.id(path));}
    }
    private static final TagKey<Block> HOST=TagKey.create(Registries.BLOCK,SpaceFabric.id("venus_host_rocks"));
    private final Kind kind;
    public VenusRegionFeature(Kind kind){super(NoneFeatureConfiguration.CODEC);this.kind=kind;}
    /** Conservatively reserve referenced structure chunks, including plots and imported calderas. */
    public static boolean canDecorate(ChunkAccess chunk){return chunk.getAllReferences().isEmpty()&&chunk.getAllStarts().isEmpty();}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        var world=context.level();var origin=context.origin();
        if(!world.getLevel().dimension().equals(PlanetDimensions.key(Planet.VENUS))||!canDecorate(world.getChunk(origin)))return false;
        return placePatch(world,origin,context.random(),kind,world.getSeed(),p->world.getBiome(p).is(kind.biome()));
    }
    public static boolean placePatch(WorldGenLevel world,BlockPos origin,RandomSource random,Kind kind,long seed,Predicate<BlockPos> belongs){
        int minX=origin.getX()&~15,minZ=origin.getZ()&~15;boolean changed=false;
        for(int dx=0;dx<16;dx++)for(int dz=0;dz<16;dz++){
            int x=minX+dx,z=minZ+dz,y=world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
            var pos=new BlockPos(x,y,z);
            if(y<world.getMinY()+18||y+12>=world.getMaxY()||!belongs.test(pos)||!world.getBlockState(pos).is(HOST)||!world.getBlockState(pos).getFluidState().isEmpty()||!world.getBlockState(pos.above()).isAir())continue;
            int rise=kind==Kind.TESSERA?RegionRelief.tessera(x,z):kind==Kind.RIFTS?RegionRelief.rift(x,z,seed):0;
            var rock=kind==Kind.RIFTS?VenusRegionBlocks.RIFT_OBSIDIAN:kind==Kind.TESSERA?VenusRegionBlocks.TESSERA:VenusRegionBlocks.BASALT_SHEET;
            if(rise<0){
                // Selectively cut only host rock; never erase an ore, fluid or foreign block below.
                boolean clear=true;for(int yy=y+rise;yy<=y;yy++)if(!world.getBlockState(new BlockPos(x,yy,z)).is(HOST)){clear=false;break;}
                if(!clear)continue;
                for(int yy=y+rise+1;yy<=y;yy++)world.setBlock(new BlockPos(x,yy,z),Blocks.AIR.defaultBlockState(),2);
            }else {
                boolean clear=true;for(int yy=y+1;yy<=y+rise;yy++)if(!world.getBlockState(new BlockPos(x,yy,z)).isAir()){clear=false;break;}
                if(!clear)continue;
                for(int yy=y+1;yy<=y+rise;yy++)world.setBlock(new BlockPos(x,yy,z),rock.defaultBlockState(),2);
            }
            var top=new BlockPos(x,y+rise,z);
            Block surface=rock;
            if(kind==Kind.TESSERA&&RegionRelief.fissure(x,z,seed))surface=VenusRegionBlocks.FISSURE;
            if(kind==Kind.RIFTS&&rise<0)surface=VenusRegionBlocks.RIFT_EMBER;
            if(kind==Kind.ASHLANDS&&random.nextInt(5)==0)surface=VenusRegionBlocks.CHARRED_ROCK;
            world.setBlock(top,surface.defaultBlockState(),2);changed=true;
            if(kind==Kind.ASHLANDS&&random.nextInt(10)==0)world.setBlock(top.above(),VenusRegionBlocks.ASH.defaultBlockState(),2);
        }
        if(kind==Kind.ASHLANDS&&random.nextInt(4)==0)lavaPocket(world,minX+8,minZ+8,belongs);
        return changed;
    }
    private static void lavaPocket(WorldGenLevel world,int x,int z,Predicate<BlockPos> belongs){
        int y=world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
        // Ash is a cosmetic layer, not the solid support used for the basin.
        if(world.getBlockState(new BlockPos(x,y,z)).is(VenusRegionBlocks.ASH))y--;
        if(!world.getBlockState(new BlockPos(x,y-1,z)).is(HOST))return;
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){
            var p=new BlockPos(x+dx,y,z+dz);
            if(!belongs.test(p)||!world.getBlockState(p).is(HOST))return;
            var above=world.getBlockState(p.above());if(!above.isAir()&&!above.is(VenusRegionBlocks.ASH))return;
        }
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){
            var p=new BlockPos(x+dx,y,z+dz);world.setBlock(p,VenusRegionBlocks.BASALT_SHEET.defaultBlockState(),2);world.setBlock(p.above(),Blocks.AIR.defaultBlockState(),2);
        }
        var center=new BlockPos(x,y,z);world.setBlock(center.below(),VenusRegionBlocks.CHARRED_ROCK.defaultBlockState(),2);
        world.setBlock(center,dev.vic17usd.spacefabric.fluid.PlanetFluids.VISCOUS_LAVA_BLOCK.defaultBlockState(),2);
    }
    public static void initialize(){
        for(var kind:Kind.values()){
            var id=SpaceFabric.id(kind.path+"_terrain");Registry.register(BuiltInRegistries.FEATURE,id,new VenusRegionFeature(kind));
            BiomeModifications.addFeature(c->c.getBiomeKey().equals(kind.biome()),GenerationStep.Decoration.LOCAL_MODIFICATIONS,ResourceKey.create(Registries.PLACED_FEATURE,id));
        }
        BiomeModifications.addFeature(c->c.getBiomeKey().equals(PlanetBiomes.VENUS_RIFTLANDS),GenerationStep.Decoration.UNDERGROUND_ORES,
            ResourceKey.create(Registries.PLACED_FEATURE,SpaceFabric.id("venus_rift_iron")));
    }
}

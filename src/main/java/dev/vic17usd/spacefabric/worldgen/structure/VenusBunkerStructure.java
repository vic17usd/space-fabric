package dev.vic17usd.spacefabric.worldgen.structure;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;

/** Deep, buried arena. Locate surveys nine heights and never opens a template. */
public final class VenusBunkerStructure extends Structure {
    public static final MapCodec<VenusBunkerStructure> CODEC=RecordCodecBuilder.mapCodec(i->i.group(
        settingsCodec(i),Codec.intRange(1,192).fieldOf("width").forGetter(v->v.width),
        Codec.intRange(1,128).fieldOf("height").forGetter(v->v.height),
        Codec.intRange(1,192).fieldOf("depth").forGetter(v->v.depth),
        VenusSurfaceStructure.Tile.CODEC.listOf().validate(list->list.isEmpty()||list.size()>256?
            DataResult.error(()->"Expected 1..256 bunker tiles"):DataResult.success(list))
            .fieldOf("tiles").forGetter(v->v.tiles)
    ).apply(i,VenusBunkerStructure::new));
    private final int width,height,depth;
    private final List<VenusSurfaceStructure.Tile> tiles;
    public VenusBunkerStructure(StructureSettings settings,int width,int height,int depth,List<VenusSurfaceStructure.Tile> tiles){
        super(settings);this.width=width;this.height=height;this.depth=depth;this.tiles=List.copyOf(tiles);
    }
    public List<VenusSurfaceStructure.Tile> tiles(){return tiles;}
    @Override public StructureType<?> type(){return VenusSurfaceStructures.BUNKER;}
    public static int baseY(int minY){return minY+8;}
    @Override protected Optional<GenerationStub> findGenerationPoint(GenerationContext context){
        Rotation rotation=Rotation.getRandom(context.random());
        var rotated=BoundingBox.fromCorners(BlockPos.ZERO,VenusSurfaceStructure.rotate(new BlockPos(width-1,0,depth-1),rotation));
        var origin=new BlockPos(context.chunkPos().getMinBlockX()+8-rotated.getCenter().getX(),
            baseY(context.heightAccessor().getMinY()),context.chunkPos().getMinBlockZ()+8-rotated.getCenter().getZ());
        var bounds=rotated.moved(origin.getX(),origin.getY(),origin.getZ());
        int coverTop=origin.getY()+height+15;
        if(coverTop>=context.heightAccessor().getMaxY())return Optional.empty();
        for(int x:new int[]{bounds.minX(),bounds.getCenter().getX(),bounds.maxX()})
            for(int z:new int[]{bounds.minZ(),bounds.getCenter().getZ(),bounds.maxZ()})
                if(context.chunkGenerator().getFirstFreeHeight(x,z,Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(),context.randomState())<=coverTop+1)return Optional.empty();
        var locator=origin.offset(VenusSurfaceStructure.rotate(new BlockPos(width/2,5,depth/2),rotation));
        return Optional.of(new GenerationStub(locator,builder->{
            for(var tile:tiles){
                var template=context.structureTemplateManager().get(tile.template()).orElseThrow(()->new IllegalStateException("Missing bunker tile "+tile.template()));
                builder.addPiece(new VenusBunkerPiece(tile.template(),template,
                    origin.offset(VenusSurfaceStructure.rotate(new BlockPos(tile.x(),0,tile.z()),rotation)),rotation));
            }
        }));
    }
}

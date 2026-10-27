package dev.vic17usd.spacefabric.worldgen.structure;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** The locate path samples only terrain heights; it never loads 200,000-block templates. */
public final class VenusSurfaceStructure extends Structure {
    public record Tile(Identifier template,int x,int z) {
        public static final Codec<Tile> CODEC=RecordCodecBuilder.create(i->i.group(
            Identifier.CODEC.fieldOf("template").forGetter(Tile::template),Codec.intRange(0,191).fieldOf("x").forGetter(Tile::x),
            Codec.intRange(0,191).fieldOf("z").forGetter(Tile::z)).apply(i,Tile::new));
    }
    public static final MapCodec<VenusSurfaceStructure> CODEC=RecordCodecBuilder.mapCodec(i->i.group(
        settingsCodec(i),Codec.intRange(1,192).fieldOf("width").forGetter(v->v.width),
        Codec.intRange(1,128).fieldOf("height").forGetter(v->v.height),Codec.intRange(1,192).fieldOf("depth").forGetter(v->v.depth),
        Codec.intRange(0,8).optionalFieldOf("padding",4).forGetter(v->v.padding),
        Tile.CODEC.listOf().validate(list->list.isEmpty()||list.size()>256?DataResult.error(()->"Expected 1..256 tiles"):DataResult.success(list)).fieldOf("tiles").forGetter(v->v.tiles)
    ).apply(i,VenusSurfaceStructure::new));
    private final int width,height,depth,padding;
    private final List<Tile> tiles;
    public VenusSurfaceStructure(StructureSettings settings,int width,int height,int depth,int padding,List<Tile> tiles) {
        super(settings);this.width=width;this.height=height;this.depth=depth;this.padding=padding;this.tiles=List.copyOf(tiles);
    }
    public List<Tile> tiles(){return tiles;}
    @Override public StructureType<?> type(){return VenusSurfaceStructures.TYPE;}
    public static BlockPos rotate(BlockPos pos,Rotation rotation){return StructureTemplate.transform(pos,Mirror.NONE,rotation,BlockPos.ZERO);}
    @Override protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        var rotation=Rotation.getRandom(context.random());
        var rotated=BoundingBox.fromCorners(BlockPos.ZERO,rotate(new BlockPos(width-1,0,depth-1),rotation));
        var start=new BlockPos(context.chunkPos().getMinBlockX()+8-rotated.getCenter().getX(),0,context.chunkPos().getMinBlockZ()+8-rotated.getCenter().getZ());
        var bounds=rotated.moved(start.getX(),0,start.getZ()).inflatedBy(padding,0,padding);
        int y=SurfacePlot.sampleHighest(bounds,(x,z)->context.chunkGenerator().getFirstFreeHeight(x,z,Heightmap.Types.WORLD_SURFACE_WG,context.heightAccessor(),context.randomState()))-1;
        if(y<context.heightAccessor().getMinY()+8||y+height+2>context.heightAccessor().getMaxY())return Optional.empty();
        var origin=new BlockPos(start.getX(),y,start.getZ());
        var plot=new BoundingBox(bounds.minX(),context.heightAccessor().getMinY()+4,bounds.minZ(),bounds.maxX(),context.heightAccessor().getMaxY()-1,bounds.maxZ());
        return Optional.of(new GenerationStub(new BlockPos(bounds.getCenter().getX(),y,bounds.getCenter().getZ()),builder->{
            // Each affected chunk is flattened before any of its own building tiles are placed.
            builder.addPiece(new VenusSurfacePiece(plot,y));
            for(var tile:tiles) {
                var template=context.structureTemplateManager().get(tile.template()).orElseThrow(()->new IllegalStateException("Missing Venus tile "+tile.template()));
                builder.addPiece(new VenusSurfacePiece(tile.template(),template,origin.offset(rotate(new BlockPos(tile.x(),0,tile.z()),rotation)),rotation));
            }
        }));
    }
}

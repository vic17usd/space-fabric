package dev.vic17usd.spacefabric.worldgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

/** A floor operation or one <=16x16 template, clipped by the chunk currently generating. */
public final class VenusSurfacePiece extends StructurePiece {
    private final boolean plot;
    private final Identifier templateId;
    private final StructureTemplate template;
    private BlockPos origin;
    private final Rotation rotation;
    private final int blend;
    private final boolean natural;
    public VenusSurfacePiece(BoundingBox box,int surfaceY) {
        this(box,surfaceY,0);
    }
    public VenusSurfacePiece(BoundingBox box,int surfaceY,int blend) {
        super(VenusSurfaceStructures.PIECE,0,box);plot=true;templateId=null;template=null;
        this.blend=blend;natural=false;
        origin=new BlockPos(box.getCenter().getX(),surfaceY,box.getCenter().getZ());rotation=Rotation.NONE;
    }
    public VenusSurfacePiece(Identifier id,StructureTemplate template,BlockPos origin,Rotation rotation) {
        this(id,template,origin,rotation,false);
    }
    public VenusSurfacePiece(Identifier id,StructureTemplate template,BlockPos origin,Rotation rotation,boolean natural) {
        super(VenusSurfaceStructures.PIECE,0,template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation),origin));
        plot=false;templateId=id;this.template=template;this.origin=origin;this.rotation=rotation;
        blend=0;this.natural=natural;
    }
    public VenusSurfacePiece(StructurePieceSerializationContext context,CompoundTag tag) {
        super(VenusSurfaceStructures.PIECE,tag);plot=tag.getBooleanOr("Plot",false);natural=tag.getBooleanOr("Natural",false);
        blend=Math.clamp(tag.getIntOr("Blend",0),0,32);
        origin=new BlockPos(tag.getIntOr("OriginX",0),tag.getIntOr("OriginY",0),tag.getIntOr("OriginZ",0));
        rotation=Rotation.valueOf(tag.getStringOr("Rotation",Rotation.NONE.name()));
        templateId=plot?null:Identifier.parse(tag.getStringOr("Template",""));
        template=plot?null:context.structureTemplateManager().get(templateId).orElseThrow();
    }
    public boolean natural(){return natural;}
    public boolean isPlot(){return plot;}
    public BlockPos origin(){return origin;}
    public int blend(){return blend;}
    @Override public Rotation getRotation(){return rotation;}
    @Override public BlockPos getLocatorPosition(){return origin;}
    @Override public void move(int x,int y,int z){super.move(x,y,z);origin=origin.offset(x,y,z);}
    @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context,CompoundTag tag) {
        tag.putBoolean("Plot",plot);tag.putInt("OriginX",origin.getX());tag.putInt("OriginY",origin.getY());tag.putInt("OriginZ",origin.getZ());tag.putString("Rotation",rotation.name());
        if(!plot)tag.putString("Template",templateId.toString());
        if(plot)tag.putInt("Blend",blend);
        if(natural)tag.putBoolean("Natural",true);
    }
    @Override public void postProcess(WorldGenLevel level,StructureManager structures,ChunkGenerator generator,RandomSource random,BoundingBox clip,ChunkPos chunk,BlockPos pivot) {
        if(plot){SurfacePlot.prepareVenus(level,boundingBox,origin.getY()+1,clip,blend);return;}
        if(natural) {
            var settings=new StructurePlaceSettings().setRotation(rotation);
            var basalt=dev.vic17usd.spacefabric.block.ModBlocks.VULCANIZED_BASALT;
            for(var block:template.filterBlocks(origin,settings,basalt)) {
                var base=block.pos();if(base.getY()!=origin.getY()||!clip.isInside(base))continue;
                int top=level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG,base.getX(),base.getZ())-1;
                for(int y=base.getY();y<=top;y++) {
                    var pos=new BlockPos(base.getX(),y,base.getZ());
                    if(!level.getBlockState(pos).isAir())level.setBlock(pos,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),2);
                }
                for(int y=base.getY()-1;y>level.getMinY();y--) {
                    var pos=new BlockPos(base.getX(),y,base.getZ());
                    var old=level.getBlockState(pos);
                    if(old.getFluidState().isEmpty()&&old.isCollisionShapeFullBlock(level,pos)||level.getBlockEntity(pos)!=null)break;
                    level.setBlock(pos,basalt.defaultBlockState(),2);
                }
            }
        }
        template.placeInWorld(level,origin,origin,new StructurePlaceSettings().setRotation(rotation).setBoundingBox(clip).setIgnoreEntities(true).setKnownShape(true),random,18);
    }
}

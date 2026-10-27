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
    public VenusSurfacePiece(BoundingBox box,int surfaceY) {
        super(VenusSurfaceStructures.PIECE,0,box);plot=true;templateId=null;template=null;
        origin=new BlockPos(box.getCenter().getX(),surfaceY,box.getCenter().getZ());rotation=Rotation.NONE;
    }
    public VenusSurfacePiece(Identifier id,StructureTemplate template,BlockPos origin,Rotation rotation) {
        super(VenusSurfaceStructures.PIECE,0,template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation),origin));
        plot=false;templateId=id;this.template=template;this.origin=origin;this.rotation=rotation;
    }
    public VenusSurfacePiece(StructurePieceSerializationContext context,CompoundTag tag) {
        super(VenusSurfaceStructures.PIECE,tag);plot=tag.getBooleanOr("Plot",false);
        origin=new BlockPos(tag.getIntOr("OriginX",0),tag.getIntOr("OriginY",0),tag.getIntOr("OriginZ",0));
        rotation=Rotation.valueOf(tag.getStringOr("Rotation",Rotation.NONE.name()));
        templateId=plot?null:Identifier.parse(tag.getStringOr("Template",""));
        template=plot?null:context.structureTemplateManager().get(templateId).orElseThrow();
    }
    public boolean isPlot(){return plot;}
    public BlockPos origin(){return origin;}
    @Override public Rotation getRotation(){return rotation;}
    @Override public BlockPos getLocatorPosition(){return origin;}
    @Override public void move(int x,int y,int z){super.move(x,y,z);origin=origin.offset(x,y,z);}
    @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context,CompoundTag tag) {
        tag.putBoolean("Plot",plot);tag.putInt("OriginX",origin.getX());tag.putInt("OriginY",origin.getY());tag.putInt("OriginZ",origin.getZ());tag.putString("Rotation",rotation.name());
        if(!plot)tag.putString("Template",templateId.toString());
    }
    @Override public void postProcess(WorldGenLevel level,StructureManager structures,ChunkGenerator generator,RandomSource random,BoundingBox clip,ChunkPos chunk,BlockPos pivot) {
        if(plot){SurfacePlot.prepareVenus(level,boundingBox,origin.getY()+1,clip);return;}
        template.placeInWorld(level,origin,origin,new StructurePlaceSettings().setRotation(rotation).setBoundingBox(clip).setIgnoreEntities(true).setKnownShape(true),random,18);
    }
}

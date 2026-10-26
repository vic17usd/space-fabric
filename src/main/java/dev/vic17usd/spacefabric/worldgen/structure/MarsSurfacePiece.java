package dev.vic17usd.spacefabric.worldgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class MarsSurfacePiece extends StructurePiece {
    private final Identifier templateId;
    private final StructureTemplate template;
    private BlockPos origin;
    private final Rotation templateRotation;
    private final int padding;

    public MarsSurfacePiece(Identifier id, StructureTemplate template, BlockPos origin, Rotation rotation,
            int padding, int minY, int maxY) {
        super(MarsSurfaceStructures.PIECE, 0, plotBounds(template, origin, rotation, padding, minY, maxY));
        this.templateId = id; this.template = template; this.origin = origin;
        this.templateRotation = rotation; this.padding = padding;
    }
    public MarsSurfacePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(MarsSurfaceStructures.PIECE, tag);
        templateId = Identifier.parse(tag.getStringOr("Template", ""));
        template = context.structureTemplateManager().get(templateId).orElseThrow();
        origin = new BlockPos(tag.getIntOr("OriginX", 0), tag.getIntOr("OriginY", 0), tag.getIntOr("OriginZ", 0));
        templateRotation = Rotation.valueOf(tag.getStringOr("TemplateRotation", Rotation.NONE.name()));
        padding = Math.clamp(tag.getIntOr("Padding", 8), 0, 16);
    }
    private static BoundingBox plotBounds(StructureTemplate template, BlockPos origin, Rotation rotation, int padding, int minY, int maxY) {
        var box = template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), origin);
        return new BoundingBox(box.minX() - padding, minY, box.minZ() - padding, box.maxX() + padding, maxY, box.maxZ() + padding);
    }
    public BoundingBox templateBounds() { return template.getBoundingBox(new StructurePlaceSettings().setRotation(templateRotation), origin); }
    public BlockPos templateOrigin() { return origin; }
    @Override public Rotation getRotation() { return templateRotation; }
    @Override public void move(int x, int y, int z) {
        super.move(x, y, z); origin = origin.offset(x, y, z);
    }
    @Override public BlockPos getLocatorPosition() {
        var box = templateBounds(); return new BlockPos(box.getCenter().getX(), origin.getY(), box.getCenter().getZ());
    }
    @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Template", templateId.toString()); tag.putString("TemplateRotation", templateRotation.name());
        tag.putInt("OriginX", origin.getX()); tag.putInt("OriginY", origin.getY()); tag.putInt("OriginZ", origin.getZ());
        tag.putInt("Padding", padding);
    }
    @Override public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator,
            RandomSource random, BoundingBox clip, ChunkPos chunk, BlockPos pivot) {
        SurfacePlot.prepare(level, boundingBox, origin.getY(), clip);
        // La preparación se completa antes de colocar bloques y habitantes en este chunk.
        var settings = new StructurePlaceSettings().setRotation(templateRotation).setBoundingBox(clip)
                .setIgnoreEntities(false).setFinalizeEntities(true).setKnownShape(true);
        template.placeInWorld(level, origin, origin, settings, random, 18);
    }
}

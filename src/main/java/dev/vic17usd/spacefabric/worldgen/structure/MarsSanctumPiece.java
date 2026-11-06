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

/** El NBT incluye aire explícito: excava solo la arena y solo dentro del chunk que se decora. */
public final class MarsSanctumPiece extends StructurePiece {
    private final Identifier id;
    private final StructureTemplate template;
    private final Rotation rotation;
    private BlockPos origin;
    public MarsSanctumPiece(Identifier id, StructureTemplate template, BlockPos origin, Rotation rotation) {
        super(MarsSurfaceStructures.SANCTUM_PIECE, 0, coveredBounds(template, origin, rotation));
        this.id = id; this.template = template; this.origin = origin; this.rotation = rotation;
    }
    public MarsSanctumPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(MarsSurfaceStructures.SANCTUM_PIECE, tag);
        id = Identifier.parse(tag.getStringOr("Template", ""));
        template = context.structureTemplateManager().get(id).orElseThrow();
        origin = new BlockPos(tag.getIntOr("OriginX", 0), tag.getIntOr("OriginY", 0), tag.getIntOr("OriginZ", 0));
        rotation = Rotation.valueOf(tag.getStringOr("TemplateRotation", Rotation.NONE.name()));
    }
    private static BoundingBox coveredBounds(StructureTemplate template, BlockPos origin, Rotation rotation) {
        var box = template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), origin);
        return new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY() + 16, box.maxZ());
    }
    public BlockPos templateOrigin() { return origin; }
    @Override public Rotation getRotation() { return rotation; }
    @Override public void move(int x, int y, int z) { super.move(x, y, z); origin = origin.offset(x, y, z); }
    @Override public BlockPos getLocatorPosition() { return new BlockPos(boundingBox.getCenter().getX(), origin.getY() + 9, boundingBox.getCenter().getZ()); }
    @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Template", id.toString()); tag.putString("TemplateRotation", rotation.name());
        tag.putInt("OriginX", origin.getX()); tag.putInt("OriginY", origin.getY()); tag.putInt("OriginZ", origin.getZ());
    }
    @Override public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator,
            RandomSource random, BoundingBox clip, ChunkPos chunk, BlockPos pivot) {
        // Una cubierta de 16 capas evita exponer la cámara si un cañón corta el terreno.
        // Solo rellena huecos y fluidos; conserva la roca ya existente.
        var cursor = new BlockPos.MutableBlockPos();
        for (int x = Math.max(boundingBox.minX(), clip.minX()); x <= Math.min(boundingBox.maxX(), clip.maxX()); x++)
            for (int z = Math.max(boundingBox.minZ(), clip.minZ()); z <= Math.min(boundingBox.maxZ(), clip.maxZ()); z++)
                for (int y = Math.max(origin.getY() + template.getSize().getY(), clip.minY()); y <= Math.min(boundingBox.maxY(), clip.maxY()); y++) {
                    cursor.set(x,y,z);
                    var state = level.getBlockState(cursor);
                    if (state.isAir() || !state.getFluidState().isEmpty()) level.setBlock(cursor,
                            dev.vic17usd.spacefabric.block.ModBlocks.MARTIAN_STONE.defaultBlockState(), 18);
                }
        template.placeInWorld(level, origin, origin, new StructurePlaceSettings().setRotation(rotation).setBoundingBox(clip)
                .setIgnoreEntities(false).setFinalizeEntities(true).setKnownShape(true), random, 18);
    }
}

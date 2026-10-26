package dev.vic17usd.spacefabric.worldgen.structure;

import com.mojang.serialization.MapCodec;
import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import java.util.Optional;

/** Arena profunda a cuatro bloques sobre el fondo del mundo; no consulta ni aplana la superficie. */
public final class MarsSanctumStructure extends Structure {
    public static final MapCodec<MarsSanctumStructure> CODEC = simpleCodec(MarsSanctumStructure::new);
    public MarsSanctumStructure(StructureSettings settings) { super(settings); }
    @Override public StructureType<?> type() { return MarsSurfaceStructures.SANCTUM; }
    @Override protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        var id = SpaceFabric.id("soulfire_netherite_sanctum");
        var template = context.structureTemplateManager().get(id).orElseThrow(() -> new IllegalStateException("Falta " + id));
        var rotation = Rotation.getRandom(context.random());
        var rotated = template.getBoundingBox(new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings().setRotation(rotation), BlockPos.ZERO);
        var origin = new BlockPos(context.chunkPos().getMinBlockX() + 8 - rotated.getCenter().getX(),
                context.heightAccessor().getMinY() + 4, context.chunkPos().getMinBlockZ() + 8 - rotated.getCenter().getZ());
        var piece = new MarsSanctumPiece(id, template, origin, rotation);
        return Optional.of(new GenerationStub(piece.getLocatorPosition(), builder -> builder.addPiece(piece)));
    }
}

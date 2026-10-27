package dev.vic17usd.spacefabric.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

/** Edificio rígido sobre una parcela local nivelada, sin cambiar el resto de Marte. */
public final class MarsSurfaceStructure extends Structure {
    public static final MapCodec<MarsSurfaceStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            Identifier.CODEC.fieldOf("template").forGetter(value -> value.template),
            Codec.intRange(0, 16).optionalFieldOf("padding", 8).forGetter(value -> value.padding)
    ).apply(instance, MarsSurfaceStructure::new));
    private final Identifier template;
    private final int padding;
    public MarsSurfaceStructure(StructureSettings settings, Identifier template, int padding) {
        super(settings); this.template = template; this.padding = padding;
    }
    @Override public StructureType<?> type() { return MarsSurfaceStructures.TYPE; }
    @Override protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        var building = context.structureTemplateManager().get(template).orElseThrow(
                () -> new IllegalStateException("Falta la plantilla de superficie " + template));
        var size = building.getSize();
        if (size.getX() < 1 || size.getZ() < 1 || size.getY() < 1 || size.getX() > 128 || size.getZ() > 128 || size.getY() > 128)
            throw new IllegalStateException("La plantilla debe medir entre 1 y 128 bloques por eje: " + template);
        var rotation = Rotation.getRandom(context.random());
        var rotated = building.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), BlockPos.ZERO);
        var start = new BlockPos(context.chunkPos().getMinBlockX() + 8 - rotated.getCenter().getX(), 0,
                context.chunkPos().getMinBlockZ() + 8 - rotated.getCenter().getZ());
        var bounds = building.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), start);
        var plot = bounds.inflatedBy(padding, 0, padding);
        int y = SurfacePlot.sampleHighest(plot, (x, z) -> context.chunkGenerator().getFirstFreeHeight(x, z,
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState())) - 1;
        if (y < context.heightAccessor().getMinY() + 8 || y + building.getSize().getY() + 2 > context.heightAccessor().getMaxY())
            return Optional.empty();
        var origin = new BlockPos(start.getX(), y, start.getZ());
        var piece = new MarsSurfacePiece(template, building, origin, rotation, padding,
                context.heightAccessor().getMinY() + 4, context.heightAccessor().getMaxY());
        return Optional.of(new GenerationStub(piece.getLocatorPosition(), builder -> builder.addPiece(piece)));
    }
}

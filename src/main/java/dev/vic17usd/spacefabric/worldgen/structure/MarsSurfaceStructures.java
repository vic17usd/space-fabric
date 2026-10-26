package dev.vic17usd.spacefabric.worldgen.structure;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public final class MarsSurfaceStructures {
    public static final StructureType<MarsSurfaceStructure> TYPE = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE, SpaceFabric.id("martian_surface"), () -> MarsSurfaceStructure.CODEC);
    public static final StructurePieceType PIECE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE,
            SpaceFabric.id("martian_surface_piece"), MarsSurfacePiece::new);
    public static final StructureType<MarsSanctumStructure> SANCTUM = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE, SpaceFabric.id("martian_sanctum"), () -> MarsSanctumStructure.CODEC);
    public static final StructurePieceType SANCTUM_PIECE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE,
            SpaceFabric.id("martian_sanctum_piece"), MarsSanctumPiece::new);
    private MarsSurfaceStructures() { }
    public static void initialize() { }
}

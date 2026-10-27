package dev.vic17usd.spacefabric.worldgen.structure;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public final class VenusSurfaceStructures {
    public static final StructureType<VenusSurfaceStructure> TYPE=Registry.register(BuiltInRegistries.STRUCTURE_TYPE,SpaceFabric.id("venus_surface"),()->VenusSurfaceStructure.CODEC);
    public static final StructurePieceType PIECE=Registry.register(BuiltInRegistries.STRUCTURE_PIECE,SpaceFabric.id("venus_surface_piece"),VenusSurfacePiece::new);
    private VenusSurfaceStructures() { }
    public static void initialize() { }
}

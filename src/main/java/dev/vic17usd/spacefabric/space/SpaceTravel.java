package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.planet.Planet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SpaceTravel {
    private SpaceTravel() { }
    public static boolean transfer(RocketEntity rocket) {
        if (!(rocket.level() instanceof ServerLevel source) || !rocket.isVehicle()) return false;
        var destination = TravelDestination.at(rocket.destinationIndex());
        var target = source.getServer().getLevel(destination.world());
        if (target == null || target == source) return false;
        boolean orbital = destination.planet() == Planet.JUPITER || destination.planet() == Planet.SATURN
                || destination.planet() == Planet.URANUS || destination.planet() == Planet.NEPTUNE;
        var floor = LandingPlanner.find(new LandingPlanner.World() {
            @Override public int surface(int x, int z) {
                int highest = target.getMinY();
                for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++)
                    highest = Math.max(highest, target.getHeight(Heightmap.Types.MOTION_BLOCKING, x + dx, z + dz));
                return target.getBlockState(new BlockPos(x, highest - 1, z)).is(ModBlocks.LANDING_PAD) ? highest - 1 : highest;
            }
            @Override public int maxY() { return target.getMaxY(); }
            @Override public boolean air(BlockPos pos) { return target.getBlockState(pos).isAir(); }
            @Override public boolean pad(BlockPos pos) { return target.getBlockState(pos).is(ModBlocks.LANDING_PAD); }
            @Override public boolean available(BlockPos pos) {
                // Fuerza la carga del único chunk que contiene toda la plataforma.
                target.getChunkAt(pos);
                var box = new AABB(pos.getX() - 2, pos.getY(), pos.getZ() - 2, pos.getX() + 3, pos.getY() + 6, pos.getZ() + 3);
                return target.getWorldBorder().isWithinBounds(box) && target.getEntities(null, box).isEmpty();
            }
        }, orbital, rocket.getUUID().hashCode()).orElse(null);
        if (floor == null) return false;
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++)
            target.setBlockAndUpdate(floor.offset(dx, 0, dz), ModBlocks.LANDING_PAD.defaultBlockState());
        // La API nativa traslada también los pasajeros y los vuelve a montar; no se duplican a mano.
        var arrived = rocket.teleport(new TeleportTransition(target,
                Vec3.atBottomCenterOf(floor.above()), Vec3.ZERO, rocket.getYRot(), 0,
                TeleportTransition.PLACE_PORTAL_TICKET));
        if (arrived instanceof RocketEntity transferred) { transferred.finishLanding(); return true; }
        return false;
    }
}

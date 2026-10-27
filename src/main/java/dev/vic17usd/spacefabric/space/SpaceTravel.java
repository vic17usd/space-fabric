package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.planet.Planet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SpaceTravel {
    public static final TagKey<Block> LANDING_SURFACE = TagKey.create(Registries.BLOCK, SpaceFabric.id("rocket_landing_surface"));
    // Loading only, not simulation or persistent force loading. Refresh while searching; expires in 5 s.
    private static final TicketType LANDING_TICKET = Registry.register(BuiltInRegistries.TICKET_TYPE,
            SpaceFabric.id("rocket_landing"), new TicketType(100, TicketType.FLAG_LOADING));

    private SpaceTravel() { }
    public static void initialize() { }

    public static LandingSearch prepare(RocketEntity rocket) {
        if (!(rocket.level() instanceof ServerLevel source)) return null;
        var destination = TravelDestination.at(rocket.destinationIndex());
        var target = source.getServer().getLevel(destination.world());
        if (target == null || target == source) return null;
        boolean orbital = isOrbital(destination.planet());
        var world = landingWorld(target);
        return new LandingSearch(new LandingSearch.Chunks() {
            @Override public boolean insideBorder(ChunkPos chunk) {
                return target.getWorldBorder().isWithinBounds(new AABB(chunk.getMinBlockX(), target.getMinY(), chunk.getMinBlockZ(),
                        chunk.getMaxBlockX() + 1, target.getMaxY(), chunk.getMaxBlockZ() + 1));
            }
            @Override public java.util.concurrent.CompletableFuture<LandingPlanner.World> load(ChunkPos chunk) {
                return target.getChunkSource().getChunkFuture(chunk.x(), chunk.z(), ChunkStatus.FULL, true)
                        .thenApply(result -> result.isSuccess() ? world : null);
            }
            @Override public void keepLoaded(ChunkPos chunk) {
                target.getChunkSource().addTicketWithRadius(LANDING_TICKET, chunk, 0);
            }
        }, orbital, rocket.getUUID().hashCode());
    }

    static boolean isOrbital(Planet planet) {
        return planet == Planet.JUPITER || planet == Planet.SATURN || planet == Planet.URANUS || planet == Planet.NEPTUNE;
    }

    public static boolean naturalGround(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(LANDING_SURFACE) && state.getFluidState().isEmpty() && state.isCollisionShapeFullBlock(world, pos);
    }

    static LandingPlanner.World landingWorld(ServerLevel target) {
        return new LandingPlanner.World() {
            @Override public int surface(int x, int z) {
                return target.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
            }
            @Override public int minY() { return target.getMinY(); }
            @Override public int maxY() { return target.getMaxY(); }
            @Override public boolean air(BlockPos pos) {
                var state = target.getBlockState(pos);
                return state.getFluidState().isEmpty() && !state.is(BlockTags.FIRE)
                        && state.getCollisionShape(target, pos).isEmpty();
            }
            @Override public boolean pad(BlockPos pos) { return target.getBlockState(pos).is(ModBlocks.LANDING_PAD); }
            @Override public boolean ground(BlockPos pos) {
                var state = target.getBlockState(pos);
                return naturalGround(state, target, pos) && target.canSeeSky(pos.above());
            }
            @Override public boolean available(BlockPos pos) {
                var box = new AABB(pos.getX() - 2, pos.getY() + 1, pos.getZ() - 2,
                        pos.getX() + 3, Math.min(pos.getY() + 54, maxY()), pos.getZ() + 3);
                return target.getWorldBorder().isWithinBounds(box) && target.getEntities(null, box).isEmpty();
            }
        };
    }

    public static boolean transfer(RocketEntity rocket, LandingSearch search) {
        if (!(rocket.level() instanceof ServerLevel source) || !rocket.isVehicle() || search == null || !search.done()) return false;
        var destination = TravelDestination.at(rocket.destinationIndex());
        if (rocket.hull() <= 0 || !rocket.permits(destination)) return false;
        var target = source.getServer().getLevel(destination.world());
        var floor = search.floor();
        if (target == null || target == source || floor == null) return false;
        var world = search.world();
        if (!rocket.variant().protects(destination.planet(), floor.getY() + 1)
                || !LandingPlanner.suitable(world, floor, search.orbital())) return false;
        if (search.orbital()) {
            // Gas giants have no rocky surface. Only these destinations retain an orbital pad.
            for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++)
                target.setBlockAndUpdate(floor.offset(dx, 0, dz), ModBlocks.LANDING_PAD.defaultBlockState());
        }
        int approach = LandingPlanner.approachHeight(world, floor, RocketHullGeometry.height(rocket.variant()));
        rocket.getPassengers().forEach(RocketCabin::prepareTransfer);
        // Native passenger transfer preserves the vehicle, inventory and pilot without manual duplicates.
        var arrived = rocket.teleport(new TeleportTransition(target,
                Vec3.atBottomCenterOf(floor.above(1 + approach)), Vec3.ZERO, rocket.getYRot(), 0,
                TeleportTransition.PLACE_PORTAL_TICKET));
        if (arrived instanceof RocketEntity transferred) { transferred.beginDescent(floor.getY() + 1); return true; }
        return false;
    }
}

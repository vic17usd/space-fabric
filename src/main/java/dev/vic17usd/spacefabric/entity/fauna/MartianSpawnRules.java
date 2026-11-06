package dev.vic17usd.spacefabric.entity.fauna;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import dev.vic17usd.spacefabric.worldgen.PlanetBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;

public final class MartianSpawnRules {
    public static final TagKey<Block> SURFACE = TagKey.create(Registries.BLOCK, SpaceFabric.id("mars_surface_spawnable"));
    private MartianSpawnRules() { }
    public static boolean surface(ServerLevelAccessor level, BlockPos pos) {
        return accepts(level.getLevel().dimension(), level.getBiome(pos).is(PlanetBiomes.key(Planet.MARS)),
                level.getBlockState(pos.below()), level.getFluidState(pos).isEmpty() && level.getFluidState(pos.above()).isEmpty(),
                level.canSeeSky(pos), pos.getY(), level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()));
    }
    public static boolean accepts(ResourceKey<Level> dimension, boolean martianBiome, BlockState ground,
            boolean dry, boolean sky, int feetY, int surfaceY) {
        return dimension.equals(PlanetDimensions.key(Planet.MARS)) && martianBiome && ground.is(SURFACE)
                && dry && sky && feetY == surfaceY;
    }
    public static boolean peaceful(EntityType<? extends Mob> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return surface(level, pos) && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }
    public static boolean hostile(EntityType<? extends Mob> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL && surface(level, pos)
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }
}

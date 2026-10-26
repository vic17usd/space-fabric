package dev.vic17usd.spacefabric.environment;

import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.planet.PlanetDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;

public final class MeteorSystem {
    private MeteorSystem() { }

    public static void initialize() {
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            if (level.getGameTime() % 200 != 0) return;
            if (PlanetDimensions.find(level).filter(PlanetEnvironment::airless).isEmpty()) return;
            if (level.getRandom().nextInt(24) != 0) return;
            if (!level.getGameRules().get(GameRules.MOB_GRIEFING)) return;
            var players = level.players().stream().filter(player -> player.isAlive() && !player.isCreative() && !player.isSpectator()).toList();
            if (players.isEmpty()) return;
            var player = players.get(level.getRandom().nextInt(players.size()));
            int x = player.blockPosition().getX() + level.getRandom().nextInt(65) - 32;
            int z = player.blockPosition().getZ() + level.getRandom().nextInt(65) - 32;
            spawn(level, x, z);
        });
    }

    public static boolean spawn(ServerLevel level, int x, int z) {
        if (PlanetDimensions.find(level).filter(PlanetEnvironment::airless).isEmpty()
                || !level.getGameRules().get(GameRules.MOB_GRIEFING)) return false;
        var chunk = level.getChunkSource().getChunkNow(x >> 4, z >> 4);
        if (chunk == null) return false; // No genera ni fuerza chunks para un evento meteorológico.
        int surface = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15) + 1;
        var pos = new BlockPos(x, Math.min(surface + 36, level.getMaxY() - 8), z);
        if (pos.getY() <= surface + 8 || !level.isEmptyBlock(pos)) return false;
        var meteor = FallingBlockEntity.fall(level, pos, ModBlocks.JOVIAN_METEORITE.defaultBlockState());
        meteor.dropItem = false;
        meteor.setHurtsEntities(1.5F, 8);
        meteor.addTag("space_fabric.meteor");
        return true;
    }
}

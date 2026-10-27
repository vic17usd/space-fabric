package dev.vic17usd.spacefabric.entity.venus;

import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.worldgen.PlanetBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

/** Combined natural-spawn limits, not merely a per-species group-size limit. */
public final class VenusSpawnRules {
    public static final int PASSIVE_CHUNK_LIMIT=2,HOSTILE_CHUNK_LIMIT=4,DAY_HOSTILE_ATTEMPTS=24;
    private VenusSpawnRules() { }
    public static boolean night(long clock){long phase=Math.floorMod(clock,24000);return phase>=13000&&phase<23000;}
    public static boolean admits(boolean hostile,long clock,int currentCount,int dayRoll) {
        if(currentCount>=(hostile?HOSTILE_CHUNK_LIMIT:PASSIVE_CHUNK_LIMIT))return false;
        return !hostile||night(clock)||dayRoll==0;
    }
    public static boolean inChunk(double x,double z,int chunkX,int chunkZ){return Math.floor(x/16)==chunkX&&Math.floor(z/16)==chunkZ;}
    public static boolean surface(EntityType<? extends Mob> type,ServerLevelAccessor level,EntitySpawnReason reason,BlockPos pos,RandomSource random) {
        if(reason!=EntitySpawnReason.NATURAL&&reason!=EntitySpawnReason.CHUNK_GENERATION)return Mob.checkMobSpawnRules(type,level,reason,pos,random);
        if(reason==EntitySpawnReason.CHUNK_GENERATION)return false; // Natural runtime spawns enforce the shared live chunk count.
        boolean hostile=type.getCategory()==MobCategory.MONSTER;
        if(!level.getLevel().dimension().equals(PlanetDimensions.key(Planet.VENUS))||!level.getBiome(pos).is(PlanetBiomes.key(Planet.VENUS))
            ||!level.canSeeSky(pos)||level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,pos.getX(),pos.getZ())!=pos.getY()
            ||!level.getFluidState(pos).isEmpty()||!Mob.checkMobSpawnRules(type,level,reason,pos,random)
            ||hostile&&level.getDifficulty()==net.minecraft.world.Difficulty.PEACEFUL)return false;
        int cx=pos.getX()>>4,cz=pos.getZ()>>4;
        var box=new AABB(cx*16,level.getMinY(),cz*16,cx*16+16,level.getMaxY(),cz*16+16);
        int current=level.getLevel().getEntitiesOfClass(VenusMob.class,box,m->m.isAlive()&&m.getType().getCategory()==type.getCategory()&&inChunk(m.getX(),m.getZ(),cx,cz)).size();
        if(!admits(hostile,level.getLevel().getDefaultClockTime(),current,random.nextInt(DAY_HOSTILE_ATTEMPTS)))return false;
        // Large fliers are solitary locally, rather than filling the monster cap with mini-bosses.
        if(type==ModVenusEntities.SULFURWING_COPPERSTINGER||type==ModVenusEntities.SULFURWING_COPPER_HARBINGER)
            return level.getLevel().getEntitiesOfClass(VenusMob.class,box.inflate(32),m->m.isAlive()&&m.getType()==type).isEmpty();
        return true;
    }
}

package dev.vic17usd.spacefabric.entity.venus;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.*;
/** Habitantes resistentes al ambiente venusiano; conservan el modificador global de gravedad. */
public abstract class VenusMob extends PathfinderMob {
 protected VenusMob(EntityType<? extends PathfinderMob> type,Level level){super(type,level);}
 @Override public boolean canBreatheUnderwater(){return true;}
 @Override public boolean isInvulnerableTo(ServerLevel level,DamageSource source){
 return source.is(DamageTypes.HOT_FLOOR)||source.is(DamageTypes.IN_FIRE)||source.is(DamageTypes.LAVA)
 ||source.is(key("hypoxia"))
 ||source.is(key("decompression"))
 ||source.is(key("crushing_pressure"))||super.isInvulnerableTo(level,source);}
 private static net.minecraft.resources.ResourceKey<DamageType> key(String name){return net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,dev.vic17usd.spacefabric.SpaceFabric.id(name));}
 @Override public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect){return effect.getEffect()!=net.minecraft.world.effect.MobEffects.POISON && super.canBeAffected(effect);}
 @Override public void tick(){if(level().getDifficulty()==net.minecraft.world.Difficulty.PEACEFUL)setTarget(null);super.tick();}
 @Override public boolean canAttack(LivingEntity target){return level().getDifficulty()!=net.minecraft.world.Difficulty.PEACEFUL && !(target instanceof VenusMob)&&super.canAttack(target);}
}

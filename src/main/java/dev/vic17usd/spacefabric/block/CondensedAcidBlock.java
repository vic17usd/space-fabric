package dev.vic17usd.spacefabric.block;

import com.mojang.serialization.MapCodec;
import dev.vic17usd.spacefabric.equipment.SuitProtection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Depósito de ácido como recurso sólido de juego, con contacto corrosivo independiente de la lluvia. */
public final class CondensedAcidBlock extends Block {
    public static final MapCodec<CondensedAcidBlock> CODEC = simpleCodec(CondensedAcidBlock::new);
    public CondensedAcidBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<CondensedAcidBlock> codec() { return CODEC; }

    @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (!(level instanceof ServerLevel) || !(entity instanceof LivingEntity living) || living.tickCount % 20 != 0) return;
        if (!living.isAlive()) return;
        if (living instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) return;
            var boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (boots.isDamageableItem()) boots.hurtAndBreak(1, player, EquipmentSlot.FEET);
            if (SuitProtection.isSealed(player)) return;
        }
        living.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
    }
}

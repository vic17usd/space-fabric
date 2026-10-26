package dev.vic17usd.spacefabric.block;

import com.mojang.serialization.MapCodec;
import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class HotVolcanicRockBlock extends Block {
    public static final MapCodec<HotVolcanicRockBlock> CODEC = simpleCodec(HotVolcanicRockBlock::new);
    public static final TagKey<Item> THERMAL_BOOTS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(SpaceFabric.MOD_ID, "thermal_boots"));

    public HotVolcanicRockBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<HotVolcanicRockBlock> codec() { return CODEC; }

    public static boolean protectsFromHeat(ItemStack boots) { return boots.is(THERMAL_BOOTS); }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (!(level instanceof ServerLevel server) || !(entity instanceof LivingEntity living)) return;
        if (living instanceof Player player && (player.isCreative() || player.isSpectator())) return;
        if (!living.isAlive() || protectsFromHeat(living.getItemBySlot(EquipmentSlot.FEET))) return;
        // Respeta la inmunidad al fuego y los invulnerability frames de Minecraft.
        if (living.tickCount % 10 == 0) {
            living.hurtServer(server, server.damageSources().hotFloor(), 1.0F);
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
        }
    }
}

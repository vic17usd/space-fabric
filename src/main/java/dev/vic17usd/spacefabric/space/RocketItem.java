package dev.vic17usd.spacefabric.space;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import dev.vic17usd.spacefabric.item.ModItems;
import net.minecraft.world.item.context.UseOnContext;

public final class RocketItem extends Item {
    public RocketItem(Properties properties) { super(properties.stacksTo(1)); }

    /** Conserva incluso fracciones de combustible al recoger el vehículo, sin crear envases nuevos. */
    public static ItemStack packed(int fuel) {
        var stack = new ItemStack(ModItems.ROCKET);
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt("RocketFuel", Math.clamp(fuel, 0, RocketRules.TANK_CAPACITY)));
        return stack;
    }
    public static int storedFuel(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getIntOr("RocketFuel", 0),
                0, RocketRules.TANK_CAPACITY);
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() != Direction.UP || context.getPlayer() == null) return InteractionResult.FAIL;
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.SUCCESS;
        var player = context.getPlayer();
        var pos = context.getClickedPos().above();
        if (!player.mayUseItemAt(pos, Direction.UP, context.getItemInHand())) return InteractionResult.FAIL;
        var rocket = new RocketEntity(ModSpaceEntities.ROCKET, level);
        rocket.restorePackedFuel(storedFuel(context.getItemInHand()));
        rocket.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        rocket.setYRot(player.getYRot());
        if (!level.getWorldBorder().isWithinBounds(rocket.getBoundingBox()) || !level.noCollision(rocket, rocket.getBoundingBox())
                || !level.getEntities(rocket, rocket.getBoundingBox()).isEmpty()) return InteractionResult.FAIL;
        if (!level.addFreshEntity(rocket)) return InteractionResult.FAIL;
        if (!player.isCreative()) context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS_SERVER;
    }
}

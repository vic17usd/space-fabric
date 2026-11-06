package dev.vic17usd.spacefabric.equipment;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** Right-click equips the environmental slot without replacing combat armor. */
public final class SuitItem extends Item {
    public SuitItem(Properties properties){super(properties);}
    @Override public InteractionResult use(Level level,Player player,InteractionHand hand) {
        var held=player.getItemInHand(hand);
        int slot=SuitProtection.slot(held);
        if(slot<0)return InteractionResult.PASS;
        if(level.isClientSide())return InteractionResult.SUCCESS;
        return SuitEquipment.tryEquip(player,slot,held)?InteractionResult.SUCCESS_SERVER:InteractionResult.FAIL;
    }
}

package dev.vic17usd.spacefabric.research;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.Level;

public final class ResearchModuleItem extends Item {
    private final int kind;
    public ResearchModuleItem(Properties p,int kind){super(p);this.kind=kind;}
    public static boolean deep(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBooleanOr("DeepSurvey",false);}
    public static boolean installLens(ItemStack target){
        if(!target.is(dev.vic17usd.spacefabric.mining.MiningContent.SCANNER)||deep(target))return false;
        CustomData.update(DataComponents.CUSTOM_DATA,target,t->t.putBoolean("DeepSurvey",true));return true;
    }
    @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
        if(level.isClientSide())return InteractionResult.SUCCESS;
        if(kind==2&&hand==InteractionHand.MAIN_HAND&&installLens(player.getOffhandItem())){player.getMainHandItem().consume(1,player);player.getInventory().setChanged();player.sendSystemMessage(ResearchSystem.text("lens_installed"));return InteractionResult.SUCCESS_SERVER;}
        player.sendSystemMessage(ResearchSystem.text(kind==2?"lens_hint":"rover_hint"));return InteractionResult.FAIL;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,TooltipDisplay display,java.util.function.Consumer<net.minecraft.network.chat.Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,display,lines,flag);lines.accept(ResearchSystem.text(kind==2?"lens_hint":"rover_hint"));}
}

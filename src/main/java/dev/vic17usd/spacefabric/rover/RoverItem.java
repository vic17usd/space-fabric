package dev.vic17usd.spacefabric.rover;

import dev.vic17usd.spacefabric.recovery.RecoveryContent;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.context.UseOnContext;

public final class RoverItem extends Item {
    public RoverItem(Properties properties){super(properties);}
    public static ItemStack packed(RoverSystems.State state) {
        return packed(state,false);
    }
    public static ItemStack packed(RoverSystems.State state,boolean aerial){
        var stack=new ItemStack(aerial?dev.vic17usd.spacefabric.colony.ExpansionContent.AEROSTAT:RecoveryContent.ROVER);
        CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.put("RoverSystems",RoverSystems.State.CODEC.encodeStart(NbtOps.INSTANCE,state).getOrThrow()));return stack;
    }
    public static RoverSystems.State stored(ItemStack stack){return RoverSystems.State.CODEC.parse(NbtOps.INSTANCE,stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getCompoundOrEmpty("RoverSystems")).result().orElse(RoverSystems.State.EMPTY);}
    public static RoverEntity create(net.minecraft.world.level.Level level,ItemStack stack){if(!stack.is(RecoveryContent.ROVER)&&!stack.is(dev.vic17usd.spacefabric.colony.ExpansionContent.AEROSTAT))throw new IllegalArgumentException("Expected the rover item");var rover=new RoverEntity(stack.is(dev.vic17usd.spacefabric.colony.ExpansionContent.AEROSTAT)?RoverEntity.AEROSTAT_TYPE:RoverEntity.TYPE,level);rover.restoreSystems(stored(stack));return rover;}
    @Override public InteractionResult useOn(UseOnContext context) {
        if(context.getClickedFace()!=Direction.UP||context.getPlayer()==null)return InteractionResult.FAIL;
        if(!(context.getLevel() instanceof ServerLevel level))return InteractionResult.SUCCESS;
        var pos=context.getClickedPos().above();var player=context.getPlayer();
        if(!player.mayUseItemAt(pos,Direction.UP,context.getItemInHand()))return InteractionResult.FAIL;
        var rover=create(level,context.getItemInHand());rover.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);rover.setYRot(player.getYRot());
        if(!level.getWorldBorder().isWithinBounds(rover.getBoundingBox())||!level.noCollision(rover,rover.getBoundingBox())||!level.getEntities(rover,rover.getBoundingBox()).isEmpty())return InteractionResult.FAIL;
        if(!level.addFreshEntity(rover))return InteractionResult.FAIL;context.getItemInHand().consume(1,player);return InteractionResult.SUCCESS_SERVER;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,TooltipDisplay display,java.util.function.Consumer<net.minecraft.network.chat.Component> lines,TooltipFlag flag) {
        super.appendHoverText(stack,context,display,lines,flag);var state=stored(stack);
        lines.accept(net.minecraft.network.chat.Component.translatable("rover.space_fabric.telemetry",state.energy(),state.oxygen(),state.coolant(),state.hull()));
        lines.accept(net.minecraft.network.chat.Component.translatable(stack.is(dev.vic17usd.spacefabric.colony.ExpansionContent.AEROSTAT)?"expansion.space_fabric.aerial_controls":"rover.space_fabric.controls"));
        lines.accept(net.minecraft.network.chat.Component.translatable("rover.space_fabric.service"));if(state.optimized())lines.accept(dev.vic17usd.spacefabric.research.ResearchSystem.text("rover_installed"));
    }
}

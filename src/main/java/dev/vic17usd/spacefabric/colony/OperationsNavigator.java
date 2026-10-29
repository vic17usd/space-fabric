package dev.vic17usd.spacefabric.colony;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** A native readable-book UI with bounded, player-discovered coordinates. No locate or chunk requests. */
public final class OperationsNavigator extends WrittenBookItem {
    public record Point(String dimension,BlockPos pos,String label) {
        public Point {pos=pos.immutable();label=label.substring(0,Math.min(80,label.length()));}
        public static final Codec<Point> CODEC=RecordCodecBuilder.create(i->i.group(Codec.string(1,256).fieldOf("dimension").forGetter(Point::dimension),BlockPos.CODEC.fieldOf("pos").forGetter(Point::pos),Codec.string(1,80).fieldOf("label").forGetter(Point::label)).apply(i,Point::new));
    }
    public static final Codec<List<Point>> POINTS=Point.CODEC.listOf(0,32);
    public OperationsNavigator(Properties p){super(p.component(DataComponents.WRITTEN_BOOK_CONTENT,WrittenBookContent.EMPTY));}
    public static List<Point> points(ItemStack stack){return POINTS.parse(NbtOps.INSTANCE,stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getListOrEmpty("Waypoints")).result().orElse(List.of());}
    public static void remember(ItemStack stack,Point point){
        var list=new ArrayList<>(points(stack));list.removeIf(p->p.dimension.equals(point.dimension)&&p.pos.equals(point.pos));list.addFirst(point);while(list.size()>32)list.removeLast();
        CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->{tag.put("Waypoints",POINTS.encodeStart(NbtOps.INSTANCE,List.copyOf(list)).getOrThrow());tag.putInt("SelectedWaypoint",0);});
    }
    public static Optional<Point> selected(ItemStack stack){var list=points(stack);if(list.isEmpty())return Optional.empty();int index=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getIntOr("SelectedWaypoint",0);return Optional.of(list.get(Math.clamp(index,0,list.size()-1)));}
    public static void cycle(ItemStack stack){var list=points(stack);if(list.isEmpty())return;CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.putInt("SelectedWaypoint",Math.floorMod((long)t.getIntOr("SelectedWaypoint",0)+1,list.size())));}
    public static void discover(ItemStack stack,Point point){var list=new ArrayList<>(points(stack));if(list.size()>=32||list.stream().anyMatch(p->p.dimension.equals(point.dimension)&&p.pos.equals(point.pos)))return;list.add(point);CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.put("Waypoints",POINTS.encodeStart(NbtOps.INSTANCE,list).getOrThrow()));}
    public static Component direction(Point point,String dimension,BlockPos from){
        if(!point.dimension.equals(dimension))return Component.translatable("operations.space_fabric.other_dimension",point.label,point.dimension);
        return Component.translatable("operations.space_fabric.target",point.label,point.pos.toShortString(),(int)Math.sqrt(point.pos.distSqr(from)),bearing(from,point.pos));
    }
    public static String bearing(BlockPos from,BlockPos to){int sector=Math.floorMod((int)Math.round(Math.toDegrees(Math.atan2((double)to.getX()-from.getX(),(double)from.getZ()-to.getZ()))/45),8);return new String[]{"N","NE","E","SE","S","SO","O","NO"}[sector];}
    public static Component hud(Point point,String dimension,BlockPos from){return point.dimension.equals(dimension)?Component.translatable("operations.space_fabric.nav_hud",point.label,bearing(from,point.pos),(int)Math.sqrt(point.pos.distSqr(from))):Component.translatable("operations.space_fabric.other_dimension",point.label,point.dimension);}
    private static void book(ItemStack stack,String title,List<Component> pages){
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT,new WrittenBookContent(Filterable.passThrough(title),"Space Fabric",0,pages.stream().map(Filterable::passThrough).toList(),true));
    }
    @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(player instanceof ServerPlayer server){
            if(player.isSecondaryUseActive())cycle(stack);
            var pages=new ArrayList<Component>();pages.add(Component.translatable("operations.space_fabric.guide"));
            for(var p:points(stack))pages.add(direction(p,level.dimension().identifier().toString(),player.blockPosition()));
            for(String key:new String[]{"project_blackbox","project_sample","project_delivery"})pages.add(Component.translatable("operations.space_fabric."+key));book(stack,"Navegador",pages);server.containerMenu.broadcastChanges();
        }
        return super.use(level,player,hand);
    }
    @Override public InteractionResult useOn(UseOnContext context){
        if(!(context.getPlayer() instanceof ServerPlayer player))return InteractionResult.SUCCESS;
        var pos=context.getClickedPos();var stack=context.getItemInHand();var level=player.level();
        if(level.getBlockEntity(pos) instanceof ColonyMachineBlockEntity machine&&machine.kind()==ColonyContent.Machine.HABITAT&&!player.isSecondaryUseActive()){
            book(stack,"Colonia",OperationsSystem.dashboard(level,machine));player.containerMenu.broadcastChanges();player.openItemGui(stack,context.getHand());return InteractionResult.SUCCESS_SERVER;
        }
        if(player.isSecondaryUseActive()){
            remember(stack,new Point(level.dimension().identifier().toString(),pos,level.getBlockState(pos).getBlock().getName().getString()));
            OperationsSystem.project(player,pos);player.sendSystemMessage(Component.translatable("operations.space_fabric.saved"));return InteractionResult.SUCCESS_SERVER;
        }
        return use(level,player,context.getHand());
    }
}

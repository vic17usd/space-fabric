package dev.vic17usd.spacefabric.sphere;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public final class SphereDemo {
    public static final ResourceKey<Level> WORLD=ResourceKey.create(Registries.DIMENSION,SpaceFabric.id("sphere_demo"));
    public static final EntityType<SphereWalkerEntity> WALKER=Registry.register(BuiltInRegistries.ENTITY_TYPE,SpaceFabric.id("sphere_walker"),EntityType.Builder.of(SphereWalkerEntity::new,MobCategory.MISC).sized(.1F,.1F).clientTrackingRange(10).updateInterval(1).noSave().fireImmune().build(ResourceKey.create(Registries.ENTITY_TYPE,SpaceFabric.id("sphere_walker"))));
    public record ReturnPoint(String dimension,double x,double y,double z,float yaw,float pitch){
        public static final Codec<ReturnPoint> CODEC=RecordCodecBuilder.create(i->i.group(Codec.STRING.fieldOf("dimension").forGetter(ReturnPoint::dimension),Codec.DOUBLE.fieldOf("x").forGetter(ReturnPoint::x),Codec.DOUBLE.fieldOf("y").forGetter(ReturnPoint::y),Codec.DOUBLE.fieldOf("z").forGetter(ReturnPoint::z),Codec.FLOAT.fieldOf("yaw").forGetter(ReturnPoint::yaw),Codec.FLOAT.fieldOf("pitch").forGetter(ReturnPoint::pitch)).apply(i,ReturnPoint::new));
    }
    private static final AttachmentType<ReturnPoint> RETURN=AttachmentRegistry.<ReturnPoint>builder().persistent(ReturnPoint.CODEC).copyOnDeath().buildAndRegister(SpaceFabric.id("sphere_demo_return"));
    private SphereDemo() { }
    public static boolean active(Level level){return level.dimension().equals(WORLD);}
    private static void mount(ServerPlayer p){
        var walker=new SphereWalkerEntity(WALKER,p.level());walker.start(p.getYRot());
        p.level().addFreshEntity(walker);p.setPos(walker.position());p.noPhysics=true;p.resetFallDistance();p.startRiding(walker,true,true);
    }
    public static int enter(ServerPlayer p){
        if(active(p.level()))return 1;
        if(p.isPassenger()){p.sendSystemMessage(Component.literal("Baja del vehículo antes de entrar a la prueba."));return 0;}
        var world=p.level().getServer().getLevel(WORLD);if(world==null)return 0;
        ((AttachmentTarget)p).setAttached(RETURN,new ReturnPoint(p.level().dimension().identifier().toString(),p.getX(),p.getY(),p.getZ(),p.getYRot(),p.getXRot()));
        // Complete the tiny planet before the tester arrives; no generation occurs outside these chunks.
        for(int x=-2;x<=1;x++)for(int z=-2;z<=1;z++)world.getChunk(x,z);
        p.teleport(new TeleportTransition(world,new Vec3(.5,123,.5),Vec3.ZERO,0,0,TeleportTransition.DO_NOTHING));mount(p);
        p.sendSystemMessage(Component.literal("Planeta esférico experimental: WASD caminar · Espacio saltar · Ctrl correr · Mayús o /planetaprueba salir para volver. Primera persona; construcción y líquidos no adaptados."));return 1;
    }
    public static int leave(ServerPlayer p){
        if(!active(p.level()))return 0;
        var back=((AttachmentTarget)p).getAttached(RETURN);if(back==null){p.sendSystemMessage(Component.literal("Sin punto de regreso. Usa /execute in minecraft:overworld run tp @s 0 100 0."));return 0;}
        var world=p.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION,Identifier.parse(back.dimension())));if(world==null)return 0;
        var vehicle=p.getVehicle();p.stopRiding();if(vehicle instanceof SphereWalkerEntity)vehicle.discard();p.noPhysics=p.isSpectator();p.resetFallDistance();
        p.teleport(new TeleportTransition(world,new Vec3(back.x(),back.y(),back.z()),Vec3.ZERO,back.yaw(),back.pitch(),TeleportTransition.DO_NOTHING));((AttachmentTarget)p).removeAttached(RETURN);return 1;
    }
    public static void initialize(){
        Registry.register(BuiltInRegistries.FEATURE,SpaceFabric.id("sphere_demo"),new SphereFeature());
        CommandRegistrationCallback.EVENT.register((dispatcher,context,selection)->dispatcher.register(Commands.literal("planetaprueba")
            .then(Commands.literal("entrar").executes(c->enter(c.getSource().getPlayerOrException())))
            .then(Commands.literal("salir").executes(c->leave(c.getSource().getPlayerOrException())))));
        ServerTickEvents.END_SERVER_TICK.register(server->{
            for(var p:server.getPlayerList().getPlayers())if(active(p.level())&&p.isAlive()&&!p.isSpectator()){
                if(p.getLastClientInput().shift()){leave(p);continue;}
                if(!(p.getVehicle() instanceof SphereWalkerEntity))mount(p);
                p.noPhysics=true;p.resetFallDistance();
            }
        });
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity,source,amount)->!(entity instanceof ServerPlayer&&active(entity.level())));
        PlayerBlockBreakEvents.BEFORE.register((level,player,pos,state,blockEntity)->!active(level));
        UseItemCallback.EVENT.register((player,level,hand)->active(level)?InteractionResult.FAIL:InteractionResult.PASS);
        UseBlockCallback.EVENT.register((player,level,hand,hit)->active(level)?InteractionResult.FAIL:InteractionResult.PASS);
    }
}

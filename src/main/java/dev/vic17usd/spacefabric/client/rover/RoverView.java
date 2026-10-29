package dev.vic17usd.spacefabric.client.rover;

import dev.vic17usd.spacefabric.rover.*;
import java.util.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;

public final class RoverView {
    private record Motion(double x,double z,float previousRoll,float roll) { }
    private static final Map<RoverEntity,Motion> MOTION=new WeakHashMap<>();
    private static RoverEntity previous;
    private static float yaw;
    private RoverView() { }
    public static float wheelRoll(RoverEntity rover,float delta) {
        var motion=MOTION.get(rover);
        if(motion==null)return 0;
        return motion.previousRoll+(motion.roll-motion.previousRoll)*delta;
    }
    public static void initialize() {
        var key=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.space_fabric.rover_inventory",org.lwjgl.glfw.GLFW.GLFW_KEY_V,KeyMapping.Category.INVENTORY));
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            if(client.level==null){MOTION.clear();previous=null;return;}
            for(var entity:client.level.entitiesForRendering())if(entity instanceof RoverEntity rover) {
                var old=MOTION.get(rover);float roll=old==null?0:old.roll;
                if(old!=null) {
                    double dx=rover.getX()-old.x,dz=rover.getZ()-old.z;
                    double heading=Math.toRadians(rover.getYRot());double distance=dx*(-Math.sin(heading))+dz*Math.cos(heading);
                    if(Math.abs(distance)<2)roll+=(float)Math.toDegrees(distance/.525);
                }
                MOTION.put(rover,new Motion(rover.getX(),rover.getZ(),old==null?roll:old.roll,roll));
            }
            if(client.player!=null&&client.player.getVehicle() instanceof RoverEntity rover) {
                float change=previous==rover?net.minecraft.util.Mth.wrapDegrees(rover.getYRot()-yaw):net.minecraft.util.Mth.wrapDegrees(rover.getYRot()-client.player.getYRot());
                client.player.setYRot(client.player.getYRot()+change);client.player.yRotO+=change;yaw=rover.getYRot();previous=rover;
                while(key.consumeClick())if(client.screen==null&&ClientPlayNetworking.canSend(RoverNetwork.Open.TYPE))ClientPlayNetworking.send(new RoverNetwork.Open());
            } else {previous=null;while(key.consumeClick()){ }}
        });
    }
}

package dev.vic17usd.spacefabric.client.sphere;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.sphere.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.network.chat.Component;

public final class SphereDemoClient {
    private static CameraType previous;
    private SphereDemoClient() { }
    public static void initialize(){
        EntityRendererRegistry.register(SphereDemo.WALKER,NoopRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(c->{
            if(c.level!=null&&SphereDemo.active(c.level)){
                if(c.player!=null)c.player.noPhysics=true;
                if(previous==null)previous=c.options.getCameraType();
                c.options.setCameraType(CameraType.FIRST_PERSON);
            }else if(previous!=null){if(c.player!=null)c.player.noPhysics=c.player.isSpectator();c.options.setCameraType(previous);previous=null;}
        });
        HudElementRegistry.addLast(SpaceFabric.id("sphere_demo"),(gui,delta)->{
            var c=Minecraft.getInstance();if(c.level==null||!SphereDemo.active(c.level)||c.options.hideGui)return;
            gui.text(c.font,Component.literal("Planeta esférico · prueba de locomoción"),8,8,0xFFFFFFFF);
            gui.text(c.font,Component.literal("WASD · Espacio: salto · Mayús: regresar"),8,20,0xFFE7DE8E);
            gui.text(c.font,Component.literal("/planetaprueba salir · Construcción experimental desactivada"),8,32,0xFFB8D4DF);
        });
    }
}

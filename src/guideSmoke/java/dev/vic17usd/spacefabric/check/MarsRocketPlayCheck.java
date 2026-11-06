package dev.vic17usd.spacefabric.check;

import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.planet.*;
import dev.vic17usd.spacefabric.space.*;
import java.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

/** Opt-in fixture: flies a real rocket in a disposable copy of a test world. */
public final class MarsRocketPlayCheck {
    private static UUID owner, rocketId; private static boolean launched, obstructed, announced; private static long deadline;
    public static boolean enabled(){return Boolean.getBoolean("spacefabric.marsRocketCheck");}
    public static void register(){ServerTickEvents.END_SERVER_TICK.register(server->{
        if(owner==null)return; var p=server.getPlayerList().getPlayer(owner);if(p==null)return;
        if(server.overworld().getGameTime()>deadline){System.out.println("MARS_ROCKET_CHECK_FAIL: deadline");owner=null;return;}
        if(!launched)return;
        if(!obstructed && p.getVehicle() instanceof RocketEntity rocket && rocket.isCruising()) {
            try {
                var field=RocketEntity.class.getDeclaredField("landingSearch"); field.setAccessible(true);
                var search=(LandingSearch)field.get(rocket);
                if(search!=null && search.done() && search.floor()!=null) {
                    var mars=server.getLevel(PlanetDimensions.key(Planet.MARS));
                    mars.setBlockAndUpdate(search.floor().above(),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
                    obstructed=true;System.out.println("MARS_ROCKET_CHECK_OBSTRUCTED: "+search.floor());
                }
            } catch(ReflectiveOperationException ex) { throw new IllegalStateException(ex); }
        }
        if(PlanetDimensions.find(p.level()).orElse(null)==Planet.MARS){
            if(!announced){announced=true;System.out.println("MARS_ROCKET_CHECK_ARRIVED: player="+p.level().dimension().identifier()+" vehicle="+p.getVehicle());}
            if(p.getVehicle() instanceof RocketEntity rocket && !rocket.isFlying()) {
                System.out.println("MARS_ROCKET_CHECK_LANDED: "+rocket.blockPosition()+" obstructed="+obstructed); owner=null;
            }
        } else if(p.getVehicle() instanceof RocketEntity rocket && rocket.getUUID().equals(rocketId) && !rocket.isFlying()) {
            System.out.println("MARS_ROCKET_CHECK_FAIL: returned to "+p.level().dimension().identifier()); owner=null;
        }
    });}
    public static void start(MinecraftServer server,UUID id){
        ServerPlayer p=server.getPlayerList().getPlayer(id);var level=server.overworld();
        p.setGameMode(GameType.CREATIVE);double x=p.getX(),z=p.getZ();
        p.teleportTo(level,x,300,z,Set.of(),0,0,false);
        var item=(RocketItem)ModItems.MARS_ROCKET;
        var rocket=item.createRocket(level,RocketItem.packed(8000,RocketVariant.MARS,100));
        rocket.setPos(x,300,z); if(!level.addFreshEntity(rocket)||!p.startRiding(rocket))throw new AssertionError("Could not board fixture");
        owner=id;rocketId=rocket.getUUID();deadline=level.getGameTime()+4000;launched=rocket.requestLaunch(p);
        System.out.println("MARS_ROCKET_CHECK_LAUNCH: "+launched+" dest="+TravelDestination.at(rocket.destinationIndex()).id());
        if(!launched)owner=null;
    }
}

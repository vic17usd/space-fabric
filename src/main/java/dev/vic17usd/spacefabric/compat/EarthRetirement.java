package dev.vic17usd.spacefabric.compat;

import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

/** Read-time migration only. Old region files are never deleted or merged into the Overworld. */
public final class EarthRetirement {
    public static final ResourceKey<LevelStem> LEGACY_STEM=ResourceKey.create(Registries.LEVEL_STEM,SpaceFabric.id("earth"));
    private EarthRetirement(){}
    public static Map<ResourceKey<LevelStem>,LevelStem> dimensions(Map<ResourceKey<LevelStem>,LevelStem> saved){
        if(!saved.containsKey(LEGACY_STEM))return saved;
        var copy=new LinkedHashMap<>(saved);copy.remove(LEGACY_STEM);return Map.copyOf(copy);
    }
    private static ListTag vector(double x,double y,double z){
        var list=new ListTag();list.add(DoubleTag.valueOf(x));list.add(DoubleTag.valueOf(y));list.add(DoubleTag.valueOf(z));return list;
    }
    private static void relocateEntity(CompoundTag entity,double dx,double dy,double dz){
        var pos=entity.getListOrEmpty("Pos");
        if(pos.size()==3)entity.put("Pos",vector(pos.getDoubleOr(0,0)+dx,pos.getDoubleOr(1,0)+dy,pos.getDoubleOr(2,0)+dz));
        entity.put("Motion",vector(0,0,0));entity.putFloat("FallDistance",0);
        for(var child:entity.getListOrEmpty("Passengers"))if(child instanceof CompoundTag passenger)relocateEntity(passenger,dx,dy,dz);
    }
    public static CompoundTag player(CompoundTag saved,BlockPos spawn){
        if(!saved.getStringOr("Dimension","").equals("space_fabric:earth"))return saved;
        var copy=saved.copy();
        double x=spawn.getX()+.5,y=spawn.getY()+1,z=spawn.getZ()+.5;
        var vehicle=copy.getCompoundOrEmpty("RootVehicle").getCompoundOrEmpty("Entity");
        if(!vehicle.isEmpty()){
            var root=vehicle.getListOrEmpty("Pos");
            relocateEntity(vehicle,x-root.getDoubleOr(0,x),y-root.getDoubleOr(1,y),z-root.getDoubleOr(2,z));
            if(vehicle.getStringOr("id","").equals("space_fabric:rocket"))vehicle.putBoolean("RetiredEarthReturn",true);
        }
        copy.putString("Dimension","minecraft:overworld");copy.put("Pos",vector(x,y,z));copy.put("Motion",vector(0,0,0));copy.putFloat("FallDistance",0);
        return copy;
    }
}

package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.atmosphere.AtmosphereProfile;
import dev.vic17usd.spacefabric.planet.Planet;

/** Certificación ficticia de hardware sobre las condiciones ambientales del mod. IDs de red estables. */
public enum RocketVariant {
    BASE("base",Planet.MOON,200,-200,200,false,false,0,1,0xFFABB6BC),
    MERCURY("mercury",Planet.MERCURY,200,-220,600,false,false,2,1,0xFFC5CCD2),
    VENUS("venus",Planet.VENUS,20000,-180,700,true,true,1,3,0xFFD9AD35),
    EARTH("earth",Planet.EARTH,200,-100,150,false,false,0,1,0xFF448EE0),
    MOON("moon",Planet.MOON,500,-230,250,false,false,1,1,0xFFD6DBE1),
    MARS("mars",Planet.MARS,200,-160,150,false,true,1,2,0xFFE66B3C),
    JUPITER("jupiter",Planet.JUPITER,20000,-200,700,false,true,3,3,0xFFB76E3A),
    SATURN("saturn",Planet.SATURN,10000,-210,600,false,false,2,2,0xFFE2C478),
    URANUS("uranus",Planet.URANUS,10000,-240,300,false,false,1,3,0xFF62D7D6),
    NEPTUNE("neptune",Planet.NEPTUNE,15000,-250,550,false,true,2,4,0xFF4269CE);
    public final String path;
    public final Planet mission;
    public final double maxKpa,minCelsius,maxCelsius;
    public final boolean acidResistant,dustProtected;
    public final int radiationRating,windRating,color;
    RocketVariant(String path,Planet mission,double maxKpa,double minCelsius,double maxCelsius,
            boolean acidResistant,boolean dustProtected,int radiationRating,int windRating,int color) {
        this.path=path;this.mission=mission;this.maxKpa=maxKpa;this.minCelsius=minCelsius;this.maxCelsius=maxCelsius;
        this.acidResistant=acidResistant;this.dustProtected=dustProtected;this.radiationRating=radiationRating;
        this.windRating=windRating;this.color=color;
    }
    /** All current designs retain the original 3000-unit tank; presentation reads this same value. */
    public int fuelCapacity(){return RocketRules.TANK_CAPACITY;}
    public net.minecraft.network.chat.Component label(){
        return net.minecraft.network.chat.Component.translatable("item.space_fabric."+(this==BASE?"rocket":path+"_rocket"));
    }
    public enum ProtectionFailure { NONE, PRESSURE, TEMPERATURE, ACID, DUST, RADIATION, WIND }
    public ProtectionFailure protectionFailure(Planet planet,double y) {
        var profile=AtmosphereProfile.of(planet);
        if(profile.pressureAt(y)>maxKpa)return ProtectionFailure.PRESSURE;
        if(profile.minTemperatureAt(y)<minCelsius||profile.maxTemperatureAt(y)>maxCelsius)return ProtectionFailure.TEMPERATURE;
        return switch(planet) {
            case VENUS -> acidResistant?ProtectionFailure.NONE:ProtectionFailure.ACID;
            case MARS -> dustProtected?ProtectionFailure.NONE:ProtectionFailure.DUST;
            case MERCURY -> radiationRating>=2?ProtectionFailure.NONE:ProtectionFailure.RADIATION;
            case JUPITER -> radiationRating<3?ProtectionFailure.RADIATION:windRating<3?ProtectionFailure.WIND:ProtectionFailure.NONE;
            case SATURN,URANUS -> windRating>=2?ProtectionFailure.NONE:ProtectionFailure.WIND;
            case NEPTUNE -> windRating>=4?ProtectionFailure.NONE:ProtectionFailure.WIND;
            default -> ProtectionFailure.NONE;
        };
    }
    public boolean protects(Planet planet,double y) {return protectionFailure(planet,y)==ProtectionFailure.NONE;}
    public boolean permits(Planet planet,boolean exclusive) {
        return (!exclusive || planet==Planet.EARTH || planet==mission) && protects(planet,240);
    }
    public static RocketVariant byId(int id){return id>=0&&id<values().length?values()[id]:BASE;}
    public static RocketVariant byName(String name){for(var v:values())if(v.path.equals(name))return v;return BASE;}
}

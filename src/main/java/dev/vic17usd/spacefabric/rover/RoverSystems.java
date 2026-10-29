package dev.vic17usd.spacefabric.rover;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.atmosphere.AtmosphereProfile;
import dev.vic17usd.spacefabric.planet.Planet;

/** Game-unit battery, life support and active cooling; no offline catch-up. */
public final class RoverSystems {
    public static final int MAX_ENERGY=24000,MAX_OXYGEN=9600,MAX_COOLANT=9600;
    public enum Supply { POWER,OXYGEN,WATER,CHILLED,REPAIR }
    public record State(int energy,int oxygen,int coolant,int hull) {
        public static final State EMPTY=new State(0,0,0,100);
        public static final Codec<State> CODEC=RecordCodecBuilder.create(i->i.group(
                Codec.INT.optionalFieldOf("energy",0).forGetter(State::energy),Codec.INT.optionalFieldOf("oxygen",0).forGetter(State::oxygen),
                Codec.INT.optionalFieldOf("coolant",0).forGetter(State::coolant),Codec.INT.optionalFieldOf("hull",100).forGetter(State::hull)).apply(i,State::new));
        public State {energy=Math.clamp(energy,0,MAX_ENERGY);oxygen=Math.clamp(oxygen,0,MAX_OXYGEN);coolant=Math.clamp(coolant,0,MAX_COOLANT);hull=Math.clamp(hull,0,100);}
        public State supply(Supply kind) {
            return switch(kind) {
                case POWER -> energy<=MAX_ENERGY-2400?new State(energy+2400,oxygen,coolant,hull):this;
                case OXYGEN -> oxygen<=MAX_OXYGEN-4800?new State(energy,oxygen+4800,coolant,hull):this;
                case WATER -> coolant<=MAX_COOLANT-2400?new State(energy,oxygen,coolant+2400,hull):this;
                case CHILLED -> coolant<=MAX_COOLANT-4800?new State(energy,oxygen,coolant+4800,hull):this;
                case REPAIR -> hull<100?new State(energy,oxygen,coolant,hull+25):this;
            };
        }
        public State tick(boolean occupied,boolean moving,Planet planet,long tick) {
            if(!occupied)return this;
            boolean hostile=planet!=Planet.EARTH;
            int power=(moving?1:0)+(tick%20==0?1:0);
            return new State(energy-power,oxygen-(hostile?1:0),coolant-(planet==Planet.VENUS?1:0),hull);
        }
        public boolean protects(Planet planet,double y) {
            return hull>0&&energy>0&&rated(planet,y)&&(planet==Planet.EARTH||oxygen>0)&&(planet!=Planet.VENUS||coolant>0);
        }
    }
    public static boolean rated(Planet planet,double y) {
        var a=AtmosphereProfile.of(planet);
        return a.pressureAt(y)<=10000&&a.minTemperatureAt(y)>=-150&&a.maxTemperatureAt(y)<=550;
    }
    public static double speed(double current,int throttle,boolean powered,boolean brake) {
        if(!powered||brake)return current*.6;
        return throttle==0?current*.86:Math.clamp(current+throttle*.012,-.10,.24);
    }
    private RoverSystems() { }
}

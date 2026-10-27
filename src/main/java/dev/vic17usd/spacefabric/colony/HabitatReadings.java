package dev.vic17usd.spacefabric.colony;

/** Server readings. Pressure/temperature are regulated setpoints, never invented sensor history. */
public record HabitatReadings(Condition condition, int oxygen, int energy, int coolant, int volume, boolean venus) {
    public enum Condition { IDLE, PRESSURIZED, LEAK, TOO_LARGE, UNLOADED, WEAK_WALL, BLOCKED_VENT,
        UNSUPPORTED, NEEDS_OXYGEN, NEEDS_POWER, NEEDS_COOLANT }
    public enum Reserve { NONE, OXYGEN, ENERGY, COOLANT }
    public HabitatReadings {
        oxygen=Math.clamp(oxygen,0,LifeSupport.CAPACITY);
        energy=Math.clamp(energy,0,LifeSupport.CAPACITY);
        coolant=Math.clamp(coolant,0,LifeSupport.CAPACITY);
        volume=Math.clamp(volume,0,SealScanner.MAX_VOLUME);
    }
    public boolean regulated() { return condition==Condition.PRESSURIZED; }
    public int pressureDeciKpa() { return regulated()?1013:-1; }
    public int temperatureCelsius() { return regulated()?22:-1; }
    public Reserve lowReserve() {
        if(!regulated())return Reserve.NONE;
        int min=Math.min(oxygen,energy);if(venus)min=Math.min(min,coolant);
        if(min>LifeSupport.CAPACITY*15/100)return Reserve.NONE;
        return min==oxygen?Reserve.OXYGEN:min==energy?Reserve.ENERGY:Reserve.COOLANT;
    }
    public static double fraction(int reserve) { return Math.clamp((double)reserve/LifeSupport.CAPACITY,0,1); }
}

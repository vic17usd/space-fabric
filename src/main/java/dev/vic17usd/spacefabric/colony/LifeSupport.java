package dev.vic17usd.spacefabric.colony;

/** Resource budgets in game units, advanced once per second by the server. */
public final class LifeSupport {
    public static final int CAPACITY=9600,CELL=2400;
    private int oxygen,energy,coolant,volume;
    private boolean pressurized;
    public int oxygen(){return oxygen;} public int energy(){return energy;} public int coolant(){return coolant;}
    public boolean pressurized(){return pressurized;}
    public void restore(int oxygen,int energy,int coolant,int volume,boolean pressurized) {
        this.oxygen=Math.clamp(oxygen,0,CAPACITY);this.energy=Math.clamp(energy,0,CAPACITY);this.coolant=Math.clamp(coolant,0,CAPACITY);
        this.volume=Math.clamp(volume,0,SealScanner.MAX_VOLUME);this.pressurized=pressurized && this.volume>0;
    }
    public boolean fill(int kind) {
        int current=kind==0?oxygen:kind==1?energy:coolant;
        if(current>CAPACITY-CELL)return false;
        if(kind==0)oxygen+=CELL;else if(kind==1)energy+=CELL;else coolant+=CELL;
        return true;
    }
    public void vent(){oxygen=0;volume=0;pressurized=false;}
    public boolean tick(int newVolume,int occupants,boolean venus) {
        if(newVolume<=0 || newVolume>SealScanner.MAX_VOLUME){vent();return false;}
        if(volume!=newVolume){pressurized=false;volume=newVolume;}
        int startup=pressurized?0:2*volume;
        int breath=Math.max(0,occupants), power=venus?15:5, cooling=venus?5:0;
        if(oxygen<startup+breath || energy<power || coolant<cooling){pressurized=false;return false;}
        oxygen-=startup+breath;energy-=power;coolant-=cooling;pressurized=true;return true;
    }
}

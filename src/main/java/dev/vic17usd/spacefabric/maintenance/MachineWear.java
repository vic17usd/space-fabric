package dev.vic17usd.spacefabric.maintenance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Game-unit chemical wear. Only loaded server ticks advance it; no elapsed-time catch-up. */
public final class MachineWear {
    public static final int MAX_DAMAGE=10000,MAX_COATING=4800,COATING_CHARGE=2400,FILTER_CHARGE=1200,REPAIR=2500;
    public enum Risk { NONE(0),ATMOSPHERE(4),HAZE(8),SOURCE(16); public final int load;Risk(int load){this.load=load;} }
    public enum Stage { HEALTHY,SERVICE,DEGRADED,CRITICAL,FAILED }
    public enum Action { REPAIR,COAT,FILTER }
    public record State(int damage,int coating,int filter,boolean installed,int phase) {
        public static final State FRESH=new State(0,0,0,false,0);
        public static final Codec<State> CODEC=RecordCodecBuilder.create(i->i.group(
                Codec.INT.optionalFieldOf("damage",0).forGetter(State::damage),Codec.INT.optionalFieldOf("coating",0).forGetter(State::coating),
                Codec.INT.optionalFieldOf("filter",0).forGetter(State::filter),Codec.BOOL.optionalFieldOf("installed",false).forGetter(State::installed),
                Codec.INT.optionalFieldOf("phase",0).forGetter(State::phase)).apply(i,State::new));
        public State {
            damage=Math.clamp(damage,0,MAX_DAMAGE);coating=Math.clamp(coating,0,MAX_COATING);filter=Math.clamp(filter,0,FILTER_CHARGE);
            installed=installed||filter>0;phase=Math.clamp(phase,0,19);
        }
    }
    private State state=State.FRESH;
    private Risk risk=Risk.NONE;
    private long sampled=Long.MIN_VALUE;
    public State state(){return state;}
    public Risk risk(){return risk;}
    public void restore(State restored){state=restored;sampled=Long.MIN_VALUE;risk=Risk.NONE;}
    public boolean needsSample(long now){return sampled==Long.MIN_VALUE||now-sampled>=20;}
    public void sample(Risk risk,long now){this.risk=risk;sampled=now;}
    public int integrity(){return MAX_DAMAGE-state.damage;}
    public Stage stage(){return stage(state.damage);}
    public static Stage stage(int damage){return damage>=10000?Stage.FAILED:damage>=8000?Stage.CRITICAL:damage>=5000?Stage.DEGRADED:damage>=2500?Stage.SERVICE:Stage.HEALTHY;}
    public boolean failed(){return stage()==Stage.FAILED;}
    public boolean allowsWork(long tick){return !failed()&&(state.damage<5000||tick%2==0);}
    public boolean tick() {
        if(risk==Risk.NONE)return false;
        int phase=state.phase+1;
        if(phase<20){state=new State(state.damage,state.coating,state.filter,state.installed,phase);return true;}
        int load=risk.load,filter=state.filter;
        if(filter>0){filter=Math.max(0,filter-load/4);load/=4;}
        int absorbed=Math.min(load,state.coating);
        state=new State(state.damage+load-absorbed,state.coating-absorbed,filter,state.installed,0);return true;
    }
    public boolean apply(Action action) {
        int damage=state.damage,coating=state.coating,filter=state.filter;
        switch(action) {
            case REPAIR -> {if(damage==0)return false;damage=Math.max(0,damage-REPAIR);}
            case COAT -> {if(coating>MAX_COATING-COATING_CHARGE)return false;coating+=COATING_CHARGE;}
            case FILTER -> {if(filter>0)return false;filter=FILTER_CHARGE;}
        }
        state=new State(damage,coating,filter,state.installed||action==Action.FILTER,state.phase);return true;
    }
}

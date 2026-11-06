package dev.vic17usd.spacefabric.transport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** The identity stays locked when empty. Draining a tank cannot silently change a gas line. */
public final class TransportBuffer {
    public record State(TransportResource resource,int amount) {
        public static final State EMPTY=new State(TransportResource.NONE,0);
        public static final Codec<State> CODEC=RecordCodecBuilder.create(i->i.group(
                TransportResource.CODEC.optionalFieldOf("resource",TransportResource.NONE).forGetter(State::resource),
                Codec.INT.optionalFieldOf("amount",0).forGetter(State::amount)).apply(i,State::new));
        public State {amount=resource==TransportResource.NONE?0:Math.clamp(amount,0,24000);}
    }
    private final TransportChannel channel;
    private State state;
    public TransportBuffer(TransportChannel channel){this.channel=channel;state=channel==TransportChannel.ENERGY?new State(TransportResource.POWER,0):State.EMPTY;}
    public State state(){return state;}
    public TransportChannel channel(){return channel;}
    public int capacity(){return channel.capacity;}
    public void restore(State value){
        if(value.resource()==TransportResource.NONE||value.resource().channel!=channel)state=channel==TransportChannel.ENERGY?new State(TransportResource.POWER,0):State.EMPTY;
        else state=new State(value.resource(),Math.min(value.amount(),capacity()));
    }
    public boolean select(TransportResource resource) {
        if(channel==TransportChannel.ENERGY&&resource==TransportResource.NONE)resource=TransportResource.POWER;
        if(state.amount()!=0||resource!=TransportResource.NONE&&resource.channel!=channel)return false;
        state=new State(resource,0);return true;
    }
    public int free(TransportResource resource){return resource.channel==channel&&(state.resource()==TransportResource.NONE||state.resource()==resource)?capacity()-state.amount():0;}
    public int available(TransportResource resource){return state.resource()==resource?state.amount():0;}
    public void insert(TransportResource resource,int amount) {
        if(amount<=0||free(resource)<amount)throw new IllegalArgumentException("Tank insertion must fit completely");
        state=new State(resource,state.amount()+amount);
    }
    public void extract(TransportResource resource,int amount) {
        if(amount<=0||available(resource)<amount)throw new IllegalArgumentException("Tank extraction must fit completely");
        state=new State(resource,state.amount()-amount);
    }
}

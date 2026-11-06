package dev.vic17usd.spacefabric.transport;

public enum TransportChannel {
    LIQUID(16000),GAS(16000),ENERGY(24000);
    public final int capacity;
    TransportChannel(int capacity){this.capacity=capacity;}
    public String path(){return name().toLowerCase(java.util.Locale.ROOT);}
}

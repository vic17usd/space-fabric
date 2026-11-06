package dev.vic17usd.spacefabric.equipment;

/** Capacidades ficticias de equipo futurista, no especificaciones de trajes espaciales actuales. */
public enum SuitTier {
    NONE("none",250,-20,50,false,false),
    VACUUM("vacuum",200,-200,450,true,false),
    VENUS("venus",10000,-150,550,true,true),
    CRYO("cryo",1000,-230,100,true,false),
    ABYSSAL("abyssal",100000,-230,550,true,true);
    public final String path;
    public final double maxKpa,minCelsius,maxCelsius;
    public final boolean oxygen,acidResistant;
    SuitTier(String path,double maxKpa,double minCelsius,double maxCelsius,boolean oxygen,boolean acidResistant) {
        this.path=path;this.maxKpa=maxKpa;this.minCelsius=minCelsius;this.maxCelsius=maxCelsius;
        this.oxygen=oxygen;this.acidResistant=acidResistant;
    }
}

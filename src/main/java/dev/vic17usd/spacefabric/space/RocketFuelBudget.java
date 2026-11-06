package dev.vic17usd.spacefabric.space;

import dev.vic17usd.spacefabric.planet.Planet;

/** Ideal circular-orbit Hohmann transfers plus a deliberately simplified launch/landing budget.
 * Units are game propellant, not kilograms: 100 units/km/s, rounded up to 100.
 * Giants use a fictional orbital landing platform, never a real solid surface.
 */
public final class RocketFuelBudget {
    private static final double AU_KM=149597870.7,SUN_MU=1.32712440018e11;
    private RocketFuelBudget() { }
    public static double orbitAu(Planet p){return switch(p){
        case MERCURY->.3871;case VENUS->.7233;case EARTH,MOON->1;
        case MARS->1.5237;case JUPITER->5.2028;case SATURN->9.5388;
        case URANUS->19.1914;case NEPTUNE->30.0611;};}
    public static double escapeKms(Planet p){return switch(p){
        case MERCURY->4.25;case VENUS->10.36;case EARTH->11.186;case MOON->2.38;
        case MARS->5.027;case JUPITER->59.5;case SATURN->35.5;case URANUS->21.3;case NEPTUNE->23.5;};}
    /** Departure and arrival excess velocities outside the planet's sphere of influence. */
    public static double[] transferKms(Planet from,Planet to) {
        if(from==to)return new double[]{0,0};
        if((from==Planet.EARTH&&to==Planet.MOON)||(from==Planet.MOON&&to==Planet.EARTH))return new double[]{.85,.85};
        double r1=orbitAu(from)*AU_KM,r2=orbitAu(to)*AU_KM,a=(r1+r2)/2;
        return new double[]{Math.abs(Math.sqrt(SUN_MU*(2/r1-1/a))-Math.sqrt(SUN_MU/r1)),
            Math.abs(Math.sqrt(SUN_MU/r2)-Math.sqrt(SUN_MU*(2/r2-1/a)))};
    }
    private static double launchLoss(Planet p){return switch(p){
        case EARTH->2;case VENUS->4;case MARS->.3;
        case JUPITER,SATURN,URANUS,NEPTUNE->5;default->.1;};}
    public record Breakdown(double departureKms,double captureKms,double landingKms,int logistics,int total) { }
    public static Breakdown budget(TravelDestination from,TravelDestination to) {
        if(from.world().equals(to.world()))return new Breakdown(0,0,0,0,0);
        if(from.planet()==to.planet())return new Breakdown(0,0,0,200,200); // Earth dimension <-> Overworld.
        var excess=transferKms(from.planet(),to.planet());
        double escape=escapeKms(from.planet()),arrival=escapeKms(to.planet());
        double departure=Math.hypot(escape,excess[0])+launchLoss(from.planet());
        double capture=Math.hypot(arrival,excess[1])-arrival;
        double landing=switch(to.planet()){
            case EARTH,VENUS,MARS->.3;case MOON,MERCURY->arrival*.35;default->1.5;};
        int maxTier=Math.max(RocketRules.tier(from.planet()),RocketRules.tier(to.planet()));
        // Gameplay allowance for long-duration propulsion reserves and cryogenic losses.
        // This is a balance curve, not an ephemeris or a claim about real fuel mass.
        int endurance=Math.max(0,maxTier-1);
        int logistics=200*Math.abs(RocketRules.tier(from.planet())-RocketRules.tier(to.planet()))+100*endurance*endurance;
        int total=(int)Math.ceil((100*(departure+capture+landing)+logistics)/100)*100;
        return new Breakdown(departure,capture,landing,logistics,total);
    }
    public static int cost(TravelDestination from,TravelDestination to){return budget(from,to).total;}
}

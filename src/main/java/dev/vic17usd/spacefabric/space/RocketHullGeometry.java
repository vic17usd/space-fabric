package dev.vic17usd.spacefabric.space;

/** Generated from rotated Blockbench bounds by tools/import_rocket_fleet.py. */
public final class RocketHullGeometry {
    private RocketHullGeometry() { }
    public static float width(RocketVariant variant){return switch(variant){
        case BASE -> 1.64F;
        case MERCURY -> 2.64F;
        case VENUS -> 2.64F;
        case EARTH -> 2.424985F;
        case MOON -> 1.64F;
        case MARS -> 1.470833F;
        case JUPITER -> 2.268249F;
        case SATURN -> 2.405217F;
        case URANUS -> 1.581333F;
        case NEPTUNE -> 1.581333F;
    };}
    public static float height(RocketVariant variant){return switch(variant){
        case BASE -> 3.304211F;
        case MERCURY -> 2.608073F;
        case VENUS -> 2.076479F;
        case EARTH -> 3.42F;
        case MOON -> 3.304211F;
        case MARS -> 3.42F;
        case JUPITER -> 3.42F;
        case SATURN -> 3.42F;
        case URANUS -> 3.42F;
        case NEPTUNE -> 3.42F;
    };}
}

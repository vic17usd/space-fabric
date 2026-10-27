package dev.vic17usd.spacefabric.space;

/** Shared server/UI rules; disabled controls always carry the reason that disabled them. */
public final class RocketNavigation {
    private RocketNavigation() { }
    public enum Reason {
        READY("ready"), SYNC("sync"), IN_FLIGHT("in_flight"), OTHER_PILOT("other_pilot"),
        CURRENT_WORLD("current_world"), UNSUPPORTED("unsupported"), UNKNOWN_WORLD("unknown_world"),
        BOARD_FIRST("board_first"), ALREADY_BOARDED("already_boarded"), BROKEN_HULL("broken_hull"), LOW_FUEL("low_fuel");
        public final String key;
        Reason(String key){this.key="gui.space_fabric.rocket_reason."+key;}
    }
    public record State(RocketVariant variant,int source,int selected,int flight,int fuel,int cost,int capacity,
                        int hull,boolean pilot,boolean occupied,boolean exclusive,boolean ready) {
        public Reason destination(int index) {
            if(!ready)return Reason.SYNC;
            if(flight!=RocketRules.IDLE)return Reason.IN_FLIGHT;
            if(occupied&&!pilot)return Reason.OTHER_PILOT;
            if(source<0)return Reason.UNKNOWN_WORLD;
            if(index<0||index>=TravelDestination.ALL.size())return Reason.UNSUPPORTED;
            if(index==source)return Reason.CURRENT_WORLD;
            if(!variant.permits(TravelDestination.at(index).planet(),exclusive))return Reason.UNSUPPORTED;
            return Reason.READY;
        }
        public Reason board() {
            if(!ready)return Reason.SYNC;
            if(flight!=RocketRules.IDLE)return Reason.IN_FLIGHT;
            if(pilot)return Reason.ALREADY_BOARDED;
            return occupied?Reason.OTHER_PILOT:Reason.READY;
        }
        public Reason launch() {
            var route=destination(selected);if(route!=Reason.READY)return route;
            if(!pilot)return Reason.BOARD_FIRST;
            if(hull<=0)return Reason.BROKEN_HULL;
            if(cost<=0||fuel<cost)return Reason.LOW_FUEL;
            return Reason.READY;
        }
        public int fuelBarWidth(int width){return capacity<=0?0:(int)Math.clamp((long)fuel*width/capacity,0,width);}
    }
}

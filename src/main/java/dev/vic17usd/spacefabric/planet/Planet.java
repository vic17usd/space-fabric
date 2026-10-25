package dev.vic17usd.spacefabric.planet;

/** Datos independientes de Minecraft: gravedad relativa a la Tierra y atmósfera. */
public enum Planet {
    MERCURY("mercury", 0.38, false, Atmosphere.Pressure.VACUUM),
    VENUS("venus", 0.90, false, Atmosphere.Pressure.CRUSHING),
    EARTH("earth", 1.00, true, Atmosphere.Pressure.NORMAL),
    MARS("mars", 0.38, false, Atmosphere.Pressure.THIN),
    JUPITER("jupiter", 2.53, false, Atmosphere.Pressure.CRUSHING),
    SATURN("saturn", 1.07, false, Atmosphere.Pressure.CRUSHING),
    URANUS("uranus", 0.89, false, Atmosphere.Pressure.CRUSHING),
    NEPTUNE("neptune", 1.14, false, Atmosphere.Pressure.CRUSHING);

    private final String path;
    private final double gravityScale;
    private final Atmosphere atmosphere;

    Planet(String path, double gravityScale, boolean breathable, Atmosphere.Pressure pressure) {
        this.path = path;
        this.gravityScale = gravityScale;
        this.atmosphere = new Atmosphere(breathable, pressure);
    }

    public String path() {
        return path;
    }

    public double gravityScale() {
        return gravityScale;
    }

    public Atmosphere atmosphere() {
        return atmosphere;
    }
}

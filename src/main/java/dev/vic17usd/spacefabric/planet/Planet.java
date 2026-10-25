package dev.vic17usd.spacefabric.planet;

/** Datos independientes de Minecraft: gravedad relativa a la Tierra y atmósfera. */
public enum Planet {
    MERCURY("mercury", 0.38, false, Atmosphere.Pressure.VACUUM, -64, 384, "#000000", "#272727"),
    VENUS("venus", 0.90, false, Atmosphere.Pressure.CRUSHING, -64, 512, "#cf8b40", "#b48236"),
    EARTH("earth", 1.00, true, Atmosphere.Pressure.NORMAL, -64, 384, "#78a7ff", "#c0d8ff"),
    MOON("moon", 0.165, false, Atmosphere.Pressure.VACUUM, -64, 384, "#000000", "#676b73"),
    MARS("mars", 0.38, false, Atmosphere.Pressure.THIN, -64, 384, "#b96540", "#a35d40"),
    JUPITER("jupiter", 2.53, false, Atmosphere.Pressure.CRUSHING, -64, 768, "#cba27a", "#9e7758"),
    SATURN("saturn", 1.07, false, Atmosphere.Pressure.CRUSHING, -64, 640, "#dfc38d", "#bca776"),
    URANUS("uranus", 0.89, false, Atmosphere.Pressure.CRUSHING, -64, 512, "#93dfdf", "#7ac2c2"),
    NEPTUNE("neptune", 1.14, false, Atmosphere.Pressure.CRUSHING, -64, 512, "#355cbb", "#2c4596");

    private final String path;
    private final double gravityScale;
    private final Atmosphere atmosphere;
    private final int minY;
    private final int height;
    private final String skyColor;
    private final String fogColor;

    Planet(String path, double gravityScale, boolean breathable, Atmosphere.Pressure pressure,
           int minY, int height, String skyColor, String fogColor) {
        this.path = path;
        this.gravityScale = gravityScale;
        this.atmosphere = new Atmosphere(breathable, pressure);
        this.minY = minY;
        this.height = height;
        this.skyColor = skyColor;
        this.fogColor = fogColor;
    }

    public int minY() { return minY; }

    public int height() { return height; }

    public String skyColor() { return skyColor; }

    public String fogColor() { return fogColor; }

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

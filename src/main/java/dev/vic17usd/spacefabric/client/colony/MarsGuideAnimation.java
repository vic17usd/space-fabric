package dev.vic17usd.spacefabric.client.colony;

import java.util.*;

/** A deterministic, world-independent storyboard. All coordinates are demonstration coordinates. */
public final class MarsGuideAnimation {
    public enum Chapter {
        PACK, BUILD, VENT, AIRLOCK, SUPPLIES, WATER, RECIPES, FINISH;
        public String key() { return name().toLowerCase(Locale.ROOT); }
    }
    public static final int DURATION = 320;
    private Chapter chapter = Chapter.PACK;
    private double frame;
    private boolean playing = true;
    public Chapter chapter() { return chapter; }
    public boolean playing() { return playing; }
    public boolean ended() { return frame >= DURATION; }
    public void toggle() { if (ended()) restart(); else playing = !playing; }
    public void restart() { frame = 0; playing = true; }
    public void tick() { if (playing && ++frame >= DURATION) { frame = DURATION; playing = false; } }
    public double progress(float partialTick) { return Math.min(1, (frame + (playing ? Math.clamp(partialTick, 0, 1) : 0)) / DURATION); }
    public void seek(double progress) { frame = Math.clamp(progress, 0, 1) * DURATION; if (ended()) playing = false; }
    public void select(int chapter) {
        int next = Math.clamp(chapter, 0, Chapter.values().length - 1);
        if (next == this.chapter.ordinal()) return;
        boolean resume = playing || ended();
        this.chapter = Chapter.values()[next]; frame = 0; playing = resume;
    }
    public void move(int direction) { select(chapter.ordinal() + direction); }
    public static double ramp(double value, double from, double to) { return Math.clamp((value - from) / (to - from), 0, 1); }
    public static double ease(double value, double from, double to) { double t = ramp(value, from, to); return t * t * (3 - 2 * t); }

    public record Voxel(int x, int y, int z, boolean door) { }
    /** Complete sealed example, including the two halves of its front door: 170 boundary blocks. */
    public static List<Voxel> shell() { return SHELL; }
    private static final List<Voxel> SHELL = makeShell();
    private static List<Voxel> makeShell() {
        var result = new ArrayList<Voxel>();
        for (int y = 0; y <= 4; y++) for (int z = 0; z <= 6; z++) for (int x = 0; x <= 6; x++)
            if (y == 0 || y == 4 || x == 0 || x == 6 || z == 0 || z == 6)
                result.add(new Voxel(x, y, z, x == 3 && z == 6 && (y == 1 || y == 2)));
        return List.copyOf(result);
    }
    public static double construction(Voxel block, double p) {
        if (block.y == 0) return ease(p, .04, .20);
        if (block.y == 4) return ease(p, .38, .54);
        return ease(p, .20 + (block.y - 1) * .055, .32 + (block.y - 1) * .055);
    }
    /** Hold the complete shell before opening the teaching cut. */
    public static double cutaway(Chapter chapter, double p) { return ease(p, chapter == Chapter.BUILD ? .72 : .06, chapter == Chapter.BUILD ? .88 : .24); }
    public static boolean cutFace(Voxel b) { return b.y == 4 || b.y > 0 && (b.x == 6 || b.z == 6); }
    public static String phase(Chapter chapter, double p) {
        return switch (chapter) {
            case PACK -> "pack.phase";
            case BUILD -> p < .20 ? "build.floor" : p < .44 ? "build.walls" : p < .56 ? "build.roof" : p < .72 ? "build.sealed" : "build.cut";
            case VENT -> p < .24 ? "vent.place" : p < .64 ? "vent.air" : "vent.select";
            case AIRLOCK -> p < .28 ? "airlock.enter" : p < .48 ? "airlock.close" : p < .78 ? "airlock.exit" : "airlock.done";
            case SUPPLIES -> p < .28 ? "supplies.insert" : p < .55 ? "supplies.wait" : "supplies.processed";
            case WATER -> p < .32 ? "water.slot" : "water.stored";
            case RECIPES -> "recipes.phase";
            case FINISH -> p < .45 ? "finish.inside" : "finish.next";
        };
    }
    public record Doors(boolean outerOpen, boolean innerOpen) { }
    public static Doors doors(double p) { return new Doors(p < .28, p >= .48 && p < .78); }
    /** Player walks through z=8 and z=5 only while the respective door is open. */
    public static float airlockPlayerZ(double p) { return (float)(9.2 - 2.6 * ease(p, .05, .25) - 2.8 * ease(p, .51, .75)); }
    public static boolean walking(double p) { return p > .05 && p < .25 || p > .51 && p < .75; }

    /** Native GUI pixels, never a scaled 320px canvas. Compact captions have three full lines. */
    public record Layout(int left, int top, int width, int height, int sceneX, int sceneY, int sceneWidth,
                         int sceneHeight, int textX, int textY, int textWidth, int textHeight, boolean wide) {
        public int controlsY() { return top + height - 35; }
        public int timelineY() { return top + height - 43; }
        public static Layout fit(int width, int height) {
            int w = Math.min(880, width - 8), h = Math.min(500, height - 8);
            int l = (width - w) / 2, t = (height - h) / 2;
            boolean wide = w >= 600 && h >= 290;
            int sw = wide ? (w - 36) * 2 / 3 : w - 20;
            int sh = h - 100 - (wide ? 0 : 34);
            return new Layout(l, t, w, h, l + 10, t + 49, sw, sh,
                    wide ? l + sw + 22 : l + 10, wide ? t + 55 : t + 52 + sh,
                    wide ? w - sw - 32 : w - 20, wide ? sh - 6 : 30, wide);
        }
    }
}

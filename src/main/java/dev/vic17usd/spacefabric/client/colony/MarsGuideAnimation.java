package dev.vic17usd.spacefabric.client.colony;

import java.util.*;

/** Deterministic display animation. It never reads or changes the world or mission progress. */
public final class MarsGuideAnimation {
    public enum Chapter {
        PACK, BUILD, VENT, AIRLOCK, SUPPLIES, WATER, RECIPES, FINISH;
        public String key() { return name().toLowerCase(Locale.ROOT); }
    }
    public static final int DURATION = 200;
    private Chapter chapter = Chapter.PACK;
    private double frame;
    private boolean playing = true;
    public Chapter chapter() { return chapter; }
    public boolean playing() { return playing; }
    public void toggle() { playing = !playing; }
    public void restart() { frame = 0; }
    public void tick() { if (playing) frame = (frame + 1) % DURATION; }
    public double progress(float partialTick) { return ((frame + (playing ? Math.clamp(partialTick, 0, 1) : 0)) % DURATION) / DURATION; }
    public void seek(double progress) { frame = Math.clamp(progress, 0, .999) * DURATION; }
    public void select(int chapter) { this.chapter = Chapter.values()[Math.clamp(chapter, 0, Chapter.values().length - 1)]; restart(); }
    public void move(int direction) { select(chapter.ordinal() + direction); }
    public static double ramp(double value, double from, double to) { return Math.clamp((value - from) / (to - from), 0, 1); }

    public record Voxel(int x, int y, int z, boolean door) { }
    /** Complete 7x7x5 shell; the illustration hides the front and raises the roof as a cutaway. */
    public static List<Voxel> shell() {
        var result = new ArrayList<Voxel>();
        for (int y = 0; y <= 4; y++) for (int z = 0; z <= 6; z++) for (int x = 0; x <= 6; x++) {
            if (y == 0 || y == 4 || x == 0 || x == 6 || z == 0 || z == 6)
                result.add(new Voxel(x, y, z, x == 3 && z == 6 && (y == 1 || y == 2)));
        }
        return List.copyOf(result);
    }
    public record DisplayVoxel(float x, float y, float z, int color) { }
    private static final List<Voxel> SHELL = shell();
    public static List<DisplayVoxel> habitat(double progress, boolean assembling, boolean protectedRoom) {
        var visible = new ArrayList<Voxel>();
        int limit = assembling ? (int)(SHELL.size() * ramp(progress, 0, .72)) : SHELL.size();
        for (int i = 0; i < limit; i++) {
            var block = SHELL.get(i);
            if (block.y() > 0 && block.y() < 4 && (block.x() == 6 || block.z() == 6)) continue;
            if (block.y() == 4 && (block.x() > 2 || block.z() > 2)) continue;
            visible.add(block);
        }
        visible.sort(Comparator.comparingInt((Voxel v) -> v.x() + v.z()).thenComparingInt(Voxel::y));
        return visible.stream().map(b -> new DisplayVoxel(b.x(), b.y() == 4 ? 5.4F : b.y(), b.z(),
                b.y() == 0 ? 0xB67B5C : protectedRoom ? 0x91CFBB : 0xB5BFC5)).toList();
    }
    public record Doors(boolean outerOpen, boolean innerOpen) { }
    public static Doors doors(double progress) {
        return progress < .28 ? new Doors(true, false) : progress < .53 ? new Doors(false, false)
                : progress < .82 ? new Doors(false, true) : new Doors(false, false);
    }
    public record Point(float x, float y) { }
    public static Point project(float x, float y, float z) { return new Point(160 + (x - z) * 13, 96 + (x + z) * 5 - y * 12); }
    public record Face(float x, float y, float ax, float ay, float bx, float by, int color) { }
    public static List<Face> faces(float x, float y, float z, int color) {
        var p = project(x, y, z);
        return List.of(new Face(p.x(), p.y() - 12, 13, 5, -13, 5, 0xFF000000 | color),
                new Face(p.x() - 13, p.y() - 7, 13, 5, 0, 12, shade(color, .62F)),
                new Face(p.x(), p.y() - 2, 13, -5, 0, 12, shade(color, .82F)));
    }
    private static int shade(int c, float factor) {
        return 0xFF000000 | (int)((c >> 16 & 255) * factor) << 16 | (int)((c >> 8 & 255) * factor) << 8 | (int)((c & 255) * factor);
    }

    public record Layout(int left, int top, int width, int height, int sceneX, int sceneY, int sceneWidth,
                         int sceneHeight, int textX, int textY, int textWidth, int textHeight) {
        public static Layout fit(int width, int height) {
            int w = Math.min(620, width - 12), h = Math.min(370, height - 12);
            int l = (width - w) / 2, t = (height - h) / 2;
            boolean wide = w >= 480;
            int sw = wide ? (w - 40) * 3 / 5 : w - 24;
            int sh = wide ? h - 113 : h - 157;
            return new Layout(l, t, w, h, l + 12, t + 52, sw, sh,
                    wide ? l + sw + 24 : l + 12, wide ? t + 58 : t + 58 + sh,
                    wide ? w - sw - 36 : w - 24, wide ? sh - 6 : 38);
        }
    }
}

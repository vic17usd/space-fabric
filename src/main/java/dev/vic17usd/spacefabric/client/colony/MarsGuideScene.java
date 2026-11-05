package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.colony.ColonyContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;

/** Affine projected cuboids, emitted as native GUI quads rather than a video or external textures. */
public final class MarsGuideScene {
    private MarsGuideScene() { }
    private static void face(GuiGraphicsExtractor gui, float x, float y, float ax, float ay, float bx, float by, int color) {
        var pose = gui.pose(); pose.pushMatrix();
        try {
            pose.mul(new Matrix3x2f(ax / 16, ay / 16, bx / 16, by / 16, x, y));
            gui.fill(0, 0, 16, 16, color);
            gui.fill(0, 0, 16, 1, 0x30000000); gui.fill(0, 15, 16, 16, 0x30000000);
            gui.fill(0, 1, 1, 15, 0x30000000); gui.fill(15, 1, 16, 15, 0x30000000);
        } finally { pose.popMatrix(); }
    }
    private static void cube(GuiGraphicsExtractor gui, float x, float y, float z, int color) {
        for (var f : MarsGuideAnimation.faces(x, y, z, color)) face(gui, f.x(), f.y(), f.ax(), f.ay(), f.bx(), f.by(), f.color());
    }
    public static void habitat(GuiGraphicsExtractor gui, double progress, boolean assembling, boolean vent, boolean protectedRoom) {
        for (var block : MarsGuideAnimation.habitat(progress, assembling, protectedRoom))
            cube(gui, block.x(), block.y(), block.z(), block.color());
        gui.nextStratum();
        if (!assembling || progress > .70) {
            cube(gui, 3, 1, 3, 0x55BECA);
            var p = MarsGuideAnimation.project(3, 1, 3);
            gui.item(new ItemStack(ColonyContent.HABITAT_CONTROLLER), (int)p.x() - 8, (int)p.y() - 12);
            if (vent) {
                var v = MarsGuideAnimation.project(3, 2, 3);
                int pulse = (int)(4 * Math.sin(progress * Math.PI * 8));
                gui.outline((int)v.x() - 12, (int)v.y() - 18, 24, 24, 0xFF65D9ED);
                gui.fill((int)v.x() - 2, (int)v.y() - 16 - pulse, (int)v.x() + 2, (int)v.y() - 3 - pulse, 0xFF8DE9BE);
            }
        }
        if (protectedRoom) {
            // A small player inside, visually distinct from the controller.
            cube(gui, 4, 1.3F, 4, 0xE5BA72);
            cube(gui, 4, 2.2F, 4, 0xF4D9AA);
        }
    }
    public static void airlock(GuiGraphicsExtractor gui, double progress) {
        var doors = MarsGuideAnimation.doors(progress);
        gui.fill(8, 40, 102, 142, 0x663F221C); gui.fill(104, 40, 206, 142, 0x6648575B); gui.fill(208, 40, 312, 142, 0x66316458);
        gui.outline(102, 39, 106, 104, 0xFFB6C4CD);
        gate(gui, 102, doors.outerOpen()); gate(gui, 206, doors.innerOpen());
        int x = progress < .28 ? 40 + (int)(116 * MarsGuideAnimation.ramp(progress, 0, .28))
                : progress < .53 ? 156 : progress < .82 ? 156 + (int)(110 * MarsGuideAnimation.ramp(progress, .53, .82)) : 266;
        gui.fill(x - 5, 82, x + 5, 102, 0xFFE7BD74);
        gui.fill(x - 4, 74, x + 4, 82, 0xFFFFDCAF);
        gui.item(new ItemStack(ColonyContent.AIRLOCK_DOOR), 96, 16); gui.item(new ItemStack(ColonyContent.AIRLOCK_DOOR), 200, 16);
    }
    private static void gate(GuiGraphicsExtractor gui, int x, boolean open) {
        if (!open) gui.fill(x - 3, 53, x + 3, 129, 0xFF8DE9BE);
        else { gui.fill(x - 3, 44, x + 3, 61, 0xFFFFCF7A); gui.fill(x - 3, 121, x + 3, 138, 0xFFFFCF7A); }
    }
}

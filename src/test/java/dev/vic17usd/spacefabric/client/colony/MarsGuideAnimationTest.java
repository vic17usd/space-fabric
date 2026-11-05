package dev.vic17usd.spacefabric.client.colony;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsGuideAnimationTest {
    @Test void playbackCanPauseSeekReplayAndChangeChaptersWithoutAdvancingAMission() {
        var animation = new MarsGuideAnimation();
        for (int tick = 0; tick < 50; tick++) animation.tick();
        assertEquals(.25, animation.progress(0));
        animation.toggle();
        for (int tick = 0; tick < 80; tick++) animation.tick();
        assertEquals(.25, animation.progress(.8F));
        animation.seek(.70); assertEquals(.70, animation.progress(0), 1e-9);
        animation.restart(); assertEquals(0, animation.progress(0));
        animation.move(-1); assertEquals(MarsGuideAnimation.Chapter.PACK, animation.chapter());
        animation.select(100); assertEquals(MarsGuideAnimation.Chapter.FINISH, animation.chapter());
        animation.move(1); assertEquals(MarsGuideAnimation.Chapter.FINISH, animation.chapter());
        animation.select(6); assertEquals(MarsGuideAnimation.Chapter.RECIPES, animation.chapter());
        assertFalse(animation.playing());
    }
    @Test void tenSecondLoopNeverLeavesItsTimelineAndSeekingIsBounded() {
        var animation = new MarsGuideAnimation();
        for (int tick = 0; tick < 2000; tick++) {
            animation.tick();
            assertTrue(animation.progress(.9F) >= 0 && animation.progress(.9F) < 1);
        }
        assertEquals(0, animation.progress(0));
        animation.seek(-5); assertEquals(0, animation.progress(0));
        animation.seek(5); assertTrue(animation.progress(0) < 1);
    }
    @Test void anAirlockNeverShowsBothDoorsOpenAndHasAClosedTransition() {
        boolean outer = false, inner = false, closed = false;
        for (int frame = 0; frame < 200; frame++) {
            var doors = MarsGuideAnimation.doors(frame / 200.0);
            assertFalse(doors.outerOpen() && doors.innerOpen());
            outer |= doors.outerOpen(); inner |= doors.innerOpen(); closed |= !doors.outerOpen() && !doors.innerOpen();
        }
        assertTrue(outer && inner && closed);
    }
    @Test void diagramsAndControlsFitSmallAndWideMinecraftGuiSizes() {
        for (int width : new int[]{320, 427, 480, 640, 1280}) for (int height : new int[]{240, 270, 360, 720}) {
            var r = MarsGuideAnimation.Layout.fit(width, height);
            assertTrue(r.left() >= 0 && r.top() >= 0);
            assertTrue(r.left() + r.width() <= width && r.top() + r.height() <= height);
            assertTrue(r.sceneWidth() > 0 && r.sceneHeight() > 0);
            assertTrue(r.textY() + r.textHeight() <= r.top() + r.height() - 52);
            assertTrue(r.sceneX() + 252 <= r.left() + r.width());
            assertTrue(r.sceneY() + Math.max(108, r.sceneHeight()) <= r.top() + r.height() - 52);
            assertTrue(r.left() + 208 < r.left() + r.width() - 88);
        }
    }
    @Test void affineCubeFacesKeepTheirEdgesAndPositiveAreaWithoutRasterRounding() {
        for (var cube : MarsGuideAnimation.shell()) {
            var faces = MarsGuideAnimation.faces(cube.x(), cube.y(), cube.z(), 0xB5BFC5);
            for (var f : faces) {
                assertTrue(Float.isFinite(f.x()) && Float.isFinite(f.y()));
                assertTrue(f.ax() * f.by() - f.ay() * f.bx() > 0);
                assertEquals(255, f.color() >>> 24);
            }
            var top = faces.get(0); var left = faces.get(1); var right = faces.get(2);
            assertEquals(top.x() + top.bx(), left.x()); assertEquals(top.y() + top.by(), left.y());
            assertEquals(top.x() + top.ax() + top.bx(), right.x());
            assertEquals(top.y() + top.ay() + top.by(), right.y());
        }
    }

    @Test void animatedRoofAndAllVisibleCuboidsStayInsideTheSceneFrame() {
        int previous = 0;
        for (int frame = 0; frame < 200; frame++) {
            var scene = MarsGuideAnimation.habitat(frame / 200.0, true, false);
            assertTrue(scene.size() >= previous); previous = scene.size();
            for (var v : scene) for (var f : MarsGuideAnimation.faces(v.x(), v.y(), v.z(), v.color())) {
                for (int a = 0; a <= 1; a++) for (int b = 0; b <= 1; b++) {
                    float x = f.x() + a * f.ax() + b * f.bx(), y = f.y() + a * f.ay() + b * f.by();
                    assertTrue(x >= 0 && x <= 320 && y >= 18 && y <= 180, "Projected cuboid outside illustration");
                }
            }
        }
        assertTrue(previous > 0);
    }
}

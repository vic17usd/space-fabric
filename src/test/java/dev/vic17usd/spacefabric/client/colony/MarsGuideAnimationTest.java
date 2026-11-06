package dev.vic17usd.spacefabric.client.colony;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarsGuideAnimationTest {
    @Test void pauseFreezesSubticksAndChapterSelectionPreservesManualPause() {
        var a = new MarsGuideAnimation();
        for (int i = 0; i < 80; i++) a.tick();
        assertEquals(.25, a.progress(0)); a.toggle();
        for (int i = 0; i < 90; i++) a.tick();
        assertEquals(.25, a.progress(.9F)); a.select(3);
        assertFalse(a.playing()); assertEquals(0, a.progress(.5F));
        a.seek(.7); assertEquals(.7, a.progress(0), 1e-9);
        a.restart(); assertEquals(0, a.progress(0)); assertTrue(a.playing());
    }
    @Test void endHoldsForReadingAndReplayAndNavigationResume() {
        var a = new MarsGuideAnimation();
        for (int i = 0; i < 2000; i++) a.tick();
        assertEquals(1, a.progress(.9F)); assertTrue(a.ended()); assertFalse(a.playing());
        a.toggle(); assertTrue(a.playing()); assertEquals(0, a.progress(0));
        a.seek(2); assertTrue(a.ended()); a.select(7); assertTrue(a.playing());
        assertEquals(MarsGuideAnimation.Chapter.FINISH, a.chapter());
        a.tick(); a.move(1); assertEquals(1.0 / MarsGuideAnimation.DURATION, a.progress(0));
        a.select(-8); assertEquals(MarsGuideAnimation.Chapter.PACK, a.chapter());
        a.seek(-1); assertEquals(0, a.progress(0));
    }
    @Test void airlockNeverOpensBothDoorsAndPlayerOnlyCrossesAnOpenDoor() {
        boolean outer = false, inner = false, closed = false;
        for (int i = 0; i <= 3200; i++) {
            double p = i / 3200.0; var d = MarsGuideAnimation.doors(p);
            assertFalse(d.outerOpen() && d.innerOpen());
            outer |= d.outerOpen(); inner |= d.innerOpen(); closed |= !d.outerOpen() && !d.innerOpen();
            double z = MarsGuideAnimation.airlockPlayerZ(p);
            if (Math.abs(z - 8.5) < .35) assertTrue(d.outerOpen(), "Player's body must clear the outer door");
            if (Math.abs(z - 5.5) < .35) assertTrue(d.innerOpen(), "Player's body must clear the inner door");
        }
        assertTrue(outer && inner && closed);
    }
    @Test void completeShellIsHeldBeforeAnyTeachingCutAndControllerVentIsEmpty() {
        assertEquals(170, MarsGuideAnimation.shell().size());
        for (double p : new double[]{.55, .6, .7, .72}) {
            assertEquals(0, MarsGuideAnimation.cutaway(MarsGuideAnimation.Chapter.BUILD, p));
            for (var v : MarsGuideAnimation.shell()) assertEquals(1, MarsGuideAnimation.construction(v, p), 1e-9);
        }
        assertTrue(MarsGuideAnimation.shell().stream().noneMatch(v -> v.x() == 3 && v.z() == 3 && (v.y() == 1 || v.y() == 2)));
        assertEquals(1, MarsGuideAnimation.cutaway(MarsGuideAnimation.Chapter.BUILD, 1));
    }
    @Test void easedMotionHasZeroVelocityAtBothEndsAndNoOvershoot() {
        assertEquals(0, MarsGuideAnimation.ease(-2, .2, .6)); assertEquals(1, MarsGuideAnimation.ease(2, .2, .6));
        double previous = 0;
        for (int i = 0; i <= 1000; i++) {
            double value = MarsGuideAnimation.ease(i / 1000.0, .2, .6);
            assertTrue(value >= previous && value <= 1); previous = value;
        }
        assertTrue(MarsGuideAnimation.ease(.20001, .2, .6) / .00001 < .001);
        assertTrue((1 - MarsGuideAnimation.ease(.59999, .2, .6)) / .00001 < .001);
    }
    @Test void smallTallAndWideLayoutsFitNativeIconsTextAndControlsWithoutOverlap() {
        for (int width : new int[]{320, 427, 480, 600, 640, 854, 1280}) for (int height : new int[]{240, 270, 300, 360, 720}) {
            var r = MarsGuideAnimation.Layout.fit(width, height);
            assertTrue(r.left() >= 0 && r.top() >= 0);
            assertTrue(r.left() + r.width() <= width && r.top() + r.height() <= height);
            assertTrue(r.sceneWidth() >= 270 && r.sceneHeight() >= 98);
            assertTrue(r.sceneY() + r.sceneHeight() < r.timelineY());
            assertTrue(r.textY() + r.textHeight() < r.timelineY());
            assertTrue(r.controlsY() > r.timelineY() + 5);
            assertTrue(r.controlsY() + 18 <= r.top() + r.height() - 15);
            assertTrue(r.sceneX() + 204 < r.sceneX() + r.sceneWidth() - 45);
            if (!r.wide()) assertEquals(30, r.textHeight());
        }
    }
}

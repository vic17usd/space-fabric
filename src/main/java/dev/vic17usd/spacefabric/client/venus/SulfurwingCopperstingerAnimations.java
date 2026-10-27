package dev.vic17usd.spacefabric.client.venus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

@Environment(EnvType.CLIENT)
public final class SulfurwingCopperstingerAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition PRIMARY_ATTACK = createPrimaryAttack();
   public static final AnimationDefinition IDLE_FOR_PRIMARY_ATTACK = createIdleForPrimaryAttack();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{PRIMARY_ATTACK};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{IDLE_FOR_PRIMARY_ATTACK};

   private SulfurwingCopperstingerAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "root",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.posVec(0.0F, -0.6F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_fore_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_fore_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_hind_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-9.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_hind_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(9.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-9.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_mid",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "stinger",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_front_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_front_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foreleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foreleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_mid_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_mid_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "midleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "midleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_hind_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_hind_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hindleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hindleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(0.8F)
         .looping()
         .addAnimation(
            "root",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_fore_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 45.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 45.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, -40.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 45.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_fore_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -45.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(35.0F, 0.0F, -45.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 40.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -45.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_hind_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 30.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 30.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -30.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.68F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 30.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_hind_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -30.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(18.0F, 0.0F, -30.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 30.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.68F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -30.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, -1.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -3.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_mid",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_end",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "stinger",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_front_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_front_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foreleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foreleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_mid_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_mid_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "midleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "midleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_hind_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_hind_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hindleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-23.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hindleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.16F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-23.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.52F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createPrimaryAttack() {
      return Builder.withLength(1.6F)
         .addAnimation(
            "root",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, -5.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.posVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_fore_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 18.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, -14.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -5.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_fore_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 14.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_hind_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, -3.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_hind_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(14.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 10.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mandible_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 18.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 25.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -3.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mandible_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -25.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(28.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_mid",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen_end",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-28.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-11.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "stinger",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_front_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_front_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foreleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foreleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crystal_crown",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.34F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForPrimaryAttack() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_mid_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_mid_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "midleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "midleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-13.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_hind_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_hind_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hindleg_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hindleg_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }
}

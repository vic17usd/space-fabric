package dev.vic17usd.spacefabric.client.venus;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class SulfurwingCopperHarbingerAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition PRIMARY_ATTACK = createPrimaryAttack();
   public static final AnimationDefinition IDLE_FOR_PRIMARY_ATTACK = createIdleForPrimaryAttack();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{PRIMARY_ATTACK};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{IDLE_FOR_PRIMARY_ATTACK};

   private SulfurwingCopperHarbingerAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(1.2F)
         .looping()
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 0.7F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_upper_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_upper_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(0.8F)
         .looping()
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_upper_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 38.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -38.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_upper_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -38.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 38.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 25.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -25.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 4.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -25.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 25.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -4.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createPrimaryAttack() {
      return Builder.withLength(1.6F)
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "arm_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(-58.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(-50.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, -3.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "arm_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(22.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(30.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(-58.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(-50.0F, 0.0F, 10.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "claw_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(-26.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "claw_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(-26.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_upper_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 10.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 34.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 44.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 30.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_upper_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -34.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -44.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -30.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_lower_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 24.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 34.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 25.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_lower_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -24.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -34.0F), Interpolations.LINEAR),
                  new Keyframe(0.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -25.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForPrimaryAttack() {
      return Builder.withLength(1.2F)
         .looping()
         .addAnimation(
            "thorax",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 0.7F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "abdomen",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }
}

package dev.vic17usd.spacefabric.client.sanctum;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class BasaltEmberGolemAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition PRIMARY_ATTACK = createPrimaryAttack();
   public static final AnimationDefinition IDLE_FOR_PRIMARY_ATTACK = createIdleForPrimaryAttack();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{PRIMARY_ATTACK};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{IDLE_FOR_PRIMARY_ATTACK};

   private BasaltEmberGolemAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.2F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(1.5F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "upper_arm_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, -1.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(1.6F)
         .looping()
         .addAnimation(
            "thigh_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(38.0784F, 3.771F, -11.7789F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(32.9444F, 13.171F, -17.8086F), Interpolations.LINEAR),
                  new Keyframe(0.2875F, KeyframeAnimations.degreeVec(12.0531F, -5.8557F, 6.7381F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(21.511F, -1.8151F, 2.3902F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(33.1334F, 1.4925F, 2.7264F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.25F, KeyframeAnimations.degreeVec(-18.822F, -4.6989F, -21.7066F), Interpolations.LINEAR),
                  new Keyframe(1.4625F, KeyframeAnimations.degreeVec(-3.6171F, 5.0478F, -22.1551F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(38.0784F, 3.771F, -11.7789F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "shin_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-40.9983F, 9.3304F, 16.2541F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(-28.5258F, 9.587F, 34.9961F), Interpolations.LINEAR),
                  new Keyframe(0.2875F, KeyframeAnimations.degreeVec(23.2961F, 2.6297F, -16.7915F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(15.4152F, 0.8114F, -5.7143F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.2907F, 1.0113F, -0.1179F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.25F, KeyframeAnimations.degreeVec(32.9332F, -12.365F, 34.991F), Interpolations.LINEAR),
                  new Keyframe(1.4625F, KeyframeAnimations.degreeVec(17.5891F, -9.8402F, 34.9948F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-40.9983F, 9.3304F, 16.2541F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foot_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(6.7416F, -2.7407F, -1.0169F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(-4.3336F, -3.1633F, -14.0952F), Interpolations.LINEAR),
                  new Keyframe(0.2875F, KeyframeAnimations.degreeVec(-32.6093F, 5.6026F, 10.4614F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-35.2589F, 0.9463F, 3.6347F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-33.5453F, -3.7239F, -6.4807F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.25F, KeyframeAnimations.degreeVec(-8.468F, 2.1603F, -11.5781F), Interpolations.LINEAR),
                  new Keyframe(1.4625F, KeyframeAnimations.degreeVec(-8.9667F, -0.3118F, -3.3695F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(6.7416F, -2.7407F, -1.0169F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "thigh_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-2.4209F, -2.8531F, -21.3107F), Interpolations.LINEAR),
                  new Keyframe(0.6875F, KeyframeAnimations.degreeVec(33.3623F, 6.0615F, -12.6511F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(37.2497F, 7.6788F, -8.1819F), Interpolations.LINEAR),
                  new Keyframe(0.875F, KeyframeAnimations.degreeVec(31.4055F, 13.3401F, -16.4628F), Interpolations.LINEAR),
                  new Keyframe(1.0625F, KeyframeAnimations.degreeVec(8.2218F, 2.4318F, -4.7979F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(24.9331F, 2.9015F, -4.6828F), Interpolations.LINEAR),
                  new Keyframe(1.25F, KeyframeAnimations.degreeVec(26.3606F, -1.0705F, -1.3265F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "shin_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(19.1102F, -7.1926F, 34.9988F), Interpolations.LINEAR),
                  new Keyframe(0.6875F, KeyframeAnimations.degreeVec(-35.3732F, 6.1561F, 23.4471F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-36.5835F, 4.0313F, 17.8726F), Interpolations.LINEAR),
                  new Keyframe(0.875F, KeyframeAnimations.degreeVec(-24.696F, 6.0649F, 34.9998F), Interpolations.LINEAR),
                  new Keyframe(1.0625F, KeyframeAnimations.degreeVec(29.5675F, -3.4863F, 8.6033F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(6.424F, -0.5236F, 9.7656F), Interpolations.LINEAR),
                  new Keyframe(1.25F, KeyframeAnimations.degreeVec(6.9833F, 0.7404F, 1.0919F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foot_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-25.1574F, 4.8944F, -9.0428F), Interpolations.LINEAR),
                  new Keyframe(0.6875F, KeyframeAnimations.degreeVec(-3.6032F, 1.218F, -5.7725F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-6.4002F, -0.8117F, -5.5381F), Interpolations.LINEAR),
                  new Keyframe(0.875F, KeyframeAnimations.degreeVec(-10.6765F, -1.9196F, -14.5267F), Interpolations.LINEAR),
                  new Keyframe(1.0625F, KeyframeAnimations.degreeVec(-39.9603F, 0.2032F, -2.4786F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-33.0127F, -0.45F, -3.267F), Interpolations.LINEAR),
                  new Keyframe(1.25F, KeyframeAnimations.degreeVec(-33.1038F, 0.9079F, -0.313F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(13.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "upper_arm_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "upper_arm_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "root",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createPrimaryAttack() {
      return Builder.withLength(1.6F)
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.94F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.18F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "upper_arm_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-49.21F, 10.47F, 115.72F), Interpolations.LINEAR),
                  new Keyframe(0.94F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-13.21F, -36.36F, -39.01F), Interpolations.LINEAR),
                  new Keyframe(1.18F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "forearm_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-1.63F, 0.61F, 47.7F), Interpolations.LINEAR),
                  new Keyframe(0.94F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-1.63F, 0.61F, 47.7F), Interpolations.LINEAR),
                  new Keyframe(1.18F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "upper_arm_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.94F, KeyframeAnimations.degreeVec(6.0F, 0.0F, -14.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "forearm_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.94F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
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
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.94F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForPrimaryAttack() {
      return Builder.withLength(2.4F).looping().build();
   }
}

package dev.vic17usd.spacefabric.client.sanctum;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class BasaltBosswardenAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition PRIMARY_ATTACK = createPrimaryAttack();
   public static final AnimationDefinition ACTION_BOSS_RAGE = createActionBossRage();
   public static final AnimationDefinition ACTION_SHIELD_BLOCK = createActionShieldBlock();
   public static final AnimationDefinition IDLE_FOR_PRIMARY_ATTACK = createIdleForPrimaryAttack();
   public static final AnimationDefinition IDLE_FOR_ACTION_BOSS_RAGE = createIdleForActionBossRage();
   public static final AnimationDefinition IDLE_FOR_ACTION_SHIELD_BLOCK = createIdleForActionShieldBlock();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{PRIMARY_ATTACK, ACTION_BOSS_RAGE, ACTION_SHIELD_BLOCK};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{
      IDLE_FOR_PRIMARY_ATTACK, IDLE_FOR_ACTION_BOSS_RAGE, IDLE_FOR_ACTION_SHIELD_BLOCK
   };

   private BasaltBosswardenAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(2.0F)
         .looping()
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 1.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "pelvis",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(0.0F, -0.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_fauld",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(1.2F)
         .looping()
         .addAnimation(
            "right_thigh",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.315F, 0.179F, 0.9283F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-11.5825F, -0.0474F, -0.1153F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(1.8248F, -0.0114F, -0.3955F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(6.7749F, 0.0398F, -0.8049F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(9.8043F, 0.551F, -7.4514F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(4.2207F, 0.2077F, -16.8535F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-22.315F, 0.179F, 0.9283F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_thigh",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-22.3318F, 0.2154F, 0.9324F), Interpolations.LINEAR),
                  new Keyframe(0.72F, KeyframeAnimations.degreeVec(-11.5825F, -0.0474F, -0.1153F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(1.8248F, -0.0114F, -0.3955F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(6.7788F, 0.0391F, -0.718F), Interpolations.LINEAR),
                  new Keyframe(0.96F, KeyframeAnimations.degreeVec(10.7999F, 0.4421F, -5.9679F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(4.2133F, 0.1968F, -16.8511F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_shin",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.2678F, -0.1501F, -0.2856F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-15.2502F, -0.0099F, 0.1236F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-31.0989F, 0.1208F, 0.4937F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-34.9774F, 0.4237F, 1.2507F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(-32.6587F, 5.3186F, 13.615F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(-3.4239F, 2.4409F, 34.3738F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-2.2678F, -0.1501F, -0.2856F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_shin",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.2332F, -0.1846F, -0.3224F), Interpolations.LINEAR),
                  new Keyframe(0.72F, KeyframeAnimations.degreeVec(-15.2503F, -0.0099F, 0.1236F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(-31.099F, 0.1208F, 0.4937F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-34.986F, 0.3608F, 1.0852F), Interpolations.LINEAR),
                  new Keyframe(0.96F, KeyframeAnimations.degreeVec(-34.6865F, 4.1335F, 11.0882F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-3.4049F, 2.4276F, 34.3652F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "pelvis",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_upper_arm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_fauld",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(1.5795F, 0.0971F, -4.658F), Interpolations.LINEAR),
                  new Keyframe(0.72F, KeyframeAnimations.degreeVec(7.698F, -0.0758F, 0.2451F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(9.6627F, -0.2153F, 0.4573F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(8.2331F, -0.2228F, 0.1369F), Interpolations.LINEAR),
                  new Keyframe(0.96F, KeyframeAnimations.degreeVec(4.6573F, -0.0977F, -5.3618F), Interpolations.LINEAR),
                  new Keyframe(1.08F, KeyframeAnimations.degreeVec(-17.9744F, -0.1818F, -15.4954F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(1.5958F, 0.0956F, -4.6769F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(7.698F, -0.0758F, 0.2451F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(9.6627F, -0.2153F, 0.4573F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(8.2292F, -0.221F, 0.0432F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(3.9563F, -0.7731F, -6.4974F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(-17.9653F, -0.1962F, -15.5087F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(1.5958F, 0.0956F, -4.6769F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createPrimaryAttack() {
      return Builder.withLength(1.8F)
         .addAnimation(
            "pelvis",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.12F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(-9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.12F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_upper_arm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 62.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 70.0F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -28.0F), Interpolations.LINEAR),
                  new Keyframe(1.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_forearm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 25.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 35.0F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -38.0F), Interpolations.LINEAR),
                  new Keyframe(1.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_hand",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 10.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 15.0F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -16.0F), Interpolations.LINEAR),
                  new Keyframe(1.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_upper_arm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_forearm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.84F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionBossRage() {
      return Builder.withLength(1.0F)
         .addAnimation(
            "pelvis",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.22F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.22F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_upper_arm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.22F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -14.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(10.0F, 0.0F, -24.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_forearm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.22F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(10.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_upper_arm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.22F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 26.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_forearm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.22F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.78F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionShieldBlock() {
      return Builder.withLength(1.2F)
         .addAnimation(
            "pelvis",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "torso",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_upper_arm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -36.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -48.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -48.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_forearm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(10.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -22.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -22.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "left_hand",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "right_upper_arm",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForPrimaryAttack() {
      return Builder.withLength(2.0F)
         .looping()
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 1.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_fauld",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionBossRage() {
      return Builder.withLength(2.0F)
         .looping()
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 1.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_fauld",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionShieldBlock() {
      return Builder.withLength(2.0F)
         .looping()
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 1.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_fauld",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }
}

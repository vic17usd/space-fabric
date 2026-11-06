package dev.vic17usd.spacefabric.client.fauna;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class GoldenEyeSandwraithAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition PRIMARY_ATTACK = createPrimaryAttack();
   public static final AnimationDefinition ACTION_DEATH = createActionDeath();
   public static final AnimationDefinition IDLE_FOR_PRIMARY_ATTACK = createIdleForPrimaryAttack();
   public static final AnimationDefinition IDLE_FOR_ACTION_DEATH = createIdleForActionDeath();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{PRIMARY_ATTACK, ACTION_DEATH};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{IDLE_FOR_PRIMARY_ATTACK, IDLE_FOR_ACTION_DEATH};

   private GoldenEyeSandwraithAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 1.8F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(3.0F, -4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crown_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -7.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crown_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -7.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "lower_body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(6.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-6.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mote_left",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.posVec(-1.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.posVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(0.7F)
         .looping()
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.posVec(0.0F, 3.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.posVec(0.0F, -2.4F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(8.0F, -7.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(18.0F, -2.0F, -7.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(8.0F, 7.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(-3.0F, 2.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(8.0F, -7.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crown_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -16.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -16.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 12.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crown_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, -11.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 15.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, -11.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 15.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, -11.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -15.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 18.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -15.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 18.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -15.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -16.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -16.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 12.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "lower_body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(11.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(-16.0F, 0.0F, -7.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(11.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "lower_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(-6.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(13.0F, 0.0F, -5.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(-16.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.0F, 0.0F, -9.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(-6.0F, 0.0F, 13.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(7.0F, 0.0F, -9.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(-11.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(7.0F, 0.0F, -9.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(7.0F, 0.0F, -13.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 7.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mote_right",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.posVec(2.0F, 2.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.posVec(-2.0F, -2.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_left",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.posVec(-3.0F, 1.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.posVec(-5.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.posVec(-2.0F, -1.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_right",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.posVec(3.0F, 1.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.posVec(5.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.posVec(2.0F, -1.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_rear",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.175F, KeyframeAnimations.posVec(0.0F, 1.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.posVec(0.0F, 0.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.525F, KeyframeAnimations.posVec(0.0F, -1.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createPrimaryAttack() {
      return Builder.withLength(1.2F)
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.degreeVec(19.0F, -4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(-16.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.posVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.degreeVec(17.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(-34.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-6.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "lower_body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_left",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(5.0F, -1.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.posVec(9.0F, -2.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.posVec(-9.0F, 3.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.posVec(-4.0F, 1.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_right",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(-5.0F, -1.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.posVec(-9.0F, -2.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.posVec(9.0F, 3.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.posVec(4.0F, 1.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_rear",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -2.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(0.58F, KeyframeAnimations.posVec(0.0F, -3.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.posVec(0.0F, 2.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.86F, KeyframeAnimations.posVec(0.0F, 1.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionDeath() {
      return Builder.withLength(1.1F)
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(0.0F, 12.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "storm_core",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(8.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-12.0F, 12.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-22.0F, 24.0F, 16.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crown_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(12.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 14.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 24.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crown_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(18.0F, 0.0F, -16.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(28.0F, 0.0F, -25.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(15.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 18.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 28.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(18.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(28.0F, 0.0F, -20.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "lower_body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(16.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(30.0F, 0.0F, -22.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "lower_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(10.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-16.0F, 0.0F, 10.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-26.0F, 0.0F, 18.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "lower_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(14.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(22.0F, 0.0F, -16.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "taper_band",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 20.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(14.0F, 0.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 22.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_left",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(-3.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(-9.0F, 7.0F, 9.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(-16.0F, 17.0F, 15.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_right",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(3.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(9.0F, 7.0F, 9.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(16.0F, 17.0F, 15.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_rear",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 1.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 7.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(0.0F, 17.0F, 22.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mote_left",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(-2.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(-8.0F, 8.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(-15.0F, 18.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mote_right",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(2.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(8.0F, 8.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(15.0F, 18.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mote_low",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, -2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 8.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(0.0F, 19.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForPrimaryAttack() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "crown_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -7.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "crown_back",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, -7.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(6.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-6.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mote_left",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.posVec(-1.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.posVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionDeath() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "wake_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wake_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }
}

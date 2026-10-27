package dev.vic17usd.spacefabric.client.venus;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class AshplumeEmberwalkerAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition ACTION_FLEE_STARTLE = createActionFleeStartle();
   public static final AnimationDefinition IDLE_FOR_ACTION_FLEE_STARTLE = createIdleForActionFleeStartle();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{ACTION_FLEE_STARTLE};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{IDLE_FOR_ACTION_FLEE_STARTLE};

   private AshplumeEmberwalkerAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(2.0F)
         .looping()
         .addAnimation(
            "neck",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-1.2F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
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
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(1.0F, 2.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(0.0F, -1.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(0.7F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, -1.0F), Interpolations.LINEAR),
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
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.9976F, -0.1031F, 0.1918F), Interpolations.LINEAR),
                  new Keyframe(0.125F, KeyframeAnimations.degreeVec(-7.2267F, -0.0069F, 0.0361F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(-1.3527F, -0.001F, -0.0455F), Interpolations.LINEAR),
                  new Keyframe(0.375F, KeyframeAnimations.degreeVec(5.5719F, 0.0032F, 0.0158F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(10.152F, 0.039F, 0.0612F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-12.9976F, -0.1031F, 0.1918F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_left_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.9093F, -0.0941F, -0.0534F), Interpolations.LINEAR),
                  new Keyframe(0.125F, KeyframeAnimations.degreeVec(1.5959F, -0.0073F, -0.0306F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(1.668F, -0.0012F, 0.0663F), Interpolations.LINEAR),
                  new Keyframe(0.375F, KeyframeAnimations.degreeVec(-1.2603F, 0.0032F, -0.0206F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-0.9277F, 0.0365F, -0.054F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.9093F, -0.0941F, -0.0534F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foot_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.9077F, 0.281F, -1.3628F), Interpolations.LINEAR),
                  new Keyframe(0.125F, KeyframeAnimations.degreeVec(5.9534F, 0.0129F, -0.2088F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(-0.8252F, 0.0026F, 0.1029F), Interpolations.LINEAR),
                  new Keyframe(0.375F, KeyframeAnimations.degreeVec(-3.8838F, -0.0028F, -0.099F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-6.0344F, -0.0458F, -0.8186F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(15.9077F, 0.281F, -1.3628F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-13.0098F, 0.0037F, -0.0564F), Interpolations.LINEAR),
                  new Keyframe(0.725F, KeyframeAnimations.degreeVec(-7.2123F, -0.0014F, -0.0114F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(-1.3886F, 6.0E-4F, 0.0156F), Interpolations.LINEAR),
                  new Keyframe(0.975F, KeyframeAnimations.degreeVec(5.5588F, 1.0E-4F, -0.0051F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(10.1221F, 0.0027F, -0.0171F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_right_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.8746F, 0.0511F, 0.0173F), Interpolations.LINEAR),
                  new Keyframe(0.725F, KeyframeAnimations.degreeVec(1.5642F, 0.0046F, 0.01F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(1.7332F, 1.0E-4F, -0.023F), Interpolations.LINEAR),
                  new Keyframe(0.975F, KeyframeAnimations.degreeVec(-1.2388F, -0.0018F, 0.0068F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-0.8839F, -0.0209F, 0.0173F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foot_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(16.2565F, -0.0843F, 0.4534F), Interpolations.LINEAR),
                  new Keyframe(0.725F, KeyframeAnimations.degreeVec(6.0151F, -0.0029F, 0.069F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(-0.8787F, -7.0E-4F, -0.0333F), Interpolations.LINEAR),
                  new Keyframe(0.975F, KeyframeAnimations.degreeVec(-3.8709F, 5.0E-4F, 0.0325F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-5.8797F, 0.0077F, 0.2689F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 2.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, -1.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionFleeStartle() {
      return Builder.withLength(0.7F)
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.posVec(0.0F, -1.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.posVec(0.0F, 1.4F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.posVec(0.0F, 0.7F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "neck",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(-4.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 18.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 32.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 26.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "wing_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -12.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(3.0F, 0.0F, -32.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(2.0F, 0.0F, -26.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_left_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(11.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_right_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foot_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "foot_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.46F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionFleeStartle() {
      return Builder.withLength(2.0F).looping().build();
   }
}

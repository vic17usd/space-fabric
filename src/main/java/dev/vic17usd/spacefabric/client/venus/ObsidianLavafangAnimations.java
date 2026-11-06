package dev.vic17usd.spacefabric.client.venus;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class ObsidianLavafangAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition PRIMARY_ATTACK = createPrimaryAttack();
   public static final AnimationDefinition IDLE_FOR_PRIMARY_ATTACK = createIdleForPrimaryAttack();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{PRIMARY_ATTACK};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{IDLE_FOR_PRIMARY_ATTACK};

   private ObsidianLavafangAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(2.0F)
         .looping()
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.35F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, -0.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "neck",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-1.5F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-1.0F, 5.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(1.0F, -5.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(0.6F)
         .looping()
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.posVec(0.0F, 0.8F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 1.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.posVec(0.0F, 0.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "neck",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(0.0F, -4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, -4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 3.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-1.0F, 4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(2.0F, -6.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-1.0F, 4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(2.0F, -6.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 4.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(4.9976F, -2.1345F, 5.986F), Interpolations.LINEAR),
                  new Keyframe(0.06F, KeyframeAnimations.degreeVec(11.7467F, -0.1184F, 5.6657F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(17.8543F, 1.6787F, 4.9058F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(21.9367F, 4.7375F, 4.8881F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(24.4091F, 8.8293F, 5.2853F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(4.9976F, -2.1345F, 5.986F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_left_shin",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-9.6222F, -4.1109F, -8.6265F), Interpolations.LINEAR),
                  new Keyframe(0.06F, KeyframeAnimations.degreeVec(-14.036F, -5.8664F, -8.0326F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-16.9367F, -6.3551F, -6.3311F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(-13.8568F, -7.2517F, -5.2911F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-6.1764F, -8.1889F, -3.6928F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-9.6222F, -4.1109F, -8.6265F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_left_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(14.238F, 4.9405F, 3.2196F), Interpolations.LINEAR),
                  new Keyframe(0.06F, KeyframeAnimations.degreeVec(11.1264F, 3.8365F, 3.404F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(7.0129F, 2.2856F, 2.9076F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(0.4747F, 0.2604F, 2.5845F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-7.5852F, -2.3677F, 2.1562F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(14.238F, 4.9405F, 3.2196F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hind_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(8.4563F, 5.2425F, -3.4824F), Interpolations.LINEAR),
                  new Keyframe(0.06F, KeyframeAnimations.degreeVec(12.7852F, 2.4313F, -1.4159F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(16.5247F, 0.4799F, 1.3697F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(19.179F, -4.0772F, 1.2262F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(22.0294F, -11.4325F, -1.5509F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(8.4563F, 5.2425F, -3.4824F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hind_right_shin",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-14.2242F, 1.3493F, 5.136F), Interpolations.LINEAR),
                  new Keyframe(0.06F, KeyframeAnimations.degreeVec(-12.0616F, 0.7438F, 2.4147F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-8.7777F, -1.5789F, -1.5364F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(-3.4883F, -0.7747F, -3.6584F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(1.805F, 0.9548F, -4.8792F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(31.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-14.2242F, 1.3493F, 5.136F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hind_right_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(14.1346F, -5.3593F, -2.1541F), Interpolations.LINEAR),
                  new Keyframe(0.06F, KeyframeAnimations.degreeVec(7.0578F, -2.52F, -0.9927F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-0.5476F, 0.5979F, 0.4299F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(-8.0284F, 3.9598F, 1.3551F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-14.4062F, 10.1071F, 1.6611F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(-19.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(14.1346F, -5.3593F, -2.1541F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-0.1926F, 5.6862F, 2.342F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(8.6081F, 1.8099F, -1.0641F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(16.727F, -1.2279F, -3.4817F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(22.0748F, -5.3633F, -6.7269F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(25.9563F, -9.634F, -10.3036F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_right_shin",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(5.7078F, -5.3244F, -5.3354F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(-5.9568F, -0.0051F, 1.5857F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(-14.5065F, 4.4725F, 4.6061F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(-14.9391F, 9.3739F, 7.7325F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-9.788F, 12.7302F, 11.1529F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_right_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(5.5883F, -0.2659F, 2.7979F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(6.2728F, -1.5302F, -0.3927F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(5.6256F, -1.6628F, -2.0265F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(1.3871F, -0.718F, -3.6478F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-5.1768F, 1.6512F, -5.4417F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hind_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(3.8847F, -6.1281F, -0.8752F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(10.5255F, -3.2273F, -1.011F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(16.5786F, -0.458F, -1.3322F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(20.2311F, 5.6268F, 1.5697F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(25.6774F, 10.2905F, 4.5351F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hind_left_shin",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(31.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(9.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-2.0199F, 3.5688F, 0.6427F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(-6.3503F, 2.1066F, 0.561F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(-8.849F, 1.5428F, 1.4971F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(-6.9237F, -2.3388F, 0.8923F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-4.9166F, -4.4962F, 1.1825F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "hind_left_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.12F, KeyframeAnimations.degreeVec(-19.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.24F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(7.4025F, 2.5776F, -0.0882F), Interpolations.LINEAR),
                  new Keyframe(0.36F, KeyframeAnimations.degreeVec(4.003F, 1.2557F, -0.0114F), Interpolations.LINEAR),
                  new Keyframe(0.42F, KeyframeAnimations.degreeVec(-0.5348F, -0.5952F, -0.4371F), Interpolations.LINEAR),
                  new Keyframe(0.48F, KeyframeAnimations.degreeVec(-6.1899F, -3.3211F, -0.8279F), Interpolations.LINEAR),
                  new Keyframe(0.54F, KeyframeAnimations.degreeVec(-12.4321F, -6.5188F, -1.415F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createPrimaryAttack() {
      return Builder.withLength(1.1F)
         .addAnimation(
            "root",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -0.4F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.posVec(0.0F, -0.8F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.posVec(0.0F, -0.3F, -3.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.posVec(0.0F, 0.0F, -2.3F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.posVec(0.0F, 0.0F, -0.7F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(6.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "neck",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(34.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -4.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 4.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(11.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "fore_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.25F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.53F, KeyframeAnimations.degreeVec(11.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.62F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.82F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForPrimaryAttack() {
      return Builder.withLength(2.0F)
         .looping()
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.35F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, -0.2F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }
}

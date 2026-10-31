package dev.vic17usd.spacefabric.client.colossus;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class SulfurColossusAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition PRIMARY_ATTACK = createPrimaryAttack();
   public static final AnimationDefinition ACTION_DEATH = createActionDeath();
   public static final AnimationDefinition ACTION_GROUND_SLAM = createActionGroundSlam();
   public static final AnimationDefinition ACTION_STAGGER = createActionStagger();
   public static final AnimationDefinition ACTION_VENTING = createActionVenting();
   public static final AnimationDefinition ACTION_VOLCANIC_BURST = createActionVolcanicBurst();
   public static final AnimationDefinition IDLE_FOR_PRIMARY_ATTACK = createIdleForPrimaryAttack();
   public static final AnimationDefinition IDLE_FOR_ACTION_DEATH = createIdleForActionDeath();
   public static final AnimationDefinition IDLE_FOR_ACTION_GROUND_SLAM = createIdleForActionGroundSlam();
   public static final AnimationDefinition IDLE_FOR_ACTION_STAGGER = createIdleForActionStagger();
   public static final AnimationDefinition IDLE_FOR_ACTION_VENTING = createIdleForActionVenting();
   public static final AnimationDefinition IDLE_FOR_ACTION_VOLCANIC_BURST = createIdleForActionVolcanicBurst();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{
      PRIMARY_ATTACK, ACTION_DEATH, ACTION_GROUND_SLAM, ACTION_STAGGER, ACTION_VENTING, ACTION_VOLCANIC_BURST
   };
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{
      IDLE_FOR_PRIMARY_ATTACK,
      IDLE_FOR_ACTION_DEATH,
      IDLE_FOR_ACTION_GROUND_SLAM,
      IDLE_FOR_ACTION_STAGGER,
      IDLE_FOR_ACTION_VENTING,
      IDLE_FOR_ACTION_VOLCANIC_BURST
   };

   private SulfurColossusAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(1.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-1.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.1F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(1.6F)
         .looping()
         .addAnimation(
            "body",
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
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(19.3672F, 4.5543F, 9.8876F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(22.8667F, 11.6333F, 9.3915F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(29.3911F, 6.9377F, 12.746F), Interpolations.LINEAR),
                  new Keyframe(0.2375F, KeyframeAnimations.degreeVec(29.5212F, 7.6191F, 15.3869F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(4.3615F, 13.0057F, 19.2475F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(4.416F, -1.7839F, 7.9385F), Interpolations.LINEAR),
                  new Keyframe(1.5125F, KeyframeAnimations.degreeVec(14.8447F, 2.3529F, 8.2476F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(19.3672F, 4.5543F, 9.8876F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-35.6206F, -15.5118F, -15.1696F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(-37.1128F, -18.0465F, -14.7626F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(-36.7728F, -20.4874F, -17.4403F), Interpolations.LINEAR),
                  new Keyframe(0.2375F, KeyframeAnimations.degreeVec(-29.9582F, -21.3787F, -26.1662F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(44.7499F, -0.221F, -40.1482F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-25.3767F, -7.3597F, -10.075F), Interpolations.LINEAR),
                  new Keyframe(1.5125F, KeyframeAnimations.degreeVec(-34.6316F, -12.4555F, -11.0747F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-35.6206F, -15.5118F, -15.1696F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(14.5037F, -0.4082F, -5.4438F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(17.228F, -3.6086F, -6.7047F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(23.2705F, -2.8535F, -5.9252F), Interpolations.LINEAR),
                  new Keyframe(0.2375F, KeyframeAnimations.degreeVec(23.747F, -4.5725F, -7.6699F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(23.0245F, -17.8232F, -18.5409F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(2.9812F, 8.5624F, -3.9856F), Interpolations.LINEAR),
                  new Keyframe(1.5125F, KeyframeAnimations.degreeVec(9.9795F, 1.0424F, -5.5159F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(14.5037F, -0.4082F, -5.4438F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_right_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-28.2022F, 7.1098F, 8.1571F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(-26.4122F, 8.5053F, 9.1451F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(-24.5762F, 7.2568F, 7.2341F), Interpolations.LINEAR),
                  new Keyframe(0.2375F, KeyframeAnimations.degreeVec(-21.5009F, 8.6889F, 9.2595F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-0.1068F, 13.8154F, 18.1746F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.1F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-27.4888F, 1.6114F, 5.5017F), Interpolations.LINEAR),
                  new Keyframe(1.5125F, KeyframeAnimations.degreeVec(-27.8682F, 6.6326F, 8.9723F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-28.2022F, 7.1098F, 8.1571F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(7.6064F, 5.1134F, -7.729F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(13.1131F, 2.9622F, -9.9739F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(13.9343F, 0.5623F, -12.467F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(12.6631F, -5.1311F, -15.255F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(6.2459F, -9.1283F, -20.5994F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.2961F, -16.0602F, -20.1815F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(-26.6276F, 4.7355F, 8.5844F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(-30.9609F, 10.2929F, 13.4328F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-27.6502F, 14.241F, 19.9027F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-18.1853F, 19.2775F, 28.134F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(9.8466F, 15.1251F, 45.3171F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(31.9922F, 5.6751F, 47.497F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(24.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(6.1092F, -4.7579F, 4.8078F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(10.0846F, -1.0242F, 5.2067F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(12.1772F, 0.2526F, 5.206F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(13.3294F, 2.5579F, 6.5962F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(14.8417F, 6.8241F, 10.3263F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(10.6435F, 4.5694F, 16.9541F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_left_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(-28.1092F, -4.6872F, -7.4513F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(-27.8467F, -6.3836F, -8.2556F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(-27.6147F, -6.7659F, -7.8747F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-25.3066F, -8.4689F, -9.8982F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(-17.6115F, -8.8212F, -17.911F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(6.2354F, -6.545F, -34.5998F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "root",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, -4.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_claw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(16.3036F, 1.3516F, 9.7809F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(16.1361F, -4.1042F, 9.3796F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(9.8191F, 0.0283F, 11.4566F), Interpolations.LINEAR),
                  new Keyframe(0.2375F, KeyframeAnimations.degreeVec(5.4854F, -2.9927F, 16.4774F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-42.2328F, 3.8284F, 24.9962F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(17.9394F, 5.9328F, 4.9524F), Interpolations.LINEAR),
                  new Keyframe(1.5125F, KeyframeAnimations.degreeVec(18.343F, 3.7452F, 6.6957F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(16.3036F, 1.3516F, 9.7809F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_claw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(17.7376F, -7.0912F, -3.5246F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(17.1766F, -7.0877F, -7.6585F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(14.4689F, -6.0723F, -11.708F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(10.0134F, -4.888F, -16.2248F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(-6.7244F, -6.3065F, -23.898F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(-22.1785F, -3.806F, -27.3341F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_left_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(22.2228F, 6.3765F, 5.8981F), Interpolations.LINEAR),
                  new Keyframe(0.65F, KeyframeAnimations.degreeVec(18.1558F, 3.9087F, 5.4797F), Interpolations.LINEAR),
                  new Keyframe(0.7F, KeyframeAnimations.degreeVec(15.8348F, 3.0574F, 4.8913F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(12.7454F, 1.8737F, 5.4498F), Interpolations.LINEAR),
                  new Keyframe(0.85F, KeyframeAnimations.degreeVec(4.8969F, -3.7053F, 8.8125F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(-13.6106F, 0.2704F, 18.5536F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_right_foot",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(14.6504F, -2.9624F, -4.9089F), Interpolations.LINEAR),
                  new Keyframe(0.075F, KeyframeAnimations.degreeVec(10.8743F, -0.7426F, -4.5175F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(3.1131F, -0.5663F, -3.4149F), Interpolations.LINEAR),
                  new Keyframe(0.2375F, KeyframeAnimations.degreeVec(0.1936F, 0.463F, -4.091F), Interpolations.LINEAR),
                  new Keyframe(0.4F, KeyframeAnimations.degreeVec(-14.9994F, 9.8761F, -6.4308F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(23.8498F, -7.9732F, -5.173F), Interpolations.LINEAR),
                  new Keyframe(1.5125F, KeyframeAnimations.degreeVec(18.4703F, -4.0054F, -5.8256F), Interpolations.LINEAR),
                  new Keyframe(1.6F, KeyframeAnimations.degreeVec(14.6504F, -2.9624F, -4.9089F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createPrimaryAttack() {
      return Builder.withLength(2.4F)
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(2.0F, -2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-3.0F, 3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.25F, KeyframeAnimations.degreeVec(-2.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(-62.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(28.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(-28.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_claw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.95F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(0.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.3F, KeyframeAnimations.degreeVec(0.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, -4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 5.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionDeath() {
      return Builder.withLength(4.0F)
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.posVec(0.0F, -5.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, -7.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.8F, KeyframeAnimations.degreeVec(13.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(17.0F, 2.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "neck",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(-42.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(-42.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "rear_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, -8.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, -18.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, -25.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, -25.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 25.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 25.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionGroundSlam() {
      return Builder.withLength(4.0F)
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.8F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.95F, KeyframeAnimations.degreeVec(-28.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.95F, KeyframeAnimations.degreeVec(-28.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.7F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.7F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(14.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.1F, KeyframeAnimations.degreeVec(0.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.1F, KeyframeAnimations.degreeVec(0.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(0.0F, -7.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 9.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.7F, KeyframeAnimations.degreeVec(0.0F, 4.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionStagger() {
      return Builder.withLength(1.2F)
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(10.0F, 3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-5.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(-8.0F, -5.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(5.0F, 3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-6.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.18F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-6.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.2F, KeyframeAnimations.degreeVec(0.0F, -12.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.55F, KeyframeAnimations.degreeVec(0.0F, 8.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionVenting() {
      return Builder.withLength(5.0F)
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.4F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.6F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.4F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "vent_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -10.0F), Interpolations.LINEAR),
                  new Keyframe(3.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -6.0F), Interpolations.LINEAR),
                  new Keyframe(4.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "vent_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -3.0F), Interpolations.LINEAR),
                  new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 10.0F), Interpolations.LINEAR),
                  new Keyframe(3.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 6.0F), Interpolations.LINEAR),
                  new Keyframe(4.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.8F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.4F, KeyframeAnimations.degreeVec(0.0F, -32.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.1F, KeyframeAnimations.degreeVec(0.0F, -32.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.4F, KeyframeAnimations.degreeVec(0.0F, 32.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.1F, KeyframeAnimations.degreeVec(0.0F, 32.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionVolcanicBurst() {
      return Builder.withLength(5.0F)
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.6F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.3F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_left_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.4F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "front_right_upper",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.6F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.4F, KeyframeAnimations.degreeVec(4.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.6F, KeyframeAnimations.degreeVec(0.0F, -5.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 7.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.8F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_left",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.4F, KeyframeAnimations.degreeVec(0.0F, -34.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.1F, KeyframeAnimations.degreeVec(0.0F, -34.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "chest_right",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.4F, KeyframeAnimations.degreeVec(0.0F, 34.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.1F, KeyframeAnimations.degreeVec(0.0F, 34.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(4.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForPrimaryAttack() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-1.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.1F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionDeath() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.1F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionGroundSlam() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-1.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.1F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionStagger() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.1F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionVenting() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_base",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.1F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionVolcanicBurst() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-1.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(1.0F, -1.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "jaw",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "tail_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.1F, KeyframeAnimations.degreeVec(0.0F, 2.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }
}

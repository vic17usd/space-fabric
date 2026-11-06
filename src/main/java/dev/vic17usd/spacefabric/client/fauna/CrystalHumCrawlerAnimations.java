package dev.vic17usd.spacefabric.client.fauna;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public final class CrystalHumCrawlerAnimations {
   public static final AnimationDefinition IDLE = createIdle();
   public static final AnimationDefinition LOCOMOTION = createLocomotion();
   public static final AnimationDefinition ACTION_GRAZE = createActionGraze();
   public static final AnimationDefinition ACTION_HIDE = createActionHide();
   public static final AnimationDefinition IDLE_FOR_ACTION_GRAZE = createIdleForActionGraze();
   public static final AnimationDefinition IDLE_FOR_ACTION_HIDE = createIdleForActionHide();
   public static final AnimationDefinition[] ACTIONS = new AnimationDefinition[]{ACTION_GRAZE, ACTION_HIDE};
   public static final AnimationDefinition[] ACTION_IDLE = new AnimationDefinition[]{IDLE_FOR_ACTION_GRAZE, IDLE_FOR_ACTION_HIDE};

   private CrystalHumCrawlerAnimations() {
   }

   private static AnimationDefinition createIdle() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
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
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
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
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
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
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createLocomotion() {
      return Builder.withLength(1.2F)
         .looping()
         .addAnimation(
            "leg_l_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-49.2607F, -17.02F, 21.3542F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(-25.9369F, -11.1997F, 32.5211F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(3.9061F, 0.7909F, 35.0543F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(32.4901F, 12.7867F, 28.9667F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(39.5067F, 21.929F, 9.8311F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-49.2607F, -17.02F, 21.3542F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_front_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(33.7876F, 17.8048F, -4.3325F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(13.0787F, 20.4113F, -30.7758F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-3.7602F, -5.0425F, -41.7667F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-23.0602F, -26.9248F, -20.2806F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-25.448F, -15.2738F, 13.2295F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(33.7876F, 17.8048F, -4.3325F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(33.3556F, -26.548F, -16.1064F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-36.9577F, 26.153F, -29.6168F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-24.1988F, 14.782F, -33.5307F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.8946F, 0.2489F, -35.1362F), Interpolations.LINEAR),
                  new Keyframe(1.05F, KeyframeAnimations.degreeVec(28.3136F, -13.3448F, -30.849F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(33.3556F, -26.548F, -16.1064F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_front_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-18.489F, 24.3701F, -7.3067F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(15.4155F, -28.1288F, 10.5081F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(9.5709F, -23.2469F, 31.2134F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-1.5833F, 1.0985F, 42.4584F), Interpolations.LINEAR),
                  new Keyframe(1.05F, KeyframeAnimations.degreeVec(-17.0616F, 26.0214F, 25.4985F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-18.489F, 24.3701F, -7.3067F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_middle",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(36.179F, 22.7232F, 4.405F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-48.7212F, -18.4231F, 24.7292F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-26.5857F, -11.5033F, 32.9278F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(2.9467F, 0.3798F, 35.229F), Interpolations.LINEAR),
                  new Keyframe(1.05F, KeyframeAnimations.degreeVec(32.4453F, 12.4687F, 28.9321F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(36.179F, 22.7232F, 4.405F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_middle_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.0881F, -13.3464F, 19.2873F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(32.2119F, 21.4792F, -6.944F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(13.5897F, 21.4273F, -31.0988F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-2.9519F, -3.4135F, -42.2449F), Interpolations.LINEAR),
                  new Keyframe(1.05F, KeyframeAnimations.degreeVec(-22.7229F, -26.7909F, -20.8933F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-22.0881F, -13.3464F, 19.2873F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_middle",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-38.7694F, 26.1188F, -31.7439F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(-24.6734F, 15.0719F, -33.8833F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.1174F, 0.5681F, -35.1004F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(28.1788F, -13.1751F, -30.6983F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(30.7737F, -27.7588F, -11.4566F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-38.7694F, 26.1188F, -31.7439F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_middle_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(16.5161F, -29.686F, 12.6393F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(9.9961F, -24.1345F, 31.4709F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-0.9055F, -0.4051F, 42.4604F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-16.7134F, 25.681F, 25.7882F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-17.0873F, 23.6143F, -13.0313F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(16.5161F, -29.686F, 12.6393F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-49.1185F, -19.543F, 27.2519F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(-27.354F, -11.9614F, 33.1776F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(1.7496F, -0.0566F, 35.2029F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(32.2873F, 12.0951F, 28.9073F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(33.1135F, 23.7595F, -1.7583F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-49.1185F, -19.543F, 27.2519F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_rear_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(31.8363F, 24.4589F, -8.6942F), Interpolations.LINEAR),
                  new Keyframe(0.15F, KeyframeAnimations.degreeVec(14.3067F, 22.6717F, -31.0832F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-1.9314F, -1.4939F, -42.4329F), Interpolations.LINEAR),
                  new Keyframe(0.45F, KeyframeAnimations.degreeVec(-22.2295F, -26.4577F, -21.74F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-19.2743F, -11.9366F, 26.493F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(31.8363F, 24.4589F, -8.6942F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(26.6334F, -29.3248F, -3.5495F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-40.3609F, 26.3619F, -33.6218F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-25.1088F, 15.4456F, -34.0924F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-0.6423F, 0.8584F, -34.8842F), Interpolations.LINEAR),
                  new Keyframe(1.05F, KeyframeAnimations.degreeVec(27.9949F, -13.0814F, -30.4945F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(26.6334F, -29.3248F, -3.5495F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_rear_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-14.875F, 22.1023F, -22.6751F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(17.4921F, -31.4113F, 14.2762F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(10.4821F, -25.0744F, 31.4983F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(-0.1882F, -1.8662F, 42.1599F), Interpolations.LINEAR),
                  new Keyframe(1.05F, KeyframeAnimations.degreeVec(-16.3232F, 25.3F, 26.0069F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-14.875F, 22.1023F, -22.6751F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -1.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -1.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -1.0F), Interpolations.LINEAR),
                  new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 1.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "root",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, -1.5F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionGraze() {
      return Builder.withLength(3.0F)
         .looping()
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "mandibles",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(-7.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(5.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(7.0F, 0.0F, -3.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(3.0F, 0.0F, -1.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.75F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.5F, KeyframeAnimations.degreeVec(7.0F, 0.0F, 3.0F), Interpolations.LINEAR),
                  new Keyframe(2.25F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 1.0F), Interpolations.LINEAR),
                  new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createActionHide() {
      return Builder.withLength(2.4F)
         .addAnimation(
            "head",
            new AnimationChannel(
               Targets.POSITION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, -0.5F, 3.5F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.posVec(0.0F, -0.5F, 3.5F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.posVec(0.0F, -0.2F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(10.0F, -5.0F, -8.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-65.0F, -8.0F, -70.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-65.0F, -8.0F, -70.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-35.0F, -4.0F, -35.0F), Interpolations.LINEAR),
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
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(10.0F, 5.0F, 8.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-65.0F, 8.0F, 70.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-65.0F, 8.0F, 70.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-35.0F, 4.0F, 35.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, -35.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, -35.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, -18.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 35.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 35.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 18.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 10.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-12.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-12.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-6.0F, 20.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_front",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-12.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-12.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-6.0F, -20.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_middle",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 10.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-12.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-12.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-6.0F, 20.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_middle",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-12.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-12.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-6.0F, -20.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 10.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-12.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-12.0F, 38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-6.0F, 20.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_rear",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-12.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-12.0F, -38.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-6.0F, -20.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_front_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_front_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_middle_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_middle_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_l_rear_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "leg_r_rear_lower",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.8F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.35F, KeyframeAnimations.degreeVec(-22.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.9F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionGraze() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_l_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .addAnimation(
            "antenna_r_tip",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(3.0F, 0.0F, 2.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-3.0F, 0.0F, -2.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }

   private static AnimationDefinition createIdleForActionHide() {
      return Builder.withLength(2.4F)
         .looping()
         .addAnimation(
            "body",
            new AnimationChannel(
               Targets.ROTATION,
               new Keyframe[]{
                  new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(1.0F, 0.0F, 0.0F), Interpolations.LINEAR),
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
                  new Keyframe(0.6F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(1.8F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                  new Keyframe(2.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
               }
            )
         )
         .build();
   }
}

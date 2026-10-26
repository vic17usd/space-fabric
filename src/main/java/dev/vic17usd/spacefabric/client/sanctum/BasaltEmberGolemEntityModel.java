package dev.vic17usd.spacefabric.client.sanctum;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public final class BasaltEmberGolemEntityModel extends EntityModel<BasaltEmberGolemRenderState> {
   private static final float TEXTURE_WIDTH = 512.0F;
   private static final float TEXTURE_HEIGHT = 512.0F;
   private static final float LOCOMOTION_TIME_SCALE = 6.586768F;
   private final KeyframeAnimation idleAnimation = BasaltEmberGolemAnimations.IDLE.bake(this.root());
   private final KeyframeAnimation locomotionAnimation = BasaltEmberGolemAnimations.LOCOMOTION.bake(this.root());
   private final KeyframeAnimation[] actionAnimations = new KeyframeAnimation[BasaltEmberGolemAnimations.ACTIONS.length];
   private final KeyframeAnimation[] actionIdleAnimations = new KeyframeAnimation[this.actionAnimations.length];

   public BasaltEmberGolemEntityModel() {
      super(createRoot(), RenderTypes::entityCutoutCull);

      for (int i = 0; i < this.actionAnimations.length; i++) {
         this.actionAnimations[i] = BasaltEmberGolemAnimations.ACTIONS[i].bake(this.root());
         this.actionIdleAnimations[i] = BasaltEmberGolemAnimations.ACTION_IDLE[i].bake(this.root());
      }
   }

   public void setupAnim(BasaltEmberGolemRenderState state) {
      super.setupAnim(state);
      long ambientMillis = (long)(state.ageInTicks * 50.0F);
      if (state.actionIndex >= 0 && state.actionIndex < this.actionAnimations.length) {
         this.actionIdleAnimations[state.actionIndex].apply(ambientMillis, 1.0F);
         this.actionAnimations[state.actionIndex].apply(state.actionTimeMillis, 1.0F);
      } else if (state.horizontalTravelSpeed > 0.02F) {
         this.locomotionAnimation.applyWalk(state.horizontalTravelPosition, 1.0F, 6.586768F, 2.5F);
      } else {
         this.idleAnimation.apply(ambientMillis, 1.0F);
      }
   }

   private static ModelPart createRoot() {
      ModelPart rig = createBoneRoot();
      ModelPart root = new ModelPart(List.of(), Map.of("root", rig));
      root.setInitialPose(PartPose.ZERO);
      return root;
   }

   private static ModelPart createBoneRoot() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put("torso", createBoneTorso());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 24.0F, 0.8043F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneTorso() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_torso",
         createCubePart(
            -7.968F,
            -7.9681F,
            -4.98F,
            15.936F,
            15.936F,
            9.96F,
            299.0F,
            146.0F,
            331.0F,
            166.0F,
            347.0F,
            146.0F,
            379.0F,
            166.0F,
            381.0F,
            48.0F,
            401.0F,
            80.0F,
            249.0F,
            48.0F,
            281.0F,
            80.0F,
            345.0F,
            48.0F,
            365.0F,
            80.0F,
            297.0F,
            48.0F,
            329.0F,
            80.0F,
            PartPose.offsetAndRotation(0.0F, -5.976F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_torso",
         createCubePart(
            -2.988F,
            -1.0956F,
            -2.988F,
            5.976F,
            2.1912F,
            5.976F,
            470.0F,
            309.0F,
            482.0F,
            321.0F,
            12.0F,
            333.0F,
            24.0F,
            345.0F,
            312.0F,
            393.0F,
            324.0F,
            397.0F,
            280.0F,
            393.0F,
            292.0F,
            397.0F,
            264.0F,
            393.0F,
            276.0F,
            397.0F,
            296.0F,
            393.0F,
            308.0F,
            397.0F,
            PartPose.offsetAndRotation(0.0F, -13.6452F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_torso",
         createCubePart(
            -2.8884F,
            -2.6394F,
            -0.5976F,
            5.7768F,
            5.2788F,
            1.1952F,
            143.0F,
            413.0F,
            155.0F,
            415.0F,
            157.0F,
            413.0F,
            169.0F,
            415.0F,
            371.0F,
            333.0F,
            373.0F,
            344.0F,
            229.0F,
            333.0F,
            241.0F,
            344.0F,
            367.0F,
            333.0F,
            369.0F,
            344.0F,
            252.0F,
            333.0F,
            264.0F,
            344.0F,
            PartPose.offsetAndRotation(-4.5816F, -10.1592F, -5.2788F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_torso",
         createCubePart(
            -3.1872F,
            -2.8884F,
            -0.6474F,
            6.3744F,
            5.7768F,
            1.2948F,
            151.0F,
            406.0F,
            164.0F,
            409.0F,
            167.0F,
            406.0F,
            180.0F,
            409.0F,
            74.0F,
            333.0F,
            77.0F,
            345.0F,
            420.0F,
            309.0F,
            433.0F,
            321.0F,
            68.0F,
            333.0F,
            71.0F,
            345.0F,
            445.0F,
            309.0F,
            458.0F,
            321.0F,
            PartPose.offsetAndRotation(4.0836F, -9.8604F, -5.3286F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_4_torso",
         createCubePart(
            -1.6434F,
            -3.1872F,
            -0.5478F,
            3.2868F,
            6.3744F,
            1.0956F,
            277.0F,
            413.0F,
            284.0F,
            415.0F,
            286.0F,
            413.0F,
            293.0F,
            415.0F,
            325.0F,
            283.0F,
            327.0F,
            296.0F,
            201.0F,
            283.0F,
            208.0F,
            296.0F,
            321.0F,
            283.0F,
            323.0F,
            296.0F,
            215.0F,
            283.0F,
            222.0F,
            296.0F,
            PartPose.offsetAndRotation(-4.1334F, -4.6812F, -5.2788F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_5_torso",
         createCubePart(
            -3.0378F,
            -2.5398F,
            -0.747F,
            6.0756F,
            5.0796F,
            1.494F,
            247.0F,
            406.0F,
            259.0F,
            409.0F,
            262.0F,
            406.0F,
            274.0F,
            409.0F,
            76.0F,
            357.0F,
            79.0F,
            367.0F,
            429.0F,
            333.0F,
            441.0F,
            343.0F,
            70.0F,
            357.0F,
            73.0F,
            367.0F,
            451.0F,
            333.0F,
            463.0F,
            343.0F,
            PartPose.offsetAndRotation(4.6812F, -3.486F, -5.4282F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_6_torso",
         createCubePart(
            -1.245F,
            -1.9422F,
            -0.5478F,
            2.49F,
            3.8844F,
            1.0956F,
            405.0F,
            413.0F,
            410.0F,
            415.0F,
            412.0F,
            413.0F,
            417.0F,
            415.0F,
            210.0F,
            376.0F,
            212.0F,
            384.0F,
            158.0F,
            376.0F,
            163.0F,
            384.0F,
            206.0F,
            376.0F,
            208.0F,
            384.0F,
            168.0F,
            376.0F,
            173.0F,
            384.0F,
            PartPose.offsetAndRotation(-0.8964F, -0.3984F, -5.1792F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_7_torso",
         createCubePart(
            -0.5478F,
            -3.5358F,
            -3.735F,
            1.0956F,
            7.0716F,
            7.47F,
            282.0F,
            254.0F,
            284.0F,
            269.0F,
            286.0F,
            254.0F,
            288.0F,
            269.0F,
            387.0F,
            254.0F,
            402.0F,
            268.0F,
            464.0F,
            254.0F,
            466.0F,
            268.0F,
            358.0F,
            254.0F,
            373.0F,
            268.0F,
            468.0F,
            254.0F,
            470.0F,
            268.0F,
            PartPose.offsetAndRotation(7.8883F, -3.0876F, 0.6972F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_8_torso",
         createCubePart(
            -0.5478F,
            -3.237F,
            -3.6852F,
            1.0956F,
            6.474F,
            7.3704F,
            290.0F,
            254.0F,
            292.0F,
            269.0F,
            294.0F,
            254.0F,
            296.0F,
            269.0F,
            13.0F,
            283.0F,
            28.0F,
            296.0F,
            345.0F,
            283.0F,
            347.0F,
            296.0F,
            483.0F,
            254.0F,
            498.0F,
            267.0F,
            349.0F,
            283.0F,
            351.0F,
            296.0F,
            PartPose.offsetAndRotation(-7.8983F, -6.474F, 0.7968F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_9_torso",
         createCubePart(
            -2.9382F,
            -3.1872F,
            -0.5478F,
            5.8764F,
            6.3744F,
            1.0956F,
            87.0F,
            413.0F,
            99.0F,
            415.0F,
            101.0F,
            413.0F,
            113.0F,
            415.0F,
            317.0F,
            283.0F,
            319.0F,
            296.0F,
            153.0F,
            283.0F,
            165.0F,
            296.0F,
            313.0F,
            283.0F,
            315.0F,
            296.0F,
            177.0F,
            283.0F,
            189.0F,
            296.0F,
            PartPose.offsetAndRotation(4.6812F, -9.96F, 5.1592F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_10_torso",
         createCubePart(
            -2.8884F,
            -3.6354F,
            -0.5976F,
            5.7768F,
            7.2708F,
            1.1952F,
            115.0F,
            413.0F,
            127.0F,
            415.0F,
            129.0F,
            413.0F,
            141.0F,
            415.0F,
            278.0F,
            254.0F,
            280.0F,
            269.0F,
            98.0F,
            254.0F,
            110.0F,
            269.0F,
            274.0F,
            254.0F,
            276.0F,
            269.0F,
            122.0F,
            254.0F,
            134.0F,
            269.0F,
            PartPose.offsetAndRotation(-4.6812F, -9.1632F, 5.1991F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_11_torso",
         createCubePart(
            -3.6852F,
            -2.241F,
            -0.5976F,
            7.3704F,
            4.482F,
            1.1952F,
            479.0F,
            406.0F,
            494.0F,
            408.0F,
            2.0F,
            413.0F,
            17.0F,
            415.0F,
            497.0F,
            357.0F,
            499.0F,
            366.0F,
            221.0F,
            357.0F,
            236.0F,
            366.0F,
            493.0F,
            357.0F,
            495.0F,
            366.0F,
            245.0F,
            357.0F,
            260.0F,
            366.0F,
            PartPose.offsetAndRotation(-1.7928F, -1.5936F, 5.1294F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_12_torso",
         createCubePart(
            -0.65F,
            -2.1F,
            -0.8F,
            1.3F,
            2.1F,
            2.4F,
            218.0F,
            393.0F,
            221.0F,
            398.0F,
            224.0F,
            393.0F,
            227.0F,
            398.0F,
            337.0F,
            393.0F,
            342.0F,
            397.0F,
            432.0F,
            393.0F,
            435.0F,
            397.0F,
            328.0F,
            393.0F,
            333.0F,
            397.0F,
            438.0F,
            393.0F,
            441.0F,
            397.0F,
            PartPose.offsetAndRotation(0.0F, -2.8701F, 4.7957F, -0.314159F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_13_torso",
         createCubePart(
            -0.8F,
            -2.1F,
            -0.8F,
            1.6F,
            2.1F,
            2.4F,
            230.0F,
            393.0F,
            233.0F,
            398.0F,
            236.0F,
            393.0F,
            239.0F,
            398.0F,
            355.0F,
            393.0F,
            360.0F,
            397.0F,
            444.0F,
            393.0F,
            447.0F,
            397.0F,
            346.0F,
            393.0F,
            351.0F,
            397.0F,
            450.0F,
            393.0F,
            453.0F,
            397.0F,
            PartPose.offsetAndRotation(0.0F, -5.4701F, 4.7957F, -0.314159F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_14_torso",
         createCubePart(
            -0.6F,
            -2.1F,
            -0.8F,
            1.2F,
            2.1F,
            2.4F,
            254.0F,
            393.0F,
            256.0F,
            398.0F,
            258.0F,
            393.0F,
            260.0F,
            398.0F,
            373.0F,
            393.0F,
            378.0F,
            397.0F,
            492.0F,
            393.0F,
            494.0F,
            397.0F,
            364.0F,
            393.0F,
            369.0F,
            397.0F,
            496.0F,
            393.0F,
            498.0F,
            397.0F,
            PartPose.offsetAndRotation(0.0F, -8.0701F, 4.7957F, -0.314159F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_15_torso",
         createCubePart(
            -0.75F,
            -2.1F,
            -0.8F,
            1.5F,
            2.1F,
            2.4F,
            242.0F,
            393.0F,
            245.0F,
            398.0F,
            248.0F,
            393.0F,
            251.0F,
            398.0F,
            391.0F,
            393.0F,
            396.0F,
            397.0F,
            456.0F,
            393.0F,
            459.0F,
            397.0F,
            382.0F,
            393.0F,
            387.0F,
            397.0F,
            462.0F,
            393.0F,
            465.0F,
            397.0F,
            PartPose.offsetAndRotation(0.0F, -10.6701F, 4.7957F, -0.314159F, 0.0F, 0.0F)
         )
      );
      children.put("pelvis", createBonePelvis());
      children.put("head", createBoneHead());
      children.put("upper_arm_l", createBoneUpperArmL());
      children.put("upper_arm_r", createBoneUpperArmR());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, -29.8799F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBonePelvis() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_pelvis",
         createCubePart(
            -6.3744F,
            -2.988F,
            -4.482F,
            12.7488F,
            5.976F,
            8.964F,
            157.0F,
            187.0F,
            182.0F,
            205.0F,
            198.0F,
            187.0F,
            223.0F,
            205.0F,
            12.0F,
            309.0F,
            30.0F,
            321.0F,
            379.0F,
            283.0F,
            404.0F,
            295.0F,
            453.0F,
            283.0F,
            471.0F,
            295.0F,
            416.0F,
            283.0F,
            441.0F,
            295.0F,
            PartPose.offsetAndRotation(0.0F, 1.9919F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_pelvis",
         createCubePart(
            -1.743F,
            -2.5896F,
            -0.5976F,
            3.486F,
            5.1792F,
            1.1952F,
            295.0F,
            413.0F,
            302.0F,
            415.0F,
            304.0F,
            413.0F,
            311.0F,
            415.0F,
            86.0F,
            357.0F,
            88.0F,
            367.0F,
            473.0F,
            333.0F,
            480.0F,
            343.0F,
            82.0F,
            357.0F,
            84.0F,
            367.0F,
            487.0F,
            333.0F,
            494.0F,
            343.0F,
            PartPose.offsetAndRotation(4.482F, 1.7927F, -4.5816F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_pelvis",
         createCubePart(
            -2.0418F,
            -2.2908F,
            -0.5478F,
            4.0836F,
            4.5816F,
            1.0956F,
            197.0F,
            413.0F,
            205.0F,
            415.0F,
            207.0F,
            413.0F,
            215.0F,
            415.0F,
            505.0F,
            357.0F,
            507.0F,
            366.0F,
            449.0F,
            357.0F,
            457.0F,
            366.0F,
            501.0F,
            357.0F,
            503.0F,
            366.0F,
            465.0F,
            357.0F,
            473.0F,
            366.0F,
            PartPose.offsetAndRotation(-3.7848F, 2.2907F, 4.482F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("thigh_l", createBoneThighL());
      children.put("thigh_r", createBoneThighR());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 0.996F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneThighL() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_thigh_l",
         createCubePart(
            -3.1872F,
            -5.229F,
            -3.8844F,
            6.3744F,
            10.458F,
            7.7688F,
            247.0F,
            223.0F,
            260.0F,
            239.0F,
            273.0F,
            223.0F,
            286.0F,
            239.0F,
            96.0F,
            146.0F,
            112.0F,
            167.0F,
            192.0F,
            146.0F,
            205.0F,
            167.0F,
            64.0F,
            146.0F,
            80.0F,
            167.0F,
            218.0F,
            146.0F,
            231.0F,
            167.0F,
            PartPose.offsetAndRotation(0.0F, 4.482F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_thigh_l",
         createCubePart(
            -1.3944F,
            -2.3904F,
            -0.5478F,
            2.7888F,
            4.7808F,
            1.0956F,
            329.0F,
            413.0F,
            335.0F,
            415.0F,
            337.0F,
            413.0F,
            343.0F,
            415.0F,
            94.0F,
            357.0F,
            96.0F,
            367.0F,
            6.0F,
            357.0F,
            12.0F,
            367.0F,
            90.0F,
            357.0F,
            92.0F,
            367.0F,
            18.0F,
            357.0F,
            24.0F,
            367.0F,
            PartPose.offsetAndRotation(1.5936F, 2.49F, -4.0239F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_thigh_l",
         createCubePart(
            -0.5976F,
            -2.0916F,
            -3.9244F,
            1.1952F,
            4.1832F,
            7.8488F,
            351.0F,
            223.0F,
            353.0F,
            239.0F,
            355.0F,
            223.0F,
            357.0F,
            239.0F,
            40.0F,
            376.0F,
            56.0F,
            384.0F,
            190.0F,
            376.0F,
            192.0F,
            384.0F,
            16.0F,
            376.0F,
            32.0F,
            384.0F,
            194.0F,
            376.0F,
            196.0F,
            384.0F,
            PartPose.offsetAndRotation(3.0677F, 1.3944F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("shin_l", createBoneShinL());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(4.3824F, 3.4859F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneShinL() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_shin_l",
         createCubePart(
            -2.988F,
            -6.225F,
            -3.6852F,
            5.976F,
            12.45F,
            7.3704F,
            146.0F,
            254.0F,
            158.0F,
            269.0F,
            170.0F,
            254.0F,
            182.0F,
            269.0F,
            366.0F,
            100.0F,
            381.0F,
            125.0F,
            456.0F,
            100.0F,
            468.0F,
            125.0F,
            336.0F,
            100.0F,
            351.0F,
            125.0F,
            480.0F,
            100.0F,
            492.0F,
            125.0F,
            PartPose.offsetAndRotation(0.0F, 5.976F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_shin_l",
         createCubePart(
            -3.237F,
            -1.8426F,
            -0.747F,
            6.474F,
            3.6852F,
            1.494F,
            183.0F,
            406.0F,
            196.0F,
            409.0F,
            199.0F,
            406.0F,
            212.0F,
            409.0F,
            95.0F,
            393.0F,
            98.0F,
            400.0F,
            351.0F,
            376.0F,
            364.0F,
            383.0F,
            89.0F,
            393.0F,
            92.0F,
            400.0F,
            371.0F,
            376.0F,
            384.0F,
            383.0F,
            PartPose.offsetAndRotation(0.0F, 0.5976F, -3.9342F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_shin_l",
         createCubePart(
            -1.1454F,
            -3.237F,
            -0.5478F,
            2.3308F,
            6.474F,
            1.0956F,
            377.0F,
            413.0F,
            382.0F,
            415.0F,
            384.0F,
            413.0F,
            389.0F,
            415.0F,
            341.0F,
            283.0F,
            343.0F,
            296.0F,
            253.0F,
            283.0F,
            258.0F,
            296.0F,
            337.0F,
            283.0F,
            339.0F,
            296.0F,
            263.0F,
            283.0F,
            268.0F,
            296.0F,
            PartPose.offsetAndRotation(1.8426F, 7.6692F, -3.8147F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_shin_l",
         createCubePart(
            -0.4731F,
            -3.0378F,
            -3.4362F,
            0.9462F,
            6.0756F,
            6.8724F,
            440.0F,
            254.0F,
            442.0F,
            268.0F,
            444.0F,
            254.0F,
            446.0F,
            268.0F,
            342.0F,
            309.0F,
            356.0F,
            321.0F,
            80.0F,
            333.0F,
            82.0F,
            345.0F,
            316.0F,
            309.0F,
            330.0F,
            321.0F,
            84.0F,
            333.0F,
            86.0F,
            345.0F,
            PartPose.offsetAndRotation(2.8386F, 7.5696F, 0.1992F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_4_shin_l",
         createCubePart(
            -1.3944F,
            -3.2868F,
            -0.5478F,
            2.7888F,
            6.5736F,
            1.0956F,
            313.0F,
            413.0F,
            319.0F,
            415.0F,
            321.0F,
            413.0F,
            327.0F,
            415.0F,
            333.0F,
            283.0F,
            335.0F,
            296.0F,
            229.0F,
            283.0F,
            235.0F,
            296.0F,
            329.0F,
            283.0F,
            331.0F,
            296.0F,
            241.0F,
            283.0F,
            247.0F,
            296.0F,
            PartPose.offsetAndRotation(1.494F, 6.972F, 3.8844F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("foot_l", createBoneFootL());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 9.462F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneFootL() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_foot_l",
         createCubePart(
            -3.735F,
            -2.1414F,
            -4.98F,
            7.47F,
            4.2828F,
            9.96F,
            395.0F,
            146.0F,
            410.0F,
            166.0F,
            425.0F,
            146.0F,
            440.0F,
            166.0F,
            134.0F,
            357.0F,
            154.0F,
            366.0F,
            269.0F,
            357.0F,
            284.0F,
            366.0F,
            105.0F,
            357.0F,
            125.0F,
            366.0F,
            293.0F,
            357.0F,
            308.0F,
            366.0F,
            PartPose.offsetAndRotation(0.0F, 1.9422F, -0.996F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 11.8524F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneThighR() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_thigh_r",
         createCubePart(
            -3.1872F,
            -5.229F,
            -3.8844F,
            6.3744F,
            10.458F,
            7.7688F,
            299.0F,
            223.0F,
            312.0F,
            239.0F,
            325.0F,
            223.0F,
            338.0F,
            239.0F,
            160.0F,
            146.0F,
            176.0F,
            167.0F,
            244.0F,
            146.0F,
            257.0F,
            167.0F,
            128.0F,
            146.0F,
            144.0F,
            167.0F,
            270.0F,
            146.0F,
            283.0F,
            167.0F,
            PartPose.offsetAndRotation(0.0F, 4.482F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_thigh_r",
         createCubePart(
            -1.3944F,
            -3.5358F,
            -0.5478F,
            2.7888F,
            7.0716F,
            1.0956F,
            361.0F,
            413.0F,
            367.0F,
            415.0F,
            369.0F,
            413.0F,
            375.0F,
            415.0F,
            460.0F,
            254.0F,
            462.0F,
            268.0F,
            416.0F,
            254.0F,
            422.0F,
            268.0F,
            456.0F,
            254.0F,
            458.0F,
            268.0F,
            428.0F,
            254.0F,
            434.0F,
            268.0F,
            PartPose.offsetAndRotation(-1.494F, 3.5856F, -4.0239F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_thigh_r",
         createCubePart(
            -0.5976F,
            -2.0916F,
            -3.9244F,
            1.1952F,
            4.1832F,
            7.8488F,
            359.0F,
            223.0F,
            361.0F,
            239.0F,
            363.0F,
            223.0F,
            365.0F,
            239.0F,
            88.0F,
            376.0F,
            104.0F,
            384.0F,
            198.0F,
            376.0F,
            200.0F,
            384.0F,
            64.0F,
            376.0F,
            80.0F,
            384.0F,
            202.0F,
            376.0F,
            204.0F,
            384.0F,
            PartPose.offsetAndRotation(-3.0677F, 1.3944F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("shin_r", createBoneShinR());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-4.3824F, 3.4859F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneShinR() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_shin_r",
         createCubePart(
            -2.988F,
            -6.225F,
            -3.6852F,
            5.976F,
            12.45F,
            7.3704F,
            194.0F,
            254.0F,
            206.0F,
            269.0F,
            218.0F,
            254.0F,
            230.0F,
            269.0F,
            426.0F,
            100.0F,
            441.0F,
            125.0F,
            12.0F,
            146.0F,
            24.0F,
            171.0F,
            396.0F,
            100.0F,
            411.0F,
            125.0F,
            36.0F,
            146.0F,
            48.0F,
            171.0F,
            PartPose.offsetAndRotation(0.0F, 5.976F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_shin_r",
         createCubePart(
            -3.237F,
            -2.1912F,
            -0.747F,
            6.474F,
            4.3824F,
            1.494F,
            215.0F,
            406.0F,
            228.0F,
            409.0F,
            231.0F,
            406.0F,
            244.0F,
            409.0F,
            487.0F,
            357.0F,
            490.0F,
            366.0F,
            365.0F,
            357.0F,
            378.0F,
            366.0F,
            481.0F,
            357.0F,
            484.0F,
            366.0F,
            387.0F,
            357.0F,
            400.0F,
            366.0F,
            PartPose.offsetAndRotation(0.0F, 1.0956F, -3.9342F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_shin_r",
         createCubePart(
            -1.1854F,
            -2.7888F,
            -0.5478F,
            2.3308F,
            5.5776F,
            1.0956F,
            391.0F,
            413.0F,
            396.0F,
            415.0F,
            398.0F,
            413.0F,
            403.0F,
            415.0F,
            387.0F,
            333.0F,
            389.0F,
            344.0F,
            331.0F,
            333.0F,
            336.0F,
            344.0F,
            383.0F,
            333.0F,
            385.0F,
            344.0F,
            341.0F,
            333.0F,
            346.0F,
            344.0F,
            PartPose.offsetAndRotation(-1.8426F, 8.2668F, -3.8147F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_shin_r",
         createCubePart(
            -0.4731F,
            -3.0378F,
            -3.4362F,
            0.9462F,
            6.0756F,
            6.8724F,
            448.0F,
            254.0F,
            450.0F,
            268.0F,
            452.0F,
            254.0F,
            454.0F,
            268.0F,
            394.0F,
            309.0F,
            408.0F,
            321.0F,
            88.0F,
            333.0F,
            90.0F,
            345.0F,
            368.0F,
            309.0F,
            382.0F,
            321.0F,
            92.0F,
            333.0F,
            94.0F,
            345.0F,
            PartPose.offsetAndRotation(-2.8386F, 7.5696F, 0.1992F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_4_shin_r",
         createCubePart(
            -1.494F,
            -2.8386F,
            -0.5478F,
            2.988F,
            5.6772F,
            1.0956F,
            345.0F,
            413.0F,
            351.0F,
            415.0F,
            353.0F,
            413.0F,
            359.0F,
            415.0F,
            379.0F,
            333.0F,
            381.0F,
            344.0F,
            307.0F,
            333.0F,
            313.0F,
            344.0F,
            375.0F,
            333.0F,
            377.0F,
            344.0F,
            319.0F,
            333.0F,
            325.0F,
            344.0F,
            PartPose.offsetAndRotation(-1.3944F, 6.1752F, 3.8844F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("foot_r", createBoneFootR());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 9.462F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneFootR() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_foot_r",
         createCubePart(
            -3.735F,
            -2.1414F,
            -4.98F,
            7.47F,
            4.2828F,
            9.96F,
            455.0F,
            146.0F,
            470.0F,
            166.0F,
            15.0F,
            187.0F,
            30.0F,
            207.0F,
            192.0F,
            357.0F,
            212.0F,
            366.0F,
            317.0F,
            357.0F,
            332.0F,
            366.0F,
            163.0F,
            357.0F,
            183.0F,
            366.0F,
            341.0F,
            357.0F,
            356.0F,
            366.0F,
            PartPose.offsetAndRotation(0.0F, 1.9422F, -0.996F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 11.8524F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneHead() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_head",
         createCubePart(
            -4.98F,
            -6.2748F,
            -4.6812F,
            9.96F,
            12.5496F,
            9.3624F,
            85.0F,
            187.0F,
            105.0F,
            206.0F,
            121.0F,
            187.0F,
            141.0F,
            206.0F,
            301.0F,
            100.0F,
            320.0F,
            125.0F,
            194.0F,
            100.0F,
            214.0F,
            125.0F,
            266.0F,
            100.0F,
            285.0F,
            125.0F,
            230.0F,
            100.0F,
            250.0F,
            125.0F,
            PartPose.offsetAndRotation(0.0F, -5.6772F, -0.3984F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_head",
         createCubePart(
            -4.3824F,
            -1.3944F,
            -0.249F,
            8.7648F,
            2.7888F,
            0.498F,
            489.0F,
            413.0F,
            507.0F,
            414.0F,
            2.0F,
            417.0F,
            20.0F,
            418.0F,
            192.0F,
            393.0F,
            193.0F,
            399.0F,
            141.0F,
            393.0F,
            159.0F,
            399.0F,
            189.0F,
            393.0F,
            190.0F,
            399.0F,
            165.0F,
            393.0F,
            183.0F,
            399.0F,
            PartPose.offsetAndRotation(0.0F, -4.8804F, -5.1593F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_head",
         createCubePart(
            -5.3286F,
            -0.7968F,
            -0.5976F,
            10.6572F,
            1.5936F,
            1.1952F,
            285.0F,
            406.0F,
            306.0F,
            408.0F,
            308.0F,
            406.0F,
            329.0F,
            408.0F,
            281.0F,
            406.0F,
            283.0F,
            409.0F,
            31.0F,
            406.0F,
            52.0F,
            409.0F,
            277.0F,
            406.0F,
            279.0F,
            409.0F,
            55.0F,
            406.0F,
            76.0F,
            409.0F,
            PartPose.offsetAndRotation(0.0F, -6.6931F, -5.478F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_head",
         createCubePart(
            -1.3197F,
            -2.5896F,
            -1.2699F,
            2.6394F,
            5.1792F,
            2.5398F,
            198.0F,
            393.0F,
            203.0F,
            398.0F,
            208.0F,
            393.0F,
            213.0F,
            398.0F,
            60.0F,
            357.0F,
            65.0F,
            367.0F,
            40.0F,
            357.0F,
            45.0F,
            367.0F,
            30.0F,
            357.0F,
            35.0F,
            367.0F,
            50.0F,
            357.0F,
            55.0F,
            367.0F,
            PartPose.offsetAndRotation(0.0F, -2.3406F, -6.1354F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_4_head",
         createCubePart(
            -0.8964F,
            -2.7292F,
            -0.3237F,
            1.7928F,
            5.4184F,
            0.6474F,
            62.0F,
            417.0F,
            66.0F,
            418.0F,
            68.0F,
            417.0F,
            72.0F,
            418.0F,
            418.0F,
            333.0F,
            419.0F,
            344.0F,
            351.0F,
            333.0F,
            355.0F,
            344.0F,
            415.0F,
            333.0F,
            416.0F,
            344.0F,
            359.0F,
            333.0F,
            363.0F,
            344.0F,
            PartPose.offsetAndRotation(-0.8964F, -9.2628F, -5.1294F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_5_head",
         createCubePart(
            -1.9422F,
            -0.3237F,
            -4.8804F,
            3.8844F,
            0.6474F,
            9.7608F,
            45.0F,
            187.0F,
            53.0F,
            207.0F,
            61.0F,
            187.0F,
            69.0F,
            207.0F,
            467.0F,
            413.0F,
            487.0F,
            414.0F,
            22.0F,
            417.0F,
            30.0F,
            418.0F,
            445.0F,
            413.0F,
            465.0F,
            414.0F,
            32.0F,
            417.0F,
            40.0F,
            418.0F,
            PartPose.offsetAndRotation(2.2908F, -11.8524F, -0.3486F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_6_head",
         createCubePart(
            -0.3486F,
            -3.8844F,
            -3.8844F,
            0.6972F,
            7.7688F,
            7.7688F,
            383.0F,
            223.0F,
            384.0F,
            239.0F,
            392.0F,
            223.0F,
            393.0F,
            239.0F,
            451.0F,
            187.0F,
            467.0F,
            203.0F,
            386.0F,
            223.0F,
            387.0F,
            239.0F,
            419.0F,
            187.0F,
            435.0F,
            203.0F,
            389.0F,
            223.0F,
            390.0F,
            239.0F,
            PartPose.offsetAndRotation(5.0198F, -7.8684F, 0.498F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_7_head",
         createCubePart(
            -1.9422F,
            -4.5318F,
            -0.3486F,
            3.8844F,
            9.0636F,
            0.6972F,
            42.0F,
            417.0F,
            50.0F,
            418.0F,
            52.0F,
            417.0F,
            60.0F,
            418.0F,
            274.0F,
            187.0F,
            275.0F,
            205.0F,
            239.0F,
            187.0F,
            247.0F,
            205.0F,
            271.0F,
            187.0F,
            272.0F,
            205.0F,
            255.0F,
            187.0F,
            263.0F,
            205.0F,
            PartPose.offsetAndRotation(1.6932F, -6.8724F, 4.2828F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_8_head",
         createCubePart(
            -0.8217F,
            -0.996F,
            -0.1892F,
            1.6434F,
            1.992F,
            0.3785F,
            74.0F,
            417.0F,
            77.0F,
            418.0F,
            79.0F,
            417.0F,
            82.0F,
            418.0F,
            21.0F,
            406.0F,
            22.0F,
            410.0F,
            468.0F,
            393.0F,
            471.0F,
            397.0F,
            18.0F,
            406.0F,
            19.0F,
            410.0F,
            474.0F,
            393.0F,
            477.0F,
            397.0F,
            PartPose.offsetAndRotation(2.7888F, -4.7808F, -5.5079F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_9_head",
         createCubePart(
            -0.8217F,
            -0.996F,
            -0.1892F,
            1.6434F,
            1.992F,
            0.3785F,
            84.0F,
            417.0F,
            87.0F,
            418.0F,
            89.0F,
            417.0F,
            92.0F,
            418.0F,
            27.0F,
            406.0F,
            28.0F,
            410.0F,
            480.0F,
            393.0F,
            483.0F,
            397.0F,
            24.0F,
            406.0F,
            25.0F,
            410.0F,
            486.0F,
            393.0F,
            489.0F,
            397.0F,
            PartPose.offsetAndRotation(-2.7888F, -4.7808F, -5.5079F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_10_head",
         createCubePart(
            -0.9F,
            -3.4F,
            -0.9F,
            1.8F,
            3.4F,
            1.8F,
            416.0F,
            393.0F,
            420.0F,
            397.0F,
            424.0F,
            393.0F,
            428.0F,
            397.0F,
            81.0F,
            393.0F,
            85.0F,
            400.0F,
            65.0F,
            393.0F,
            69.0F,
            400.0F,
            57.0F,
            393.0F,
            61.0F,
            400.0F,
            73.0F,
            393.0F,
            77.0F,
            400.0F,
            PartPose.offsetAndRotation(3.5F, -10.2761F, 2.7957F, -0.558505F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_11_head",
         createCubePart(
            -0.35F,
            -2.0F,
            -0.4F,
            0.7F,
            2.0F,
            0.8F,
            439.0F,
            413.0F,
            440.0F,
            415.0F,
            442.0F,
            413.0F,
            443.0F,
            415.0F,
            2.0F,
            406.0F,
            4.0F,
            410.0F,
            12.0F,
            406.0F,
            13.0F,
            410.0F,
            508.0F,
            393.0F,
            510.0F,
            397.0F,
            15.0F,
            406.0F,
            16.0F,
            410.0F,
            PartPose.offsetAndRotation(3.5F, -12.3761F, 4.1457F, -0.558505F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_12_head",
         createCubePart(
            -0.9F,
            -3.4F,
            -0.9F,
            1.8F,
            3.4F,
            1.8F,
            400.0F,
            393.0F,
            404.0F,
            397.0F,
            408.0F,
            393.0F,
            412.0F,
            397.0F,
            49.0F,
            393.0F,
            53.0F,
            400.0F,
            33.0F,
            393.0F,
            37.0F,
            400.0F,
            25.0F,
            393.0F,
            29.0F,
            400.0F,
            41.0F,
            393.0F,
            45.0F,
            400.0F,
            PartPose.offsetAndRotation(-3.5F, -10.2761F, 2.7957F, -0.558505F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_13_head",
         createCubePart(
            -0.35F,
            -2.0F,
            -0.4F,
            0.7F,
            2.0F,
            0.8F,
            433.0F,
            413.0F,
            434.0F,
            415.0F,
            436.0F,
            413.0F,
            437.0F,
            415.0F,
            504.0F,
            393.0F,
            506.0F,
            397.0F,
            6.0F,
            406.0F,
            7.0F,
            410.0F,
            500.0F,
            393.0F,
            502.0F,
            397.0F,
            9.0F,
            406.0F,
            10.0F,
            410.0F,
            PartPose.offsetAndRotation(-3.5F, -12.3761F, 4.1457F, -0.558505F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, -13.944F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneUpperArmL() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_upper_arm_l",
         createCubePart(
            -3.5856F,
            -7.47F,
            -3.984F,
            7.1712F,
            14.94F,
            7.968F,
            135.0F,
            223.0F,
            149.0F,
            239.0F,
            163.0F,
            223.0F,
            177.0F,
            239.0F,
            449.0F,
            48.0F,
            465.0F,
            78.0F,
            80.0F,
            100.0F,
            94.0F,
            130.0F,
            417.0F,
            48.0F,
            433.0F,
            78.0F,
            108.0F,
            100.0F,
            122.0F,
            130.0F,
            PartPose.offsetAndRotation(4.5816F, 4.98F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_upper_arm_l",
         createCubePart(
            -3.8346F,
            -2.0418F,
            -0.6474F,
            7.6692F,
            4.0836F,
            1.2948F,
            79.0F,
            406.0F,
            94.0F,
            409.0F,
            97.0F,
            406.0F,
            112.0F,
            409.0F,
            184.0F,
            376.0F,
            187.0F,
            384.0F,
            112.0F,
            376.0F,
            127.0F,
            384.0F,
            178.0F,
            376.0F,
            181.0F,
            384.0F,
            135.0F,
            376.0F,
            150.0F,
            384.0F,
            PartPose.offsetAndRotation(4.5816F, -0.3486F, -4.1334F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_upper_arm_l",
         createCubePart(
            -0.5976F,
            -3.237F,
            -3.735F,
            1.1952F,
            6.474F,
            7.47F,
            314.0F,
            254.0F,
            316.0F,
            269.0F,
            318.0F,
            254.0F,
            320.0F,
            269.0F,
            125.0F,
            283.0F,
            140.0F,
            296.0F,
            361.0F,
            283.0F,
            363.0F,
            296.0F,
            97.0F,
            283.0F,
            112.0F,
            296.0F,
            365.0F,
            283.0F,
            367.0F,
            296.0F,
            PartPose.offsetAndRotation(8.217F, 0.996F, 0.2988F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_upper_arm_l",
         createCubePart(
            -3.8844F,
            -0.3984F,
            -4.3326F,
            7.7688F,
            0.7968F,
            8.6652F,
            291.0F,
            187.0F,
            307.0F,
            204.0F,
            323.0F,
            187.0F,
            339.0F,
            204.0F,
            350.0F,
            406.0F,
            367.0F,
            408.0F,
            407.0F,
            406.0F,
            423.0F,
            408.0F,
            331.0F,
            406.0F,
            348.0F,
            408.0F,
            425.0F,
            406.0F,
            441.0F,
            408.0F,
            PartPose.offsetAndRotation(4.5816F, -2.5398F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_4_upper_arm_l",
         createCubePart(
            -1.2948F,
            -1.7928F,
            -3.2868F,
            2.5896F,
            3.5856F,
            6.5736F,
            273.0F,
            283.0F,
            278.0F,
            296.0F,
            283.0F,
            283.0F,
            288.0F,
            296.0F,
            411.0F,
            376.0F,
            424.0F,
            383.0F,
            471.0F,
            376.0F,
            476.0F,
            383.0F,
            391.0F,
            376.0F,
            404.0F,
            383.0F,
            481.0F,
            376.0F,
            486.0F,
            383.0F,
            PartPose.offsetAndRotation(1.1952F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("forearm_l", createBoneForearmL());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(7.8684F, -10.956F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneForearmL() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_forearm_l",
         createCubePart(
            -3.3864F,
            -8.964F,
            -3.7848F,
            6.7728F,
            17.928F,
            7.5696F,
            468.0F,
            223.0F,
            482.0F,
            238.0F,
            14.0F,
            254.0F,
            28.0F,
            269.0F,
            45.0F,
            48.0F,
            60.0F,
            84.0F,
            135.0F,
            48.0F,
            149.0F,
            84.0F,
            15.0F,
            48.0F,
            30.0F,
            84.0F,
            163.0F,
            48.0F,
            177.0F,
            84.0F,
            PartPose.offsetAndRotation(0.0F, 8.7647F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_forearm_l",
         createCubePart(
            -2.7888F,
            -2.1414F,
            -0.5976F,
            5.5776F,
            4.2828F,
            1.1952F,
            171.0F,
            413.0F,
            182.0F,
            415.0F,
            184.0F,
            413.0F,
            195.0F,
            415.0F,
            6.0F,
            376.0F,
            8.0F,
            385.0F,
            409.0F,
            357.0F,
            420.0F,
            366.0F,
            2.0F,
            376.0F,
            4.0F,
            385.0F,
            429.0F,
            357.0F,
            440.0F,
            366.0F,
            PartPose.offsetAndRotation(-0.1992F, 2.6891F, -3.9641F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_forearm_l",
         createCubePart(
            -0.5478F,
            -3.237F,
            -3.8346F,
            1.0956F,
            6.474F,
            7.6692F,
            306.0F,
            254.0F,
            308.0F,
            269.0F,
            310.0F,
            254.0F,
            312.0F,
            269.0F,
            69.0F,
            283.0F,
            84.0F,
            296.0F,
            353.0F,
            283.0F,
            355.0F,
            296.0F,
            41.0F,
            283.0F,
            56.0F,
            296.0F,
            357.0F,
            283.0F,
            359.0F,
            296.0F,
            PartPose.offsetAndRotation(3.237F, 11.0555F, 0.0996F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_forearm_l",
         createCubePart(
            -2.0916F,
            -3.8346F,
            -0.5478F,
            4.1832F,
            7.6692F,
            1.0956F,
            217.0F,
            413.0F,
            225.0F,
            415.0F,
            227.0F,
            413.0F,
            235.0F,
            415.0F,
            302.0F,
            254.0F,
            304.0F,
            269.0F,
            242.0F,
            254.0F,
            250.0F,
            269.0F,
            298.0F,
            254.0F,
            300.0F,
            269.0F,
            258.0F,
            254.0F,
            266.0F,
            269.0F,
            PartPose.offsetAndRotation(1.494F, 7.2707F, 4.0338F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("hand_l", createBoneHandL());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(4.5816F, 12.2508F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneHandL() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_hand_l",
         createCubePart(
            -3.6354F,
            -2.988F,
            -4.0338F,
            7.2708F,
            5.976F,
            8.0676F,
            15.0F,
            223.0F,
            30.0F,
            239.0F,
            45.0F,
            223.0F,
            60.0F,
            239.0F,
            70.0F,
            309.0F,
            86.0F,
            321.0F,
            154.0F,
            309.0F,
            169.0F,
            321.0F,
            42.0F,
            309.0F,
            58.0F,
            321.0F,
            181.0F,
            309.0F,
            196.0F,
            321.0F,
            PartPose.offsetAndRotation(0.0F, 2.6892F, -0.1494F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_hand_l",
         createCubePart(
            -3.7848F,
            -1.743F,
            -0.5976F,
            7.5696F,
            3.526F,
            1.1952F,
            19.0F,
            413.0F,
            34.0F,
            415.0F,
            36.0F,
            413.0F,
            51.0F,
            415.0F,
            117.0F,
            393.0F,
            119.0F,
            400.0F,
            219.0F,
            376.0F,
            234.0F,
            383.0F,
            113.0F,
            393.0F,
            115.0F,
            400.0F,
            241.0F,
            376.0F,
            256.0F,
            383.0F,
            PartPose.offsetAndRotation(0.0F, 3.9342F, -4.3027F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_hand_l",
         createCubePart(
            -0.6225F,
            -2.7888F,
            -3.9342F,
            1.245F,
            5.5776F,
            7.8684F,
            367.0F,
            223.0F,
            369.0F,
            239.0F,
            371.0F,
            223.0F,
            373.0F,
            239.0F,
            148.0F,
            333.0F,
            164.0F,
            344.0F,
            391.0F,
            333.0F,
            393.0F,
            344.0F,
            121.0F,
            333.0F,
            137.0F,
            344.0F,
            395.0F,
            333.0F,
            397.0F,
            344.0F,
            PartPose.offsetAndRotation(3.3366F, 2.988F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 17.4299F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneUpperArmR() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_upper_arm_r",
         createCubePart(
            -3.5856F,
            -7.47F,
            -3.984F,
            7.1712F,
            14.94F,
            7.968F,
            191.0F,
            223.0F,
            205.0F,
            239.0F,
            219.0F,
            223.0F,
            233.0F,
            239.0F,
            48.0F,
            100.0F,
            64.0F,
            130.0F,
            136.0F,
            100.0F,
            150.0F,
            130.0F,
            16.0F,
            100.0F,
            32.0F,
            130.0F,
            164.0F,
            100.0F,
            178.0F,
            130.0F,
            PartPose.offsetAndRotation(-4.5816F, 4.98F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_upper_arm_r",
         createCubePart(
            -3.8346F,
            -1.7928F,
            -0.6474F,
            7.6692F,
            3.5856F,
            1.2948F,
            115.0F,
            406.0F,
            130.0F,
            409.0F,
            133.0F,
            406.0F,
            148.0F,
            409.0F,
            107.0F,
            393.0F,
            110.0F,
            400.0F,
            307.0F,
            376.0F,
            322.0F,
            383.0F,
            101.0F,
            393.0F,
            104.0F,
            400.0F,
            329.0F,
            376.0F,
            344.0F,
            383.0F,
            PartPose.offsetAndRotation(-4.5816F, -0.6474F, -4.1334F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_upper_arm_r",
         createCubePart(
            -0.5976F,
            -2.988F,
            -3.735F,
            1.1952F,
            5.976F,
            7.47F,
            338.0F,
            254.0F,
            340.0F,
            269.0F,
            342.0F,
            254.0F,
            344.0F,
            269.0F,
            289.0F,
            309.0F,
            304.0F,
            321.0F,
            104.0F,
            333.0F,
            106.0F,
            345.0F,
            262.0F,
            309.0F,
            277.0F,
            321.0F,
            108.0F,
            333.0F,
            110.0F,
            345.0F,
            PartPose.offsetAndRotation(-8.217F, 0.6972F, 0.2988F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_upper_arm_r",
         createCubePart(
            -3.8844F,
            -0.3984F,
            -4.3326F,
            7.7688F,
            0.7968F,
            8.6652F,
            355.0F,
            187.0F,
            371.0F,
            204.0F,
            387.0F,
            187.0F,
            403.0F,
            204.0F,
            388.0F,
            406.0F,
            405.0F,
            408.0F,
            443.0F,
            406.0F,
            459.0F,
            408.0F,
            369.0F,
            406.0F,
            386.0F,
            408.0F,
            461.0F,
            406.0F,
            477.0F,
            408.0F,
            PartPose.offsetAndRotation(-4.5816F, -2.5398F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_4_upper_arm_r",
         createCubePart(
            -1.3446F,
            -1.7928F,
            -0.5478F,
            2.6892F,
            3.5856F,
            1.0956F,
            419.0F,
            413.0F,
            424.0F,
            415.0F,
            426.0F,
            413.0F,
            431.0F,
            415.0F,
            133.0F,
            393.0F,
            135.0F,
            400.0F,
            5.0F,
            393.0F,
            10.0F,
            400.0F,
            129.0F,
            393.0F,
            131.0F,
            400.0F,
            15.0F,
            393.0F,
            20.0F,
            400.0F,
            PartPose.offsetAndRotation(-6.6732F, 6.474F, -4.0836F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_5_upper_arm_r",
         createCubePart(
            -1.2948F,
            -1.7928F,
            -3.2868F,
            2.5896F,
            3.5856F,
            6.5736F,
            293.0F,
            283.0F,
            298.0F,
            296.0F,
            303.0F,
            283.0F,
            308.0F,
            296.0F,
            451.0F,
            376.0F,
            464.0F,
            383.0F,
            491.0F,
            376.0F,
            496.0F,
            383.0F,
            431.0F,
            376.0F,
            444.0F,
            383.0F,
            501.0F,
            376.0F,
            506.0F,
            383.0F,
            PartPose.offsetAndRotation(-1.1952F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("forearm_r", createBoneForearmR());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-7.8684F, -10.956F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneForearmR() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_forearm_r",
         createCubePart(
            -3.3864F,
            -8.964F,
            -3.7848F,
            6.7728F,
            17.928F,
            7.5696F,
            42.0F,
            254.0F,
            56.0F,
            269.0F,
            70.0F,
            254.0F,
            84.0F,
            269.0F,
            105.0F,
            48.0F,
            120.0F,
            84.0F,
            191.0F,
            48.0F,
            205.0F,
            84.0F,
            75.0F,
            48.0F,
            90.0F,
            84.0F,
            219.0F,
            48.0F,
            233.0F,
            84.0F,
            PartPose.offsetAndRotation(0.0F, 8.7647F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_forearm_r",
         createCubePart(
            -1.992F,
            -2.739F,
            -0.5976F,
            3.984F,
            5.478F,
            1.1952F,
            237.0F,
            413.0F,
            245.0F,
            415.0F,
            247.0F,
            413.0F,
            255.0F,
            415.0F,
            403.0F,
            333.0F,
            405.0F,
            344.0F,
            275.0F,
            333.0F,
            283.0F,
            344.0F,
            399.0F,
            333.0F,
            401.0F,
            344.0F,
            291.0F,
            333.0F,
            299.0F,
            344.0F,
            PartPose.offsetAndRotation(-1.0956F, 3.1871F, -3.9641F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_forearm_r",
         createCubePart(
            -0.5478F,
            -3.6852F,
            -3.8346F,
            1.0956F,
            7.3704F,
            7.6692F,
            322.0F,
            254.0F,
            324.0F,
            269.0F,
            334.0F,
            254.0F,
            336.0F,
            269.0F,
            438.0F,
            223.0F,
            453.0F,
            238.0F,
            326.0F,
            254.0F,
            328.0F,
            269.0F,
            408.0F,
            223.0F,
            423.0F,
            238.0F,
            330.0F,
            254.0F,
            332.0F,
            269.0F,
            PartPose.offsetAndRotation(-3.237F, 11.4539F, 0.0996F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_3_forearm_r",
         createCubePart(
            -2.0916F,
            -2.8884F,
            -0.5478F,
            4.1832F,
            5.7768F,
            1.0956F,
            257.0F,
            413.0F,
            265.0F,
            415.0F,
            267.0F,
            413.0F,
            275.0F,
            415.0F,
            100.0F,
            333.0F,
            102.0F,
            345.0F,
            36.0F,
            333.0F,
            44.0F,
            345.0F,
            96.0F,
            333.0F,
            98.0F,
            345.0F,
            52.0F,
            333.0F,
            60.0F,
            345.0F,
            PartPose.offsetAndRotation(-1.494F, 6.3743F, 4.0338F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put("hand_r", createBoneHandR());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-4.5816F, 12.2508F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneHandR() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_hand_r",
         createCubePart(
            -3.6354F,
            -2.988F,
            -4.0338F,
            7.2708F,
            5.976F,
            8.0676F,
            75.0F,
            223.0F,
            90.0F,
            239.0F,
            105.0F,
            223.0F,
            120.0F,
            239.0F,
            126.0F,
            309.0F,
            142.0F,
            321.0F,
            208.0F,
            309.0F,
            223.0F,
            321.0F,
            98.0F,
            309.0F,
            114.0F,
            321.0F,
            235.0F,
            309.0F,
            250.0F,
            321.0F,
            PartPose.offsetAndRotation(0.0F, 2.6892F, -0.1494F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_hand_r",
         createCubePart(
            -3.7848F,
            -1.743F,
            -0.5976F,
            7.5696F,
            3.526F,
            1.1952F,
            53.0F,
            413.0F,
            68.0F,
            415.0F,
            70.0F,
            413.0F,
            85.0F,
            415.0F,
            125.0F,
            393.0F,
            127.0F,
            400.0F,
            263.0F,
            376.0F,
            278.0F,
            383.0F,
            121.0F,
            393.0F,
            123.0F,
            400.0F,
            285.0F,
            376.0F,
            300.0F,
            383.0F,
            PartPose.offsetAndRotation(0.0F, 3.9342F, -4.3027F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_2_hand_r",
         createCubePart(
            -0.6225F,
            -2.7888F,
            -3.9342F,
            1.245F,
            5.5776F,
            7.8684F,
            375.0F,
            223.0F,
            377.0F,
            239.0F,
            379.0F,
            223.0F,
            381.0F,
            239.0F,
            202.0F,
            333.0F,
            218.0F,
            344.0F,
            407.0F,
            333.0F,
            409.0F,
            344.0F,
            175.0F,
            333.0F,
            191.0F,
            344.0F,
            411.0F,
            333.0F,
            413.0F,
            344.0F,
            PartPose.offsetAndRotation(-3.3366F, 2.988F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, 17.4299F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createCubePart(
      float x,
      float y,
      float z,
      float width,
      float height,
      float depth,
      float downU1,
      float downV1,
      float downU2,
      float downV2,
      float upU1,
      float upV1,
      float upU2,
      float upV2,
      float westU1,
      float westV1,
      float westU2,
      float westV2,
      float northU1,
      float northV1,
      float northU2,
      float northV2,
      float eastU1,
      float eastV1,
      float eastU2,
      float eastV2,
      float southU1,
      float southV1,
      float southU2,
      float southV2,
      PartPose pose
   ) {
      Cube cube = new BasaltEmberGolemEntityModel.RigCube(x, y, z, width, height, depth);
      float maxX = x + width;
      float maxY = y + height;
      float maxZ = z + depth;
      Vertex minMinMin = vertex(x, y, z);
      Vertex maxMinMin = vertex(maxX, y, z);
      Vertex maxMaxMin = vertex(maxX, maxY, z);
      Vertex minMaxMin = vertex(x, maxY, z);
      Vertex minMinMax = vertex(x, y, maxZ);
      Vertex maxMinMax = vertex(maxX, y, maxZ);
      Vertex maxMaxMax = vertex(maxX, maxY, maxZ);
      Vertex minMaxMax = vertex(x, maxY, maxZ);
      cube.polygons[0] = polygon(Direction.DOWN, downU1, downV1, downU2, downV2, maxMinMax, minMinMax, minMinMin, maxMinMin);
      cube.polygons[1] = polygon(Direction.UP, upU1, upV1, upU2, upV2, maxMaxMin, minMaxMin, minMaxMax, maxMaxMax);
      cube.polygons[2] = polygon(Direction.WEST, westU1, westV1, westU2, westV2, minMinMin, minMinMax, minMaxMax, minMaxMin);
      cube.polygons[3] = polygon(Direction.NORTH, northU1, northV1, northU2, northV2, maxMinMin, minMinMin, minMaxMin, maxMaxMin);
      cube.polygons[4] = polygon(Direction.EAST, eastU1, eastV1, eastU2, eastV2, maxMinMax, maxMinMin, maxMaxMin, maxMaxMax);
      cube.polygons[5] = polygon(Direction.SOUTH, southU1, southV1, southU2, southV2, minMinMax, maxMinMax, maxMaxMax, minMaxMax);
      ModelPart part = new ModelPart(List.of(cube), Map.of());
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static Vertex vertex(float x, float y, float z) {
      return new Vertex(x, y, z, 0.0F, 0.0F);
   }

   private static Polygon polygon(Direction direction, float u1, float v1, float u2, float v2, Vertex first, Vertex second, Vertex third, Vertex fourth) {
      return new Polygon(new Vertex[]{first, second, third, fourth}, u1, v1, u2, v2, 512.0F, 512.0F, false, direction);
   }

   private static final class RigCube extends Cube {
      private RigCube(float x, float y, float z, float width, float height, float depth) {
         super(0, 0, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, false, 512.0F, 512.0F, EnumSet.allOf(Direction.class));
      }

      public void compile(Pose pose, VertexConsumer vertices, int light, int overlay, int color) {
         Vector3f transformed = new Vector3f();

         for (Polygon face : this.polygons) {
            pose.transformNormal(face.normal(), transformed);
            float normalX = transformed.x();
            float normalY = transformed.y();
            float normalZ = transformed.z();

            for (Vertex vertex : face.vertices()) {
               pose.pose().transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), transformed);
               vertices.addVertex(transformed.x(), transformed.y(), transformed.z(), color, vertex.u(), vertex.v(), overlay, light, normalX, normalY, normalZ);
            }
         }
      }
   }
}

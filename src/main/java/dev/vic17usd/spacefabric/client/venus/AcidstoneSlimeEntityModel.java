package dev.vic17usd.spacefabric.client.venus;

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

public final class AcidstoneSlimeEntityModel extends EntityModel<AcidstoneSlimeRenderState> {
   private static final float TEXTURE_WIDTH = 512.0F;
   private static final float TEXTURE_HEIGHT = 512.0F;
   private static final float LOCOMOTION_TIME_SCALE = 5.555556F;
   private final KeyframeAnimation idleAnimation = AcidstoneSlimeAnimations.IDLE.bake(this.root());
   private final KeyframeAnimation locomotionAnimation = AcidstoneSlimeAnimations.LOCOMOTION.bake(this.root());
   private final KeyframeAnimation[] actionAnimations = new KeyframeAnimation[AcidstoneSlimeAnimations.ACTIONS.length];
   private final KeyframeAnimation[] actionIdleAnimations = new KeyframeAnimation[this.actionAnimations.length];

   public AcidstoneSlimeEntityModel() {
      super(createRoot(), RenderTypes::entityCutoutCull);

      for (int i = 0; i < this.actionAnimations.length; i++) {
         this.actionAnimations[i] = AcidstoneSlimeAnimations.ACTIONS[i].bake(this.root());
         this.actionIdleAnimations[i] = AcidstoneSlimeAnimations.ACTION_IDLE[i].bake(this.root());
      }
   }

   public void setupAnim(AcidstoneSlimeRenderState state) {
      super.setupAnim(state);
      long ambientMillis = (long)(state.ageInTicks * 50.0F);
      if (state.actionIndex >= 0 && state.actionIndex < this.actionAnimations.length) {
         this.actionIdleAnimations[state.actionIndex].apply(ambientMillis, 1.0F);
         this.actionAnimations[state.actionIndex].apply(state.actionTimeMillis, 1.0F);
      } else if (state.horizontalTravelSpeed > 0.02F) {
         this.locomotionAnimation.applyWalk(state.horizontalTravelPosition, state.horizontalTravelSpeed, 5.555556F, 2.5F);
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
      children.put("body", createBoneBody());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-0.315F, 24.0F, -0.4778F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneBody() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_body",
         createCubePart(
            -11.0656F,
            -10.0597F,
            -11.0657F,
            22.1313F,
            20.1194F,
            22.1313F,
            16.0F,
            48.0F,
            60.0F,
            92.0F,
            76.0F,
            48.0F,
            120.0F,
            92.0F,
            316.0F,
            48.0F,
            360.0F,
            88.0F,
            196.0F,
            48.0F,
            240.0F,
            88.0F,
            136.0F,
            48.0F,
            180.0F,
            88.0F,
            256.0F,
            48.0F,
            300.0F,
            88.0F,
            PartPose.offsetAndRotation(0.0F, -9.0537F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_body",
         createCubePart(
            -0.9053F,
            -0.6036F,
            -0.8551F,
            1.8107F,
            1.2072F,
            1.7101F,
            315.0F,
            166.0F,
            319.0F,
            169.0F,
            322.0F,
            166.0F,
            326.0F,
            169.0F,
            500.0F,
            166.0F,
            503.0F,
            168.0F,
            473.0F,
            166.0F,
            477.0F,
            168.0F,
            495.0F,
            166.0F,
            498.0F,
            168.0F,
            479.0F,
            166.0F,
            483.0F,
            168.0F,
            PartPose.offsetAndRotation(6.237F, -19.1637F, -3.4203F, 0.0F, -0.20944F, 0.0F)
         )
      );
      children.put(
         "__cube_2_body",
         createCubePart(
            -0.7042F,
            -0.7546F,
            -1.006F,
            1.4084F,
            1.509F,
            2.0119F,
            191.0F,
            166.0F,
            194.0F,
            170.0F,
            197.0F,
            166.0F,
            200.0F,
            170.0F,
            294.0F,
            166.0F,
            298.0F,
            169.0F,
            413.0F,
            166.0F,
            416.0F,
            169.0F,
            287.0F,
            166.0F,
            291.0F,
            169.0F,
            419.0F,
            166.0F,
            422.0F,
            169.0F,
            PartPose.offsetAndRotation(10.8645F, -3.8226F, -7.9472F, 0.0F, 0.0F, -0.069813F)
         )
      );
      children.put(
         "__cube_3_body",
         createCubePart(
            -0.6036F,
            -0.6539F,
            -0.5533F,
            1.2072F,
            1.3078F,
            1.1066F,
            22.0F,
            176.0F,
            24.0F,
            178.0F,
            26.0F,
            176.0F,
            28.0F,
            178.0F,
            457.0F,
            166.0F,
            459.0F,
            169.0F,
            449.0F,
            166.0F,
            451.0F,
            169.0F,
            445.0F,
            166.0F,
            447.0F,
            169.0F,
            453.0F,
            166.0F,
            455.0F,
            169.0F,
            PartPose.offsetAndRotation(10.9651F, -10.7638F, 1.8107F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_4_body",
         createCubePart(
            -0.8048F,
            -0.9556F,
            -0.9054F,
            1.6096F,
            1.9113F,
            1.8107F,
            167.0F,
            166.0F,
            170.0F,
            170.0F,
            185.0F,
            166.0F,
            188.0F,
            170.0F,
            147.0F,
            166.0F,
            151.0F,
            170.0F,
            173.0F,
            166.0F,
            176.0F,
            170.0F,
            139.0F,
            166.0F,
            143.0F,
            170.0F,
            179.0F,
            166.0F,
            182.0F,
            170.0F,
            PartPose.offsetAndRotation(10.7639F, -4.8286F, -3.4203F, 0.0F, 0.139626F, 0.0F)
         )
      );
      children.put(
         "__cube_5_body",
         createCubePart(
            -0.7544F,
            -0.855F,
            -1.006F,
            1.509F,
            1.7101F,
            2.0119F,
            203.0F,
            166.0F,
            206.0F,
            170.0F,
            209.0F,
            166.0F,
            212.0F,
            170.0F,
            308.0F,
            166.0F,
            312.0F,
            169.0F,
            425.0F,
            166.0F,
            428.0F,
            169.0F,
            301.0F,
            166.0F,
            305.0F,
            169.0F,
            431.0F,
            166.0F,
            434.0F,
            169.0F,
            PartPose.offsetAndRotation(-10.7639F, -15.0895F, -5.0299F, 0.0F, -0.15708F, 0.0F)
         )
      );
      children.put(
         "__cube_6_body",
         createCubePart(
            -0.855F,
            -0.7546F,
            -0.7544F,
            1.7101F,
            1.509F,
            1.509F,
            329.0F,
            166.0F,
            332.0F,
            169.0F,
            353.0F,
            166.0F,
            356.0F,
            169.0F,
            359.0F,
            166.0F,
            362.0F,
            169.0F,
            341.0F,
            166.0F,
            344.0F,
            169.0F,
            335.0F,
            166.0F,
            338.0F,
            169.0F,
            347.0F,
            166.0F,
            350.0F,
            169.0F,
            PartPose.offsetAndRotation(5.8346F, -4.0238F, 10.7638F, 0.0F, 0.0F, 0.139626F)
         )
      );
      children.put(
         "__cube_7_body",
         createCubePart(
            -1.2529F,
            -0.9139F,
            -1.4741F,
            2.5059F,
            1.8278F,
            2.9481F,
            380.0F,
            138.0F,
            385.0F,
            144.0F,
            390.0F,
            138.0F,
            395.0F,
            144.0F,
            75.0F,
            166.0F,
            81.0F,
            170.0F,
            85.0F,
            166.0F,
            90.0F,
            170.0F,
            65.0F,
            166.0F,
            71.0F,
            170.0F,
            94.0F,
            166.0F,
            99.0F,
            170.0F,
            PartPose.offsetAndRotation(-6.0358F, -19.0379F, 5.5328F, 0.0F, 0.0F, -1.570796F)
         )
      );
      children.put(
         "__cube_8_body",
         createCubePart(
            -1.2529F,
            -0.3538F,
            -1.0613F,
            2.5059F,
            0.7075F,
            2.1226F,
            103.0F,
            166.0F,
            108.0F,
            170.0F,
            112.0F,
            166.0F,
            117.0F,
            170.0F,
            64.0F,
            176.0F,
            68.0F,
            177.0F,
            30.0F,
            176.0F,
            35.0F,
            177.0F,
            58.0F,
            176.0F,
            62.0F,
            177.0F,
            37.0F,
            176.0F,
            42.0F,
            177.0F,
            PartPose.offsetAndRotation(-4.8566F, -19.0379F, 5.5328F, 0.0F, 0.0F, -1.570796F)
         )
      );
      children.put(
         "__cube_9_body",
         createCubePart(
            -1.2529F,
            -0.3538F,
            -1.0613F,
            2.5059F,
            0.7075F,
            2.1226F,
            121.0F,
            166.0F,
            126.0F,
            170.0F,
            130.0F,
            166.0F,
            135.0F,
            170.0F,
            76.0F,
            176.0F,
            80.0F,
            177.0F,
            44.0F,
            176.0F,
            49.0F,
            177.0F,
            70.0F,
            176.0F,
            74.0F,
            177.0F,
            51.0F,
            176.0F,
            56.0F,
            177.0F,
            PartPose.offsetAndRotation(-7.215F, -19.0379F, 5.5328F, 0.0F, 0.0F, -1.570796F)
         )
      );
      children.put(
         "__cube_10_body",
         createCubePart(
            -0.8592F,
            -0.6267F,
            -1.0108F,
            1.7184F,
            1.2534F,
            2.0217F,
            155.0F,
            166.0F,
            158.0F,
            170.0F,
            161.0F,
            166.0F,
            164.0F,
            170.0F,
            238.0F,
            166.0F,
            242.0F,
            169.0F,
            365.0F,
            166.0F,
            368.0F,
            169.0F,
            231.0F,
            166.0F,
            235.0F,
            169.0F,
            371.0F,
            166.0F,
            374.0F,
            169.0F,
            PartPose.offsetAndRotation(-6.0358F, -19.893F, 5.5328F, 0.0F, 0.0F, 1.570796F)
         )
      );
      children.put(
         "__cube_11_body",
         createCubePart(
            -0.8592F,
            -0.2426F,
            -0.7278F,
            1.7184F,
            0.4852F,
            1.4556F,
            377.0F,
            166.0F,
            380.0F,
            169.0F,
            383.0F,
            166.0F,
            386.0F,
            169.0F,
            121.0F,
            176.0F,
            124.0F,
            177.0F,
            111.0F,
            176.0F,
            114.0F,
            177.0F,
            106.0F,
            176.0F,
            109.0F,
            177.0F,
            116.0F,
            176.0F,
            119.0F,
            177.0F,
            PartPose.offsetAndRotation(-6.8445F, -19.893F, 5.5328F, 0.0F, 0.0F, 1.570796F)
         )
      );
      children.put(
         "__cube_12_body",
         createCubePart(
            -0.8592F,
            -0.2426F,
            -0.7278F,
            1.7184F,
            0.4852F,
            1.4556F,
            389.0F,
            166.0F,
            392.0F,
            169.0F,
            395.0F,
            166.0F,
            398.0F,
            169.0F,
            141.0F,
            176.0F,
            144.0F,
            177.0F,
            131.0F,
            176.0F,
            134.0F,
            177.0F,
            126.0F,
            176.0F,
            129.0F,
            177.0F,
            136.0F,
            176.0F,
            139.0F,
            177.0F,
            PartPose.offsetAndRotation(-5.2271F, -19.893F, 5.5328F, 0.0F, 0.0F, 1.570796F)
         )
      );
      children.put(
         "__cube_13_body",
         createCubePart(
            -0.9621F,
            -0.7017F,
            -1.1318F,
            1.9241F,
            1.4034F,
            2.2636F,
            49.0F,
            166.0F,
            53.0F,
            171.0F,
            57.0F,
            166.0F,
            61.0F,
            171.0F,
            223.0F,
            166.0F,
            228.0F,
            169.0F,
            245.0F,
            166.0F,
            249.0F,
            169.0F,
            215.0F,
            166.0F,
            220.0F,
            169.0F,
            252.0F,
            166.0F,
            256.0F,
            169.0F,
            PartPose.offsetAndRotation(-5.5328F, -18.9876F, -5.0299F, 0.0F, 0.0F, -1.570796F)
         )
      );
      children.put(
         "__cube_14_body",
         createCubePart(
            -0.9622F,
            -0.2716F,
            -0.8149F,
            1.9241F,
            0.5432F,
            1.6298F,
            259.0F,
            166.0F,
            263.0F,
            169.0F,
            266.0F,
            166.0F,
            270.0F,
            169.0F,
            151.0F,
            176.0F,
            154.0F,
            177.0F,
            82.0F,
            176.0F,
            86.0F,
            177.0F,
            146.0F,
            176.0F,
            149.0F,
            177.0F,
            88.0F,
            176.0F,
            92.0F,
            177.0F,
            PartPose.offsetAndRotation(-4.6273F, -18.9876F, -5.0299F, 0.0F, 0.0F, -1.570796F)
         )
      );
      children.put(
         "__cube_15_body",
         createCubePart(
            -0.962F,
            -0.2716F,
            -0.8149F,
            1.9241F,
            0.5432F,
            1.6298F,
            273.0F,
            166.0F,
            277.0F,
            169.0F,
            280.0F,
            166.0F,
            284.0F,
            169.0F,
            161.0F,
            176.0F,
            164.0F,
            177.0F,
            94.0F,
            176.0F,
            98.0F,
            177.0F,
            156.0F,
            176.0F,
            159.0F,
            177.0F,
            100.0F,
            176.0F,
            104.0F,
            177.0F,
            PartPose.offsetAndRotation(-6.4383F, -18.9876F, -5.0299F, 0.0F, 0.0F, -1.570796F)
         )
      );
      children.put(
         "__cube_16_body",
         createCubePart(
            -0.5981F,
            -0.4363F,
            -0.7036F,
            1.1961F,
            0.8725F,
            1.4072F,
            437.0F,
            166.0F,
            439.0F,
            169.0F,
            441.0F,
            166.0F,
            443.0F,
            169.0F,
            490.0F,
            166.0F,
            493.0F,
            168.0F,
            505.0F,
            166.0F,
            507.0F,
            168.0F,
            485.0F,
            166.0F,
            488.0F,
            168.0F,
            2.0F,
            176.0F,
            4.0F,
            178.0F,
            PartPose.offsetAndRotation(-5.5328F, -19.5409F, -5.0299F, 0.0F, 0.0F, 1.570796F)
         )
      );
      children.put(
         "__cube_17_body",
         createCubePart(
            -0.598F,
            -0.1689F,
            -0.5066F,
            1.1961F,
            0.3377F,
            1.0131F,
            6.0F,
            176.0F,
            8.0F,
            178.0F,
            10.0F,
            176.0F,
            12.0F,
            178.0F,
            178.0F,
            176.0F,
            180.0F,
            177.0F,
            170.0F,
            176.0F,
            172.0F,
            177.0F,
            166.0F,
            176.0F,
            168.0F,
            177.0F,
            174.0F,
            176.0F,
            176.0F,
            177.0F,
            PartPose.offsetAndRotation(-6.0957F, -19.5409F, -5.0299F, 0.0F, 0.0F, 1.570796F)
         )
      );
      children.put(
         "__cube_18_body",
         createCubePart(
            -0.598F,
            -0.1689F,
            -0.5066F,
            1.1961F,
            0.3377F,
            1.0131F,
            14.0F,
            176.0F,
            16.0F,
            178.0F,
            18.0F,
            176.0F,
            20.0F,
            178.0F,
            194.0F,
            176.0F,
            196.0F,
            177.0F,
            186.0F,
            176.0F,
            188.0F,
            177.0F,
            182.0F,
            176.0F,
            184.0F,
            177.0F,
            190.0F,
            176.0F,
            192.0F,
            177.0F,
            PartPose.offsetAndRotation(-4.97F, -19.5409F, -5.0299F, 0.0F, 0.0F, 1.570796F)
         )
      );
      children.put("foot_front_left", createBoneFootFrontLeft());
      children.put("foot_front_right", createBoneFootFrontRight());
      children.put("foot_back_left", createBoneFootBackLeft());
      children.put("foot_back_right", createBoneFootBackRight());
      children.put("stone_top", createBoneStoneTop());
      children.put("stone_left_high", createBoneStoneLeftHigh());
      children.put("stone_left_middle", createBoneStoneLeftMiddle());
      children.put("stone_left_front", createBoneStoneLeftFront());
      children.put("stone_right", createBoneStoneRight());
      children.put("stone_back", createBoneStoneBack());
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(0.0F, -8.0478F, 0.0F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneFootFrontLeft() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_foot_front_left",
         createCubePart(
            -3.5209F,
            -4.0239F,
            -3.5609F,
            7.0818F,
            8.0478F,
            7.0818F,
            462.0F,
            108.0F,
            476.0F,
            122.0F,
            14.0F,
            138.0F,
            28.0F,
            152.0F,
            210.0F,
            108.0F,
            224.0F,
            124.0F,
            154.0F,
            108.0F,
            168.0F,
            124.0F,
            126.0F,
            108.0F,
            140.0F,
            124.0F,
            182.0F,
            108.0F,
            196.0F,
            124.0F,
            PartPose.offsetAndRotation(0.0F, 3.5209F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      children.put(
         "__cube_1_foot_front_left",
         createCubePart(
            -0.3018F,
            -0.6539F,
            -0.6539F,
            0.6036F,
            1.3078F,
            1.3078F,
            461.0F,
            166.0F,
            462.0F,
            169.0F,
            470.0F,
            166.0F,
            471.0F,
            169.0F,
            407.0F,
            166.0F,
            410.0F,
            169.0F,
            464.0F,
            166.0F,
            465.0F,
            169.0F,
            401.0F,
            166.0F,
            404.0F,
            169.0F,
            467.0F,
            166.0F,
            468.0F,
            169.0F,
            PartPose.offsetAndRotation(3.4203F, 3.1185F, 0.1006F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(7.5448F, 0.503F, -7.5448F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneFootFrontRight() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_foot_front_right",
         createCubePart(
            -3.5608F,
            -4.0239F,
            -3.5609F,
            7.0818F,
            8.0478F,
            7.0818F,
            42.0F,
            138.0F,
            56.0F,
            152.0F,
            70.0F,
            138.0F,
            84.0F,
            152.0F,
            322.0F,
            108.0F,
            336.0F,
            124.0F,
            266.0F,
            108.0F,
            280.0F,
            124.0F,
            238.0F,
            108.0F,
            252.0F,
            124.0F,
            294.0F,
            108.0F,
            308.0F,
            124.0F,
            PartPose.offsetAndRotation(0.0F, 3.5209F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-7.5448F, 0.503F, -7.5448F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneFootBackLeft() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_foot_back_left",
         createCubePart(
            -3.5209F,
            -4.0239F,
            -3.5209F,
            7.0818F,
            8.0478F,
            7.0818F,
            350.0F,
            108.0F,
            364.0F,
            122.0F,
            378.0F,
            108.0F,
            392.0F,
            122.0F,
            460.0F,
            48.0F,
            474.0F,
            64.0F,
            404.0F,
            48.0F,
            418.0F,
            64.0F,
            376.0F,
            48.0F,
            390.0F,
            64.0F,
            432.0F,
            48.0F,
            446.0F,
            64.0F,
            PartPose.offsetAndRotation(0.0F, 3.5209F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(7.5448F, 0.503F, 7.5447F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneFootBackRight() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_foot_back_right",
         createCubePart(
            -3.5608F,
            -4.0239F,
            -3.5209F,
            7.0818F,
            8.0478F,
            7.0818F,
            406.0F,
            108.0F,
            420.0F,
            122.0F,
            434.0F,
            108.0F,
            448.0F,
            122.0F,
            98.0F,
            108.0F,
            112.0F,
            124.0F,
            42.0F,
            108.0F,
            56.0F,
            124.0F,
            14.0F,
            108.0F,
            28.0F,
            124.0F,
            70.0F,
            108.0F,
            84.0F,
            124.0F,
            PartPose.offsetAndRotation(0.0F, 3.5209F, 0.0F, 0.0F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-7.5448F, 0.503F, 7.5447F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneStoneTop() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_stone_top",
         createCubePart(
            -3.0179F,
            -1.3077F,
            -2.0119F,
            6.0358F,
            2.6155F,
            4.0239F,
            225.0F,
            138.0F,
            237.0F,
            146.0F,
            245.0F,
            138.0F,
            257.0F,
            146.0F,
            496.0F,
            138.0F,
            504.0F,
            143.0F,
            449.0F,
            138.0F,
            461.0F,
            143.0F,
            483.0F,
            138.0F,
            491.0F,
            143.0F,
            466.0F,
            138.0F,
            478.0F,
            143.0F,
            PartPose.offsetAndRotation(0.0F, -0.4024F, 0.0F, 0.0F, 0.15708F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(1.006F, -18.711F, 1.0059F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneStoneLeftHigh() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_stone_left_high",
         createCubePart(
            -1.006F,
            -1.6096F,
            -1.4083F,
            2.0119F,
            3.2191F,
            2.8167F,
            400.0F,
            138.0F,
            404.0F,
            144.0F,
            424.0F,
            138.0F,
            428.0F,
            144.0F,
            368.0F,
            138.0F,
            374.0F,
            144.0F,
            408.0F,
            138.0F,
            412.0F,
            144.0F,
            356.0F,
            138.0F,
            362.0F,
            144.0F,
            416.0F,
            138.0F,
            420.0F,
            144.0F,
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.122173F, 0.0F, 0.10472F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(10.8645F, -13.5805F, 5.0298F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneStoneLeftMiddle() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_stone_left_middle",
         createCubePart(
            -1.4084F,
            -2.5149F,
            -2.1629F,
            2.8167F,
            5.0298F,
            4.3257F,
            158.0F,
            138.0F,
            164.0F,
            147.0F,
            170.0F,
            138.0F,
            176.0F,
            147.0F,
            116.0F,
            138.0F,
            125.0F,
            148.0F,
            134.0F,
            138.0F,
            140.0F,
            148.0F,
            98.0F,
            138.0F,
            107.0F,
            148.0F,
            146.0F,
            138.0F,
            152.0F,
            148.0F,
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.122173F, 0.087266F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(10.8645F, -7.8465F, -0.503F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneStoneLeftFront() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_stone_left_front",
         createCubePart(
            -1.2575F,
            -2.3641F,
            -1.2072F,
            2.5149F,
            4.7281F,
            2.4143F,
            29.0F,
            166.0F,
            34.0F,
            171.0F,
            39.0F,
            166.0F,
            44.0F,
            171.0F,
            212.0F,
            138.0F,
            217.0F,
            147.0F,
            192.0F,
            138.0F,
            197.0F,
            147.0F,
            182.0F,
            138.0F,
            187.0F,
            147.0F,
            202.0F,
            138.0F,
            207.0F,
            147.0F,
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.087266F, 0.0F, 0.0F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(10.8645F, -12.0716F, -7.0418F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneStoneRight() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_stone_right",
         createCubePart(
            -1.1065F,
            -1.861F,
            -1.5592F,
            2.2131F,
            3.7221F,
            3.1185F,
            432.0F,
            138.0F,
            436.0F,
            144.0F,
            440.0F,
            138.0F,
            444.0F,
            144.0F,
            326.0F,
            138.0F,
            332.0F,
            145.0F,
            338.0F,
            138.0F,
            342.0F,
            145.0F,
            314.0F,
            138.0F,
            320.0F,
            145.0F,
            346.0F,
            138.0F,
            350.0F,
            145.0F,
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.10472F, 0.0F, -0.069813F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-10.8645F, -9.0537F, 2.0119F, 0.0F, 0.0F, 0.0F);
      part.loadPose(pose);
      part.setInitialPose(pose);
      return part;
   }

   private static ModelPart createBoneStoneBack() {
      Map<String, ModelPart> children = new LinkedHashMap<>();
      children.put(
         "__cube_0_stone_back",
         createCubePart(
            -1.7102F,
            -1.9114F,
            -1.1568F,
            3.4203F,
            3.8227F,
            2.3137F,
            5.0F,
            166.0F,
            12.0F,
            171.0F,
            17.0F,
            166.0F,
            24.0F,
            171.0F,
            303.0F,
            138.0F,
            308.0F,
            146.0F,
            265.0F,
            138.0F,
            272.0F,
            146.0F,
            293.0F,
            138.0F,
            298.0F,
            146.0F,
            279.0F,
            138.0F,
            286.0F,
            146.0F,
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.139626F)
         )
      );
      ModelPart part = new ModelPart(List.of(), children);
      PartPose pose = PartPose.offsetAndRotation(-2.0119F, -11.0656F, 10.8644F, 0.0F, 0.0F, 0.0F);
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
      Cube cube = new AcidstoneSlimeEntityModel.RigCube(x, y, z, width, height, depth);
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

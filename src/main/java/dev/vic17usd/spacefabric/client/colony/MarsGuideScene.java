package dev.vic17usd.spacefabric.client.colony;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.vic17usd.spacefabric.colony.ColonyContent;
import dev.vic17usd.spacefabric.block.ModBlocks;
import dev.vic17usd.spacefabric.mixin.client.GuideGuiStateAccessor;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/** Native block and player models in a reusable, depth-buffered Fabric picture-in-picture target.
 * No temporary level, block entity, inventory, entity spawning, or world writes are involved. */
public final class MarsGuideScene {
    private MarsGuideScene() { }
    public static void initialize() { PictureInPictureRendererRegistry.register(Renderer::new); }

    public record View(float yaw, float pitch, float centerX, float centerY, float centerZ, float spanX, float spanY) {
        public float scale(int width, int height) { return Math.min((width - 8) / spanX, (height - 8) / spanY); }
    }
    public static View view(MarsGuideAnimation.Chapter chapter, double p) {
        float yaw = (float)Math.toRadians(142 + 5 * MarsGuideAnimation.ease(p, .1, .9));
        float pitch = (float)Math.toRadians(-27);
        if (chapter == MarsGuideAnimation.Chapter.SUPPLIES || chapter == MarsGuideAnimation.Chapter.WATER)
            return new View((float)Math.toRadians(-38), pitch, .5F, .5F, .5F, 1.65F, 1.65F);
        // Bounds include the rising roof, assembling blocks, doors and walking player's full height.
        return new View(yaw, pitch, 3.5F, 2.8F, chapter == MarsGuideAnimation.Chapter.AIRLOCK ? 5.7F : 3.5F, 11.8F, 10.5F);
    }
    public record State(int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea,
                        ScreenRectangle bounds, MarsGuideAnimation.Chapter chapter, double progress, View view,
                        AvatarRenderState player) implements PictureInPictureRenderState { }

    public static void extract(GuiGraphicsExtractor gui, int x, int y, int width, int height,
                               MarsGuideAnimation.Chapter chapter, double progress) {
        if (width < 12 || height < 12) return;
        var view = view(chapter, progress);
        var bounds = new ScreenRectangle(x, y, width, height);
        AvatarRenderState player = null;
        var client = Minecraft.getInstance();
        if ((chapter == MarsGuideAnimation.Chapter.AIRLOCK || chapter == MarsGuideAnimation.Chapter.FINISH)) {
            // Dedicated preview state, not the world's cached renderer state. Skin only: no live pose/equipment changes.
            player = new AvatarRenderState();
            player.entityType = EntityType.PLAYER; player.skin = client.player == null ? DefaultPlayerSkin.getDefaultSkin() : client.player.getSkin(); player.pose = Pose.STANDING;
            player.scale = player.ageScale = 1; player.lightCoords = LightCoordsUtil.FULL_BRIGHT;
            player.boundingBoxWidth = .6F; player.boundingBoxHeight = 1.8F; player.eyeHeight = 1.62F;
            player.showHat = player.showJacket = player.showLeftPants = player.showRightPants = player.showLeftSleeve = player.showRightSleeve = true;
            player.bodyRot = 180; player.walkAnimationPos = (float)(progress * 36);
            player.walkAnimationSpeed = chapter == MarsGuideAnimation.Chapter.AIRLOCK && MarsGuideAnimation.walking(progress) ? .65F : 0;
        }
        ((GuideGuiStateAccessor)gui).spaceFabric$guideRenderState().addPicturesInPictureState(
                new State(x, y, x + width, y + height, view.scale(width, height), bounds, bounds, chapter, progress, view, player));
    }

    public static final class Renderer extends PictureInPictureRenderer<State> {
        private final Minecraft client;
        private final BlockModelResolver resolver;
        private final BlockDisplayContext context = BlockDisplayContext.create();
        private final Map<BlockState, BlockModelRenderState> models = new HashMap<>();
        private final CameraRenderState camera = new CameraRenderState();
        private Object modelSet;
        private static final Vector3f[] NORMALS = {new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)};
        private static final SubmitNodeCollector.CustomGeometryRenderer AIR_OUTLINE = (pose, vertices) -> {
            for (int axis = 0; axis < 3; axis++) for (int a = 0; a <= 1; a++) for (int b = 0; b <= 1; b++) {
                float x = 3 + (axis == 0 ? 0 : a), y = 2 + (axis == 1 ? 0 : axis == 0 ? a : b), z = 3 + (axis == 2 ? 0 : b);
                vertices.addVertex(pose, x, y, z).setColor(0xFF8DD7AF).setNormal(pose, NORMALS[axis]).setLineWidth(2);
                vertices.addVertex(pose, x + (axis == 0 ? 1 : 0), y + (axis == 1 ? 1 : 0), z + (axis == 2 ? 1 : 0))
                        .setColor(0xFF8DD7AF).setNormal(pose, NORMALS[axis]).setLineWidth(2);
            }
        };
        private static final BlockState HULL = ColonyContent.PRESSURE_HULL.defaultBlockState();
        private static final BlockState FLOOR = ModBlocks.IRON_OXIDE_REGOLITH.defaultBlockState();
        private static final BlockState CONTROLLER = ColonyContent.HABITAT_CONTROLLER.defaultBlockState();
        private static final BlockState[][] DOORS = makeDoors();
        public Renderer(PictureInPictureRendererRegistry.Context context) {
            super(context.bufferSource()); client = context.minecraft(); resolver = new BlockModelResolver(client.getModelManager());
        }
        private static BlockState[][] makeDoors() {
            var states = new BlockState[2][2];
            for (int open = 0; open < 2; open++) for (int half = 0; half < 2; half++)
                states[open][half] = ColonyContent.AIRLOCK_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.SOUTH)
                        .setValue(DoorBlock.OPEN, open == 1).setValue(DoorBlock.HALF, half == 0 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER);
            return states;
        }
        @Override public Class<State> getRenderStateClass() { return State.class; }
        @Override protected float getTranslateY(int textureHeight, int guiScale) { return textureHeight / 2F; }
        @Override protected String getTextureLabel() { return "space_fabric_mars_training"; }
        @Override protected void renderToTexture(State state, PoseStack pose) {
            // Resource reloads replace the block model set. Never retain obsolete atlas/model references.
            var current = client.getModelManager().getBlockModelSet();
            if (modelSet != current) { models.clear(); modelSet = current; }
            client.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
            var view = state.view();
            var rotation = MarsGuideCamera.apply(pose, view);
            switch (state.chapter()) {
                case SUPPLIES, WATER -> block(pose, CONTROLLER, 0, 0, 0, 1);
                case AIRLOCK -> airlock(pose, state.progress());
                default -> habitat(pose, state);
            }
            if (state.player() != null) {
                camera.orientation = rotation.conjugate(new Quaternionf()).rotateY((float)Math.PI);
                boolean airlock = state.chapter() == MarsGuideAnimation.Chapter.AIRLOCK;
                client.getEntityRenderDispatcher().submit(state.player(), camera, airlock ? 3.5 : 4.6, 1,
                        airlock ? MarsGuideAnimation.airlockPlayerZ(state.progress()) : 4.5, pose,
                        client.gameRenderer.getFeatureRenderDispatcher().getSubmitNodeStorage());
            }
            if (state.chapter() == MarsGuideAnimation.Chapter.VENT && state.progress() >= .24)
                client.gameRenderer.getFeatureRenderDispatcher().getSubmitNodeStorage().submitCustomGeometry(pose, RenderTypes.lines(), AIR_OUTLINE);
            // Same native submission/flush path as GuiEntityRenderer, including translucent model layers.
            client.gameRenderer.getFeatureRenderDispatcher().renderAllFeatures();
        }
        private void habitat(PoseStack pose, State state) {
            double p = state.progress(), cut = MarsGuideAnimation.cutaway(state.chapter(), p);
            for (var b : MarsGuideAnimation.shell()) {
                double built = state.chapter() == MarsGuideAnimation.Chapter.BUILD ? MarsGuideAnimation.construction(b, p) : 1;
                if (built == 0) continue;
                boolean removed = MarsGuideAnimation.cutFace(b);
                float size = removed ? (float)(1 - cut) : 1;
                if (size < .005F) continue;
                float y = (float)(b.y() + (1 - built) * 1.1 + (b.y() == 4 ? cut * 1.5 : 0));
                BlockState model = b.door() ? DOORS[0][b.y() - 1] : b.y() == 0 ? FLOOR : HULL;
                block(pose, model, b.x(), y, b.z(), size * (float)built);
            }
            if (state.chapter() != MarsGuideAnimation.Chapter.BUILD || p > .9)
                block(pose, CONTROLLER, 3, 1, 3, state.chapter() == MarsGuideAnimation.Chapter.VENT ? (float)MarsGuideAnimation.ease(p, .08, .24) : 1);
        }
        private void airlock(PoseStack pose, double p) {
            // A cut section of the vestibule; the near wall and roof are hidden for teaching.
            for (int z = 2; z <= 9; z++) for (int x = 2; x <= 4; x++) block(pose, FLOOR, x, 0, z, 1);
            for (int z = 2; z <= 8; z++) for (int y = 1; y <= 3; y++) block(pose, HULL, 2, y, z, 1);
            var doors = MarsGuideAnimation.doors(p);
            for (int half = 0; half < 2; half++) {
                block(pose, DOORS[doors.outerOpen() ? 1 : 0][half], 3, half + 1, 8, 1);
                block(pose, DOORS[doors.innerOpen() ? 1 : 0][half], 3, half + 1, 5, 1);
            }
            for (int z = 5; z <= 8; z += 3) { block(pose, HULL, 3, 3, z, 1); block(pose, HULL, 4, 1, z, 1); }
        }
        private void block(PoseStack pose, BlockState state, float x, float y, float z, float size) {
            if (size <= 0) return;
            var model = models.computeIfAbsent(state, key -> { var result = new BlockModelRenderState(); resolver.update(result, key, context); return result; });
            pose.pushPose();
            pose.translate(x + (1 - size) / 2, y, z + (1 - size) / 2);
            pose.scale(size, size, size);
            model.submit(pose, client.gameRenderer.getFeatureRenderDispatcher().getSubmitNodeStorage(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
            pose.popPose();
        }
        @Override public void close() { models.clear(); super.close(); }
    }
}

package dev.vic17usd.spacefabric.client.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.space.RocketEntity;
import dev.vic17usd.spacefabric.space.RocketVariant;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public final class RocketRenderer extends EntityRenderer<RocketEntity, RocketRenderer.State> {
    private static final Identifier TEXTURE = SpaceFabric.id("textures/entity/rocket.png");
    private static final Identifier FLIGHT_MATERIAL=SpaceFabric.id("textures/entity/flight_white.png");
    private final java.util.EnumMap<RocketVariant,RocketFleetModel> models=new java.util.EnumMap<>(RocketVariant.class);
    private final RocketCockpitModel cockpit = new RocketCockpitModel();
    public static final class State extends EntityRenderState {
        float yaw,journeyProgress;boolean cockpitView,spaceView;
        RocketVariant variant=RocketVariant.BASE;
        dev.vic17usd.spacefabric.planet.Planet origin,destination;
    }
    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context); shadowRadius = 0.9F;
        for(var variant:RocketVariant.values())models.put(variant,new RocketFleetModel(variant));
    }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(RocketEntity entity, State state, float delta) {
        super.extractRenderState(entity, state, delta); state.yaw = entity.getYRot(); state.variant=entity.variant();
        var client = Minecraft.getInstance();
        var viewer = client.getCameraEntity();
        state.cockpitView = client.options.getCameraType().isFirstPerson()
                && viewer != null && viewer.getVehicle() == entity;
        state.spaceView=entity==RocketFlightView.rocket()&&entity.isCruising();
        state.journeyProgress=dev.vic17usd.spacefabric.space.RocketJourney.progress(entity.journeyTicks()+delta,entity.journeyDuration());
        state.origin=dev.vic17usd.spacefabric.space.TravelDestination.at(entity.originIndex()).planet();
        state.destination=dev.vic17usd.spacefabric.space.TravelDestination.at(entity.destinationIndex()).planet();
    }
    @Override public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if(state.spaceView) {
            poses.pushPose();poses.translate(camera.pos.x-state.x,camera.pos.y-state.y,camera.pos.z-state.z);
            poses.mulPose(Axis.YP.rotationDegrees(-state.yaw));
            collector.submitCustomGeometry(poses,RenderTypes.entityCutout(FLIGHT_MATERIAL,false),
                (pose,vertices)->RocketFlightScene.render(pose,vertices,state.origin,state.destination,state.journeyProgress));
            poses.popPose();
        }
        if (state.cockpitView) {
            poses.pushPose();
            // El habitáculo acompaña la cámara durante la interpolación del vehículo.
            // Gira con el cohete, no con la mirada: se pueden observar paredes, suelo y mandos.
            poses.translate(camera.pos.x-state.x, camera.pos.y-state.y, camera.pos.z-state.z);
            poses.mulPose(Axis.YP.rotationDegrees(-state.yaw));
            int cabinLight=0x00F000B0; // Iluminación interior, también en planetas sin luz.
            collector.submitCustomGeometry(poses, RenderTypes.entityCutout(TEXTURE),
                    (pose, vertices) -> cockpit.renderGeometry(pose, vertices, cabinLight, OverlayTexture.NO_OVERLAY));
            poses.popPose();
            super.submit(state, poses, collector, camera);
            return;
        }
        poses.pushPose(); poses.mulPose(Axis.YP.rotationDegrees(-state.yaw));
        // Las UV libres de Blockbench no siguen el despliegue UV del cubo vanilla.
        // Enviar los vértices evita que Sodium reutilice las UV del constructor Cube.
        int light = state.lightCoords;
        var model=models.get(state.variant);
        collector.submitCustomGeometry(poses, RenderTypes.entityCutout(model.texture),
                (pose, vertices) -> model.renderGeometry(pose, vertices, light, OverlayTexture.NO_OVERLAY));
        poses.popPose();
        super.submit(state, poses, collector, camera);
    }
}

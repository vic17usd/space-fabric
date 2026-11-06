package dev.vic17usd.spacefabric.mixin.client;

import dev.vic17usd.spacefabric.client.equipment.SuitLayer;
import dev.vic17usd.spacefabric.equipment.SuitEquipment;
import dev.vic17usd.spacefabric.equipment.SuitLoadout;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=AvatarRenderer.class,remap=false)
public abstract class SuitRendererMixin extends LivingEntityRenderer<Avatar,AvatarRenderState,PlayerModel> {
    protected SuitRendererMixin(EntityRendererProvider.Context context,PlayerModel model,float shadow){super(context,model,shadow);}
    @Inject(method="<init>",at=@At("TAIL"))
    private void spaceFabric$addSuitLayer(EntityRendererProvider.Context context,boolean slim,CallbackInfo callback) {
        addLayer(new SuitLayer(this,context.getEquipmentRenderer()));
    }
    @Inject(method="extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",at=@At("TAIL"))
    private void spaceFabric$suitSnapshot(Avatar entity,AvatarRenderState state,float delta,CallbackInfo callback) {
        ((FabricRenderState)state).setData(SuitLayer.LOADOUT,entity instanceof Player player?SuitEquipment.get(player).snapshot():SuitLoadout.empty());
    }
}

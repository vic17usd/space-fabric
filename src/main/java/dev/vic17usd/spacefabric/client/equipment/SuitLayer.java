package dev.vic17usd.spacefabric.client.equipment;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.vic17usd.spacefabric.equipment.SuitLoadout;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

/** Native equipment rendering on a thinner model: ordinary armor stays visible above the suit. */
public final class SuitLayer extends RenderLayer<AvatarRenderState,PlayerModel> {
    public static final RenderStateDataKey<SuitLoadout> LOADOUT=RenderStateDataKey.create(()->"space_fabric:dedicated_suit");
    private final HumanoidArmorLayer<AvatarRenderState,PlayerModel,HumanoidModel<AvatarRenderState>> delegate;
    public SuitLayer(RenderLayerParent<AvatarRenderState,PlayerModel> parent,EquipmentLayerRenderer equipment) {
        super(parent);
        var models=HumanoidModel.createArmorMeshSet(new CubeDeformation(.35F),new CubeDeformation(.75F))
            .map(mesh->new HumanoidModel<AvatarRenderState>(LayerDefinition.create(mesh,64,32).bakeRoot()));
        delegate=new HumanoidArmorLayer<>(parent,models,equipment);
    }
    @Override public void submit(PoseStack poses,SubmitNodeCollector collector,int light,AvatarRenderState state,float yaw,float pitch) {
        var suit=((FabricRenderState)state).getData(LOADOUT);
        if(suit==null||suit.items().stream().allMatch(net.minecraft.world.item.ItemStack::isEmpty))return;
        var head=state.headEquipment;var chest=state.chestEquipment;var legs=state.legsEquipment;var feet=state.feetEquipment;
        try {
            state.headEquipment=suit.items().get(0);state.chestEquipment=suit.items().get(1);
            state.legsEquipment=suit.items().get(2);state.feetEquipment=suit.items().get(3);
            delegate.submit(poses,collector,light,state,yaw,pitch);
        } finally {state.headEquipment=head;state.chestEquipment=chest;state.legsEquipment=legs;state.feetEquipment=feet;}
    }
}

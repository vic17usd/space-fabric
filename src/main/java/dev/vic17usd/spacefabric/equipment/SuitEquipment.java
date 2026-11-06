package dev.vic17usd.spacefabric.equipment;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.menu.SuitMenu;
import java.util.ArrayList;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.gamerules.GameRules;

public final class SuitEquipment {
    public static final AttachmentType<SuitLoadout> LOADOUT=AttachmentRegistry.<SuitLoadout>builder()
        .initializer(SuitLoadout::empty).persistent(SuitLoadout.CODEC).copyOnDeath()
        .syncWith(SuitLoadout.STREAM_CODEC,AttachmentSyncPredicate.all()).buildAndRegister(SpaceFabric.id("suit_inventory"));
    private SuitEquipment() { }
    public static SuitLoadout get(Player player){return ((AttachmentTarget)player).getAttachedOrCreate(LOADOUT);}
    public static void set(Player player,SuitLoadout loadout){((AttachmentTarget)player).setAttached(LOADOUT,loadout.snapshot());}
    public static boolean removable(Player player,ItemStack stack){
        return !EnchantmentHelper.has(stack,EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)||player.isCreative();
    }
    public static boolean tryEquip(Player player,int slot,ItemStack held) {
        if(!player.isAlive()||player.isSpectator()||!SuitProtection.accepts(slot,held)||!get(player).items().get(slot).isEmpty())return false;
        var items=new ArrayList<>(get(player).items());items.set(slot,held.copyWithCount(1));set(player,new SuitLoadout(items));
        held.shrink(1);return true;
    }
    public record DeathResult(SuitLoadout remaining,java.util.List<ItemStack> drops) { }
    public static DeathResult deathResult(SuitLoadout loadout,boolean keepInventory,boolean spectator) {
        if(keepOnDeath(keepInventory,spectator))return new DeathResult(loadout.snapshot(),java.util.List.of());
        return new DeathResult(SuitLoadout.empty(),loadout.items().stream().filter(stack->!stack.isEmpty()
            &&!EnchantmentHelper.has(stack,EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)).map(ItemStack::copy).toList());
    }
    public static void environmentalWear(ServerPlayer player,int amount) {
        var worn=get(player).snapshot();
        for(var stack:worn.items())if(stack.isDamageableItem())
            stack.hurtAndBreak(amount,player.level(),player,item->player.sendOverlayMessage(Component.translatable("message.space_fabric.suit_broken")));
        set(player,worn);
    }
    public static boolean keepOnDeath(boolean keepInventory,boolean spectator){return keepInventory||spectator;}
    public static java.util.Optional<SuitLoadout> migrationCandidate(SuitLoadout dedicated,ItemStack head,ItemStack chest,ItemStack legs,ItemStack feet) {
        if(dedicated.items().stream().anyMatch(s->!s.isEmpty())||SuitProtection.tier(head,chest,legs,feet)==SuitTier.NONE)return java.util.Optional.empty();
        return java.util.Optional.of(new SuitLoadout(java.util.List.of(head,chest,legs,feet)));
    }
    public static void open(ServerPlayer player) {
        if(!player.isAlive()||player.isSpectator())return;
        var slots=new net.minecraft.world.entity.EquipmentSlot[]{net.minecraft.world.entity.EquipmentSlot.HEAD,net.minecraft.world.entity.EquipmentSlot.CHEST,net.minecraft.world.entity.EquipmentSlot.LEGS,net.minecraft.world.entity.EquipmentSlot.FEET};
        var migration=migrationCandidate(get(player),player.getItemBySlot(slots[0]),player.getItemBySlot(slots[1]),player.getItemBySlot(slots[2]),player.getItemBySlot(slots[3]));
        if(migration.isPresent()) {
            set(player,migration.get());
            for(var slot:slots)player.setItemSlot(slot,ItemStack.EMPTY);
            player.sendOverlayMessage(Component.translatable("message.space_fabric.suit_migrated"));
        }
        player.openMenu(new SimpleMenuProvider((id,inventory,p)->new SuitMenu(id,inventory,new SuitContainer(player)),Component.translatable("gui.space_fabric.suit_inventory")));
    }
    public record Open() implements CustomPacketPayload {
        public static final Type<Open> TYPE=new Type<>(SpaceFabric.id("open_suit_inventory"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Open> CODEC=new StreamCodec<>() {
            public Open decode(RegistryFriendlyByteBuf b){return new Open();}
            public void encode(RegistryFriendlyByteBuf b,Open p) { }
        };
        @Override public Type<Open> type(){return TYPE;}
    }
    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(Open.TYPE,Open.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Open.TYPE,(payload,context)->open(context.player()));
        ServerLivingEntityEvents.AFTER_DEATH.register((entity,source)->{
            if(!(entity instanceof ServerPlayer player))return;
            var outcome=deathResult(get(player),player.level().getGameRules().get(GameRules.KEEP_INVENTORY),player.isSpectator());
            set(player,outcome.remaining());
            for(var stack:outcome.drops())player.spawnAtLocation(player.level(),stack.copy());
        });
        ServerPlayerEvents.COPY_FROM.register((previous,current,alive)->{
            boolean keep=alive||keepOnDeath(previous.level().getGameRules().get(GameRules.KEEP_INVENTORY),previous.isSpectator());
            set(current,keep?get(previous):SuitLoadout.empty());
        });
    }
}

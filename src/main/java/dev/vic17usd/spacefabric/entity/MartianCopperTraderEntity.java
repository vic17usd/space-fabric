package dev.vic17usd.spacefabric.entity;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Aldeano mercader permanente con soporte vital; comparte el sistema global de gravedad planetaria. */
public final class MartianCopperTraderEntity extends AbstractVillager {
    public static final int RESTOCK_INTERVAL = 24_000;
    private long nextRestock;

    public MartianCopperTraderEntity(EntityType<? extends MartianCopperTraderEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 24)
                .add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.FOLLOW_RANGE, 24)
                .add(Attributes.SAFE_FALL_DISTANCE, 6);
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.1));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(2, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.6));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason reason, SpawnGroupData group) {
        var result = super.finalizeSpawn(level, difficulty, reason, group);
        setAge(0); setHomeTo(blockPosition(), 24); setPersistenceRequired();
        return result;
    }

    /** No confiere invulnerabilidad general: fuego, combate y presión profunda siguen siendo peligros. */
    public static boolean resistsAtmosphere(DamageSource source) {
        return source.is(ResourceKey.create(Registries.DAMAGE_TYPE, SpaceFabric.id("hypoxia")))
                || source.is(ResourceKey.create(Registries.DAMAGE_TYPE, SpaceFabric.id("decompression")));
    }
    @Override public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        return resistsAtmosphere(source) || super.isInvulnerableTo(level, source);
    }
    @Override public boolean canBreatheUnderwater() { return true; }
    @Override public boolean showProgressBar() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) { return null; }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isAlive() || isBaby() || isTrading() || player.isSecondaryUseActive()) return InteractionResult.PASS;
        if (!level().isClientSide()) {
            if (getOffers().isEmpty()) return InteractionResult.PASS;
            setTradingPlayer(player);
            openTradingScreen(player, getDisplayName(), 1);
        }
        return InteractionResult.SUCCESS;
    }

    public static MerchantOffers createTradeOffers() {
        var trades = new MerchantOffers();
        trades.add(new MerchantOffer(new ItemCost(Items.COPPER_INGOT, 12), new ItemStack(Items.EMERALD), 16, 2, 0.05F));
        trades.add(new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(Items.COPPER_INGOT, 8), 16, 2, 0.05F));
        trades.add(new MerchantOffer(new ItemCost(Items.EMERALD), new ItemStack(ModItems.IRON_OXIDE_DUST, 2), 16, 3, 0.05F));
        trades.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.SULFATE_DUST), 12, 3, 0.05F));
        trades.add(new MerchantOffer(new ItemCost(ModItems.MARTIAN_OLIVINE), new ItemStack(Items.EMERALD, 3), 12, 4, 0.05F));
        trades.add(new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(ModItems.ROCKET_FUEL_CELL), 4, 5, 0.05F));
        return trades;
    }
    @Override protected void updateTrades(ServerLevel level) { offers.addAll(createTradeOffers()); }
    @Override protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp() && level() instanceof ServerLevel server) ExperienceOrb.award(server, position(), 3);
    }
    @Override protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (nextRestock == 0) nextRestock = level.getGameTime() + RESTOCK_INTERVAL;
        if (level.getGameTime() >= nextRestock && !isTrading()) {
            if (offers != null) offers.forEach(MerchantOffer::resetUses);
            nextRestock = level.getGameTime() + RESTOCK_INTERVAL;
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.VILLAGER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.VILLAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.VILLAGER_DEATH; }

    @Override protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putLong("NextRestock", nextRestock);
        if (hasHome()) output.store("SettlementHome", BlockPos.CODEC, getHomePosition());
    }
    @Override protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        nextRestock = Math.max(0, input.getLongOr("NextRestock", 0));
        input.read("SettlementHome", BlockPos.CODEC).ifPresent(home -> setHomeTo(home, 24));
        setPersistenceRequired();
    }
}

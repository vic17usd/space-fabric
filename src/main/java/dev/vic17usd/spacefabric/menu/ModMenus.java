package dev.vic17usd.spacefabric.menu;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
    public static final MenuType<ColonyMachineMenu> COLONY_MACHINE=Registry.register(BuiltInRegistries.MENU,SpaceFabric.id("colony_machine"),
            new MenuType<>(ColonyMachineMenu::new,net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final MenuType<HabitatMenu> HABITAT = Registry.register(BuiltInRegistries.MENU, SpaceFabric.id("habitat"),
            new MenuType<>(HabitatMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final MenuType<SuitMenu> SUIT = Registry.register(BuiltInRegistries.MENU, SpaceFabric.id("suit_inventory"),
            new MenuType<>(SuitMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final MenuType<RocketMenu> ROCKET = Registry.register(BuiltInRegistries.MENU, SpaceFabric.id("rocket"),
            new ExtendedMenuType<>((id, inventory, entityId) -> new RocketMenu(id, inventory), ByteBufCodecs.VAR_INT));
    public static final MenuType<RefineryMenu> REFINERY = Registry.register(BuiltInRegistries.MENU, SpaceFabric.id("fuel_refinery"),
            new ExtendedMenuType<>((id, inventory, pos) -> new RefineryMenu(id, inventory), BlockPos.STREAM_CODEC));
    private ModMenus() { }
    public static void initialize() { }
}

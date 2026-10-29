package dev.vic17usd.spacefabric.command;

import java.util.List;
import dev.vic17usd.spacefabric.planet.Planet;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/** Display names and Spanish lookup aliases. Persistent registry/template IDs stay unchanged. */
public final class StructureNames {
    public record Entry(String id, String alias, Planet planet) {
        public String translationKey() { return "structure.space_fabric."+id; }
        public String locateCommand() {
            return "/execute in space_fabric:"+planet.path()+" run locate structure #space_fabric:"+alias;
        }
    }
    public static final List<Entry> ALL=List.of(
            new Entry("martian_breathforge","base_marciana",Planet.MARS),
            new Entry("martian_haven_settlement","pueblo_marciano",Planet.MARS),
            new Entry("soulfire_netherite_sanctum","santuario_marciano",Planet.MARS),
            new Entry("copper_crown_village","pueblo_venusiano",Planet.VENUS),
            new Entry("sunstone_pyramid_bazaar","piramide_venusiana",Planet.VENUS),
            new Entry("venus_sulfur_caldera","caldera_venusiana",Planet.VENUS),
            new Entry("venusian_ash_outpost","puesto_ceniza_venusiano",Planet.VENUS),
            new Entry("venusian_sulfur_vault","boveda_venusiana",Planet.VENUS),
            new Entry("venus_basalt_haven","refugio_venusiano",Planet.VENUS),
            new Entry("rustfall_foundry","fundicion_venusiana",Planet.VENUS),
            new Entry("venus_research_station","laboratorio_venusiano",Planet.VENUS),
            new Entry("venus_recovery_shelter","recuperacion_venusiana",Planet.VENUS),
            new Entry("venus_cloud_station","estacion_atmosferica",Planet.VENUS));
    private StructureNames() { }
    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher,context,selection)->dispatcher.register(
                Commands.literal("estructuras").executes(command->{
                    var source=command.getSource();
                    source.sendSuccess(()->Component.translatable("commands.space_fabric.structures.title"),false);
                    for(var entry:ALL) {
                        var line=Component.translatable(entry.translationKey()).withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(" — #space_fabric:"+entry.alias()).withStyle(ChatFormatting.GRAY));
                        line.withStyle(style->style.withClickEvent(new ClickEvent.SuggestCommand(entry.locateCommand())));
                        source.sendSuccess(()->line,false);
                    }
                    source.sendSuccess(()->Component.translatable("commands.space_fabric.structures.hint"),false);
                    return ALL.size();
                })));
    }
}

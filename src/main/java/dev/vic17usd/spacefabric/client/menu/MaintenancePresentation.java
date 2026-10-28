package dev.vic17usd.spacefabric.client.menu;

import java.util.List;
import net.minecraft.network.chat.Component;

public final class MaintenancePresentation {
    private MaintenancePresentation() { }
    public static int color(int integrity){return integrity<=2000?0xFFFF8B78:integrity<=7500?0xFFFFCD69:0xFF65E5BE;}
    public static Component integrity(int value){return Component.translatable("maintenance.space_fabric.integrity",value/100);}
    public static List<Component> tooltip(int integrity,int coating,int filter) {
        return List.of(integrity(integrity),Component.translatable("maintenance.space_fabric.coating",coating,4800),
                Component.translatable("maintenance.space_fabric.filter",filter,1200),Component.translatable("maintenance.space_fabric.hint"));
    }
}

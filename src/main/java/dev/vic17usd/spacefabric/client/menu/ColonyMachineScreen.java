package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.ColonyMachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ColonyMachineScreen extends SpaceContainerScreen<ColonyMachineMenu> {
    public ColonyMachineScreen(ColonyMachineMenu menu,Inventory inventory,Component title){super(menu,inventory,title,248,220);}
    @Override public void extractBackground(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta) {
        super.extractBackground(gui,mouseX,mouseY,delta);
        gui.fill(leftPos+82,topPos+53,leftPos+238,topPos+58,0xFF07121C);
        int length=menu.duration()==0?0:Math.clamp(menu.progress()*156/menu.duration(),0,156);
        gui.fill(leftPos+82,topPos+53,leftPos+82+length,topPos+58,0xFF65E5BE);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        gui.text(font,title,8,7,0xFFE5F5FF);
        String[] states={"idle","inputs","output_full","sunlight","heat","processing","maintenance_failure"};
        gui.text(font,Component.translatable("colony.space_fabric."+states[Math.clamp(menu.status(),0,states.length-1)]),8,21,MaintenancePresentation.color(menu.integrity()));
        gui.text(font,Component.translatable("gui.space_fabric.habitat.inputs"),12,36,0xFFB5D6E5);
        gui.text(font,Component.translatable("maintenance.space_fabric.progress",menu.progress()/20,menu.duration()/20),82,39,0xFFB5D6E5);
        gui.text(font,MaintenancePresentation.integrity(menu.integrity()),82,66,MaintenancePresentation.color(menu.integrity()));
        gui.text(font,Component.translatable("maintenance.space_fabric.coating",menu.coating(),4800),82,82,0xFFB5D6E5);
        gui.text(font,Component.translatable("maintenance.space_fabric.filter",menu.filter(),1200),82,98,0xFFB5D6E5);
        if(menu.integrity()>0&&menu.integrity()<=5000)gui.text(font,Component.translatable("maintenance.space_fabric.half_speed"),82,111,0xFFFFCD69);
        gui.text(font,playerInventoryTitle,44,122,0xFFB5D6E5);
    }
}

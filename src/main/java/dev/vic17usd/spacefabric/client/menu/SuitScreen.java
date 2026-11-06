package dev.vic17usd.spacefabric.client.menu;

import dev.vic17usd.spacefabric.menu.SuitMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class SuitScreen extends SpaceContainerScreen<SuitMenu> {
    public SuitScreen(SuitMenu menu,Inventory inventory,Component title){super(menu,inventory,title,176,184);}
    @Override protected void extractLabels(GuiGraphicsExtractor gui,int mouseX,int mouseY) {
        gui.text(font,title,8,7,0xFFE5F5FF);
        gui.text(font,Component.translatable("gui.space_fabric.suit_separate"),8,23,0xFFABCCD8);
        for(int i=0;i<4;i++)gui.text(font,Component.translatable("gui.space_fabric.suit_part_"+i),12+i*40,36,0xFFABCCD8);
        var tier=menu.suitTier();
        gui.text(font,Component.translatable("gui.space_fabric.suit_tier."+tier.path),8,76,tier==dev.vic17usd.spacefabric.equipment.SuitTier.NONE?0xFFE7B660:0xFF56D8BC);
        gui.text(font,playerInventoryTitle,8,92,0xFFABCCD8);
    }
}

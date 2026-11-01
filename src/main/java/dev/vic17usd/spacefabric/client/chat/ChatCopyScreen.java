package dev.vic17usd.spacefabric.client.chat;

import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Local snapshot, newest messages first; copies entire messages, including wrapped text. */
public final class ChatCopyScreen extends Screen {
    private final List<String> messages;
    private int page,rows;
    public ChatCopyScreen(List<String> messages){super(ChatTools.text("title"));this.messages=List.copyOf(messages);}
    @Override protected void init(){
        rows=Math.max(1,(height-90)/32);page=Math.clamp(page,0,Math.max(0,(messages.size()-1)/rows));
        addRenderableWidget(Button.builder(ChatTools.text("all"),b->copy(String.join("\n",messages.reversed()))).bounds(8,28,110,20).build()).active=!messages.isEmpty();
        for(int row=0;row<rows&&page*rows+row<messages.size();row++){
            String message=messages.get(page*rows+row);
            addRenderableWidget(Button.builder(ChatTools.text("copy"),b->copy(message)).bounds(width-66,56+row*32,58,20).tooltip(Tooltip.create(Component.literal(message))).build());
        }
        addRenderableWidget(Button.builder(Component.literal("<"),b->{page--;rebuildWidgets();}).bounds(8,height-26,26,20).build()).active=page>0;
        addRenderableWidget(Button.builder(Component.literal(">"),b->{page++;rebuildWidgets();}).bounds(38,height-26,26,20).build()).active=(page+1)*rows<messages.size();
        addRenderableWidget(Button.builder(ChatTools.text("back"),b->onClose()).bounds(width-92,height-26,84,20).build());
    }
    private void copy(String value){minecraft.keyboardHandler.setClipboard(value);ChatTools.notify("copied");}
    @Override public void onClose(){minecraft.setScreen(minecraft.gui.getChat().restoreChatScreen());}
    @Override public boolean isPauseScreen(){return false;}
    @Override public void extractRenderState(GuiGraphicsExtractor gui,int mx,int my,float delta){
        gui.fill(0,0,width,height,0xD015202A);gui.text(font,title,8,10,0xFFFFFFFF);
        for(int row=0;row<rows&&page*rows+row<messages.size();row++){
            var lines=font.split(Component.literal(messages.get(page*rows+row)),Math.max(40,width-88));
            for(int line=0;line<Math.min(2,lines.size());line++)gui.text(font,lines.get(line),8,57+row*32+line*10,0xFFE2EDF4);
        }
        super.extractRenderState(gui,mx,my,delta);
    }
}

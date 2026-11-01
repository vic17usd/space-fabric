package dev.vic17usd.spacefabric.client.chat;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.mixin.client.ChatMessagesAccessor;
import dev.vic17usd.spacefabric.mixin.client.ChatDraftAccessor;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;

public final class ChatTools {
    private static final java.util.Map<net.minecraft.client.gui.screens.Screen,java.util.List<Button>> TOOLS=new java.util.WeakHashMap<>();
    private static final AtomicBoolean BUSY=new AtomicBoolean();
    private static final ExecutorService COPIER=Executors.newSingleThreadExecutor(r->{var t=new Thread(r,"Space Fabric screenshot clipboard");t.setDaemon(true);return t;});
    private ChatTools() { }
    public static Component text(String key,Object... args){return Component.translatable("chat_tools.space_fabric."+key,args);}
    public static void notify(String key,Object... args){var c=Minecraft.getInstance();c.execute(()->{if(c.player!=null)c.player.sendOverlayMessage(text(key,args));});}
    public static void initialize(){
        ScreenEvents.AFTER_INIT.register((c,screen,width,height)->{
            if(!(screen instanceof ChatScreen))return;
            int x=Math.max(4,Math.min(width-148,(int)(net.minecraft.client.gui.components.ChatComponent.getWidth(c.options.chatWidth().get())*c.options.chatScale().get())-140));
            var copy=Button.builder(text("copy"),b->{
                var access=(ChatMessagesAccessor)c.gui.getChat();
                var messages=access.spaceFabric$messages().stream().filter(access.spaceFabric$visibleFilter()).map(m->m.content().getString()).toList();
                var draft=(ChatDraftAccessor)c.screen;draft.spaceFabric$initial(draft.spaceFabric$input().getValue());
                c.gui.getChat().preserveCurrentChatScreen();c.setScreen(new ChatCopyScreen(messages));
            }).bounds(x,height-42,66,18).tooltip(Tooltip.create(text("copy_hint"))).build();
            var capture=Button.builder(text("capture"),b->capture()).bounds(x+70,height-42,72,18).tooltip(Tooltip.create(text("capture_hint"))).build();
            Screens.getWidgets(screen).add(copy);Screens.getWidgets(screen).add(capture);
            // ChatScreen checks links before regular widgets; consume tool clicks first.
            boolean first=!TOOLS.containsKey(screen);TOOLS.put(screen,java.util.List.of(copy,capture));
            if(first)ScreenMouseEvents.allowMouseClick(screen).register((s,event)->{
                for(var button:TOOLS.getOrDefault(s,java.util.List.of()))if(button.isMouseOver(event.x(),event.y())&&event.button()==0){button.mouseClicked(event,false);return false;}
                return true;
            });
        });
    }
    private static void capture(){
        if(!BUSY.compareAndSet(false,true)){notify("busy");return;}
        var c=Minecraft.getInstance();
        try{
            Screenshot.takeScreenshot(c.getMainRenderTarget(),image->COPIER.execute(()->{
                Path file=null;
                try(image){
                    var directory=c.gameDirectory.toPath().resolve("screenshots");Files.createDirectories(directory);
                    file=Files.createTempFile(directory,"space-fabric-",".png");image.writeToFile(file);
                    var png=ImageIO.read(file.toFile());
                    if(png==null)throw new java.io.IOException("Invalid screenshot PNG");
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ClipboardImage(png),null);
                    notify("capture_copied");
                }catch(Exception|LinkageError failure){
                    SpaceFabric.LOGGER.warn("Screenshot clipboard failed",failure);
                    if(file!=null&&Files.isRegularFile(file)){
                        String saved=file.toAbsolutePath().toString();
                        c.execute(()->{c.keyboardHandler.setClipboard(saved);notify("capture_saved",saved);});
                    }else notify("capture_failed");
                }finally{BUSY.set(false);}
            }));
        }catch(RuntimeException failure){BUSY.set(false);SpaceFabric.LOGGER.warn("Screenshot capture failed",failure);notify("capture_failed");}
    }
}

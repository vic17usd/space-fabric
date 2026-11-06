package dev.vic17usd.spacefabric.client.chat;

import dev.vic17usd.spacefabric.SpaceFabric;
import dev.vic17usd.spacefabric.mixin.client.ChatMessagesAccessor;
import dev.vic17usd.spacefabric.mixin.client.ChatDraftAccessor;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
            // Leave room at the end of the input for the independent capture button.
            ((ChatDraftAccessor)screen).spaceFabric$input().setWidth(Math.max(20,width-82));
            var capture=Button.builder(text("capture"),b->capture()).bounds(width-76,height-22,72,18).tooltip(Tooltip.create(text("capture_hint"))).build();
            Screens.getWidgets(screen).add(capture);
            boolean first=!TOOLS.containsKey(screen);TOOLS.put(screen,java.util.List.of(capture));
            if(first){
                ScreenEvents.afterExtract(screen).register((s,gui,mx,my,delta)->{
                    var hit=hit(c,s,mx,my);if(hit==null)return;
                    boolean hover=hit.contains(mx,my);gui.nextStratum();
                    gui.fill(hit.x()-1,hit.y()-1,hit.x()+hit.size()+1,hit.y()+hit.size()+1,hover?0xEE536A7B:0xC022303C);
                    gui.pose().pushMatrix();gui.pose().translate(hit.x(),hit.y()).scale(hit.size()/8F);
                    gui.outline(0,0,6,6,0xFF8EA5B6);gui.outline(2,2,6,6,0xFFFFFFFF);gui.pose().popMatrix();
                    if(hover){var message=((ChatMessagesAccessor)c.gui.getChat()).spaceFabric$lines().get(hit.lineIndex()).parent().content();gui.setTooltipForNextFrame(c.font,text(ScreenshotChatMessage.matches(message)?"copy_image_hint":"copy_hint"),mx,my);}
                });
                // Consume icon clicks before vanilla handles message links or suggestions.
                ScreenMouseEvents.allowMouseClick(screen).register((s,event)->{
                    var hit=hit(c,s,event.x(),event.y());
                    if(event.button()==0&&hit!=null&&hit.contains(event.x(),event.y())){
                        var access=(ChatMessagesAccessor)c.gui.getChat();
                        var message=access.spaceFabric$lines().get(hit.lineIndex()).parent().content();
                        if(ScreenshotChatMessage.matches(message)){
                            var file=ScreenshotChatMessage.file(message,c.gameDirectory.toPath());
                            if(file.isPresent())copyScreenshot(file.get());else notify("capture_missing");
                        }else{c.keyboardHandler.setClipboard(message.getString());notify("copied");}
                        return false;
                    }
                    for(var button:TOOLS.getOrDefault(s,java.util.List.of()))if(button.isMouseOver(event.x(),event.y())&&event.button()==0){button.mouseClicked(event,false);return false;}
                    return true;
                });
            }
        });
    }
    private static ChatCopyHit hit(Minecraft c,net.minecraft.client.gui.screens.Screen screen,double mx,double my){
        var chat=c.gui.getChat();var access=(ChatMessagesAccessor)chat;
        var hit=ChatCopyHit.at(screen.width,screen.height,net.minecraft.client.gui.components.ChatComponent.getWidth(c.options.chatWidth().get()),c.options.chatScale().get(),access.spaceFabric$lineHeight(),access.spaceFabric$scroll(),access.spaceFabric$lines().size(),chat.getLinesPerPage(),mx,my);
        if(hit!=null&&!access.spaceFabric$visibleFilter().test(access.spaceFabric$lines().get(hit.lineIndex()).parent()))return null;
        return hit;
    }
    private static void putImage(Path file) throws java.io.IOException {
        SystemImageClipboard.copy(file);
    }
    private static void copyScreenshot(Path file){
        if(!BUSY.compareAndSet(false,true)){notify("busy");return;}
        COPIER.execute(()->{
            try{
                var folder=Minecraft.getInstance().gameDirectory.toPath().resolve("screenshots").toRealPath();
                var real=file.toRealPath();
                if(!real.startsWith(folder))throw new java.io.IOException("Screenshot outside screenshot directory");
                putImage(real);notify("capture_copied");
            }catch(Exception|LinkageError failure){SpaceFabric.LOGGER.warn("Screenshot image copy failed",failure);notify("capture_copy_failed");}
            finally{BUSY.set(false);}
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
                    putImage(file);
                    notify("capture_copied");
                }catch(Exception|LinkageError failure){
                    SpaceFabric.LOGGER.warn("Screenshot clipboard failed",failure);
                    if(file!=null&&Files.isRegularFile(file))notify("capture_copy_failed");else notify("capture_failed");
                }finally{BUSY.set(false);}
            }));
        }catch(RuntimeException failure){BUSY.set(false);SpaceFabric.LOGGER.warn("Screenshot capture failed",failure);notify("capture_failed");}
    }
}

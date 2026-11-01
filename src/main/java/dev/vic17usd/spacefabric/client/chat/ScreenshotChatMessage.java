package dev.vic17usd.spacefabric.client.chat;

import java.nio.file.*;
import java.util.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.TranslatableContents;

/** Uses vanilla's screenshot component and file link, independent of the client's language. */
public final class ScreenshotChatMessage {
    private ScreenshotChatMessage() { }
    private static Component success(Component message){
        if(message.getContents() instanceof TranslatableContents t&&t.getKey().equals("screenshot.success"))return message;
        for(var sibling:message.getSiblings()){var found=success(sibling);if(found!=null)return found;}
        return null;
    }
    public static boolean matches(Component message){return success(message)!=null;}
    public static Optional<Path> file(Component message,Path gameDirectory){
        var notice=success(message);if(notice==null)return Optional.empty();
        var translation=(TranslatableContents)notice.getContents();
        for(var arg:translation.getArgs())if(arg instanceof Component component){
            var found=linkedFile(component);
            if(found.isPresent()){
                var file=found.get().toAbsolutePath().normalize();
                var screenshots=gameDirectory.toAbsolutePath().normalize().resolve("screenshots");
                if(file.startsWith(screenshots)&&file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))return Optional.of(file);
            }
        }
        return Optional.empty();
    }
    private static Optional<Path> linkedFile(Component component){
        if(component.getStyle().getClickEvent() instanceof ClickEvent.OpenFile open){
            try{return Optional.of(Path.of(open.path()));}catch(InvalidPathException ignored){return Optional.empty();}
        }
        for(var sibling:component.getSiblings()){
            var result=linkedFile(sibling);if(result.isPresent())return result;
        }
        return Optional.empty();
    }
}

package dev.vic17usd.spacefabric.client.chat;

import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.imageio.ImageIO;
import net.minecraft.network.chat.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ScreenshotChatMessageTest {
    @TempDir Path game;
    private Component success(Path png){
        return Component.translatable("screenshot.success",Component.literal(png.getFileName().toString()).withStyle(s->s.withClickEvent(new ClickEvent.OpenFile(png))));
    }
    @Test void f2NoticeCopiesTheLinkedImagePixelsRatherThanTheNoticeText() throws Exception {
        var file=game.resolve("screenshots/2026-11-01_19.23.16.png");Files.createDirectories(file.getParent());
        var pixels=new BufferedImage(3,2,BufferedImage.TYPE_INT_ARGB);pixels.setRGB(2,1,0xFF123456);ImageIO.write(pixels,"png",file.toFile());
        var message=success(file);assertTrue(ScreenshotChatMessage.matches(message));
        var image=ClipboardImage.fromPng(ScreenshotChatMessage.file(message,game).orElseThrow());
        assertFalse(image.isDataFlavorSupported(DataFlavor.stringFlavor));
        assertEquals(0xFF123456,((BufferedImage)image.getTransferData(DataFlavor.imageFlavor)).getRGB(2,1));
    }
    @Test void screenshotIdentificationUsesTheTranslationKeyAndSurvivesWrappedComponents(){
        var png=game.resolve("screenshots/captura con espacios.png");
        var wrapped=Component.literal("[Cliente] ").append(success(png));
        assertTrue(ScreenshotChatMessage.matches(wrapped));assertEquals(png.toAbsolutePath(),ScreenshotChatMessage.file(wrapped,game).orElseThrow());
        assertFalse(ScreenshotChatMessage.matches(Component.literal("Captura de pantalla guardada como captura.png")));
        assertFalse(ScreenshotChatMessage.matches(Component.literal("Screenshot saved as captura.png")));
    }
    @Test void ordinaryMessagesAndOtherLocalFilesAreNotTreatedAsScreenshots(){
        assertTrue(ScreenshotChatMessage.file(Component.literal("Hola"),game).isEmpty());
        assertTrue(ScreenshotChatMessage.file(success(game.resolve("private.png")),game).isEmpty());
        assertTrue(ScreenshotChatMessage.file(success(game.resolve("screenshots/note.txt")),game).isEmpty());
        assertTrue(ScreenshotChatMessage.file(success(game.resolve("screenshots/../private.png")),game).isEmpty());
    }
    @Test void missingOrInvalidImagesCannotBecomeTextClipboardPayloads() throws Exception {
        assertThrows(java.io.IOException.class,()->ClipboardImage.fromPng(game.resolve("missing.png")));
        var invalid=game.resolve("invalid.png");Files.writeString(invalid,"not a PNG");
        assertThrows(java.io.IOException.class,()->ClipboardImage.fromPng(invalid));
    }
}

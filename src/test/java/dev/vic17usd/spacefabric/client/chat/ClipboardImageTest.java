package dev.vic17usd.spacefabric.client.chat;

import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClipboardImageTest {
    @Test void chatAccessorsMatchTheActual261Fields() throws Exception {
        assertEquals(java.util.List.class,net.minecraft.client.gui.components.ChatComponent.class.getDeclaredField("trimmedMessages").getType());
        assertEquals(int.class,net.minecraft.client.gui.components.ChatComponent.class.getDeclaredField("chatScrollbarPos").getType());
        assertEquals(int.class,net.minecraft.client.gui.components.ChatComponent.class.getDeclaredMethod("getLineHeight").getReturnType());
        assertEquals(java.util.function.Predicate.class,net.minecraft.client.gui.components.ChatComponent.class.getDeclaredField("visibleMessageFilter").getType());
        assertEquals(net.minecraft.client.gui.components.EditBox.class,net.minecraft.client.gui.screens.ChatScreen.class.getDeclaredField("input").getType());
        assertEquals(String.class,net.minecraft.client.gui.screens.ChatScreen.class.getDeclaredField("initial").getType());
    }
    @Test void clipboardPayloadContainsTheFullImageAndRejectsTextRequests() throws Exception {
        var png=new BufferedImage(3,2,BufferedImage.TYPE_INT_ARGB);png.setRGB(2,1,0xFF123456);
        var transfer=new ClipboardImage(png);
        assertArrayEquals(new DataFlavor[]{DataFlavor.imageFlavor},transfer.getTransferDataFlavors());
        assertSame(png,transfer.getTransferData(DataFlavor.imageFlavor));
        assertFalse(transfer.isDataFlavorSupported(DataFlavor.stringFlavor));
        assertThrows(UnsupportedFlavorException.class,()->transfer.getTransferData(DataFlavor.stringFlavor));
        assertEquals(0xFF123456,((BufferedImage)transfer.getTransferData(DataFlavor.imageFlavor)).getRGB(2,1));
    }
}

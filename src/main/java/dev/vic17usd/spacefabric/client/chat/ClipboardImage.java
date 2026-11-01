package dev.vic17usd.spacefabric.client.chat;

import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;

/** Kept separate from OS clipboard access so image transfer can be tested without a desktop. */
public record ClipboardImage(BufferedImage image) implements Transferable {
    public DataFlavor[] getTransferDataFlavors(){return new DataFlavor[]{DataFlavor.imageFlavor};}
    public boolean isDataFlavorSupported(DataFlavor flavor){return DataFlavor.imageFlavor.equals(flavor);}
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if(!isDataFlavorSupported(flavor))throw new UnsupportedFlavorException(flavor);
        return image;
    }
}

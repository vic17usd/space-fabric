package dev.vic17usd.spacefabric.client.chat;

import com.sun.jna.*;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.win32.StdCallLibrary;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.*;

/** Win32 clipboard access works even though Minecraft disables AWT's desktop toolkit. */
final class WindowsImageClipboard {
    static final int CF_DIB=8;
    interface Api {
        Pointer allocate(int size);Pointer lock(Pointer handle);void unlock(Pointer handle);void free(Pointer handle);
        boolean open(Pointer owner);void close();boolean empty();Pointer set(int format,Pointer handle);int pngFormat();
    }
    interface User extends StdCallLibrary {
        boolean OpenClipboard(Pointer owner);boolean CloseClipboard();boolean EmptyClipboard();
        Pointer SetClipboardData(int format,Pointer handle);int RegisterClipboardFormatW(WString name);
    }
    interface Kernel extends StdCallLibrary {
        Pointer GlobalAlloc(int flags,SIZE_T bytes);Pointer GlobalLock(Pointer handle);
        boolean GlobalUnlock(Pointer handle);Pointer GlobalFree(Pointer handle);
    }
    private static final class NativeApi implements Api {
        private final User user=Native.load("user32",User.class);
        private final Kernel kernel=Native.load("kernel32",Kernel.class);
        public Pointer allocate(int size){return kernel.GlobalAlloc(0x42,new SIZE_T(size));}
        public Pointer lock(Pointer h){return kernel.GlobalLock(h);}
        public void unlock(Pointer h){kernel.GlobalUnlock(h);}
        public void free(Pointer h){kernel.GlobalFree(h);}
        public boolean open(Pointer owner){return user.OpenClipboard(owner);}
        public void close(){user.CloseClipboard();}
        public boolean empty(){return user.EmptyClipboard();}
        public Pointer set(int format,Pointer h){return user.SetClipboardData(format,h);}
        public int pngFormat(){return user.RegisterClipboardFormatW(new WString("PNG"));}
    }
    static byte[] dib(BufferedImage image){
        int size=Math.multiplyExact(Math.multiplyExact(image.getWidth(),image.getHeight()),4);
        var data=ByteBuffer.allocate(Math.addExact(40,size)).order(ByteOrder.LITTLE_ENDIAN);
        data.putInt(40).putInt(image.getWidth()).putInt(image.getHeight()).putShort((short)1).putShort((short)32);
        data.putInt(0).putInt(size).putInt(0).putInt(0).putInt(0).putInt(0);
        // CF_DIB is bottom-up BGRA. Screenshots are opaque; RGB is flattened over black.
        for(int y=image.getHeight()-1;y>=0;y--)for(int x=0;x<image.getWidth();x++){
            int argb=image.getRGB(x,y),a=argb>>>24;
            data.put((byte)((argb&255)*a/255)).put((byte)(((argb>>>8)&255)*a/255)).put((byte)(((argb>>>16)&255)*a/255)).put((byte)255);
        }
        return data.array();
    }
    static void copy(long owner,BufferedImage image,byte[] png) throws IOException {
        if(owner==0)throw new IOException("Missing Minecraft clipboard owner window");
        write(new NativeApi(),new Pointer(owner),dib(image),png);
    }
    private static Pointer memory(Api api,byte[] bytes) throws IOException {
        var handle=api.allocate(bytes.length);if(handle==null)throw new IOException("GlobalAlloc failed");
        var address=api.lock(handle);
        if(address==null){api.free(handle);throw new IOException("GlobalLock failed");}
        try{address.write(0,bytes,0,bytes.length);}catch(RuntimeException failure){api.unlock(handle);api.free(handle);throw failure;}
        api.unlock(handle);return handle;
    }
    static void write(Api api,Pointer owner,byte[] dib,byte[] png) throws IOException {
        Pointer bitmap=null,original=null;boolean opened=false;
        try{
            bitmap=memory(api,dib);int format=api.pngFormat();
            if(format!=0)original=memory(api,png);
            for(int attempt=0;attempt<8&&!opened;attempt++){
                opened=api.open(owner);
                if(!opened)try{Thread.sleep(25);}catch(InterruptedException interrupted){Thread.currentThread().interrupt();throw new IOException("Clipboard copy interrupted",interrupted);}
            }
            if(!opened)throw new IOException("Clipboard is busy");
            if(!api.empty())throw new IOException("EmptyClipboard failed");
            if(api.set(CF_DIB,bitmap)==null)throw new IOException("SetClipboardData failed");
            bitmap=null; // Windows owns this allocation from this point onward.
            if(original!=null&&api.set(format,original)!=null)original=null;
        }finally{
            if(opened)api.close();
            if(bitmap!=null)api.free(bitmap);
            if(original!=null)api.free(original);
        }
    }
}

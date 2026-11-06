package dev.vic17usd.spacefabric.client.chat;

import com.sun.jna.*;
import java.awt.image.BufferedImage;
import java.nio.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WindowsImageClipboardTest {
    private static final class Fake implements WindowsImageClipboard.Api,AutoCloseable {
        final Map<Pointer,Memory> memory=new LinkedHashMap<>();final Set<Pointer> freed=new HashSet<>();
        final Map<Integer,Pointer> published=new HashMap<>();int attempts,closeCount,busy;boolean failDib,failPng,failLock;
        public Pointer allocate(int size){var p=new Memory(size);memory.put(p,p);return p;}
        public Pointer lock(Pointer h){return failLock?null:h;}
        public void unlock(Pointer h){assertFalse(freed.contains(h));}
        public void free(Pointer h){assertTrue(freed.add(h),"Double free");assertFalse(published.containsValue(h),"OS-owned memory was freed");}
        public boolean open(Pointer owner){assertNotNull(owner);return ++attempts>busy;}
        public void close(){closeCount++;}
        public boolean empty(){return true;}
        public Pointer set(int format,Pointer h){if((format==8&&failDib)||(format==1000&&failPng))return null;published.put(format,h);return h;}
        public int pngFormat(){return 1000;}
        public void closeMemory(){memory.values().forEach(Memory::close);}
    }
    @Test void dibHasTheRealWindowsHeaderAndBottomUpBgraPixels(){
        var image=new BufferedImage(2,2,BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0,0,0xFF112233);image.setRGB(0,1,0xFF778899);
        var dib=ByteBuffer.wrap(WindowsImageClipboard.dib(image)).order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(56,dib.capacity());assertEquals(40,dib.getInt(0));assertEquals(2,dib.getInt(4));assertEquals(2,dib.getInt(8));
        assertEquals(1,dib.getShort(12));assertEquals(32,dib.getShort(14));assertEquals(16,dib.getInt(20));
        assertEquals(0xFF778899,dib.getInt(40));assertEquals(0xFF112233,dib.getInt(48));
    }
    @Test void busyClipboardRetriesAndTransfersBothImageFormatsWithoutFreeingThem() throws Exception {
        var api=new Fake();api.busy=2;
        try{
            WindowsImageClipboard.write(api,new Pointer(7),new byte[]{1,2,3,4},new byte[]{9,8,7});
            assertEquals(3,api.attempts);assertEquals(1,api.closeCount);assertTrue(api.freed.isEmpty());
            assertArrayEquals(new byte[]{1,2,3,4},api.published.get(8).getByteArray(0,4));
            assertArrayEquals(new byte[]{9,8,7},api.published.get(1000).getByteArray(0,3));
        }finally{api.closeMemory();}
    }
    @Test void failedPublishClosesTheClipboardAndReclaimsUntransferredMemory(){
        var api=new Fake();api.failDib=true;
        try{
            assertThrows(java.io.IOException.class,()->WindowsImageClipboard.write(api,new Pointer(7),new byte[]{1},new byte[]{2}));
            assertEquals(1,api.closeCount);assertEquals(2,api.freed.size());assertTrue(api.published.isEmpty());
        }finally{api.closeMemory();}
    }
    @Test void optionalPngFailureKeepsTheStandardDibImageAvailable() throws Exception {
        var api=new Fake();api.failPng=true;
        try{
            WindowsImageClipboard.write(api,new Pointer(7),new byte[]{1},new byte[]{2});
            assertNotNull(api.published.get(8));assertEquals(1,api.freed.size());assertEquals(1,api.closeCount);
        }finally{api.closeMemory();}
    }
    @Test void allocationLockFailureIsCleanedUpBeforeTouchingTheClipboard(){
        var api=new Fake();api.failLock=true;
        try{
            assertThrows(java.io.IOException.class,()->WindowsImageClipboard.write(api,new Pointer(7),new byte[]{1},new byte[]{2}));
            assertEquals(0,api.attempts);assertEquals(0,api.closeCount);assertEquals(1,api.freed.size());
        }finally{api.closeMemory();}
    }
}

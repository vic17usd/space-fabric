package dev.vic17usd.spacefabric.client.chat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatCopyHitTest {
    @Test void oneClickHitsTheActualWrappedLineIncludingItsScrollOffset(){
        var newest=ChatCopyHit.at(426,240,320,1,9,0,30,10,40,195);
        assertEquals(0,newest.lineIndex());assertTrue(newest.contains(newest.x()+2,newest.y()+2));
        var scrolled=ChatCopyHit.at(426,240,320,1,9,7,30,10,40,186);
        assertEquals(8,scrolled.lineIndex());
    }
    @Test void scaledAndSpacedChatUsesTheNativeBaseline(){
        for(double scale:new double[]{.3,.5,.75,1})for(int spacing:new int[]{9,13,18}){
            double baseline=Math.floor((240-40)/scale)*scale;
            for(int row=0;row<6;row++){
                var hit=ChatCopyHit.at(426,240,320,scale,spacing,3,30,6,40,baseline-(row+.5)*spacing*scale);
                assertNotNull(hit);assertEquals(3+row,hit.lineIndex());
                assertTrue(hit.x()+hit.size()<=424);assertTrue(hit.size()<=spacing*scale);
            }
        }
    }
    @Test void inputAreaUnloadedRowsAndEmptyHistoryCannotBeCopied(){
        assertNull(ChatCopyHit.at(426,240,320,1,9,0,30,10,40,225));
        assertNull(ChatCopyHit.at(426,240,320,1,9,0,0,10,40,195));
        assertNull(ChatCopyHit.at(426,240,320,1,9,28,30,10,40,177));
        assertNull(ChatCopyHit.at(426,240,320,1,9,0,30,10,420,195));
    }
    @Test void clickingAnyWrappedLineCopiesItsFullParentMessage(){
        var parent=new net.minecraft.client.multiplayer.chat.GuiMessage(0,net.minecraft.network.chat.Component.literal("Primera línea y segunda línea completas"),null,null,null);
        var a=new net.minecraft.client.multiplayer.chat.GuiMessage.Line(parent,net.minecraft.util.FormattedCharSequence.EMPTY,false);
        var b=new net.minecraft.client.multiplayer.chat.GuiMessage.Line(parent,net.minecraft.util.FormattedCharSequence.EMPTY,true);
        assertSame(a.parent(),b.parent());assertEquals(parent.content().getString(),b.parent().content().getString());
    }
}

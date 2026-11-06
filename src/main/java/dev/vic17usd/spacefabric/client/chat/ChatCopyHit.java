package dev.vic17usd.spacefabric.client.chat;

/** Mirrors vanilla's scaled chat baseline; scroll indexes refer to wrapped native lines. */
public record ChatCopyHit(int lineIndex,int x,int y,int size) {
    public boolean contains(double mx,double my){return mx>=x&&mx<x+size&&my>=y&&my<y+size;}
    public static ChatCopyHit at(int width,int height,int chatWidth,double scale,int lineHeight,int scroll,int count,int visible,double mx,double my) {
        if(scale<=0||lineHeight<=0||visible<=0)return null;
        double step=lineHeight*scale,baseline=Math.floor((height-40)/scale)*scale;
        int row=(int)Math.floor((baseline-my)/step);
        if(row<0||row>=visible||scroll+row<0||scroll+row>=count)return null;
        int size=Math.max(1,Math.min(12,(int)Math.floor(step)-1));
        int x=Math.max(0,Math.min(width-size-2,(int)Math.ceil(chatWidth+8*scale)));
        if(mx<4*scale||mx>=x+size)return null;
        int y=(int)Math.floor(baseline-(row+1)*step+(step-size)/2);
        return new ChatCopyHit(scroll+row,x,y,size);
    }
}

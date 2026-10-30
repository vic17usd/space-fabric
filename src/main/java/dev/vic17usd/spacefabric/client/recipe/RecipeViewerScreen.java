package dev.vic17usd.spacefabric.client.recipe;

import dev.vic17usd.spacefabric.item.ModItems;
import dev.vic17usd.spacefabric.recipe.RecipeCard;
import dev.vic17usd.spacefabric.recipe.RecipeSearch;
import dev.vic17usd.spacefabric.recipe.RecipeViewerNetwork;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/** Searchable registry catalog and server-backed recipe cards. All interaction is read-only. */
public final class RecipeViewerScreen extends Screen {
    private static final AtomicInteger SEQUENCES=new AtomicInteger();
    private final Screen previous;
    private List<ItemStack> all=List.of(),filtered=List.of();
    private ItemStack selected=new ItemStack(ModItems.ROCKET),hovered=ItemStack.EMPTY;
    private EditBox search;
    private Button previousRecipe,nextRecipe,previousItems,nextItems,recipesButton,usagesButton;
    private RecipeCard card;
    private int left,top,panelWidth,panelHeight,columns,rows,itemPage,recipePage,total,sequence;
    private boolean usages,loading,unsupported;
    private long requestedAt;
    public RecipeViewerScreen(Screen previous) {super(Component.translatable("gui.space_fabric.recipe_viewer"));this.previous=previous;}
    @Override protected void init() {
        String query=search==null?"":search.getValue();
        panelWidth=Math.min(width-12,560);panelHeight=Math.min(height-12,330);
        left=(width-panelWidth)/2;top=(height-panelHeight)/2;
        columns=Math.max(1,(panelWidth-204)/20);rows=Math.max(1,(panelHeight-112)/20);
        if(all.isEmpty()&&minecraft.level!=null)all=BuiltInRegistries.ITEM.stream()
            .map(item->item.getDefaultInstance()).filter(s->!s.isEmpty()&&s.getItem().isEnabled(minecraft.level.enabledFeatures()))
            .sorted(Comparator.<ItemStack,Boolean>comparing(s->!BuiltInRegistries.ITEM.getKey(s.getItem()).getNamespace().equals("space_fabric"))
                .thenComparing(s->s.getHoverName().getString(),String.CASE_INSENSITIVE_ORDER)).toList();
        search=addRenderableWidget(new EditBox(font,left+196,top+34,Math.max(20,panelWidth-204),18,Component.translatable("gui.space_fabric.recipe_search")));
        search.setMaxLength(128);search.setHint(Component.translatable("gui.space_fabric.recipe_search"));
        search.setResponder(text->{itemPage=0;filter(text);updateButtons();});search.setValue(query);filter(query);
        recipesButton=button("recipes",8,34,84,()->select(selected,false));
        usagesButton=button("usages",96,34,84,()->select(selected,true));
        button("refresh",128,8,60,()->request(recipePage));
        previousRecipe=button("previous",8,panelHeight-55,48,()->request(recipePage-1));
        nextRecipe=button("next",132,panelHeight-55,48,()->request(recipePage+1));
        button("back",8,panelHeight-27,172,this::onClose);
        previousItems=button("previous",196,panelHeight-27,46,()->{itemPage--;updateButtons();});
        nextItems=button("next",panelWidth-54,panelHeight-27,46,()->{itemPage++;updateButtons();});
        request(recipePage);
    }
    private Button button(String name,int x,int y,int w,Runnable action) {
        return addRenderableWidget(Button.builder(Component.translatable("gui.space_fabric.viewer."+name),b->action.run()).bounds(left+x,top+y,w,18).build());
    }
    private void filter(String query) {
        filtered=all.stream().filter(s->RecipeSearch.matches(query,s.getHoverName().getString(),BuiltInRegistries.ITEM.getKey(s.getItem()).toString())).toList();
        itemPage=Math.clamp(itemPage,0,Math.max(0,(filtered.size()-1)/(columns*rows)));
    }
    private void select(ItemStack stack,boolean usages) {if(stack.isEmpty())return;selected=stack.copy();this.usages=usages;request(0);}
    private void request(int page) {
        unsupported=!ClientPlayNetworking.canSend(RecipeViewerNetwork.Request.TYPE);
        card=null;total=0;recipePage=Math.max(0,page);loading=!unsupported;sequence=SEQUENCES.incrementAndGet();requestedAt=Util.getMillis();
        if(!unsupported)ClientPlayNetworking.send(new RecipeViewerNetwork.Request(sequence,BuiltInRegistries.ITEM.getKey(selected.getItem()).toString(),usages,recipePage));
        updateButtons();
    }
    public void accept(RecipeViewerNetwork.Reply reply) {
        if(reply.sequence()!=sequence)return;
        recipePage=reply.page();total=reply.total();card=reply.card().orElse(null);loading=false;updateButtons();
    }
    private void updateButtons() {
        if(previousRecipe==null)return;
        previousRecipe.active=!loading&&recipePage>0;nextRecipe.active=!loading&&recipePage+1<total;
        previousItems.active=itemPage>0;nextItems.active=(itemPage+1)*columns*rows<filtered.size();
        recipesButton.active=usages;usagesButton.active=!usages;
    }
    @Override protected void setInitialFocus(){if(recipesButton!=null)setInitialFocus(recipesButton);}
    @Override public void tick() {if(loading&&Util.getMillis()-requestedAt>10000){loading=false;unsupported=true;updateButtons();}}
    @Override public boolean isPauseScreen(){return false;}
    /** Keep the world visible: the default Screen background adds blur and a dark veil. */
    @Override public void extractBackground(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta) {
        if(minecraft.level==null)super.extractBackground(gui,mouseX,mouseY,delta);
    }
    @Override public void onClose(){minecraft.setScreen(previous);}
    private void slot(GuiGraphicsExtractor gui,List<ItemStack> choices,int x,int y,int mouseX,int mouseY) {
        gui.fill(x,y,x+16,y+16,0x550A1721);gui.outline(x-1,y-1,18,18,0x99405968);
        if(choices.isEmpty())return;
        var stack=choices.get((int)((Util.getMillis()/1000)%choices.size()));
        gui.item(stack,x,y);gui.itemDecorations(font,stack,x,y);
        if(mouseX>=x-1&&mouseX<x+17&&mouseY>=y-1&&mouseY<y+17){hovered=stack;gui.outline(x-1,y-1,18,18,0xFF56D8BC);}
    }
    @Override public void extractRenderState(GuiGraphicsExtractor gui,int mouseX,int mouseY,float delta) {
        hovered=ItemStack.EMPTY;
        // An outline rather than a filled panel leaves the catalog background transparent.
        gui.outline(left-1,top-1,panelWidth+2,panelHeight+2,0x99668D9D);
        gui.verticalLine(left+188,top+30,top+panelHeight-8,0x99405968);
        gui.text(font,title,left+8,top+12,0xFFE6F5FF);
        gui.text(font,Component.translatable("gui.space_fabric.viewer.items",filtered.size()),left+196,top+12,0xFFABCCD8);
        gui.item(selected,left+8,top+61);
        gui.text(font,font.plainSubstrByWidth(selected.getHoverName().getString(),152),left+30,top+65,0xFFE6F5FF);
        int offset=itemPage*columns*rows;
        for(int i=0;i<columns*rows&&offset+i<filtered.size();i++)
            slot(gui,List.of(filtered.get(offset+i)),left+196+(i%columns)*20,top+61+(i/columns)*20,mouseX,mouseY);
        if(card!=null) {
            Component category=Component.translatable("gui.space_fabric.viewer.kind."+card.kind());
            if(category.getString().equals("gui.space_fabric.viewer.kind."+card.kind()))category=Component.literal(card.kind());
            gui.text(font,font.plainSubstrByWidth(category.getString(),172),left+8,top+83,0xFF56D8BC);
            for(int i=0;i<card.inputs().size();i++)
                slot(gui,card.inputs().get(i),left+16+(i%Math.clamp(card.width(),1,3))*20,top+102+(i/Math.clamp(card.width(),1,3))*20,mouseX,mouseY);
            gui.text(font,"→",left+94,top+(card.kind().equals("colony")?108:126),0xFFE6F5FF);
            if(card.kind().equals("colony")) {
                // Machine outputs are simultaneous products, not interchangeable alternatives.
                for(int i=0;i<card.outputs().size();i++)slot(gui,List.of(card.outputs().get(i)),left+118+(i%2)*20,top+102+(i/2)*20,mouseX,mouseY);
            } else slot(gui,card.outputs(),left+120,top+122,mouseX,mouseY);
            if(!card.station().isEmpty())slot(gui,List.of(card.station()),left+160,top+102,mouseX,mouseY);
            if(card.duration()>0)gui.text(font,Component.translatable("gui.space_fabric.viewer.time",card.duration()/20F,card.experience()),left+8,top+Math.min(169,panelHeight-70),0xFFABCCD8);
            if(panelHeight>=270)gui.text(font,font.plainSubstrByWidth(card.id(),172),left+8,top+190,0xFF7896A4);
        } else {
            String state=loading?"loading":unsupported?"unavailable":"empty";
            gui.textWithWordWrap(font,Component.translatable("gui.space_fabric.viewer."+state),left+8,top+109,172,0xFFABCCD8);
        }
        gui.centeredText(font,(total==0?0:recipePage+1)+" / "+total,left+94,top+panelHeight-50,0xFFE6F5FF);
        gui.centeredText(font,(filtered.isEmpty()?0:itemPage+1)+" / "+Math.max(1,(filtered.size()+columns*rows-1)/(columns*rows)),left+(196+panelWidth)/2,top+panelHeight-42,0xFFABCCD8);
        super.extractRenderState(gui,mouseX,mouseY,delta);
        if(!hovered.isEmpty()) {
            var tooltip=new ArrayList<>(Screen.getTooltipFromItem(minecraft,hovered));tooltip.add(Component.translatable("gui.space_fabric.viewer.click_help"));
            if(card!=null&&card.id().startsWith("space_fabric:colony/venus_core_extraction"))tooltip.add(dev.vic17usd.spacefabric.mining.ProspectingSystem.text("browser"));
            if(card!=null&&card.id().equals("space_fabric:colony/geothermal"))tooltip.add(Component.translatable("balance.space_fabric.geothermal"));
            if(card!=null&&(card.id().equals("space_fabric:colony/venus_composite")||card.id().equals("space_fabric:colony/venus_composite_iron")))tooltip.add(Component.translatable("balance.space_fabric.composite"));
            gui.setTooltipForNextFrame(font,tooltip,Optional.empty(),mouseX,mouseY);
        }
    }
    @Override public boolean mouseClicked(MouseButtonEvent event,boolean doubleClick) {
        if(!hovered.isEmpty()&&(event.button()==0||event.button()==1)){clearFocus();search.setFocused(false);select(hovered,event.button()==1);return true;}
        return super.mouseClicked(event,doubleClick);
    }
    @Override public boolean keyPressed(KeyEvent event) {
        if(!search.isFocused()&&!hovered.isEmpty()&&(event.key()==GLFW.GLFW_KEY_R||event.key()==GLFW.GLFW_KEY_U)) {
            select(hovered,event.key()==GLFW.GLFW_KEY_U);return true;
        }
        return super.keyPressed(event);
    }
}

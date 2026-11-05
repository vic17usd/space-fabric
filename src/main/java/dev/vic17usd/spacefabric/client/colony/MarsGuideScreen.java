package dev.vic17usd.spacefabric.client.colony;

import dev.vic17usd.spacefabric.client.recipe.RecipeViewerClient;
import dev.vic17usd.spacefabric.colony.*;
import dev.vic17usd.spacefabric.recipe.*;
import java.util.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;

/** Read-only illustrated tutorial. Scenes are examples; server snapshots alone describe the expedition. */
public final class MarsGuideScreen extends Screen {
    private final MarsGuideAnimation animation = new MarsGuideAnimation();
    private MarsGuideNetwork.State state;
    private MarsGuideAnimation.Layout layout;
    private Button previous, next, pause, previousRecipe, nextRecipe;
    private final List<Button> recipes = new ArrayList<>();
    private ItemStack hovered = ItemStack.EMPTY;
    private final List<ItemStack> recipeItems = List.of(new ItemStack(ColonyContent.HABITAT_CONTROLLER), new ItemStack(ColonyContent.PRESSURE_HULL),
            new ItemStack(ColonyContent.EMPTY_CANISTER), new ItemStack(ColonyContent.CHARGED_POWER_CELL), new ItemStack(ColonyContent.WATER_CANISTER),
            new ItemStack(ColonyContent.ELECTROLYZER), new ItemStack(ColonyContent.OXYGEN_CANISTER));
    private int recipeIndex, sequence, recipePage, recipeTotal;
    private RecipeCard recipe;
    private boolean loading, unavailable;
    private long requestedAt;
    private int mouseX, mouseY;

    public MarsGuideScreen(MarsGuideNetwork.State state) { super(text("title")); this.state = state; }
    private static Component text(String key, Object... args) { return Component.translatable("mars.tutorial." + key, args); }
    public void accept(MarsGuideNetwork.State state) { this.state = state; }
    public void accept(RecipeViewerNetwork.Reply reply) {
        if (!loading || reply.sequence() != sequence) return;
        recipe = reply.card().orElse(null); recipePage = reply.page(); recipeTotal = reply.total(); loading = false;
        updateButtons();
    }
    @Override protected void init() {
        layout = MarsGuideAnimation.Layout.fit(width, height);
        int l = layout.left(), t = layout.top(), w = layout.width(), h = layout.height();
        for (int i = 0; i < MarsGuideAnimation.Chapter.values().length; i++) {
            final int chapter = i;
            addRenderableWidget(Button.builder(Component.literal(Integer.toString(i + 1)), b -> select(chapter))
                    .tooltip(Tooltip.create(text(MarsGuideAnimation.Chapter.values()[i].key() + ".title")))
                    .bounds(l + w - 188 + i * 22, t + 8, 20, 18).build());
        }
        previous = button("previous", l + 12, t + h - 43, 64, () -> select(animation.chapter().ordinal() - 1));
        pause = button(animation.playing() ? "pause" : "play", l + 80, t + h - 43, 60, () -> { animation.toggle(); updateButtons(); });
        button("replay", l + 144, t + h - 43, 64, animation::restart);
        next = button("next", l + w - 88, t + h - 43, 76, () -> select(animation.chapter().ordinal() + 1));
        recipes.clear();
        for (int i = 0; i < recipeItems.size(); i++) {
            final int index = i;
            var b = addRenderableWidget(Button.builder(Component.empty(), clicked -> { recipeIndex = index; requestRecipe(0); })
                    .tooltip(Tooltip.create(recipeItems.get(i).getHoverName()))
                    .bounds(layout.sceneX() + i * 23, layout.sceneY() + 2, 22, 20).build());
            recipes.add(b);
        }
        previousRecipe = button("recipe_previous", layout.sceneX() + 168, layout.sceneY() + 2, 40, () -> requestRecipe(recipePage - 1));
        nextRecipe = button("recipe_next", layout.sceneX() + 212, layout.sceneY() + 2, 40, () -> requestRecipe(recipePage + 1));
        updateButtons();
    }
    private Button button(String key, int x, int y, int width, Runnable action) {
        return addRenderableWidget(Button.builder(text(key), b -> action.run()).bounds(x, y, width, 18).build());
    }
    private void select(int chapter) {
        animation.select(chapter);
        if (animation.chapter() == MarsGuideAnimation.Chapter.RECIPES && recipe == null && !loading) requestRecipe(0);
        updateButtons();
    }
    private void updateButtons() {
        if (previous == null) return;
        previous.active = animation.chapter().ordinal() > 0;
        next.active = animation.chapter().ordinal() + 1 < MarsGuideAnimation.Chapter.values().length;
        pause.setMessage(text(animation.playing() ? "pause" : "play"));
        boolean show = animation.chapter() == MarsGuideAnimation.Chapter.RECIPES;
        for (int i = 0; i < recipes.size(); i++) { recipes.get(i).visible = show; recipes.get(i).active = i != recipeIndex || unavailable || recipe == null && !loading; }
        previousRecipe.visible = nextRecipe.visible = show;
        previousRecipe.active = !loading && recipePage > 0;
        nextRecipe.active = !loading && recipePage + 1 < recipeTotal;
    }
    private void requestRecipe(int page) {
        sequence = RecipeViewerClient.nextSequence(); recipePage = Math.max(0, page); recipe = null; recipeTotal = 0;
        unavailable = !ClientPlayNetworking.canSend(RecipeViewerNetwork.Request.TYPE); loading = !unavailable; requestedAt = Util.getMillis();
        if (loading) ClientPlayNetworking.send(new RecipeViewerNetwork.Request(sequence,
                BuiltInRegistries.ITEM.getKey(recipeItems.get(recipeIndex).getItem()).toString(), false, recipePage));
        updateButtons();
    }
    @Override public void tick() {
        animation.tick();
        if (loading && Util.getMillis() - requestedAt > 10000) { loading = false; unavailable = true; updateButtons(); }
    }
    @Override public boolean isPauseScreen() { return true; }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) { }
    @Override public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_LEFT) { select(animation.chapter().ordinal() - 1); return true; }
        if (event.key() == GLFW.GLFW_KEY_RIGHT) { select(animation.chapter().ordinal() + 1); return true; }
        if (event.key() == GLFW.GLFW_KEY_SPACE) { animation.toggle(); updateButtons(); return true; }
        if (event.key() == GLFW.GLFW_KEY_HOME) { animation.restart(); return true; }
        return super.keyPressed(event);
    }
    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int y = layout.top() + layout.height() - 52;
        if (event.button() == 0 && event.x() >= layout.left() + 12 && event.x() <= layout.left() + layout.width() - 12
                && event.y() >= y - 3 && event.y() <= y + 5) {
            animation.seek((event.x() - layout.left() - 12) / (layout.width() - 24)); return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
    @Override public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX; this.mouseY = mouseY;
        hovered = ItemStack.EMPTY;
        int l = layout.left(), t = layout.top(), w = layout.width(), h = layout.height();
        gui.fill(l, t, l + w, t + h, 0xF010202A);
        gui.outline(l, t, w, h, 0xFF466774);
        gui.text(font, title, l + 12, t + 13, 0xFFFFD29C);
        var chapter = animation.chapter();
        gui.text(font, text("chapter", chapter.ordinal() + 1, MarsGuideAnimation.Chapter.values().length,
                text(chapter.key() + ".title")), l + 12, t + 34, 0xFFE9F1F2);
        int sceneHeight = chapter == MarsGuideAnimation.Chapter.RECIPES ? Math.max(108, layout.sceneHeight()) : layout.sceneHeight();
        gui.fill(layout.sceneX(), layout.sceneY(), layout.sceneX() + layout.sceneWidth(), layout.sceneY() + sceneHeight, 0xFF152D38);
        gui.enableScissor(layout.sceneX(), layout.sceneY(), layout.sceneX() + layout.sceneWidth(), layout.sceneY() + sceneHeight);
        double progress = animation.progress(delta);
        var pose = gui.pose(); pose.pushMatrix();
        try {
            float scale = Math.min(layout.sceneWidth() / 320F, layout.sceneHeight() / 180F);
            pose.translate(layout.sceneX() + (layout.sceneWidth() - 320 * scale) / 2, layout.sceneY() + (layout.sceneHeight() - 180 * scale) / 2);
            pose.scale(scale, scale);
            drawScene(gui, progress);
        } finally { pose.popMatrix(); }
        if (chapter == MarsGuideAnimation.Chapter.RECIPES) recipe(gui, progress);
        gui.disableScissor();
        gui.nextStratum();
        // Real font wrapping, separate from the scaled illustration, keeps captions legible on small GUI sizes.
        int y = layout.textY();
        for (var line : chapter == MarsGuideAnimation.Chapter.RECIPES && w < 480 ? List.<net.minecraft.util.FormattedCharSequence>of()
                : font.split(text(chapter.key() + ".caption"), layout.textWidth())) {
            if (y + font.lineHeight > layout.textY() + layout.textHeight()) break;
            gui.text(font, line, layout.textX(), y, 0xFFDFEBEE); y += font.lineHeight + 2;
        }
        int barY = t + h - 52;
        gui.fill(l + 12, barY, l + w - 12, barY + 2, 0xFF39505C);
        gui.fill(l + 12, barY, l + 12 + (int)((w - 24) * progress), barY + 2, 0xFF65D9ED);
        Component footer = state.has(MarsProgress.COMPLETE) ? text("status_complete")
                : state.has(MarsProgress.ARRIVAL) ? text("status", Integer.bitCount(state.flags() & MarsProgress.REQUIREMENTS)) : text("status_not_started");
        gui.text(font, footer, l + 12, t + h - 16, 0xFF8DE9BE);
        if (w >= 480) gui.text(font, text("controls"), l + w - 250, t + h - 16, 0xFF8CAAB7);
        if (mouseX >= l && mouseX < l + w && mouseY >= t + h - 22 && mouseY < t + h)
            gui.setTooltipForNextFrame(font, text("safety"), mouseX, mouseY);
        super.extractRenderState(gui, mouseX, mouseY, delta);
        if (chapter == MarsGuideAnimation.Chapter.RECIPES) for (int i = 0; i < recipes.size(); i++) {
            var b = recipes.get(i); gui.item(recipeItems.get(i), b.getX() + 3, b.getY() + 2);
        }
        if (!hovered.isEmpty()) gui.setTooltipForNextFrame(font, hovered, mouseX, mouseY);
    }
    private void label(GuiGraphicsExtractor gui, String key, int x, int y, int color) { gui.centeredText(font, text(key), x, y, color); }
    private void icon(GuiGraphicsExtractor gui, ItemStack stack, int x, int y) {
        gui.item(stack, x, y); gui.itemDecorations(font, stack, x, y);
        // Item names are also provided as button tooltips in the recipe row.
    }
    private void drawScene(GuiGraphicsExtractor gui, double p) {
        switch (animation.chapter()) {
            case PACK -> {
                label(gui, "pack.scene", 160, 12, 0xFFFFD29C);
                ItemStack[] kit = {new ItemStack(ColonyContent.HABITAT_CONTROLLER), new ItemStack(ColonyContent.AIRLOCK_DOOR, 2),
                        new ItemStack(Blocks.STONE, 192), new ItemStack(ColonyContent.OXYGEN_CANISTER, 2),
                        new ItemStack(ColonyContent.CHARGED_POWER_CELL, 2), new ItemStack(ColonyContent.WATER_CANISTER, 2)};
                for (int i = 0; i < kit.length; i++) {
                    int x = 45 + (i % 3) * 90, y = 48 + (i / 3) * 68;
                    gui.outline(x - 7, y - 7, 34, 34, (int)(p * 6) == i ? 0xFF65D9ED : 0xFF486370);
                    icon(gui, kit[i], x + 2, y + 2);
                    int row = 0;
                    for (var line : font.split(kit[i].getHoverName(), 84)) {
                        gui.centeredText(font, line, x + 10, y + 30 + row * 10, 0xFFE4EDF0); row++;
                    }
                }
            }
            case BUILD, VENT, FINISH -> {
                MarsGuideScene.habitat(gui, p, animation.chapter() == MarsGuideAnimation.Chapter.BUILD,
                        animation.chapter() == MarsGuideAnimation.Chapter.VENT, animation.chapter() == MarsGuideAnimation.Chapter.FINISH);
                gui.nextStratum();
                label(gui, animation.chapter() == MarsGuideAnimation.Chapter.VENT ? "vent.scene" : "cutaway", 160, 8, 0xFF65D9ED);
                label(gui, animation.chapter() == MarsGuideAnimation.Chapter.FINISH ? "finish.scene" : "build.scene", 160, 166, 0xFFE5C48B);
            }
            case AIRLOCK -> {
                MarsGuideScene.airlock(gui, p);
                label(gui, "outside", 52, 153, 0xFFECC0A1); label(gui, "airlock", 156, 153, 0xFFDFE9ED); label(gui, "inside", 262, 153, 0xFF8DE9BE);
                var doors = MarsGuideAnimation.doors(p);
                label(gui, doors.outerOpen() ? "airlock.enter" : doors.innerOpen() ? "airlock.exit" : "airlock.close", 160, 4, 0xFF65D9ED);
            }
            case SUPPLIES, WATER -> controller(gui, p, animation.chapter() == MarsGuideAnimation.Chapter.WATER);
            case RECIPES -> { }
        }
    }
    private void controller(GuiGraphicsExtractor gui, double p, boolean water) {
        boolean processed = p >= .48;
        label(gui, water ? "water.scene" : "supplies.scene", 160, 9, 0xFF65D9ED);
        gui.item(new ItemStack(ColonyContent.HABITAT_CONTROLLER), 30, 65);
        int x = 82, y = 45;
        for (int i = 0; i < 9; i++) {
            int sx = x + i % 3 * 30, sy = y + i / 3 * 30;
            gui.fill(sx, sy, sx + 26, sy + 26, 0xFF0A1C24);
            gui.outline(sx, sy, 26, 26, water && i == 2 ? 0xFF65D9ED : 0xFF6C8995);
        }
        if (!processed) {
            int fall = (int)(-20 * (1 - MarsGuideAnimation.ramp(p, 0, .20)));
            icon(gui, new ItemStack(ColonyContent.OXYGEN_CANISTER), x + 5, y + 5 + fall);
            icon(gui, new ItemStack(ColonyContent.CHARGED_POWER_CELL), x + 35, y + 5 + fall);
        } else {
            icon(gui, new ItemStack(ColonyContent.EMPTY_POWER_CELL), x + 65, y + 35);
            icon(gui, new ItemStack(ColonyContent.EMPTY_CANISTER), x + 5, y + 65);
        }
        if (water && p > .18) icon(gui, new ItemStack(ColonyContent.WATER_CANISTER, 2), x + 65,
                y + 5 - (int)(24 * (1 - MarsGuideAnimation.ramp(p, .18, .40))));
        if (!water && p < .48) {
            float v = (float)MarsGuideAnimation.ramp(p, 0, .48);
            gui.fill(188, 60, 188 + (int)(100 * v), 64, 0xFF65D9ED);
        }
        gui.text(font, Component.literal("O₂"), 190, 78, 0xFF8DE9BE);
        gui.text(font, Component.literal(processed ? "2251" : "0"), 229, 78, 0xFFE1ECEF);
        gui.text(font, text("power"), 190, 97, 0xFFFFD29C);
        gui.text(font, Component.literal(processed ? "2395" : "0"), 242, 97, 0xFFE1ECEF);
        label(gui, water ? "water.slot" : processed ? "supplies.processed" : "supplies.wait", 160, 152, 0xFFE1ECEF);
        label(gui, "example", 160, 168, 0xFF91ACB8);
    }
    private void nativeItem(GuiGraphicsExtractor gui, ItemStack stack, int x, int y) {
        icon(gui, stack, x, y);
        if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) hovered = stack;
    }
    private void recipe(GuiGraphicsExtractor gui, double p) {
        int left = layout.sceneX() + 10, top = layout.sceneY() + 29;
        if (recipe == null) {
            gui.textWithWordWrap(font, text(loading ? "loading" : unavailable ? "unavailable" : "no_recipe"), left, top + 20, layout.sceneWidth() - 20, 0xFFDAE8EC); return;
        }
        int count = recipe.inputs().size();
        int columns = Math.clamp(recipe.width(), 1, 3);
        for (int i = 0; i < count; i++) {
            var choices = recipe.inputs().get(i);
            int x = left + i % columns * 22, y = top + i / columns * 22;
            gui.fill(x, y, x + 20, y + 20, 0xFF0A1C24); gui.outline(x, y, 20, 20, 0xFF536F7D);
            if (!choices.isEmpty()) nativeItem(gui, choices.get((int)(p * choices.size()) % choices.size()), x + 2, y + 2);
        }
        gui.text(font, "→", left + 85, top + 24, 0xFF65D9ED);
        gui.text(font, text(recipe.duration() > 0 ? "machine_recipe" : "craft_recipe"), left + 108, top, 0xFF65D9ED);
        for (int i = 0; i < recipe.outputs().size(); i++) nativeItem(gui, recipe.outputs().get(i), left + 108 + i % 4 * 23, top + 19 + i / 4 * 23);
        if (!recipe.station().isEmpty()) nativeItem(gui, recipe.station(), left + 78, top + 45);
        gui.text(font, Component.literal((recipePage + 1) + "/" + recipeTotal), left + 108, top + 49, 0xFFAFCBD4);
        if (recipe.duration() > 0) gui.text(font, text("seconds", recipe.duration() / 20), left + 146, top + 49, 0xFFAAC6D1);
    }
}

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

/** Training console. Simulation and server progress never share a state or completion path. */
public final class MarsGuideScreen extends Screen {
    private static final int INK = 0xFF101820, STAGE = 0xFF17232C, TEXT = 0xFFF0EBE4, MUTED = 0xFFABBAC5,
            MARS = 0xFFE5A078, OXYGEN = 0xFF8DD7AF, POWER = 0xFFE8C777, WATER = 0xFF7DCDE5;
    private final MarsGuideAnimation animation = new MarsGuideAnimation();
    private final MarsGuideRecipeRequest request = new MarsGuideRecipeRequest();
    private MarsGuideNetwork.State state;
    private MarsGuideAnimation.Layout layout;
    private ConsoleButton previous, next, pause, previousRecipe, nextRecipe;
    private final List<ConsoleButton> chapters = new ArrayList<>(), recipes = new ArrayList<>();
    private final List<ItemStack> recipeItems = List.of(new ItemStack(ColonyContent.HABITAT_CONTROLLER), new ItemStack(ColonyContent.PRESSURE_HULL),
            new ItemStack(ColonyContent.AIRLOCK_DOOR), new ItemStack(Blocks.LEVER), new ItemStack(ColonyContent.EMPTY_POWER_CELL),
            new ItemStack(ColonyContent.CHARGED_POWER_CELL), new ItemStack(ColonyContent.EMPTY_CANISTER), new ItemStack(ColonyContent.WATER_CANISTER),
            new ItemStack(ColonyContent.ELECTROLYZER), new ItemStack(ColonyContent.OXYGEN_CANISTER));
    private final List<ItemStack> kit = List.of(new ItemStack(ColonyContent.HABITAT_CONTROLLER), new ItemStack(ColonyContent.AIRLOCK_DOOR, 2),
            new ItemStack(ColonyContent.PRESSURE_HULL, 192), new ItemStack(Blocks.LEVER, 4), new ItemStack(ColonyContent.OXYGEN_CANISTER, 2),
            new ItemStack(ColonyContent.CHARGED_POWER_CELL, 2), new ItemStack(ColonyContent.WATER_CANISTER, 2));
    private final ItemStack oxygen = new ItemStack(ColonyContent.OXYGEN_CANISTER), cell = new ItemStack(ColonyContent.CHARGED_POWER_CELL),
            emptyCell = new ItemStack(ColonyContent.EMPTY_POWER_CELL), emptyCan = new ItemStack(ColonyContent.EMPTY_CANISTER),
            water = new ItemStack(ColonyContent.WATER_CANISTER, 2);
    private ItemStack hovered = ItemStack.EMPTY;
    private int recipeIndex, recipePage, recipeTotal, mouseX, mouseY, captionPage;
    private RecipeCard recipe;
    private boolean unavailable;
    private long transitionAt, lastAnimationTick = Util.getMillis();
    private String lastPhase = "";
    private List<net.minecraft.util.FormattedCharSequence> captionLines = List.of();

    public MarsGuideScreen(MarsGuideNetwork.State state) { super(text("title")); this.state = state; }
    private static Component text(String key, Object... args) { return Component.translatable("mars.tutorial." + key, args); }
    public void accept(MarsGuideNetwork.State state) { this.state = state; }
    public void accept(RecipeViewerNetwork.Reply reply) {
        if (!request.accept(reply.sequence(), Util.getMillis())) return;
        recipe = reply.card().orElse(null); recipePage = reply.page(); recipeTotal = reply.total(); unavailable = false; updateButtons();
    }
    @Override protected void init() {
        layout = MarsGuideAnimation.Layout.fit(width, height); lastPhase = "";
        int l = layout.left(), t = layout.top(), w = layout.width();
        chapters.clear();
        int tabWidth = (w - 20) / 8;
        for (int i = 0; i < 8; i++) {
            final int chapter = i;
            var label = Component.literal(Integer.toString(i + 1));
            if (layout.wide()) label.append(" ").append(text(MarsGuideAnimation.Chapter.values()[i].key() + ".short"));
            var b = button(label, l + 10 + i * tabWidth, t + 27, tabWidth - 2, () -> select(chapter));
            b.setTooltip(Tooltip.create(text("chapter", i + 1, 8, text(MarsGuideAnimation.Chapter.values()[i].key() + ".title"))));
            chapters.add(b);
        }
        var close = button(Component.literal("×"), l + w - 28, t + 5, 18, this::onClose);
        close.setTooltip(Tooltip.create(text("close")));
        int y = layout.controlsY(), bw = (w - 29) / 4;
        previous = button(text("previous"), l + 10, y, bw, () -> select(animation.chapter().ordinal() - 1));
        pause = button(text("pause"), l + 13 + bw, y, bw, this::toggle);
        button(text("replay"), l + 16 + bw * 2, y, bw, this::replay);
        next = button(text("next"), l + 19 + bw * 3, y, bw, () -> select(animation.chapter().ordinal() + 1));
        recipes.clear();
        for (int i = 0; i < recipeItems.size(); i++) {
            final int index = i;
            var b = button(Component.empty(), layout.sceneX() + 4 + i * 20, layout.sceneY() + 3, 19, () -> { recipeIndex = index; requestRecipe(0); });
            b.setTooltip(Tooltip.create(recipeItems.get(i).getHoverName())); recipes.add(b);
        }
        previousRecipe = button(Component.literal("‹"), layout.sceneX() + layout.sceneWidth() - 45, layout.sceneY() + 3, 19, () -> requestRecipe(recipePage - 1));
        nextRecipe = button(Component.literal("›"), layout.sceneX() + layout.sceneWidth() - 23, layout.sceneY() + 3, 19, () -> requestRecipe(recipePage + 1));
        previousRecipe.setTooltip(Tooltip.create(text("recipe_previous"))); nextRecipe.setTooltip(Tooltip.create(text("recipe_next")));
        updateButtons();
    }
    private ConsoleButton button(Component label, int x, int y, int width, Runnable action) {
        return addRenderableWidget(new ConsoleButton(x, y, width, label, action));
    }
    private final class ConsoleButton extends Button {
        boolean selected;
        ConsoleButton(int x, int y, int w, Component label, Runnable action) { super(x, y, w, 18, label, b -> action.run(), DEFAULT_NARRATION); }
        @Override protected void extractContents(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
            int bg = selected ? 0xFF543C32 : isHoveredOrFocused() && active ? 0xFF364653 : 0xFF22303B;
            gui.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bg);
            if (selected) gui.fill(getX(), getY() + 16, getX() + getWidth(), getY() + 18, MARS);
            if (isFocused()) gui.outline(getX(), getY(), getWidth(), getHeight(), TEXT);
            gui.centeredText(font, getMessage(), getX() + getWidth() / 2, getY() + 5, active ? TEXT : 0xFF77838D);
        }
    }
    private void select(int chapter) {
        var before = animation.chapter(); animation.select(chapter);
        if (before != animation.chapter()) { transitionAt = Util.getMillis(); lastPhase = ""; captionPage = 0; }
        if (animation.chapter() == MarsGuideAnimation.Chapter.RECIPES && recipe == null && !request.pending()) requestRecipe(0);
        updateButtons();
    }
    private void toggle() { animation.toggle(); updateButtons(); }
    private void replay() { animation.restart(); captionPage = 0; updateButtons(); }
    private void updateButtons() {
        if (previous == null) return;
        previous.active = animation.chapter().ordinal() > 0; next.active = animation.chapter().ordinal() < 7;
        pause.active = !animation.ended();
        pause.setMessage(text(animation.ended() ? "finished" : animation.playing() ? "pause" : "play"));
        for (int i = 0; i < chapters.size(); i++) chapters.get(i).selected = i == animation.chapter().ordinal();
        boolean show = animation.chapter() == MarsGuideAnimation.Chapter.RECIPES;
        for (int i = 0; i < recipes.size(); i++) { recipes.get(i).visible = show; recipes.get(i).selected = i == recipeIndex; }
        previousRecipe.visible = nextRecipe.visible = show;
        previousRecipe.active = !request.pending() && recipePage > 0;
        nextRecipe.active = !request.pending() && recipePage + 1 < recipeTotal;
    }
    private void requestRecipe(int page) {
        int sequence = RecipeViewerClient.nextSequence(); recipePage = Math.max(0, page); recipe = null; recipeTotal = 0;
        unavailable = minecraft.getConnection() == null || !ClientPlayNetworking.canSend(RecipeViewerNetwork.Request.TYPE);
        request.begin(sequence, Util.getMillis(), !unavailable);
        if (!unavailable) ClientPlayNetworking.send(new RecipeViewerNetwork.Request(sequence,
                BuiltInRegistries.ITEM.getKey(recipeItems.get(recipeIndex).getItem()).toString(), false, recipePage));
        updateButtons();
    }
    @Override public void tick() {
        boolean playing = animation.playing(); animation.tick(); lastAnimationTick = Util.getMillis();
        if (request.expire(Util.getMillis())) { unavailable = true; updateButtons(); }
        if (playing != animation.playing()) updateButtons();
    }
    @Override public boolean isPauseScreen() { return true; }
    @Override public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) { }
    @Override public boolean keyPressed(KeyEvent event) {
        switch (event.key()) {
            case GLFW.GLFW_KEY_LEFT -> { select(animation.chapter().ordinal() - 1); return true; }
            case GLFW.GLFW_KEY_RIGHT -> { select(animation.chapter().ordinal() + 1); return true; }
            case GLFW.GLFW_KEY_SPACE -> { toggle(); return true; }
            case GLFW.GLFW_KEY_HOME, GLFW.GLFW_KEY_R -> { replay(); return true; }
            case GLFW.GLFW_KEY_DOWN -> { captionPage++; return true; }
            case GLFW.GLFW_KEY_UP -> { captionPage = Math.max(0, captionPage - 1); return true; }
            default -> { if (event.key() >= GLFW.GLFW_KEY_1 && event.key() <= GLFW.GLFW_KEY_8) { select(event.key() - GLFW.GLFW_KEY_1); return true; } }
        }
        return super.keyPressed(event);
    }
    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && event.x() >= layout.sceneX() && event.x() <= layout.left() + layout.width() - 10
                && event.y() >= layout.timelineY() - 3 && event.y() <= layout.timelineY() + 4) {
            animation.seek((event.x() - layout.sceneX()) / (layout.width() - 20)); updateButtons(); return true;
        }
        if (event.button() == 0 && event.x() >= layout.textX() && event.x() <= layout.textX() + layout.textWidth()
                && event.y() >= layout.textY() && event.y() < layout.textY() + layout.textHeight() && captionLines.size() > captionCapacity()) {
            captionPage++; return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
    @Override public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX; this.mouseY = mouseY; hovered = ItemStack.EMPTY;
        int l = layout.left(), t = layout.top(), w = layout.width(), h = layout.height();
        gui.fill(0, 0, width, height, 0xC0080D12); gui.fill(l, t, l + w, t + h, INK);
        gui.fill(l, t, l + 3, t + 22, MARS);
        gui.text(font, text("console", animation.chapter().ordinal() + 1), l + 10, t + 10, MARS);
        if (layout.wide()) gui.text(font, text("training"), l + w - 188, t + 10, MUTED);
        gui.fill(layout.sceneX(), layout.sceneY(), layout.sceneX() + layout.sceneWidth(), layout.sceneY() + layout.sceneHeight(), STAGE);
        double p = animation.progress((Util.getMillis() - lastAnimationTick) / 50F);
        gui.enableScissor(layout.sceneX(), layout.sceneY(), layout.sceneX() + layout.sceneWidth(), layout.sceneY() + layout.sceneHeight());
        switch (animation.chapter()) {
            case PACK -> packing(gui, p);
            case SUPPLIES, WATER -> controller(gui, p, animation.chapter() == MarsGuideAnimation.Chapter.WATER);
            case RECIPES -> recipe(gui, p);
            default -> {
                MarsGuideScene.extract(gui, layout.sceneX(), layout.sceneY() + 12, layout.sceneWidth(), layout.sceneHeight() - 12, animation.chapter(), p);
                gui.nextStratum();
                String badge = animation.chapter() == MarsGuideAnimation.Chapter.BUILD && p < .72 ? "build.scene" : "cutaway";
                gui.text(font, text(badge), layout.sceneX() + 6, layout.sceneY() + 5, MUTED);
                if (animation.chapter() == MarsGuideAnimation.Chapter.VENT && p >= .24) {
                    int bx = layout.sceneX() + layout.sceneWidth() - 90, by = layout.sceneY() + 22;
                    gui.fill(bx, by, bx + 84, by + 28, 0xE0101820);
                    gui.text(font, text("air_clear"), bx + 5, by + 5, OXYGEN);
                    gui.text(font, text("above"), bx + 5, by + 16, TEXT);
                }
                if (animation.chapter() == MarsGuideAnimation.Chapter.AIRLOCK) {
                    var doors = MarsGuideAnimation.doors(p);
                    gui.text(font, text(doors.outerOpen() ? "outer_open" : "outer_closed"), layout.sceneX() + 6, layout.sceneY() + layout.sceneHeight() - 12, MARS);
                    var label = text(doors.innerOpen() ? "inner_open" : "inner_closed");
                    gui.text(font, label, layout.sceneX() + layout.sceneWidth() - 6 - font.width(label), layout.sceneY() + layout.sceneHeight() - 12, OXYGEN);
                }
            }
        }
        // A brief eased reveal on chapter changes. It never advances the simulation or server state.
        double reveal = MarsGuideAnimation.ease(Util.getMillis() - transitionAt, 0, 220);
        if (reveal < 1) { gui.nextStratum(); gui.fill(layout.sceneX(), layout.sceneY(), layout.sceneX() + layout.sceneWidth(), layout.sceneY() + layout.sceneHeight(), ((int)(255 * (1 - reveal)) << 24) | 0x17232C); }
        gui.disableScissor(); gui.nextStratum();
        caption(gui, p);
        int barY = layout.timelineY();
        gui.fill(l + 10, barY, l + w - 10, barY + 2, 0xFF3B4852);
        gui.fill(l + 10, barY, l + 10 + (int)((w - 20) * p), barY + 2, MARS);
        int scrubX = l + 10 + (int)((w - 20) * p); gui.fill(scrubX - 1, barY - 1, scrubX + 1, barY + 3, TEXT);
        Component footer = state.has(MarsProgress.COMPLETE) ? text("status_complete")
                : state.has(MarsProgress.ARRIVAL) ? text("status", Integer.bitCount(state.flags() & MarsProgress.REQUIREMENTS)) : text("status_not_started");
        gui.text(font, footer, l + 10, t + h - 12, OXYGEN);
        if (layout.wide()) {
            var label = text(animation.ended() ? "ended" : animation.playing() ? "playing" : "paused");
            gui.text(font, label, l + w - 10 - font.width(label), t + h - 12, MUTED);
        }
        if (mouseX >= l && mouseX < l + w && mouseY >= t + h - 15 && mouseY < t + h)
            gui.setTooltipForNextFrame(font, text("safety"), mouseX, mouseY);
        super.extractRenderState(gui, mouseX, mouseY, delta);
        if (animation.chapter() == MarsGuideAnimation.Chapter.RECIPES) for (int i = 0; i < recipes.size(); i++) {
            var b = recipes.get(i); gui.item(recipeItems.get(i), b.getX() + 1, b.getY() + 1);
        }
        if (!hovered.isEmpty()) gui.setTooltipForNextFrame(font, hovered, mouseX, mouseY);
    }
    private int captionCapacity() { return Math.max(1, layout.textHeight() / 10); }
    private void caption(GuiGraphicsExtractor gui, double p) {
        String phase = MarsGuideAnimation.phase(animation.chapter(), p);
        int x = layout.textX(), y = layout.textY();
        if (layout.wide()) {
            gui.text(font, text(animation.chapter().key() + ".title"), x, y, MARS); y += 22;
            for (var line : font.split(text(phase), layout.textWidth())) { gui.text(font, line, x, y, TEXT); y += 11; }
            y += 16;
            for (var line : font.split(text(animation.chapter().key() + ".caption"), layout.textWidth())) { gui.text(font, line, x, y, MUTED); y += 11; }
            if (y + 45 <= layout.textY() + layout.textHeight()) {
                y += 15; gui.text(font, text("example_short"), x, y, MARS);
                for (var line : font.split(text("controls"), layout.textWidth())) { y += 11; gui.text(font, line, x, y, MUTED); }
            }
        } else {
            if (!phase.equals(lastPhase)) { captionLines = font.split(text(phase + ".help"), layout.textWidth() - 17); lastPhase = phase; captionPage = 0; }
            int cap = captionCapacity(), pages = Math.max(1, (captionLines.size() + cap - 1) / cap); captionPage = Math.floorMod(captionPage, pages);
            for (int i = 0; i < cap && captionPage * cap + i < captionLines.size(); i++) gui.text(font, captionLines.get(captionPage * cap + i), x, y + i * 10, TEXT);
            if (pages > 1) gui.text(font, "↕", x + layout.textWidth() - 10, y + 10, MARS);
        }
    }
    private void packing(GuiGraphicsExtractor gui, double p) {
        int cellWidth = (layout.sceneWidth() - 12) / 4, rowHeight = Math.min(80, (layout.sceneHeight() - 8) / 2);
        int startY = layout.sceneY() + (layout.sceneHeight() - rowHeight * 2) / 2;
        int size = layout.wide() ? 32 : 16;
        for (int i = 0; i < kit.size(); i++) {
            int x = layout.sceneX() + 6 + i % 4 * cellWidth, y = startY + i / 4 * rowHeight;
            gui.fill(x + 1, y + 1, x + cellWidth - 3, y + rowHeight - 3, i == Math.min(6, (int)(p * 7)) ? 0xFF35404A : 0xFF1E2C36);
            sizedItem(gui, kit.get(i), x + 7, y + 5, size, false);
            gui.text(font, Component.literal("×" + kit.get(i).getCount()), x + size + 13, y + 9, TEXT);
            int lineY = y + size + 8;
            for (var line : font.split(text("kit." + i), cellWidth - 10)) { gui.text(font, line, x + 5, lineY, MUTED); lineY += 10; }
        }
        int x = layout.sceneX() + 6 + 3 * cellWidth, y = startY + rowHeight;
        for (var line : font.split(text("kit.note"), cellWidth - 8)) { gui.text(font, line, x + 4, y + 5, MARS); y += 10; }
    }
    private void controller(GuiGraphicsExtractor gui, double p, boolean waterScene) {
        int x = layout.sceneX(), y = layout.sceneY(), w = layout.sceneWidth(), h = layout.sceneHeight();
        int size = layout.wide() ? 32 : 16, step = size + 4;
        int modelWidth = Math.max(65, w / 4), gridX = x + modelWidth + 6, gridY = y + (h - step * 3) / 2;
        MarsGuideScene.extract(gui, x + 4, y + 9, modelWidth - 8, h - 18, animation.chapter(), p);
        gui.nextStratum();
        for (int i = 0; i < 9; i++) {
            int sx = gridX + i % 3 * step, sy = gridY + i / 3 * step;
            gui.fill(sx, sy, sx + size + 2, sy + size + 2, INK);
            int accent = i == 0 ? OXYGEN : i == 1 ? POWER : WATER;
            if (i < 3) { gui.fill(sx, sy - 2, sx + size + 2, sy, accent); gui.text(font, Integer.toString(i + 1), sx + size / 2 - 2, gridY - 13, accent); }
            if (waterScene && i == 2) gui.outline(sx - 1, sy - 1, size + 4, size + 4, WATER);
        }
        int bx = gridX + 3 * step + 11, bw = x + w - bx - 8;
        if (!waterScene) {
            double insert = MarsGuideAnimation.ease(p, .05, .28), process = MarsGuideAnimation.ease(p, .28, .55);
            if (p < .55) {
                int dy = (int)((1 - insert) * size);
                sizedItem(gui, oxygen, gridX + 1, gridY + 1 - dy, size, true);
                sizedItem(gui, cell, gridX + step + 1, gridY + 1 - dy, size, true);
            } else {
                double returned = MarsGuideAnimation.ease(p, .55, .70);
                sizedItem(gui, emptyCell, gridX + step + 1 + (int)(step * returned), gridY + 1 + (int)(step * returned), size, true);
                sizedItem(gui, emptyCan, gridX + 1, gridY + 1 + (int)(2 * step * returned), size, true);
            }
            meter(gui, bx, gridY + 4, bw, "O₂", (int)(2251 * process), 600, OXYGEN);
            meter(gui, bx, gridY + (layout.wide() ? 60 : 35), bw, text("power").getString(), (int)(2395 * process), 1500, POWER);
        } else {
            if (p > .04) sizedItem(gui, water, gridX + 2 * step + 1, gridY + 1 - (int)(size * (1 - MarsGuideAnimation.ease(p, .04, .32))), size, true);
            meter(gui, bx, gridY + 15, bw, text("water.label").getString(), p >= .32 ? 2000 : 0, 2000, WATER);
        }
    }
    private void meter(GuiGraphicsExtractor gui, int x, int y, int width, String label, int value, int minimum, int color) {
        gui.text(font, label, x, y, color); gui.text(font, Integer.toString(value), x + width - font.width(Integer.toString(value)), y, TEXT);
        gui.fill(x, y + 12, x + width, y + 15, 0xFF3B4852); gui.fill(x, y + 12, x + (int)(width * Math.min(1, value / (double)minimum)), y + 15, color);
        gui.text(font, text("minimum", minimum), x, y + 18, MUTED);
    }
    private void sizedItem(GuiGraphicsExtractor gui, ItemStack stack, int x, int y, int size, boolean count) {
        var pose = gui.pose(); pose.pushMatrix();
        try {
            pose.translate(x, y); pose.scale(size / 16F, size / 16F); gui.item(stack, 0, 0);
            if (count) gui.itemDecorations(font, stack, 0, 0);
        } finally { pose.popMatrix(); }
        if (mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size) hovered = stack;
    }
    private void recipe(GuiGraphicsExtractor gui, double p) {
        int size = layout.wide() ? 32 : 16, step = size + 4, gap = layout.wide() ? 36 : 26;
        int groupWidth = 3 * step + gap + 146;
        int x = layout.sceneX() + (layout.sceneWidth() - groupWidth) / 2;
        int y = layout.sceneY() + 24 + (layout.sceneHeight() - 24 - 3 * step) / 2;
        if (recipe == null) { gui.textWithWordWrap(font, text(request.pending() ? "loading" : unavailable ? "unavailable" : "no_recipe"), layout.sceneX() + 10, y + 10, layout.sceneWidth() - 20, TEXT); return; }
        int columns = Math.clamp(recipe.width(), 1, 3);
        for (int i = 0; i < recipe.inputs().size(); i++) {
            int sx = x + i % columns * step, sy = y + i / columns * step;
            gui.fill(sx, sy, sx + size + 2, sy + size + 2, INK);
            var choices = recipe.inputs().get(i);
            if (!choices.isEmpty()) sizedItem(gui, choices.get(Math.min(choices.size() - 1, (int)(p * choices.size()))), sx + 1, sy + 1, size, true);
        }
        int outputX = x + 3 * step + gap;
        gui.text(font, "→", x + 3 * step + 6, y + step, MARS);
        gui.text(font, text(recipe.duration() > 0 ? "machine_recipe" : "craft_recipe"), outputX, y + 1, MUTED);
        if (recipe.kind().equals("colony")) {
            for (int i = 0; i < recipe.outputs().size(); i++) sizedItem(gui, recipe.outputs().get(i), outputX + i % 4 * step, y + 17 + i / 4 * step, size, true);
        } else if (!recipe.outputs().isEmpty()) {
            sizedItem(gui, recipe.outputs().get(Math.min(recipe.outputs().size() - 1, (int)(p * recipe.outputs().size()))), outputX, y + 17, size, true);
        }
        if (!recipe.station().isEmpty()) sizedItem(gui, recipe.station(), x + 3 * step + 2, y + 3 * step - size, size, false);
        gui.text(font, Component.literal((recipePage + 1) + "/" + recipeTotal), outputX, y + 3 * step - 11, TEXT);
        if (recipe.duration() > 0) gui.text(font, text("seconds", recipe.duration() / 20F), outputX + 37, y + 3 * step - 11, MUTED);
    }
}

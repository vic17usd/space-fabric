package dev.vic17usd.spacefabric.mixin.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Fabric registers PiP renderers; 26.1 exposes no extractor method for a custom PiP state. */
@Mixin(GuiGraphicsExtractor.class)
public interface GuideGuiStateAccessor {
    @Accessor("guiRenderState") GuiRenderState spaceFabric$guideRenderState();
}

package dev.vic17usd.spacefabric.mixin.client;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatScreen.class)
public interface ChatDraftAccessor {
    @Accessor("input") EditBox spaceFabric$input();
    @Accessor("initial") void spaceFabric$initial(String value);
}

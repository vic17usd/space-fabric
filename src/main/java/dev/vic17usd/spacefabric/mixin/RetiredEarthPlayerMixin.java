package dev.vic17usd.spacefabric.mixin;

import dev.vic17usd.spacefabric.compat.EarthRetirement;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Applies to both the singleplayer owner and server player.dat, without editing files directly. */
@Mixin(PlayerList.class)
public abstract class RetiredEarthPlayerMixin {
    @Shadow @Final private MinecraftServer server;
    @Inject(method="loadPlayerData",at=@At("RETURN"),cancellable=true)
    private void spaceFabric$returnHome(NameAndId profile,CallbackInfoReturnable<Optional<CompoundTag>> result){
        result.setReturnValue(result.getReturnValue().map(tag->EarthRetirement.player(tag,server.overworld().getRespawnData().pos())));
    }
}

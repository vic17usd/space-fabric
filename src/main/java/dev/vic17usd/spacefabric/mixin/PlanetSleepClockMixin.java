package dev.vic17usd.spacefabric.mixin;

import dev.vic17usd.spacefabric.SpaceFabric;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Dormir en Tierra avanza su reloj propio, sin depender del marcador del Overworld. */
@Mixin(value = ServerClockManager.class, remap = false)
public abstract class PlanetSleepClockMixin {
    @Inject(method = "moveToTimeMarker", at = @At("HEAD"), cancellable = true)
    private void spaceFabric$wakeUp(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> marker,
                                   CallbackInfoReturnable<Boolean> callback) {
        if (!marker.identifier().equals(Identifier.withDefaultNamespace("wake_up_from_sleep"))) return;
        if (clock.unwrapKey().filter(key -> key.identifier().getNamespace().equals(SpaceFabric.MOD_ID)).isEmpty()) return;
        var manager = (ServerClockManager) (Object) this;
        long nextDawn = (Math.floorDiv(manager.getTotalTicks(clock), 24000L) + 1) * 24000L;
        manager.setTotalTicks(clock, nextDawn);
        callback.setReturnValue(true);
    }
}

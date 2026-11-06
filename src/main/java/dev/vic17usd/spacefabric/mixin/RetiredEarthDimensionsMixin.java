package dev.vic17usd.spacefabric.mixin;

import dev.vic17usd.spacefabric.compat.EarthRetirement;
import net.minecraft.core.Registry;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Old level.dat dimension maps must not recreate the retired world after a reload. */
@Mixin(WorldDimensions.class)
public abstract class RetiredEarthDimensionsMixin {
    @Inject(method="bake",at=@At("HEAD"),cancellable=true)
    private void spaceFabric$retireEarth(Registry<LevelStem> registry,CallbackInfoReturnable<WorldDimensions.Complete> result){
        var saved=((WorldDimensions)(Object)this).dimensions();
        if(saved.containsKey(EarthRetirement.LEGACY_STEM))result.setReturnValue(new WorldDimensions(EarthRetirement.dimensions(saved)).bake(registry));
    }
}

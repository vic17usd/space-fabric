package dev.vic17usd.spacefabric.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

/** Hielo estable: no se transforma en agua terrestre al calentarse ni al romperse. */
public final class MethaneIceBlock extends Block {
    public static final float SLIPPERINESS = 0.995F;
    public static final MapCodec<MethaneIceBlock> CODEC = simpleCodec(MethaneIceBlock::new);

    public MethaneIceBlock(Properties properties) { super(properties.friction(SLIPPERINESS)); }
    @Override protected MapCodec<MethaneIceBlock> codec() { return CODEC; }
}

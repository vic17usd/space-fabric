package dev.vic17usd.spacefabric.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

/** Hielo de trampa fría: no hereda el derretimiento por luz de IceBlock. */
public final class CraterIceBlock extends Block {
    public static final MapCodec<CraterIceBlock> CODEC = simpleCodec(CraterIceBlock::new);

    public CraterIceBlock(Properties properties) {
        super(properties.strength(0.25F, 0.4F).friction(0.98F));
    }

    @Override protected MapCodec<CraterIceBlock> codec() { return CODEC; }
}

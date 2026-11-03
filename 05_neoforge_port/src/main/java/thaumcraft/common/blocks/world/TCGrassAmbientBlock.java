package thaumcraft.common.blocks.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Legacy {@code thaumcraft:grass_ambient} block retained for registry parity and existing world
 * compatibility. Natural Magical Forest ambient wispy motes are now spawned purely client-side by
 * {@link thaumcraft.client.world.TCMagicalForestAmbientClient} without placing this block in chunks.
 */
public class TCGrassAmbientBlock extends GrassBlock {
    public static final MapCodec<GrassBlock> CODEC = simpleCodec(TCGrassAmbientBlock::new);

    public TCGrassAmbientBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GrassBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // Handled globally for the Magical Forest biome by TCMagicalForestAmbientClient.
    }
}

package thaumcraft.common.blocks.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.lib.fx.TCFXDispatcher;

public class TCGrassAmbientBlock extends GrassBlock {
    public static final MapCodec<GrassBlock> CODEC = simpleCodec(TCGrassAmbientBlock::new);

    public TCGrassAmbientBlock(Properties properties) { super(properties); }

    @Override
    public MapCodec<GrassBlock> codec() { return CODEC; }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int light = level.getBrightness(LightLayer.SKY, pos.above()) - level.getSkyDarken();
        float angle = level.getSunAngle(1.0F);
        angle += ((angle < Math.PI ? 0.0F : Mth.TWO_PI) - angle) * 0.2F;
        light = Mth.clamp(Math.round(light * Mth.cos(angle)), 0, 15);
        if (4 + light * 2 < 1 + random.nextInt(13)) {
            BlockPos candidate = pos.offset(random.nextInt(17) - 8, 5, random.nextInt(17) - 8);
            for (int i = 0; i < 10 && candidate.getY() > 50
                    && !level.getBlockState(candidate).is(Blocks.GRASS_BLOCK); i++) {
                candidate = candidate.below();
            }
            if (level.getBlockState(candidate).is(Blocks.GRASS_BLOCK)) {
                TCFXDispatcher.drawWispyMotesOnBlock(level, candidate.above(), 400, -0.01F);
            }
        }
    }
}

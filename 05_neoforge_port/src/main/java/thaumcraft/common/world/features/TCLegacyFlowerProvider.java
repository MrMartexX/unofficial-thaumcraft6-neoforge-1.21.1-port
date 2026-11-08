package thaumcraft.common.world.features;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import thaumcraft.common.registry.TCFeatures;

/**
 * Legacy 1.12.2 2D simplex noise flower provider for magical forest patches.
 * Uses seed 2345L, 1 octave, and scale 1/48 on the X/Z plane to select a flower species.
 */
public final class TCLegacyFlowerProvider extends BlockStateProvider {
    public static final MapCodec<TCLegacyFlowerProvider> CODEC = BlockState.CODEC.listOf().optionalFieldOf("states")
            .xmap(
                    opt -> opt.map(TCLegacyFlowerProvider::new).orElseGet(TCLegacyFlowerProvider::new),
                    provider -> Optional.of(provider.states)
            );

    private static final PerlinSimplexNoise NOISE =
            new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(2345L)), List.of(0));

    private static final List<BlockState> DEFAULT_STATES = List.of(
            Blocks.DANDELION.defaultBlockState(),
            Blocks.POPPY.defaultBlockState(),
            Blocks.BLUE_ORCHID.defaultBlockState(),
            Blocks.ALLIUM.defaultBlockState(),
            Blocks.AZURE_BLUET.defaultBlockState(),
            Blocks.RED_TULIP.defaultBlockState(),
            Blocks.ORANGE_TULIP.defaultBlockState(),
            Blocks.WHITE_TULIP.defaultBlockState(),
            Blocks.PINK_TULIP.defaultBlockState(),
            Blocks.OXEYE_DAISY.defaultBlockState()
    );

    private final List<BlockState> states;

    public TCLegacyFlowerProvider() {
        this(DEFAULT_STATES);
    }

    public TCLegacyFlowerProvider(List<BlockState> states) {
        this.states = (states == null || states.isEmpty()) ? DEFAULT_STATES : List.copyOf(states);
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return TCFeatures.LEGACY_FLOWER_PROVIDER.get();
    }

    @Override
    public BlockState getState(RandomSource random, BlockPos pos) {
        if (states == DEFAULT_STATES) {
            return TCLegacyPatchFeature.pickLegacyMagicalForestFlower(pos);
        }
        if (states.isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }
        double d0 = Mth.clamp((1.0D + NOISE.getValue(pos.getX() / 48.0D, pos.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
        int index = (int) (d0 * states.size());
        return states.get(Math.min(index, states.size() - 1));
    }

    public List<BlockState> states() {
        return states;
    }
}

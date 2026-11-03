package thaumcraft.common.world.features;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

/** Legacy decorator anchor sampling; counts, patch sizes and predicates remain datapack-owned. */
public final class TCLegacyPatchFeature extends RandomPatchFeature {
    public enum Anchor { FLOWER, GRASS, WATERLILY, VISHROOM, CINDERPEARL, RANDOM_HEIGHT }
    private final Anchor anchor;

    public TCLegacyPatchFeature(Anchor anchor) {
        super(RandomPatchConfiguration.CODEC);
        this.anchor = anchor;
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        switch (anchor) {
            case FLOWER -> pos = pos.atY(context.random().nextInt(Math.max(1, pos.getY() + 32)));
            case GRASS, WATERLILY, RANDOM_HEIGHT -> {
                pos = pos.atY(context.random().nextInt(Math.max(1, pos.getY() * 2)));
                if (anchor == Anchor.GRASS) {
                    while (pos.getY() > 0 && (level.isEmptyBlock(pos) || level.getBlockState(pos).is(BlockTags.LEAVES)))
                        pos = pos.below();
                } else if (anchor == Anchor.WATERLILY) {
                    while (pos.getY() > 0 && level.isEmptyBlock(pos.below())) pos = pos.below();
                }
            }
            case VISHROOM -> {
                pos = pos.below();
                while (pos.getY() > 50 && !level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) pos = pos.below();
                if (!level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) return false;
                pos = pos.above();
            }
            case CINDERPEARL -> {
                if (!level.getBlockState(pos.below()).is(BlockTags.SAND)
                        || level.getBiome(pos).value().getBaseTemperature() <= 1.0F) return false;
            }
        }
        return super.place(new FeaturePlaceContext<>(Optional.empty(), level, context.chunkGenerator(),
                context.random(), pos, context.config()));
    }
}

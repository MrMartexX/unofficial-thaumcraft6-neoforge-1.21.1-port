package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.common.world.trees.TCBigMagicTreeGenerator;

/** Natural world generation feature for Legacy Thaumcraft 6 {@code WorldGenBigMagicTree}. */
public final class TCBigMagicTreeFeature extends Feature<NoneFeatureConfiguration> {
    public TCBigMagicTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (origin.getY() < level.getLevel().getSeaLevel() || !level.getFluidState(origin).isEmpty()) {
            return false;
        }
        RandomSource random = context.random();
        return new TCBigMagicTreeGenerator().generate(level, random, origin);
    }
}

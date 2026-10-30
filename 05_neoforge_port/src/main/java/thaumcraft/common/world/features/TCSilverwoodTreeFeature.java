package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.common.world.trees.TCSilverwoodTreeGenerator;

/** Natural world generation feature for iconic Thaumcraft Silverwood trees. */
public final class TCSilverwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    public TCSilverwoodTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        return new TCSilverwoodTreeGenerator(false, 7, 4).generate(level, random, origin);
    }
}

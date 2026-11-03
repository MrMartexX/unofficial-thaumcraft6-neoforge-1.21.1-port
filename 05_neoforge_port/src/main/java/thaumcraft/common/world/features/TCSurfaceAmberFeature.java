package thaumcraft.common.world.features;

import java.util.Optional;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

/** Legacy amber: first-air surface Y minus nextInt(25), one replacement attempt. */
public final class TCSurfaceAmberFeature extends ReplaceBlockFeature {
    public TCSurfaceAmberFeature() { super(ReplaceBlockConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        return super.place(new FeaturePlaceContext<>(Optional.empty(), context.level(), context.chunkGenerator(),
                context.random(), context.origin().below(context.random().nextInt(25)), context.config()));
    }
}

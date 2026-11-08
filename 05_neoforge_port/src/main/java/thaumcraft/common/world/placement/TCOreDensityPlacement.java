package thaumcraft.common.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.registry.TCFeatures;

/**
 * Legacy ThaumcraftWorldGenerator.generateOres count placement modifier:
 * gates cinnabar/quartz/amber generation on TCConfig.GENERATE_* and scales
 * the attempt count by Math.round(baseCount * (TCConfig.ORE_DENSITY / 100.0F)).
 */
public final class TCOreDensityPlacement extends RepeatingPlacement {
    public static final MapCodec<TCOreDensityPlacement> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("ore").forGetter(TCOreDensityPlacement::ore),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("count").forGetter(TCOreDensityPlacement::baseCount)
            ).apply(instance, TCOreDensityPlacement::new));

    private final String ore;
    private final int baseCount;

    public TCOreDensityPlacement(String ore, int baseCount) {
        this.ore = ore;
        this.baseCount = baseCount;
    }

    public String ore() {
        return ore;
    }

    public int baseCount() {
        return baseCount;
    }

    @Override
    protected int count(RandomSource random, BlockPos pos) {
        return computeConfiguredCount(ore, baseCount);
    }

    public static int computeConfiguredCount(String ore, int baseCount) {
        if (!TCConfig.SPEC.isLoaded()) {
            return baseCount;
        }
        boolean enabled = switch (ore) {
            case "cinnabar" -> TCConfig.GENERATE_CINNABAR.get();
            case "quartz" -> TCConfig.GENERATE_QUARTZ.get();
            case "amber" -> TCConfig.GENERATE_AMBER.get();
            default -> true;
        };
        if (!enabled) {
            return 0;
        }
        float density = TCConfig.ORE_DENSITY.get() / 100.0F;
        if (density <= 0.0F) {
            return 0;
        }
        return Math.max(0, Math.round(baseCount * density));
    }

    @Override
    public PlacementModifierType<?> type() {
        return TCFeatures.ORE_DENSITY_COUNT.get();
    }
}

package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.common.registry.TCBiomes;
import thaumcraft.common.world.trees.TCSilverwoodTreeGenerator;

/** Natural world generation feature for iconic Thaumcraft Silverwood trees. */
public final class TCSilverwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    private final boolean forestDecorator;

    public TCSilverwoodTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        this(codec, false);
    }

    public TCSilverwoodTreeFeature(Codec<NoneFeatureConfiguration> codec, boolean forestDecorator) {
        super(codec);
        this.forestDecorator = forestDecorator;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (origin.getY() < level.getLevel().getSeaLevel() || !level.getFluidState(origin).isEmpty()) {
            return false;
        }
        RandomSource random = context.random();
        Holder<Biome> biome = level.getBiome(origin);
        if (!forestDecorator) {
            float support = getLegacySilverwoodSupport(biome);
            if (support <= 0.0F || random.nextFloat() >= support) {
                return false;
            }
        }
        int minHeight = forestDecorator ? 8 : 7;
        int randHeight = forestDecorator ? 5 : 4;
        return new TCSilverwoodTreeGenerator(false, minHeight, randHeight).generate(level, random, origin);
    }

    /**
     * Port of legacy {@code ThaumcraftWorldGenerator.generateSilverwood} check from Thaumcraft 6 (1.12.2):
     * {@code BiomeHandler.getBiomeSupportsGreatwood(bi) / 2.0f > random.nextFloat() || magical || forest || mutated_forest}.
     * <ul>
     *   <li>{@code PLAINS}, {@code SAVANNA}, {@code SWAMP}, {@code CONIFEROUS} (Taiga/Grove): {@code 0.2f / 2.0f = 0.1f}</li>
     *   <li>{@code FOREST}, {@code FLOWER_FOREST}, {@code WINDSWEPT_FOREST} & {@code EERIE}: {@code 1.0f}</li>
     * </ul>
     */
    public static float getLegacySilverwoodSupport(Holder<Biome> biome) {
        if (biome.is(TCBiomes.EERIE)
                || biome.is(Biomes.FOREST)
                || biome.is(Biomes.FLOWER_FOREST)
                || biome.is(Biomes.WINDSWEPT_FOREST)) {
            return 1.0F;
        }
        return TCGreatwoodTreeFeature.getLegacyGreatwoodSupport(biome) / 2.0F;
    }
}

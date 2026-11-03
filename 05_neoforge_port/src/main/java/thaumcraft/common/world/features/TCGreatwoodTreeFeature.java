package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.common.registry.TCBiomes;
import thaumcraft.common.world.trees.TCGreatwoodTreeGenerator;

/** Natural world generation feature for iconic Thaumcraft Greatwood trees. */
public final class TCGreatwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    public TCGreatwoodTreeFeature(Codec<NoneFeatureConfiguration> codec) {
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
        Holder<Biome> biome = level.getBiome(origin);
        if (!biome.is(TCBiomes.MAGICAL_FOREST)) {
            float support = getLegacyGreatwoodSupport(biome);
            if (support <= 0.0F || random.nextFloat() >= support) {
                return false;
            }
        }
        boolean spiders = random.nextInt(8) == 0;
        return new TCGreatwoodTreeGenerator(spiders).generate(level, random, origin);
    }

    /**
     * Port of legacy {@code BiomeHandler.getBiomeSupportsGreatwood(int)} from Thaumcraft 6 (1.12.2):
     * <ul>
     *   <li>{@code MAGICAL} / {@code FOREST} (deciduous): {@code 1.0f}</li>
     *   <li>{@code LUSH}: {@code 0.5f}</li>
     *   <li>{@code PLAINS}, {@code SAVANNA}, {@code SWAMP}, {@code CONIFEROUS} (Taiga/Grove): {@code 0.2f}</li>
     * </ul>
     */
    public static float getLegacyGreatwoodSupport(Holder<Biome> biome) {
        if (biome.is(TCBiomes.MAGICAL_FOREST) || biome.is(TCBiomes.EERIE)) {
            return 1.0F;
        }
        if (biome.is(BiomeTags.IS_TAIGA)
                || biome.is(Biomes.GROVE)
                || biome.is(Biomes.PLAINS)
                || biome.is(Biomes.SUNFLOWER_PLAINS)
                || biome.is(Biomes.MEADOW)
                || biome.is(BiomeTags.IS_SAVANNA)
                || biome.is(Biomes.SWAMP)
                || biome.is(Biomes.MANGROVE_SWAMP)) {
            return 0.2F;
        }
        if (biome.is(Biomes.CHERRY_GROVE) || biome.is(Biomes.LUSH_CAVES)) {
            return 0.5F;
        }
        if (biome.is(BiomeTags.IS_FOREST) || biome.is(Biomes.WINDSWEPT_FOREST)) {
            return 1.0F;
        }
        return 0.0F;
    }
}

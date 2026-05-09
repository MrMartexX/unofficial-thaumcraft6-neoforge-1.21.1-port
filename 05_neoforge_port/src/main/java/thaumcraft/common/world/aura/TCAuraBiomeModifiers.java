package thaumcraft.common.world.aura;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

final class TCAuraBiomeModifiers {
    private TCAuraBiomeModifiers() {
    }

    static float getBiomeAuraModifier(Holder<Biome> biome, BlockPos samplePos) {
        List<Float> modifiers = new ArrayList<>();
        ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
        Biome value = biome.value();
        float temperature = value.getBaseTemperature();
        float downfall = value.getModifiedClimateSettings().downfall();

        addTagModifiers(biome, modifiers);
        addExactBiomeModifiers(key, modifiers);
        addClimateModifiers(biome, samplePos, temperature, downfall, modifiers);

        if (modifiers.isEmpty()) {
            return 0.5F;
        }

        float total = 0.0F;
        for (float modifier : modifiers) {
            total += modifier;
        }
        return total / modifiers.size();
    }

    static Holder<Biome> getUncachedBiome(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        return level.getUncachedNoiseBiome(
                QuartPos.fromBlock(pos.getX()),
                QuartPos.fromBlock(pos.getY()),
                QuartPos.fromBlock(pos.getZ())
        );
    }

    private static void addTagModifiers(Holder<Biome> biome, List<Float> modifiers) {
        if (biome.is(BiomeTags.IS_OCEAN)) {
            modifiers.add(0.33F);
        }
        if (biome.is(BiomeTags.IS_RIVER)) {
            modifiers.add(0.4F);
        }
        if (biome.is(BiomeTags.IS_NETHER)) {
            modifiers.add(0.125F);
        }
        if (biome.is(BiomeTags.IS_END)) {
            modifiers.add(0.125F);
        }
        if (biome.is(BiomeTags.IS_BADLANDS)) {
            modifiers.add(0.33F);
        }
        if (biome.is(BiomeTags.IS_TAIGA)) {
            modifiers.add(0.33F);
        }
        if (biome.is(BiomeTags.IS_FOREST)) {
            modifiers.add(0.5F);
        }
        if (biome.is(BiomeTags.IS_JUNGLE)) {
            modifiers.add(0.6F);
        }
        if (biome.is(BiomeTags.IS_SAVANNA)) {
            modifiers.add(0.25F);
        }
        if (biome.is(BiomeTags.IS_MOUNTAIN)) {
            modifiers.add(0.3F);
        }
        if (biome.is(BiomeTags.IS_HILL)) {
            modifiers.add(0.33F);
        }
        if (biome.is(BiomeTags.IS_BEACH)) {
            modifiers.add(0.3F);
        }
    }

    private static void addExactBiomeModifiers(ResourceKey<Biome> key, List<Float> modifiers) {
        if (key == null) {
            return;
        }
        if (key.equals(Biomes.PLAINS) || key.equals(Biomes.SUNFLOWER_PLAINS) || key.equals(Biomes.MEADOW)) {
            modifiers.add(0.3F);
        } else if (key.equals(Biomes.DESERT)) {
            modifiers.add(0.25F);
        } else if (key.equals(Biomes.SWAMP) || key.equals(Biomes.MANGROVE_SWAMP)) {
            modifiers.add(0.5F);
        } else if (key.equals(Biomes.MUSHROOM_FIELDS)) {
            modifiers.add(0.75F);
        } else if (key.equals(Biomes.LUSH_CAVES) || key.equals(Biomes.CHERRY_GROVE)) {
            modifiers.add(0.5F);
        } else if (key.equals(Biomes.DRIPSTONE_CAVES) || key.equals(Biomes.STONY_SHORE) || key.equals(Biomes.STONY_PEAKS)) {
            modifiers.add(0.3F);
        } else if (key.equals(Biomes.DEEP_DARK)) {
            modifiers.add(0.3F);
        } else if (key.equals(Biomes.SOUL_SAND_VALLEY) || key.equals(Biomes.BASALT_DELTAS)) {
            modifiers.add(0.125F);
        } else if (key.equals(Biomes.WARPED_FOREST) || key.equals(Biomes.CRIMSON_FOREST)) {
            modifiers.add(0.5F);
        }
    }

    private static void addClimateModifiers(
            Holder<Biome> biome,
            BlockPos samplePos,
            float temperature,
            float downfall,
            List<Float> modifiers
    ) {
        if (biome.value().coldEnoughToSnow(samplePos)) {
            modifiers.add(0.25F);
        } else if (temperature <= 0.25F) {
            modifiers.add(0.25F);
        }

        if (temperature >= 1.0F) {
            modifiers.add(0.33F);
        }
        if (downfall >= 0.85F) {
            modifiers.add(0.4F);
        }
        if (downfall <= 0.05F && !biome.is(BiomeTags.IS_NETHER) && !biome.is(BiomeTags.IS_END)) {
            modifiers.add(0.125F);
        }
    }
}

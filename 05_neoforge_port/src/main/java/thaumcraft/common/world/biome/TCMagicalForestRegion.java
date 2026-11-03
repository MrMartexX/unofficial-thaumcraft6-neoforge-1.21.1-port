package thaumcraft.common.world.biome;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ModifiedVanillaOverworldBuilder;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.worldgen.RegionUtils;
import thaumcraft.common.registry.TCBiomes;

/**
 * TerraBlender Overworld Region for Thaumcraft 6 Magical Forest.
 *
 * <p>TerraBlender's {@code LayeredNoiseUtil.overworld()} already generates 1.12.2-style
 * {@code GenLayer} Voronoi/ZoomLayer polygons ({@code Region: thaumcraft:overworld_magical_forest}).
 * Previously, filtering {@code TCMagicalForestRegion} to a narrow climate sub-rectangle
 * ({@code T: 2, H: 2..4}, i.e. {@code V >= -0.10}) caused the independent TerraBlender region
 * boundary to intersect the internal {@code V = -0.10} (Forest/Plains) and {@code T = -0.15 / +0.20}
 * contours, pinching 15-20 block mini Magical Forests between a vanilla Forest on one side
 * ({@code Region: minecraft:overworld}) and a vanilla Plains on the other side
 * ({@code Region: thaumcraft:overworld_magical_forest, H: 1, V < -0.10}).</p>
 *
 * <p>To eliminate internal climate pinching inside the region polygon while matching 1.12.2's
 * {@code BiomeType.COOL} + {@code BiomeType.WARM} registration, we replace all non-ocean,
 * non-river, non-beach surface land parameter points across {@code temperatures[1..3]}
 * ({@code [-0.45, +0.55]}) and {@code humidities[0..4]} ({@code [-1.00, +1.00]}).</p>
 */
public class TCMagicalForestRegion extends Region {
    private static final List<ResourceKey<Biome>> UNFROZEN_TEMPERATE_LAND_BIOMES = List.of(
            Biomes.PLAINS,
            Biomes.SUNFLOWER_PLAINS,
            Biomes.FLOWER_FOREST,
            Biomes.FOREST,
            Biomes.BIRCH_FOREST,
            Biomes.OLD_GROWTH_BIRCH_FOREST,
            Biomes.DARK_FOREST,
            Biomes.TAIGA,
            Biomes.OLD_GROWTH_SPRUCE_TAIGA,
            Biomes.OLD_GROWTH_PINE_TAIGA,
            Biomes.MEADOW,
            Biomes.CHERRY_GROVE,
            Biomes.SAVANNA,
            Biomes.SAVANNA_PLATEAU,
            Biomes.WINDSWEPT_SAVANNA,
            Biomes.JUNGLE,
            Biomes.SPARSE_JUNGLE,
            Biomes.BAMBOO_JUNGLE,
            Biomes.WINDSWEPT_HILLS,
            Biomes.WINDSWEPT_GRAVELLY_HILLS,
            Biomes.WINDSWEPT_FOREST,
            Biomes.GROVE,
            Biomes.SNOWY_SLOPES,
            Biomes.JAGGED_PEAKS,
            Biomes.FROZEN_PEAKS,
            Biomes.STONY_PEAKS,
            Biomes.SWAMP
    );

    public TCMagicalForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            for (ResourceKey<Biome> biome : UNFROZEN_TEMPERATE_LAND_BIOMES) {
                replaceInTemperateWarmCoolBand(builder, biome);
            }
            // Mangrove Swamp uses a merged temperature span [0.20, 1.00] (temperatures[3..4]) at erosions[6];
            // replace it as well so T: 3 has no internal erosion[6] boundary.
            builder.replaceBiome(Biomes.MANGROVE_SWAMP, TCBiomes.MAGICAL_FOREST);
        });
    }

    private static void replaceInTemperateWarmCoolBand(
            ModifiedVanillaOverworldBuilder builder,
            ResourceKey<Biome> vanillaBiome) {
        List<Climate.ParameterPoint> points = RegionUtils.getVanillaParameterPoints(vanillaBiome);
        // Target temperatures[1..3] = [-0.45, +0.55] across ALL humidities[0..4] = [-1.00, +1.00],
        // ALL continentalness slices, and ALL erosion slices (excluding only temperatures[0] frozen
        // and temperatures[4] desert/badlands).
        long minTempQuantized = Climate.quantizeCoord(-0.46F);
        long maxTempQuantized = Climate.quantizeCoord(0.56F);

        for (Climate.ParameterPoint point : points) {
            if (point.temperature().min() >= minTempQuantized
                    && point.temperature().max() <= maxTempQuantized) {
                builder.replaceBiome(point, TCBiomes.MAGICAL_FOREST);
            }
        }
    }
}

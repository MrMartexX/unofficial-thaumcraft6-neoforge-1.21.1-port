package thaumcraft.common.world.biome;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import thaumcraft.common.registry.TCBiomes;

/**
 * TerraBlender Overworld Region for Thaumcraft 6 Magical Forest.
 * Injects the magical forest biome into the 1.21.1 Multi-Noise climate grid
 * within temperate, moist inland terrain, matching legacy climate characteristics.
 */
public class TCMagicalForestRegion extends Region {
    public TCMagicalForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Temperature: 0.15F .. 0.45F (Temperate, aligns with Oak/Birch forests and plains)
        // Humidity: 0.30F .. 0.70F (Moist/lush, aligns with dark forest and wetlands)
        // Continentalness: 0.35F .. 0.80F (Inland/deep mainland, avoiding open oceans and coasts)
        // Erosion: 0.00F .. 0.55F (Rolling hills, valleys, and gentle plateaus)
        // Depth: 0.0F (Surface)
        // Weirdness: 0.45F .. 0.75F (Narrow variant band to create atmospheric, compact magical groves)
        // Offset: 0.0F
        this.addBiome(mapper,
                Climate.parameters(
                        Climate.Parameter.span(0.15F, 0.45F),
                        Climate.Parameter.span(0.30F, 0.70F),
                        Climate.Parameter.span(0.35F, 0.80F),
                        Climate.Parameter.span(0.00F, 0.55F),
                        Climate.Parameter.point(0.0F),
                        Climate.Parameter.span(0.45F, 0.75F),
                        0.0F
                ),
                TCBiomes.MAGICAL_FOREST
        );
    }
}

package thaumcraft.common.world.biome;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(Biomes.FOREST, TCBiomes.MAGICAL_FOREST);
            builder.replaceBiome(Biomes.FLOWER_FOREST, TCBiomes.MAGICAL_FOREST);
        });
    }
}


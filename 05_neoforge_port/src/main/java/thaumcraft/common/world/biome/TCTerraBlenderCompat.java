package thaumcraft.common.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Regions;
import thaumcraft.Thaumcraft;

/** Isolates TerraBlender API calls so the mod can still load if TerraBlender is absent. */
public final class TCTerraBlenderCompat {
    private static final int MAGICAL_FOREST_REGION_WEIGHT = 1;
    private static volatile boolean enabled = false;

    private TCTerraBlenderCompat() {
    }

    public static void registerRegions() {
        Regions.register(new TCMagicalForestRegion(
                ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "overworld_magical_forest"),
                MAGICAL_FOREST_REGION_WEIGHT
        ));
        enabled = true;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static Holder<Biome> filterMagicalForest(
            Climate.ParameterList<Holder<Biome>> parameters,
            int quartX,
            int quartY,
            int quartZ,
            Climate.Sampler sampler,
            Holder<Biome> current) {
        return TCMagicalForestShapeFilter.filterMagicalForest(parameters, quartX, quartY, quartZ, sampler, current);
    }
}

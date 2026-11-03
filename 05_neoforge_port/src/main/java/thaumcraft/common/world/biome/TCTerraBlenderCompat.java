package thaumcraft.common.world.biome;

import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;
import thaumcraft.Thaumcraft;

public final class TCTerraBlenderCompat {
    private TCTerraBlenderCompat() {}

    public static void registerRegions() {
        // Legacy Thaumcraft 6 biomeMagicalForestWeight = 5 in WARM and COOL types.
        // In TerraBlender overworld (default weight = 10), weight 3 provides an authentic,
        // well-balanced distribution (~1 magical forest per 10-15 temperate forest encounters).
        Regions.register(new TCMagicalForestRegion(
                ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "overworld_magical_forest"),
                3
        ));
        Thaumcraft.LOGGER.info("Registered Thaumcraft Magical Forest region with TerraBlender (weight: 3).");
    }
}

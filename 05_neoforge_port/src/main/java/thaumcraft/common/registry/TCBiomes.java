package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import thaumcraft.Thaumcraft;

/** Registry keys for Thaumcraft biomes ported to NeoForge 1.21.1. */
public final class TCBiomes {
    public static final ResourceKey<Biome> MAGICAL_FOREST =
            ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "magical_forest"));

    public static final ResourceKey<Biome> EERIE =
            ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "eerie"));

    public static final ResourceKey<Biome> ELDRITCH =
            ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "eldritch"));

    private TCBiomes() {
    }
}

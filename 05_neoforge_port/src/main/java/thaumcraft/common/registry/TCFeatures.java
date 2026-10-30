package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.world.features.TCCrystalClusterFeature;

/** NeoForge worldgen feature registrations used by Thaumcraft datapack features. */
public final class TCFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Thaumcraft.MODID);

    public static final DeferredHolder<Feature<?>, TCCrystalClusterFeature> CRYSTAL_CLUSTER_AER =
            FEATURES.register("crystal_cluster_aer",
                    () -> new TCCrystalClusterFeature(NoneFeatureConfiguration.CODEC, TCBlocks.CRYSTAL_AER));
    public static final DeferredHolder<Feature<?>, TCCrystalClusterFeature> CRYSTAL_CLUSTER_IGNIS =
            FEATURES.register("crystal_cluster_ignis",
                    () -> new TCCrystalClusterFeature(NoneFeatureConfiguration.CODEC, TCBlocks.CRYSTAL_IGNIS));
    public static final DeferredHolder<Feature<?>, TCCrystalClusterFeature> CRYSTAL_CLUSTER_AQUA =
            FEATURES.register("crystal_cluster_aqua",
                    () -> new TCCrystalClusterFeature(NoneFeatureConfiguration.CODEC, TCBlocks.CRYSTAL_AQUA));
    public static final DeferredHolder<Feature<?>, TCCrystalClusterFeature> CRYSTAL_CLUSTER_TERRA =
            FEATURES.register("crystal_cluster_terra",
                    () -> new TCCrystalClusterFeature(NoneFeatureConfiguration.CODEC, TCBlocks.CRYSTAL_TERRA));
    public static final DeferredHolder<Feature<?>, TCCrystalClusterFeature> CRYSTAL_CLUSTER_ORDO =
            FEATURES.register("crystal_cluster_ordo",
                    () -> new TCCrystalClusterFeature(NoneFeatureConfiguration.CODEC, TCBlocks.CRYSTAL_ORDO));
    public static final DeferredHolder<Feature<?>, TCCrystalClusterFeature> CRYSTAL_CLUSTER_PERDITIO =
            FEATURES.register("crystal_cluster_perditio",
                    () -> new TCCrystalClusterFeature(NoneFeatureConfiguration.CODEC, TCBlocks.CRYSTAL_PERDITIO));

    private TCFeatures() {
    }
}

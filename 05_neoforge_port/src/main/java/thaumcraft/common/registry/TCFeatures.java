package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.world.features.TCBigMagicTreeFeature;
import thaumcraft.common.world.features.TCCrystalClusterFeature;
import thaumcraft.common.world.features.TCCultistPortalFeature;
import thaumcraft.common.world.features.TCGreatwoodTreeFeature;
import thaumcraft.common.world.features.TCMoundFeature;
import thaumcraft.common.world.features.TCLegacyPatchFeature;
import thaumcraft.common.world.features.TCSurfaceAmberFeature;
import thaumcraft.common.world.features.TCSilverwoodTreeFeature;

/** NeoForge worldgen feature registrations used by Thaumcraft datapack features. */
public final class TCFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Thaumcraft.MODID);

    public static final DeferredHolder<Feature<?>, TCCrystalClusterFeature> CRYSTAL_CLUSTER =
            FEATURES.register("crystal_cluster",
                    () -> new TCCrystalClusterFeature(NoneFeatureConfiguration.CODEC));

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

    public static final DeferredHolder<Feature<?>, TCGreatwoodTreeFeature> TREE_GREATWOOD =
            FEATURES.register("tree_greatwood",
                    () -> new TCGreatwoodTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, TCSilverwoodTreeFeature> TREE_SILVERWOOD =
            FEATURES.register("tree_silverwood",
                    () -> new TCSilverwoodTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, TCSilverwoodTreeFeature> TREE_SILVERWOOD_CANOPY =
            FEATURES.register("tree_silverwood_canopy",
                    () -> new TCSilverwoodTreeFeature(NoneFeatureConfiguration.CODEC, true));
    public static final DeferredHolder<Feature<?>, TCBigMagicTreeFeature> TREE_BIG_MAGIC =
            FEATURES.register("tree_big_magic",
                    () -> new TCBigMagicTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, TCMoundFeature> MOUND =
            FEATURES.register("mound",
                    () -> new TCMoundFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, TCLegacyPatchFeature> LEGACY_FLOWERS =
            FEATURES.register("legacy_flowers", () -> new TCLegacyPatchFeature(TCLegacyPatchFeature.Anchor.FLOWER));
    public static final DeferredHolder<Feature<?>, TCSurfaceAmberFeature> SURFACE_AMBER_ORE =
            FEATURES.register("surface_amber_ore", TCSurfaceAmberFeature::new);
    public static final DeferredHolder<Feature<?>, TCLegacyPatchFeature> LEGACY_GRASS =
            FEATURES.register("legacy_grass", () -> new TCLegacyPatchFeature(TCLegacyPatchFeature.Anchor.GRASS));
    public static final DeferredHolder<Feature<?>, TCLegacyPatchFeature> LEGACY_WATERLILY =
            FEATURES.register("legacy_waterlily", () -> new TCLegacyPatchFeature(TCLegacyPatchFeature.Anchor.WATERLILY));
    public static final DeferredHolder<Feature<?>, TCLegacyPatchFeature> LEGACY_VISHROOM =
            FEATURES.register("legacy_vishroom", () -> new TCLegacyPatchFeature(TCLegacyPatchFeature.Anchor.VISHROOM));
    public static final DeferredHolder<Feature<?>, TCLegacyPatchFeature> LEGACY_CINDERPEARL =
            FEATURES.register("legacy_cinderpearl", () -> new TCLegacyPatchFeature(TCLegacyPatchFeature.Anchor.CINDERPEARL));
    public static final DeferredHolder<Feature<?>, TCLegacyPatchFeature> LEGACY_RANDOM_PATCH =
            FEATURES.register("legacy_random_patch", () -> new TCLegacyPatchFeature(TCLegacyPatchFeature.Anchor.RANDOM_HEIGHT));
    public static final DeferredHolder<Feature<?>, TCCultistPortalFeature> CULTIST_PORTAL_LESSER =
            FEATURES.register("cultist_portal_lesser",
                    () -> new TCCultistPortalFeature(NoneFeatureConfiguration.CODEC));

    private TCFeatures() {
    }
}

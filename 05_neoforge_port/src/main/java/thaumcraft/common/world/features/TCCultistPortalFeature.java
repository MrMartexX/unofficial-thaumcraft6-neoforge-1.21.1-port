package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.common.entities.TCCultistPortalLesserEntity;
import thaumcraft.common.registry.TCEntityTypes;

/**
 * Natural world generation feature restoring TC6 surface lesser cultist portals.
 * Spawns an active lesser portal in the Overworld which summons Crimson Cultists.
 */
public final class TCCultistPortalFeature extends Feature<NoneFeatureConfiguration> {
    public TCCultistPortalFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        BlockState belowState = level.getBlockState(origin.below());
        if (!belowState.isSolid() || belowState.isAir()) {
            return false;
        }

        if (!level.getBlockState(origin).isAir() || !level.getBlockState(origin.above()).isAir()) {
            return false;
        }

        TCCultistPortalLesserEntity portal =
                new TCCultistPortalLesserEntity(TCEntityTypes.CULTIST_PORTAL_LESSER.get(), level.getLevel());
        portal.moveTo(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, context.random().nextFloat() * 360.0F, 0.0F);
        portal.finalizeSpawn(level, level.getCurrentDifficultyAt(origin), MobSpawnType.STRUCTURE, null);
        level.addFreshEntity(portal);

        return true;
    }
}

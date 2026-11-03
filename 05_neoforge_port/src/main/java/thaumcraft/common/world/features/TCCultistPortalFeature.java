package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
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
        return placePortalAt(context.level(), context.random(), context.origin());
    }

    public static boolean placePortalNear(WorldGenLevel level, RandomSource random, int centerX, int centerZ) {
        for (int radius = 0; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    int x = centerX + dx;
                    int z = centerZ + dz;
                    if (!level.hasChunkAt(new BlockPos(x, 64, z))) {
                        continue;
                    }
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                    if (placePortalAt(level, random, new BlockPos(x, y, z))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean placePortalAt(WorldGenLevel level, RandomSource random, BlockPos origin) {
        if (origin.getY() < level.getLevel().getSeaLevel()
                || !level.getFluidState(origin).isEmpty()
                || !level.getFluidState(origin.below()).isEmpty()) {
            return false;
        }

        BlockState belowState = level.getBlockState(origin.below());
        if (!isValidPortalGround(belowState)) {
            return false;
        }

        BlockState originState = level.getBlockState(origin);
        BlockState aboveState = level.getBlockState(origin.above());
        if (!(originState.isAir() || originState.canBeReplaced())
                || !(aboveState.isAir() || aboveState.canBeReplaced())
                || !level.getFluidState(origin.above()).isEmpty()) {
            return false;
        }

        spawnPlannedPortal(level, random, origin);
        return true;
    }

    /** Called only after structure admission, at its persisted position and owning chunk. */
    public static void spawnPlannedPortal(WorldGenLevel level, RandomSource random, BlockPos origin) {
        TCCultistPortalLesserEntity portal =
                new TCCultistPortalLesserEntity(TCEntityTypes.CULTIST_PORTAL_LESSER.get(), level.getLevel());
        portal.moveTo(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        portal.finalizeSpawn(level, level.getCurrentDifficultyAt(origin), MobSpawnType.STRUCTURE, null);
        level.addFreshEntity(portal);

    }

    public static boolean isValidPortalGround(BlockState belowState) {
        return belowState.isSolid()
                && !belowState.isAir()
                && !belowState.is(BlockTags.LEAVES)
                && !belowState.is(BlockTags.LOGS)
                && !belowState.is(Blocks.ICE)
                && !belowState.is(Blocks.PACKED_ICE)
                && !belowState.is(Blocks.BLUE_ICE)
                && belowState.getFluidState().isEmpty();
    }
}

package thaumcraft.common.crafting.infusion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

/** Requests a bounded legacy surroundings rescan after an altar-relevant world change. */
public final class TCInfusionSurroundingsInvalidator {
    private TCInfusionSurroundingsInvalidator() {
    }

    public static void requestNearby(Level level, BlockPos changedPos) {
        if (!(level instanceof ServerLevel serverLevel) || changedPos == null) {
            return;
        }
        for (int dx = -TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE;
             dx <= TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE; dx++) {
            for (int dz = -TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE;
                 dz <= TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE; dz++) {
                for (int dy = -TCInfusionMatrixBlockEntity.LEGACY_SCAN_MAX_Y_OFFSET;
                     dy <= -TCInfusionMatrixBlockEntity.LEGACY_SCAN_MIN_Y_OFFSET; dy++) {
                    BlockPos matrixPos = changedPos.offset(dx, dy, dz);
                    if (serverLevel.isLoaded(matrixPos)
                            && serverLevel.getBlockEntity(matrixPos) instanceof TCInfusionMatrixBlockEntity matrix) {
                        matrix.requestSurroundingsRefresh();
                    }
                }
            }
        }
    }
}

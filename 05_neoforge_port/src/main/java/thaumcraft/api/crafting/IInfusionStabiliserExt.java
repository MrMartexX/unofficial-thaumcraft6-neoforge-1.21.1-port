package thaumcraft.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

/** Extended Thaumcraft 6 infusion stabilizer contract. */
public interface IInfusionStabiliserExt extends IInfusionStabiliser {
    /** Returns the stabilization supplied by a symmetrical pair, not by one block. */
    float getStabilizationAmount(LevelReader level, BlockPos pos);

    default boolean hasSymmetryPenalty(LevelReader level, BlockPos pos1, BlockPos pos2) {
        return false;
    }

    default float getSymmetryPenalty(LevelReader level, BlockPos pos) {
        return 0.0F;
    }
}

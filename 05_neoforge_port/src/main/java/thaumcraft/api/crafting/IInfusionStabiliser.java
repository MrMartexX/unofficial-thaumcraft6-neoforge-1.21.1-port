package thaumcraft.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

/**
 * Public compatibility contract for blocks that participate in infusion symmetry.
 *
 * <p>The misspelt method name is retained from Thaumcraft 6 deliberately. Addons compiled against the
 * modern port can keep the legacy semantic contract while using the current world API.</p>
 */
public interface IInfusionStabiliser {
    boolean canStabaliseInfusion(LevelReader level, BlockPos pos);
}

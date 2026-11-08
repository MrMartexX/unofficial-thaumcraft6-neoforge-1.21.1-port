package thaumcraft.api.items;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Interface implemented by items with architectural / multi-block placement preview capabilities.
 */
public interface IArchitect {

    /**
     * Returns the hit result that should be used as the starting point.
     */
    HitResult getArchitectMOP(ItemStack stack, Level world, LivingEntity player);

    /**
     * @return whether this item triggers block highlighting events
     */
    boolean useBlockHighlight(ItemStack stack);

    /**
     * Returns a list of blocks that should be highlighted or affected in world.
     */
    ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, Level world, BlockPos pos, Direction side, Player player);

    /**
     * Which axis should be displayed.
     */
    boolean showAxis(ItemStack stack, Level world, Player player, Direction side, EnumAxis axis);

    enum EnumAxis {
        X, // east / west
        Y, // up / down
        Z  // north / south
    }
}

package thaumcraft.api.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Modern boundary for TC6 jar labels.
 *
 * <p>Legacy Forge exposed this API so blocks and tile entities could accept a label item and decide
 * whether the label should be consumed. The port keeps the same contract shape while using
 * DataComponents for the aspect stored on filled labels.</p>
 */
public interface ILabelable {
    boolean applyLabel(Player player, BlockPos pos, Direction side, ItemStack labelStack);
}

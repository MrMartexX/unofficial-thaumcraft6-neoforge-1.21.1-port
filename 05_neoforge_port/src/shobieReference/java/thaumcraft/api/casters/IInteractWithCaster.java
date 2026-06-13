package thaumcraft.api.casters;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Blocks or tile entities implementing this interface can respond
 * to being right-clicked with a caster item (gauntlet).
 */
public interface IInteractWithCaster {
    
    /**
     * Called when this block/tile is right-clicked with a caster.
     * 
     * @param level The world
     * @param casterStack The caster item stack
     * @param player The player using the caster
     * @param pos The block position
     * @param side The side clicked
     * @param hand The hand used
     * @return True if the interaction was handled (prevent further processing)
     */
    boolean onCasterRightClick(Level level, ItemStack casterStack, Player player, 
                               BlockPos pos, Direction side, InteractionHand hand);
}

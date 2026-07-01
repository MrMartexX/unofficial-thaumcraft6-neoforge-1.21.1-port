package thaumcraft.api.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Gear that contributes passive warp while worn or active.
 */
public interface IWarpingGear {
    int getWarp(ItemStack stack, Player player);
}

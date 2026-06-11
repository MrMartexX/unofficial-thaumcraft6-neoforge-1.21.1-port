package thaumcraft.api.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Gear implementing this interface reduces vis costs for the wearer.
 *
 * <p>The returned value is a percentage, matching the legacy Thaumcraft 6 API.
 * The total wearer discount is capped by the consumer, not by individual items.</p>
 */
public interface IVisDiscountGear {
    int getVisDiscount(ItemStack stack, Player player);
}

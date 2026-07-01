package thaumcraft.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Gear that enables Thaumcraft's in-world thaumometer popups.
 */
public interface IGoggles {
    boolean showIngamePopups(ItemStack stack, LivingEntity wearer);
}

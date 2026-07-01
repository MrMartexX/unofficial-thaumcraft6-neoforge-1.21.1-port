package thaumcraft.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Gear that reveals hidden Thaumcraft nodes, auras, and similar overlays.
 */
public interface IRevealer {
    boolean showNodes(ItemStack stack, LivingEntity wearer);
}

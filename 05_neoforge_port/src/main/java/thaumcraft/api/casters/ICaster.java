package thaumcraft.api.casters;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Modernized public caster contract.
 *
 * <p>Legacy Thaumcraft exposed this as an item-stack API. The port keeps that
 * shape, but uses modern Minecraft types and Data Components internally.</p>
 */
public interface ICaster {
    float getConsumptionModifier(ItemStack stack, Player player, boolean crafting);

    boolean consumeVis(ItemStack stack, Player player, float amount, boolean crafting, boolean simulate);

    ItemStack getFocusStack(ItemStack stack);

    void setFocus(ItemStack stack, ItemStack focus);

    ItemStack getPickedBlock(ItemStack stack);
}

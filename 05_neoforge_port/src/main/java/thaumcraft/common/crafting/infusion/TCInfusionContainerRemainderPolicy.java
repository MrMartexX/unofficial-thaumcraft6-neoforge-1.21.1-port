package thaumcraft.common.crafting.infusion;

import java.util.Optional;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Legacy-compatible container/remainder policy for infusion mutation.
 *
 * <p>Legacy calls {@code getContainerItem} only for consumed side-pedestal components.
 * The center catalyst is replaced by the result and does not leave a crafting remainder.
 */
public final class TCInfusionContainerRemainderPolicy {
    private static final Set<Item> KNOWN_REMAINDER_INPUTS = Set.of(
            Items.WATER_BUCKET,
            Items.LAVA_BUCKET,
            Items.MILK_BUCKET,
            Items.POWDER_SNOW_BUCKET,
            Items.COD_BUCKET,
            Items.SALMON_BUCKET,
            Items.TROPICAL_FISH_BUCKET,
            Items.PUFFERFISH_BUCKET,
            Items.AXOLOTL_BUCKET,
            Items.TADPOLE_BUCKET,
            Items.POTION,
            Items.SPLASH_POTION,
            Items.LINGERING_POTION,
            Items.HONEY_BOTTLE,
            Items.GLASS_BOTTLE,
            Items.MUSHROOM_STEW,
            Items.RABBIT_STEW,
            Items.BEETROOT_SOUP,
            Items.SUSPICIOUS_STEW
    );

    private TCInfusionContainerRemainderPolicy() {
    }

    public static boolean requiresExplicitPolicy(TCInfusionCraftingPlan plan) {
        return false;
    }

    public static Optional<String> firstBlockingInput(TCInfusionCraftingPlan plan) {
        return Optional.empty();
    }

    public static boolean hasKnownRemainderInput(TCInfusionCraftingPlan plan) {
        if (plan == null) {
            return false;
        }
        if (isKnownRemainderInput(plan.catalyst())) {
            return true;
        }
        for (ItemStack component : plan.components()) {
            if (isKnownRemainderInput(component)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack remainderForComponent(ItemStack input) {
        if (input == null || input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (input.hasCraftingRemainingItem()) {
            return input.getCraftingRemainingItem().copy();
        }
        Item item = input.getItem();
        if (item == Items.WATER_BUCKET || item == Items.LAVA_BUCKET || item == Items.MILK_BUCKET || item == Items.POWDER_SNOW_BUCKET) {
            return new ItemStack(Items.BUCKET);
        }
        if (item == Items.COD_BUCKET || item == Items.SALMON_BUCKET || item == Items.TROPICAL_FISH_BUCKET || item == Items.PUFFERFISH_BUCKET
                || item == Items.AXOLOTL_BUCKET || item == Items.TADPOLE_BUCKET) {
            return new ItemStack(Items.WATER_BUCKET);
        }
        if (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.HONEY_BOTTLE) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (item == Items.MUSHROOM_STEW || item == Items.RABBIT_STEW || item == Items.BEETROOT_SOUP || item == Items.SUSPICIOUS_STEW) {
            return new ItemStack(Items.BOWL);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isKnownRemainderInput(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && (stack.hasCraftingRemainingItem() || KNOWN_REMAINDER_INPUTS.contains(stack.getItem()));
    }
}

package thaumcraft.common.crafting.infusion;

import java.util.Optional;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Temporary guard for infusion inputs that need explicit container/remainder handling.
 *
 * <p>The mutation executor is still audit-only. Until legacy container-item and real
 * essentia-source timing are implemented, plans containing known remainder inputs are
 * rejected instead of silently deleting buckets, bottles or bowls.
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
        return firstBlockingInput(plan).isPresent();
    }

    public static Optional<String> firstBlockingInput(TCInfusionCraftingPlan plan) {
        if (plan == null) {
            return Optional.empty();
        }
        if (isKnownRemainderInput(plan.catalyst())) {
            return Optional.of("catalyst");
        }
        for (int index = 0; index < plan.components().size(); index++) {
            if (isKnownRemainderInput(plan.component(index))) {
                return Optional.of("component[" + index + "]");
            }
        }
        return Optional.empty();
    }

    public static boolean isKnownRemainderInput(ItemStack stack) {
        return stack != null && !stack.isEmpty() && KNOWN_REMAINDER_INPUTS.contains(stack.getItem());
    }
}
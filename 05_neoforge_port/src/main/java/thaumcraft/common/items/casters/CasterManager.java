package thaumcraft.common.items.casters;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.IVisDiscountGear;

public final class CasterManager {
    public static final int MAX_VIS_DISCOUNT_PERCENT = 50;
    private static final List<VisDiscountStackProvider> EXTRA_DISCOUNT_STACK_PROVIDERS = new CopyOnWriteArrayList<>();

    private CasterManager() {
    }

    public static AutoCloseable registerVisDiscountStackProvider(VisDiscountStackProvider provider) {
        EXTRA_DISCOUNT_STACK_PROVIDERS.add(provider);
        return () -> EXTRA_DISCOUNT_STACK_PROVIDERS.remove(provider);
    }

    public static float getTotalVisDiscount(Player player) {
        return getTotalVisDiscountPercent(player) / 100.0F;
    }

    public static int getTotalVisDiscountPercent(Player player) {
        if (player == null) {
            return 0;
        }
        int total = 0;
        for (ItemStack stack : player.getInventory().armor) {
            total += getVisDiscount(stack, player);
        }
        for (VisDiscountStackProvider provider : EXTRA_DISCOUNT_STACK_PROVIDERS) {
            Iterable<ItemStack> stacks = provider.getDiscountStacks(player);
            if (stacks == null) {
                continue;
            }
            for (ItemStack stack : stacks) {
                total += getVisDiscount(stack, player);
            }
        }
        return Math.min(MAX_VIS_DISCOUNT_PERCENT, Math.max(0, total));
    }

    public static int getDiscountedVisCost(int baseVis, Player player) {
        if (baseVis <= 0) {
            return 0;
        }
        return (int)(baseVis * (1.0F - getTotalVisDiscount(player)));
    }

    private static int getVisDiscount(ItemStack stack, Player player) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IVisDiscountGear gear)) {
            return 0;
        }
        return Math.max(0, gear.getVisDiscount(stack, player));
    }

    public interface VisDiscountStackProvider {
        Iterable<ItemStack> getDiscountStacks(Player player);
    }
}

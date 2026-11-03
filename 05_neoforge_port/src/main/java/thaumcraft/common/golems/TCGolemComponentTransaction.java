package thaumcraft.common.golems;

import java.util.*;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/** Plans cumulative costs, then escrows extraction; a failed commit never starts a job. */
public final class TCGolemComponentTransaction {
    private record Extraction(IItemHandler source, int slot, ItemStack stack) {}
    private TCGolemComponentTransaction() {}

    public static boolean consume(List<IItemHandler> sources, ItemStack[] costs, Consumer<ItemStack> refund) {
        List<Extraction> plan = new ArrayList<>(), escrow = new ArrayList<>();
        var reserved = new IdentityHashMap<IItemHandler, Map<Integer, Integer>>();
        for (ItemStack cost : costs) {
            if (cost.isEmpty()) continue;
            int needed = cost.getCount();
            for (IItemHandler source : sources) {
                var used = reserved.computeIfAbsent(source, key -> new HashMap<>());
                for (int slot = 0; slot < source.getSlots() && needed > 0; slot++) {
                    int already = used.getOrDefault(slot, 0);
                    ItemStack accessible = source.extractItem(slot, needed + already, true);
                    if (!ItemStack.isSameItemSameComponents(accessible, cost)) continue;
                    int amount = Math.min(needed, accessible.getCount() - already);
                    if (amount <= 0) continue;
                    plan.add(new Extraction(source, slot, cost.copyWithCount(amount)));
                    used.put(slot, already + amount);
                    needed -= amount;
                }
            }
            if (needed != 0) return false;
        }
        boolean committed = false;
        try {
            for (Extraction step : plan) {
                ItemStack extracted = step.source.extractItem(step.slot, step.stack.getCount(), false);
                if (!extracted.isEmpty()) escrow.add(new Extraction(step.source, step.slot, extracted.copy()));
                if (!ItemStack.matches(extracted, step.stack)) return false;
            }
            committed = true;
            return true;
        } finally {
            if (!committed) for (int i = escrow.size() - 1; i >= 0; i--) {
                Extraction step = escrow.get(i);
                ItemStack remainder = step.source.insertItem(step.slot, step.stack.copy(), false);
                if (!remainder.isEmpty()) remainder = ItemHandlerHelper.insertItemStacked(step.source, remainder, false);
                if (!remainder.isEmpty()) refund.accept(remainder);
            }
        }
    }
}

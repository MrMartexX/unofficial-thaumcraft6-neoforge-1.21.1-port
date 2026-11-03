package thaumcraft.common.golems;

import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

final class TCGolemTransactionAudit {
    static void run(List<TCGolemBehaviorAudit.Row> rows) {
        ItemStackHandler stock = new ItemStackHandler(1);
        stock.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 3));
        List<ItemStack> refunds = new ArrayList<>();
        boolean cumulative = !TCGolemComponentTransaction.consume(List.of(stock), new ItemStack[]{
                new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.IRON_INGOT, 2)}, refunds::add)
                && stock.getStackInSlot(0).getCount() == 3 && refunds.isEmpty();
        rows.add(new TCGolemBehaviorAudit.Row("golem_press_cumulative_cost_plan", cumulative, "Duplicate component costs cannot spend the same three ingots twice"));
        ItemStackHandler changing = new ItemStackHandler(1) {
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return simulate ? super.extractItem(slot, amount, true) : ItemStack.EMPTY;
            }
        };
        changing.setStackInSlot(0, new ItemStack(Items.GOLD_INGOT, 2));
        boolean rolledBack = !TCGolemComponentTransaction.consume(List.of(stock, changing), new ItemStack[]{
                new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.GOLD_INGOT, 1)}, refunds::add)
                && stock.getStackInSlot(0).getCount() == 3 && changing.getStackInSlot(0).getCount() == 2 && refunds.isEmpty();
        rows.add(new TCGolemBehaviorAudit.Row("golem_press_changed_extraction_rollback", rolledBack, "A later extraction refusing its simulation refunds earlier components and rejects the craft"));
        ItemStackHandler noInsert = new ItemStackHandler(1) {
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
        };
        noInsert.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 3));
        boolean conserved = !TCGolemComponentTransaction.consume(List.of(noInsert, changing), new ItemStack[]{
                new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.GOLD_INGOT, 1)}, refunds::add)
                && noInsert.getStackInSlot(0).getCount() == 1 && refunds.size() == 1 && refunds.get(0).getCount() == 2;
        rows.add(new TCGolemBehaviorAudit.Row("golem_press_noninsertable_refund_conservation", conserved, "If a provider rejects rollback insertion, extracted items reach the caller's explicit refund path rather than disappearing"));
        boolean committed = TCGolemComponentTransaction.consume(List.of(stock), new ItemStack[]{new ItemStack(Items.IRON_INGOT, 2)}, refunds::add)
                && stock.getStackInSlot(0).getCount() == 1;
        rows.add(new TCGolemBehaviorAudit.Row("golem_press_exact_commit", committed, "A valid extraction consumes exactly its planned quantity"));
    }
}

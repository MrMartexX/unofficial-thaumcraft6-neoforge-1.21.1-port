package thaumcraft.common.items.tools;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public final class TCSpecialMiningResult {
    private TCSpecialMiningResult() {
    }

    public static ItemStack findSpecialMiningResult(ItemStack drop) {
        if (drop == null || drop.isEmpty()) {
            return ItemStack.EMPTY;
        }

        var item = drop.getItem();
        if (item == Items.RAW_IRON || item == Items.IRON_ORE || item == Items.DEEPSLATE_IRON_ORE) {
            return new ItemStack(TCItems.CLUSTER_IRON.get(), drop.getCount());
        } else if (item == Items.RAW_GOLD || item == Items.GOLD_ORE || item == Items.DEEPSLATE_GOLD_ORE || item == Items.NETHER_GOLD_ORE) {
            return new ItemStack(TCItems.CLUSTER_GOLD.get(), drop.getCount());
        } else if (item == Items.RAW_COPPER || item == Items.COPPER_ORE || item == Items.DEEPSLATE_COPPER_ORE) {
            return new ItemStack(TCItems.CLUSTER_COPPER.get(), drop.getCount());
        } else if (item == TCItems.QUICKSILVER.get() || item == TCBlocks.ORE_CINNABAR.get().asItem()) {
            return new ItemStack(TCItems.CLUSTER_CINNABAR.get(), drop.getCount());
        } else if (item == TCBlocks.ORE_AMBER.get().asItem()) {
            return new ItemStack(TCItems.AMBER.get(), drop.getCount());
        } else if (item == Items.QUARTZ || item == Items.NETHER_QUARTZ_ORE) {
            return new ItemStack(TCItems.CLUSTER_QUARTZ.get(), drop.getCount());
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack findSpecialMiningResult(ItemStack drop, float chance, RandomSource rand) {
        if (drop == null || drop.isEmpty() || rand.nextFloat() > chance) {
            return drop;
        }

        ItemStack special = findSpecialMiningResult(drop);
        return !special.isEmpty() ? special : drop;
    }
}

package thaumcraft.common.research.theorycraft;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.common.registry.TCItems;

final class TCTheorycraftItemOptions {
    private TCTheorycraftItemOptions() {
    }

    static List<ItemStack> artificeOptions() {
        ArrayList<ItemStack> options = new ArrayList<>();
        add(options, TCItems.VIS_RESONATOR.get());
        add(options, TCItems.THAUMOMETER.get());
        add(options, Blocks.ANVIL.asItem());
        add(options, Blocks.ACTIVATOR_RAIL.asItem());
        add(options, Blocks.DISPENSER.asItem());
        add(options, Blocks.DROPPER.asItem());
        add(options, Blocks.ENCHANTING_TABLE.asItem());
        add(options, Blocks.ENDER_CHEST.asItem());
        add(options, Blocks.JUKEBOX.asItem());
        add(options, Blocks.DAYLIGHT_DETECTOR.asItem());
        add(options, Blocks.PISTON.asItem());
        add(options, Blocks.HOPPER.asItem());
        add(options, Blocks.STICKY_PISTON.asItem());
        add(options, Items.MAP);
        add(options, Items.COMPASS);
        add(options, Items.TNT_MINECART);
        add(options, Items.COMPARATOR);
        add(options, Items.CLOCK);
        return List.copyOf(options);
    }

    static ItemStack stackFromId(String id) {
        if (id == null || id.isBlank()) {
            return ItemStack.EMPTY;
        }
        ResourceLocation key = ResourceLocation.tryParse(id);
        if (key == null) {
            return ItemStack.EMPTY;
        }
        return BuiltInRegistries.ITEM.getOptional(key)
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    private static void add(List<ItemStack> options, Item item) {
        if (item != null && item != Items.AIR) {
            options.add(new ItemStack(item));
        }
    }
}

package thaumcraft.common.items.equipment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IWarpingGear;

public final class TCEquipmentHelper {
    private static final List<ExtraEquipmentStackProvider> EXTRA_EQUIPMENT_STACK_PROVIDERS = new CopyOnWriteArrayList<>();

    private TCEquipmentHelper() {
    }

    public static AutoCloseable registerExtraEquipmentStackProvider(ExtraEquipmentStackProvider provider) {
        EXTRA_EQUIPMENT_STACK_PROVIDERS.add(provider);
        return () -> EXTRA_EQUIPMENT_STACK_PROVIDERS.remove(provider);
    }

    public static List<ItemStack> getVisibleEquipmentStacks(Player player) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if (player == null) {
            return stacks;
        }

        stacks.add(player.getMainHandItem());
        stacks.add(player.getOffhandItem());
        stacks.addAll(player.getInventory().armor);
        for (ExtraEquipmentStackProvider provider : EXTRA_EQUIPMENT_STACK_PROVIDERS) {
            Iterable<ItemStack> extraStacks = provider.getEquipmentStacks(player);
            if (extraStacks == null) {
                continue;
            }
            for (ItemStack stack : extraStacks) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    public static boolean hasGoggles(Player player) {
        for (ItemStack stack : getVisibleEquipmentStacks(player)) {
            if (!stack.isEmpty()
                    && stack.getItem() instanceof IGoggles goggles
                    && goggles.showIngamePopups(stack, player)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRevealer(Player player) {
        for (ItemStack stack : getVisibleEquipmentStacks(player)) {
            if (!stack.isEmpty()
                    && stack.getItem() instanceof IRevealer revealer
                    && revealer.showNodes(stack, player)) {
                return true;
            }
        }
        return false;
    }

    public static int getWarp(Player player) {
        int total = 0;
        for (ItemStack stack : getVisibleEquipmentStacks(player)) {
            if (!stack.isEmpty() && stack.getItem() instanceof IWarpingGear warpingGear) {
                total += Math.max(0, warpingGear.getWarp(stack, player));
            }
        }
        return total;
    }

    public interface ExtraEquipmentStackProvider {
        Iterable<ItemStack> getEquipmentStacks(Player player);
    }
}

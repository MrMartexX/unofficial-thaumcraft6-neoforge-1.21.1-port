package thaumcraft.common.items.equipment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.registry.TCDataComponents;

public final class TCEquipmentHelper {
    private static final List<ExtraEquipmentStackProvider> EXTRA_EQUIPMENT_STACK_PROVIDERS = new CopyOnWriteArrayList<>();

    static {
        try {
            Class<?> curiosApi = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            registerExtraEquipmentStackProvider(player -> {
                try {
                    Object optional = curiosApi.getMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class).invoke(null, player);
                    if (optional instanceof java.util.Optional<?> opt && opt.isPresent()) {
                        Object handler = opt.get();
                        Object equipped = handler.getClass().getMethod("getEquippedCurios").invoke(handler);
                        if (equipped instanceof net.neoforged.neoforge.items.IItemHandler itemHandler) {
                            List<ItemStack> list = new ArrayList<>(itemHandler.getSlots());
                            for (int i = 0; i < itemHandler.getSlots(); i++) {
                                ItemStack s = itemHandler.getStackInSlot(i);
                                if (!s.isEmpty()) {
                                    list.add(s);
                                }
                            }
                            return list;
                        }
                    }
                } catch (Throwable ignored) {
                }
                return List.of();
            });
        } catch (Throwable ignored) {
        }
    }

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

    public static int getStackWarp(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        int total = 0;
        if (stack.getItem() instanceof IWarpingGear warpingGear) {
            total += Math.max(0, warpingGear.getWarp(stack, player));
        }
        total += stack.getOrDefault(TCDataComponents.ITEM_WARP.get(), 0);
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("TC.WARP")) {
            total += Math.max(0, customData.copyTag().getByte("TC.WARP"));
        }
        return total;
    }

    public static int getWarp(Player player) {
        int total = 0;
        for (ItemStack stack : getVisibleEquipmentStacks(player)) {
            total += getStackWarp(stack, player);
        }
        return total;
    }

    public interface ExtraEquipmentStackProvider {
        Iterable<ItemStack> getEquipmentStacks(Player player);
    }
}

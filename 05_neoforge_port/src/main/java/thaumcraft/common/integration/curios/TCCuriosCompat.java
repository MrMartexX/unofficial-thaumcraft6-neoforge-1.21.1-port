package thaumcraft.common.integration.curios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public final class TCCuriosCompat {
    public static final String MODID = "curios";

    private TCCuriosCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    public static ItemStack findFirstCurio(Player player, Item item) {
        if (!isLoaded()) {
            return ItemStack.EMPTY;
        }
        return CuriosInternal.findFirstCurio(player, item);
    }

    public static List<ItemStack> getAllEquippedCurios(Player player) {
        if (!isLoaded()) {
            return List.of();
        }
        return CuriosInternal.getAllEquippedCurios(player);
    }

    public static boolean isCurioEquipped(Player player, Item item) {
        if (!isLoaded()) {
            return false;
        }
        return CuriosInternal.isCurioEquipped(player, item);
    }

    public static boolean consumeCurio(Player player, Item item) {
        if (!isLoaded()) {
            return false;
        }
        return CuriosInternal.consumeCurio(player, item);
    }

    private static final class CuriosInternal {
        static ItemStack findFirstCurio(Player player, Item item) {
            return CuriosApi.getCuriosInventory(player)
                    .flatMap(inv -> inv.findFirstCurio(item))
                    .map(SlotResult::stack)
                    .orElse(ItemStack.EMPTY);
        }

        static List<ItemStack> getAllEquippedCurios(Player player) {
            return CuriosApi.getCuriosInventory(player)
                    .map(inv -> {
                        var equipped = inv.getEquippedCurios();
                        List<ItemStack> list = new ArrayList<>(equipped.getSlots());
                        for (int i = 0; i < equipped.getSlots(); i++) {
                            ItemStack s = equipped.getStackInSlot(i);
                            if (!s.isEmpty()) {
                                list.add(s);
                            }
                        }
                        return list;
                    })
                    .orElse(List.of());
        }

        static boolean isCurioEquipped(Player player, Item item) {
            return CuriosApi.getCuriosInventory(player)
                    .flatMap(inv -> inv.findFirstCurio(item))
                    .map(res -> !res.stack().isEmpty())
                    .orElse(false);
        }

        static boolean consumeCurio(Player player, Item item) {
            return CuriosApi.getCuriosInventory(player).map(inv -> {
                Optional<SlotResult> result = inv.findFirstCurio(item);
                if (result.isPresent()) {
                    SlotResult res = result.get();
                    ItemStack stack = res.stack();
                    stack.shrink(1);
                    return true;
                }
                return false;
            }).orElse(false);
        }
    }
}

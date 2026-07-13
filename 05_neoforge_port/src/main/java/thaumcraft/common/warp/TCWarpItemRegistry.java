package thaumcraft.common.warp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;

public final class TCWarpItemRegistry {
    private static final Map<ResourceLocation, Integer> ITEM_WARP = new LinkedHashMap<>();

    private TCWarpItemRegistry() {
    }

    public static void bootstrap() {
        ITEM_WARP.clear();
        register(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "jar_brain"), 1);
    }

    public static void register(ResourceLocation itemId, int amount) {
        if (itemId == null || amount <= 0) {
            return;
        }
        ITEM_WARP.put(itemId, amount);
    }

    public static int getWarp(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return ITEM_WARP.getOrDefault(itemId, 0);
    }

    public static Map<ResourceLocation, Integer> entries() {
        return Collections.unmodifiableMap(ITEM_WARP);
    }
}

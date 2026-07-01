package thaumcraft.common.items.casters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

public final class CasterManager {
    public static final int MAX_VIS_DISCOUNT_PERCENT = 50;
    private static final List<VisDiscountStackProvider> EXTRA_DISCOUNT_STACK_PROVIDERS = new CopyOnWriteArrayList<>();
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();

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
        return Math.min(MAX_VIS_DISCOUNT_PERCENT, Math.max(0, getRawTotalVisDiscountPercent(player)));
    }

    public static int getRawTotalVisDiscountPercent(Player player) {
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
        return Math.max(0, total);
    }

    public static int getDiscountedVisCost(int baseVis, Player player) {
        if (baseVis <= 0) {
            return 0;
        }
        return (int)(baseVis * (1.0F - getTotalVisDiscount(player)));
    }

    public static float getCasterConsumptionModifier(Player player) {
        return Math.max(0.1F, 1.0F - getRawTotalVisDiscountPercent(player) / 100.0F);
    }

    public static boolean isOnCooldown(Player player) {
        return player != null && COOLDOWNS.getOrDefault(player.getUUID(), 0L) > System.currentTimeMillis();
    }

    public static void setCooldown(Player player, int cooldownTicks) {
        setCooldown(player, player == null ? ItemStack.EMPTY : player.getMainHandItem(), cooldownTicks);
    }

    public static void setCooldown(Player player, ItemStack casterStack, int cooldownTicks) {
        if (player == null || cooldownTicks <= 0) {
            return;
        }
        COOLDOWNS.put(player.getUUID(), System.currentTimeMillis() + cooldownTicks * 50L);
        if (!casterStack.isEmpty()) {
            player.getCooldowns().addCooldown(casterStack.getItem(), cooldownTicks);
        }
    }

    public static void clearCooldown(Player player, ItemStack casterStack) {
        if (player == null) {
            return;
        }
        COOLDOWNS.remove(player.getUUID());
        if (!casterStack.isEmpty()) {
            player.getCooldowns().removeCooldown(casterStack.getItem());
        }
    }

    public static float getAuraPool(ItemCaster caster, ItemStack stack, Player player) {
        if (caster == null || player == null || !(player.level() instanceof ServerLevel serverLevel)) {
            return 0.0F;
        }
        int area = caster.area();
        BlockPos center = player.blockPosition();
        if (area == 1) {
            return auraAt(serverLevel, center)
                    + auraAt(serverLevel, center.offset(16, 0, 0))
                    + auraAt(serverLevel, center.offset(-16, 0, 0))
                    + auraAt(serverLevel, center.offset(0, 0, 16))
                    + auraAt(serverLevel, center.offset(0, 0, -16));
        }
        if (area >= 2) {
            float total = 0.0F;
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = -1; zz <= 1; zz++) {
                    total += auraAt(serverLevel, center.offset(xx * 16, 0, zz * 16));
                }
            }
            return total;
        }
        return auraAt(serverLevel, center);
    }

    public static boolean consumeVis(ItemCaster caster, ItemStack stack, Player player, float amount, boolean crafting, boolean simulate) {
        if (amount <= 0.0F) {
            return true;
        }
        if (caster == null || player == null || !(player.level() instanceof ServerLevel)) {
            return false;
        }
        float adjusted = amount * getCasterConsumptionModifier(player);
        if (getAuraPool(caster, stack, player) < adjusted) {
            return false;
        }
        if (simulate) {
            return true;
        }
        if (caster.area() == 1) {
            return drainAcrossOffsets(player, adjusted, new int[][] {
                    {0, 0}, {16, 0}, {-16, 0}, {0, 16}, {0, -16}
            });
        }
        if (caster.area() >= 2) {
            int[][] offsets = new int[9][2];
            int index = 0;
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = -1; zz <= 1; zz++) {
                    offsets[index++] = new int[] {xx * 16, zz * 16};
                }
            }
            return drainAcrossOffsets(player, adjusted, offsets);
        }
        return AuraHandler.drainVis(player.level(), player.blockPosition(), adjusted, false) >= adjusted;
    }

    private static boolean drainAcrossOffsets(Player player, float amount, int[][] offsets) {
        float remaining = amount;
        float chunkDrain = Math.max(1.0F, amount / offsets.length);
        int attempts = 0;
        while (remaining > 0.0F) {
            attempts++;
            for (int[] offset : offsets) {
                float requested = Math.min(chunkDrain, remaining);
                remaining -= AuraHandler.drainVis(
                        player.level(),
                        player.blockPosition().offset(offset[0], 0, offset[1]),
                        requested,
                        false
                );
                if (remaining <= 0.0F || attempts > 1000) {
                    return remaining <= 0.0F;
                }
            }
        }
        return true;
    }

    private static float auraAt(ServerLevel level, BlockPos pos) {
        AuraChunk chunk = AuraHandler.ensureAuraChunk(level, new ChunkPos(pos));
        return chunk.getVis();
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

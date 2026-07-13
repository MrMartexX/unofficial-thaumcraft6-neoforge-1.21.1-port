package thaumcraft.common.warp;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

public final class TCWarpManager {
    private static final String FIRST_STEPS = "FIRSTSTEPS";
    private static final String WARP_RESEARCH = "WARP";

    private TCWarpManager() {
    }

    public static TCPlayerWarp get(ServerPlayer player) {
        return TCPlayerWarpStore.get(player);
    }

    public static int set(ServerPlayer player, TCWarpType type, int amount) {
        if (player == null || type == null) {
            return 0;
        }

        TCPlayerWarp warp = TCPlayerWarpStore.get(player);
        int before = warp.get(type);
        warp.set(type, amount);
        int change = warp.get(type) - before;
        if (change > 0) {
            warp.setCounter(warp.totalWarp());
        }
        TCPlayerWarpStore.set(player, warp);
        afterMutation(player, type, change, true);
        return warp.get(type);
    }

    public static int add(ServerPlayer player, TCWarpType type, int amount) {
        return add(player, type, amount, true);
    }

    public static int add(ServerPlayer player, TCWarpType type, int amount, boolean announce) {
        if (player == null || type == null || amount == 0) {
            return player == null || type == null ? 0 : TCPlayerWarpStore.get(player).get(type);
        }

        TCPlayerWarp warp = TCPlayerWarpStore.get(player);
        int before = warp.get(type);
        int effectiveAmount = amount;
        if (amount < 0 && before + amount < 0) {
            effectiveAmount = -before;
        }

        warp.add(type, effectiveAmount);
        int change = warp.get(type) - before;
        if (change == 0) {
            return warp.get(type);
        }
        if (change > 0) {
            warp.setCounter(warp.totalWarp());
        }
        TCPlayerWarpStore.set(player, warp);
        afterMutation(player, type, change, announce);
        return warp.get(type);
    }

    public static int reduce(ServerPlayer player, TCWarpType type, int amount) {
        return add(player, type, -Math.max(0, amount), true);
    }

    public static int applyCraftingWarp(ServerPlayer player, ItemStack crafted, boolean wussMode) {
        if (player == null || crafted == null || crafted.isEmpty() || wussMode) {
            return 0;
        }
        int amount = TCWarpItemRegistry.getWarp(crafted);
        if (amount <= 0) {
            return 0;
        }
        add(player, TCWarpType.NORMAL, amount);
        return amount;
    }

    public static void clear(ServerPlayer player) {
        if (player == null) {
            return;
        }
        TCPlayerWarpStore.clear(player);
        TCWarpNetwork.syncToPlayer(player);
    }

    public static void sync(ServerPlayer player) {
        TCWarpNetwork.syncToPlayer(player);
    }

    private static void afterMutation(ServerPlayer player, TCWarpType type, int change, boolean announce) {
        if (announce) {
            TCWarpNetwork.sendWarpMessage(player, type, change);
        }
        if (change != 0 && type != TCWarpType.TEMPORARY) {
            maybeUnlockWarpResearch(player);
        }
        TCWarpNetwork.syncToPlayer(player);
    }

    private static void maybeUnlockWarpResearch(ServerPlayer player) {
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        if (!TCResearchManager.isResearchComplete(knowledge, FIRST_STEPS)
                || TCResearchManager.isResearchComplete(knowledge, WARP_RESEARCH)) {
            return;
        }

        TCResearchManager.completeResearch(player, WARP_RESEARCH);
        player.displayClientMessage(Component.translatable("research.WARP.warn"), true);
    }
}

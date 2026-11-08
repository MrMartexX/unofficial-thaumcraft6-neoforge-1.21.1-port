package thaumcraft.common.warp;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class TCPlayerWarpStore {
    private static final String ROOT_KEY = "ThaumcraftWarp";

    private TCPlayerWarpStore() {
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer cloned) || !(event.getOriginal() instanceof ServerPlayer original)) {
            return;
        }
        CompoundTag oldData = original.getPersistentData();
        if (oldData.contains(ROOT_KEY)) {
            cloned.getPersistentData().put(ROOT_KEY, oldData.getCompound(ROOT_KEY).copy());
        }
    }

    public static TCPlayerWarp get(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        return TCPlayerWarp.load(persistentData.getCompound(ROOT_KEY));
    }

    public static void set(ServerPlayer player, TCPlayerWarp warp) {
        player.getPersistentData().put(ROOT_KEY, warp.save());
    }

    public static int add(ServerPlayer player, TCWarpType type, int amount) {
        TCPlayerWarp warp = get(player);
        int updated = warp.add(type, amount);
        set(player, warp);
        return updated;
    }

    public static int reduce(ServerPlayer player, TCWarpType type, int amount) {
        return add(player, type, -amount);
    }

    public static void clear(ServerPlayer player) {
        TCPlayerWarp warp = new TCPlayerWarp();
        set(player, warp);
    }
}

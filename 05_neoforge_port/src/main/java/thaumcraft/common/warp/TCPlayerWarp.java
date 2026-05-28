package thaumcraft.common.warp;

import java.util.EnumMap;
import net.minecraft.nbt.CompoundTag;

public final class TCPlayerWarp {
    public static final int MAX_WARP = 500;
    private static final String TAG_WARP = "warp";
    private static final String TAG_COUNTER = "counter";

    private final EnumMap<TCWarpType, Integer> warp = new EnumMap<>(TCWarpType.class);
    private int counter;

    public TCPlayerWarp() {
        clear();
    }

    public void clear() {
        for (TCWarpType type : TCWarpType.values()) {
            warp.put(type, 0);
        }
        counter = 0;
    }

    public int get(TCWarpType type) {
        return warp.getOrDefault(type, 0);
    }

    public void set(TCWarpType type, int amount) {
        if (type != null) {
            warp.put(type, clampWarp(amount));
        }
    }

    public int add(TCWarpType type, int amount) {
        if (type == null) {
            return 0;
        }
        int updated = clampWarp(get(type) + amount);
        warp.put(type, updated);
        return updated;
    }

    public int reduce(TCWarpType type, int amount) {
        return add(type, -amount);
    }

    public int actualWarp() {
        return get(TCWarpType.PERMANENT) + get(TCWarpType.NORMAL);
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int amount) {
        counter = Math.max(0, amount);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        int[] values = new int[TCWarpType.values().length];
        for (TCWarpType type : TCWarpType.values()) {
            values[type.ordinal()] = get(type);
        }
        tag.putIntArray(TAG_WARP, values);
        tag.putInt(TAG_COUNTER, counter);
        return tag;
    }

    public static TCPlayerWarp load(CompoundTag tag) {
        TCPlayerWarp playerWarp = new TCPlayerWarp();
        if (tag == null) {
            return playerWarp;
        }

        int[] values = tag.getIntArray(TAG_WARP);
        TCWarpType[] types = TCWarpType.values();
        for (int i = 0; i < values.length && i < types.length; i++) {
            playerWarp.set(types[i], values[i]);
        }
        playerWarp.setCounter(tag.getInt(TAG_COUNTER));
        return playerWarp;
    }

    private static int clampWarp(int amount) {
        return Math.max(0, Math.min(MAX_WARP, amount));
    }
}

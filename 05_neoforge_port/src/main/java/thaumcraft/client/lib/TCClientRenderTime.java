package thaumcraft.client.lib;

import net.minecraft.client.DeltaTracker;

public final class TCClientRenderTime {
    private static final long GLOBAL_UPDATE_FRESH_NANOS = 50_000_000L;

    private static float guiPartialTick;
    private static long lastDeltaTrackerUpdateNanos = Long.MIN_VALUE;

    private TCClientRenderTime() {
    }

    public static float update(float partialTick) {
        guiPartialTick = clamp(partialTick);
        return guiPartialTick;
    }

    public static float update(DeltaTracker deltaTracker) {
        if (deltaTracker == null) {
            return guiPartialTick;
        }

        lastDeltaTrackerUpdateNanos = System.nanoTime();
        return update(deltaTracker.getGameTimeDeltaPartialTick(true));
    }

    public static float updateScreenFallback(float partialTick) {
        long now = System.nanoTime();
        if (lastDeltaTrackerUpdateNanos == Long.MIN_VALUE
                || now - lastDeltaTrackerUpdateNanos > GLOBAL_UPDATE_FRESH_NANOS) {
            return update(partialTick);
        }

        return guiPartialTick;
    }

    public static float guiPartialTick() {
        return guiPartialTick;
    }

    private static float clamp(float partialTick) {
        if (Float.isNaN(partialTick) || Float.isInfinite(partialTick)) {
            return 0.0F;
        }
        if (partialTick < 0.0F) {
            return 0.0F;
        }
        return Math.min(1.0F, partialTick);
    }
}
package thaumcraft.common.warp;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class TCWarpClientCache {
    private static TCPlayerWarp currentWarp = new TCPlayerWarp();
    private static final Queue<TCWarpMessagePayload> messageQueue = new ConcurrentLinkedQueue<>();

    private TCWarpClientCache() {
    }

    public static void accept(TCWarpSyncPayload payload) {
        currentWarp = payload == null ? new TCPlayerWarp() : payload.toWarp();
    }

    public static void accept(TCWarpMessagePayload payload) {
        if (payload != null && payload.change() != 0) {
            messageQueue.offer(payload);
        }
    }

    public static TCPlayerWarp currentWarp() {
        return currentWarp.copy();
    }

    public static TCWarpMessagePayload pollMessage() {
        return messageQueue.poll();
    }

    public static void clear() {
        currentWarp = new TCPlayerWarp();
        messageQueue.clear();
    }
}

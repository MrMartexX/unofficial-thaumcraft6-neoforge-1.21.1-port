package thaumcraft.common.world.aura;

import java.util.Optional;
import net.minecraft.world.level.ChunkPos;

public final class TCAuraClientCache {
    private static TCAuraSyncPayload currentAura;

    private TCAuraClientCache() {
    }

    public static void accept(TCAuraSyncPayload payload) {
        currentAura = payload;
    }

    public static Optional<TCAuraSyncPayload> getCurrentAura() {
        return Optional.ofNullable(currentAura);
    }

    public static boolean isCurrentChunk(ChunkPos chunkPos) {
        return currentAura != null && currentAura.chunkX() == chunkPos.x && currentAura.chunkZ() == chunkPos.z;
    }

    public static void clear() {
        currentAura = null;
    }
}

package thaumcraft.common.research.theorycraft;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;

public final class TCResearchTableClientCache {
    private static final Map<BlockPos, TCResearchTableSyncPayload> TABLES = new HashMap<>();

    private TCResearchTableClientCache() {
    }

    public static void accept(TCResearchTableSyncPayload payload) {
        TABLES.put(payload.pos(), payload);
    }

    public static TCResearchTableSyncPayload get(BlockPos pos) {
        return TABLES.get(pos);
    }

    public static void clear() {
        TABLES.clear();
    }
}

package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;

/** Client-owned display state; no gameplay authority is stored here. */
public final class TCInfusionClientFXCache {
    private static final Map<EssentiaKey, TCInfusionEssentiaSourcePayload> ESSENTIA_TRAILS = new LinkedHashMap<>();
    private static final Map<SourceKey, TCInfusionSourcePayload> SOURCES = new LinkedHashMap<>();

    private TCInfusionClientFXCache() {
    }

    public static synchronized void accept(TCInfusionEssentiaSourcePayload payload) {
        EssentiaKey key = new EssentiaKey(payload.matrixPos(), payload.sourcePos(), payload.color());
        ESSENTIA_TRAILS.remove(key);
        ESSENTIA_TRAILS.put(key, payload);
    }

    public static synchronized void accept(TCInfusionSourcePayload payload) {
        SourceKey key = new SourceKey(payload.matrixPos(), payload.targetPos(), payload.color());
        SOURCES.remove(key);
        SOURCES.put(key, payload);
    }

    public static synchronized List<TCInfusionEssentiaSourcePayload> drainEssentiaTrails() {
        List<TCInfusionEssentiaSourcePayload> drained = new ArrayList<>(ESSENTIA_TRAILS.values());
        ESSENTIA_TRAILS.clear();
        return List.copyOf(drained);
    }

    public static synchronized List<TCInfusionSourcePayload> drainSources() {
        List<TCInfusionSourcePayload> drained = new ArrayList<>(SOURCES.values());
        SOURCES.clear();
        return List.copyOf(drained);
    }

    public static synchronized void clear() {
        ESSENTIA_TRAILS.clear();
        SOURCES.clear();
    }

    public static synchronized int pendingEssentiaTrailCount() {
        return ESSENTIA_TRAILS.size();
    }

    public static synchronized int pendingSourceCount() {
        return SOURCES.size();
    }

    private record EssentiaKey(BlockPos matrixPos, BlockPos sourcePos, int color) {
    }

    private record SourceKey(BlockPos matrixPos, BlockPos targetPos, int color) {
    }
}

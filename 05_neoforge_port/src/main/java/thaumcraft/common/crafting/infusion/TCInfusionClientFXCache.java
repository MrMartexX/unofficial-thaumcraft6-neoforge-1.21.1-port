package thaumcraft.common.crafting.infusion;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;

/** Client-owned display state; no gameplay authority is stored here. */
public final class TCInfusionClientFXCache {
    private static final int LEGACY_DEFAULT_SOURCE_LIFETIME = 15;
    private static final int LEGACY_PEDESTAL_SOURCE_LIFETIME = 60;
    private static final Map<EssentiaKey, TCInfusionEssentiaSourcePayload> ESSENTIA_TRAILS = new LinkedHashMap<>();
    private static final Map<SourceKey, TCInfusionSourcePayload> PENDING_SOURCES = new LinkedHashMap<>();
    private static final Map<SourceKey, SourceFX> ACTIVE_SOURCES = new LinkedHashMap<>();
    private static int tickCount;

    private TCInfusionClientFXCache() {
    }

    public static synchronized void accept(TCInfusionEssentiaSourcePayload payload) {
        EssentiaKey key = new EssentiaKey(payload.matrixPos(), payload.sourcePos(), payload.color());
        ESSENTIA_TRAILS.remove(key);
        ESSENTIA_TRAILS.put(key, payload);
    }

    public static synchronized void accept(TCInfusionSourcePayload payload) {
        SourceKey key = new SourceKey(payload.matrixPos(), payload.targetPos(), payload.color());
        PENDING_SOURCES.remove(key);
        PENDING_SOURCES.put(key, payload);
    }

    public static synchronized void tick(Level level) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        tickCount++;
        flushEssentiaTrails(level);
        activatePendingSources(level);
        tickActiveSources(level);
    }

    public static synchronized void clear() {
        ESSENTIA_TRAILS.clear();
        PENDING_SOURCES.clear();
        ACTIVE_SOURCES.clear();
        tickCount = 0;
    }

    public static synchronized int pendingEssentiaTrailCount() {
        return ESSENTIA_TRAILS.size();
    }

    public static synchronized int activeSourceCount() {
        return ACTIVE_SOURCES.size();
    }

    private static void flushEssentiaTrails(Level level) {
        for (TCInfusionEssentiaSourcePayload payload : ESSENTIA_TRAILS.values()) {
            BlockPos destination = level.getBlockEntity(payload.matrixPos()) instanceof TCInfusionMatrixBlockEntity
                    ? payload.matrixPos().below()
                    : payload.matrixPos();
            TCFXDispatcher.essentiaTrailFx(
                    level,
                    payload.sourcePos(),
                    destination,
                    tickCount,
                    payload.color(),
                    0.1F,
                    payload.extension()
            );
        }
        ESSENTIA_TRAILS.clear();
    }

    private static void activatePendingSources(Level level) {
        for (Map.Entry<SourceKey, TCInfusionSourcePayload> entry : PENDING_SOURCES.entrySet()) {
            TCInfusionSourcePayload payload = entry.getValue();
            if (!(level.getBlockEntity(payload.matrixPos()) instanceof TCInfusionMatrixBlockEntity)) {
                continue;
            }
            int lifetime = level.getBlockEntity(payload.targetPos()) instanceof TCInfusionPedestalBlockEntity
                    ? LEGACY_PEDESTAL_SOURCE_LIFETIME
                    : LEGACY_DEFAULT_SOURCE_LIFETIME;
            ACTIVE_SOURCES.put(entry.getKey(), new SourceFX(payload, lifetime));
        }
        PENDING_SOURCES.clear();
    }

    private static void tickActiveSources(Level level) {
        var iterator = ACTIVE_SOURCES.entrySet().iterator();
        while (iterator.hasNext()) {
            SourceFX source = iterator.next().getValue();
            if (source.ticks <= 0) {
                iterator.remove();
                continue;
            }
            if (!(level.getBlockEntity(source.payload.targetPos()) instanceof TCInfusionPedestalBlockEntity pedestal)) {
                iterator.remove();
                continue;
            }
            ItemStack stack = pedestal.getStoredStack();
            if (!stack.isEmpty()) {
                TCFXDispatcher.drawInfusionPedestalParticles(
                        level,
                        source.payload.targetPos(),
                        source.payload.matrixPos(),
                        stack
                );
            }
            source.ticks--;
        }
    }

    private record EssentiaKey(BlockPos matrixPos, BlockPos sourcePos, int color) {
    }

    private record SourceKey(BlockPos matrixPos, BlockPos targetPos, int color) {
    }

    private static final class SourceFX {
        private final TCInfusionSourcePayload payload;
        private int ticks;

        private SourceFX(TCInfusionSourcePayload payload, int ticks) {
            this.payload = payload;
            this.ticks = ticks;
        }
    }
}

package thaumcraft.common.world.aura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class AuraHandler {
    public static final int AURA_CEILING = AuraChunk.BASE_CEILING;

    private static final int UPDATE_INTERVAL_TICKS = 20;
    private static final float[] PHASE_TABLE = new float[] {0.25F, 0.15F, 0.1F, 0.05F, 0.0F, 0.05F, 0.1F, 0.15F};
    private static final float[] MAX_TABLE = new float[] {0.15F, 0.05F, 0.0F, -0.05F, -0.15F, -0.05F, 0.0F, 0.05F};
    private static final List<DirectionOffset> HORIZONTAL_DIRECTIONS = List.of(
            new DirectionOffset(1, 0),
            new DirectionOffset(-1, 0),
            new DirectionOffset(0, 1),
            new DirectionOffset(0, -1)
    );

    private AuraHandler() {
    }

    public static Optional<AuraChunk> getAuraChunk(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        return getAuraChunk(serverLevel, new ChunkPos(pos));
    }

    public static Optional<AuraChunk> getAuraChunk(ServerLevel level, ChunkPos pos) {
        return TCAuraSavedData.get(level)
                .getChunk(pos.x, pos.z)
                .map(AuraChunk::copy);
    }

    public static AuraChunk seedAuraChunk(ServerLevel level, BlockPos pos, int base) {
        ChunkPos chunkPos = new ChunkPos(pos);
        int clampedBase = Math.min(AURA_CEILING, Math.max(0, base));
        return TCAuraSavedData.get(level)
                .setChunk(chunkPos.x, chunkPos.z, clampedBase, clampedBase, 0.0F)
                .copy();
    }

    public static AuraChunk ensureAuraChunk(ServerLevel level, ChunkPos chunkPos) {
        TCAuraSavedData data = TCAuraSavedData.get(level);
        data.markLoaded(chunkPos.x, chunkPos.z);
        Optional<AuraChunk> existing = data.getChunk(chunkPos.x, chunkPos.z);
        if (existing.isPresent()) {
            return existing.get().copy();
        }
        AuraChunk generated = generateAuraChunk(level, chunkPos);
        data.setChunk(chunkPos.x, chunkPos.z, generated.getBase(), generated.getVis(), generated.getFlux());
        return generated;
    }

    public static void unloadAuraChunk(ServerLevel level, ChunkPos chunkPos) {
        TCAuraSavedData.get(level).markUnloaded(chunkPos.x, chunkPos.z);
    }

    public static AuraChunk generateAuraChunk(ServerLevel level, ChunkPos chunkPos) {
        float life = getChunkBiomeAuraModifier(level, chunkPos.x, chunkPos.z);
        Random random = seededChunkRandom(level.getSeed(), chunkPos.x, chunkPos.z);
        float noise = (float)(1.0D + random.nextGaussian() * 0.10000000149011612D);
        int base = Math.min(AURA_CEILING, Math.max(0, (int)(life * AURA_CEILING * noise)));
        return new AuraChunk(chunkPos.x, chunkPos.z, base, base, 0.0F);
    }

    public static AuraStats getAuraStats(ServerLevel level) {
        TCAuraSavedData data = TCAuraSavedData.get(level);
        return new AuraStats(data.savedChunkCount(), data.loadedChunkCount());
    }

    static float getChunkBiomeAuraModifier(ServerLevel level, int chunkX, int chunkZ) {
        float life = sampleBiomeAuraModifier(level, chunkX, chunkZ);
        life += sampleBiomeAuraModifier(level, chunkX + 1, chunkZ);
        life += sampleBiomeAuraModifier(level, chunkX - 1, chunkZ);
        life += sampleBiomeAuraModifier(level, chunkX, chunkZ + 1);
        life += sampleBiomeAuraModifier(level, chunkX, chunkZ - 1);
        return life / 5.0F;
    }

    public static float getTotalAura(Level level, BlockPos pos) {
        return getAuraChunk(level, pos)
                .map(chunk -> chunk.getVis() + chunk.getFlux())
                .orElse(0.0F);
    }

    public static float getFluxSaturation(Level level, BlockPos pos) {
        return getAuraChunk(level, pos)
                .map(chunk -> chunk.getBase() > 0 ? chunk.getFlux() / chunk.getBase() : 0.0F)
                .orElse(0.0F);
    }

    public static float getVis(Level level, BlockPos pos) {
        return getAuraChunk(level, pos).map(AuraChunk::getVis).orElse(0.0F);
    }

    public static float getFlux(Level level, BlockPos pos) {
        return getAuraChunk(level, pos).map(AuraChunk::getFlux).orElse(0.0F);
    }

    public static int getAuraBase(Level level, BlockPos pos) {
        return getAuraChunk(level, pos).map(AuraChunk::getBase).orElse(0);
    }

    public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        if (player != null) {
            return false;
        }
        return getAuraChunk(level, pos)
                .map(chunk -> chunk.getBase() > 0 && chunk.getVis() / chunk.getBase() < 0.1F)
                .orElse(false);
    }

    public static void addVis(Level level, BlockPos pos, float amount) {
        if (amount <= 0.0F || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        TCAuraSavedData data = TCAuraSavedData.get(serverLevel);
        ChunkPos chunkPos = new ChunkPos(pos);
        data.getChunk(chunkPos.x, chunkPos.z).ifPresent(chunk -> {
            chunk.setVis(chunk.getVis() + amount);
            data.markChanged();
        });
    }

    public static void addFlux(Level level, BlockPos pos, float amount, boolean showEffect) {
        if (amount <= 0.0F || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        TCAuraSavedData data = TCAuraSavedData.get(serverLevel);
        ChunkPos chunkPos = new ChunkPos(pos);
        data.getChunk(chunkPos.x, chunkPos.z).ifPresent(chunk -> {
            chunk.setFlux(chunk.getFlux() + amount);
            data.markChanged();
        });
    }

    public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        if (amount <= 0.0F || !(level instanceof ServerLevel serverLevel)) {
            return 0.0F;
        }
        TCAuraSavedData data = TCAuraSavedData.get(serverLevel);
        ChunkPos chunkPos = new ChunkPos(pos);
        Optional<AuraChunk> chunk = data.getChunk(chunkPos.x, chunkPos.z);
        if (chunk.isEmpty()) {
            return 0.0F;
        }
        float drained = Math.min(amount, chunk.get().getVis());
        if (drained <= 0.0F) {
            return 0.0F;
        }
        if (!simulate) {
            chunk.get().setVis(chunk.get().getVis() - drained);
            data.markChanged();
        }
        return drained;
    }

    public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        if (amount <= 0.0F || !(level instanceof ServerLevel serverLevel)) {
            return 0.0F;
        }
        TCAuraSavedData data = TCAuraSavedData.get(serverLevel);
        ChunkPos chunkPos = new ChunkPos(pos);
        Optional<AuraChunk> chunk = data.getChunk(chunkPos.x, chunkPos.z);
        if (chunk.isEmpty()) {
            return 0.0F;
        }
        float drained = Math.min(amount, chunk.get().getFlux());
        if (drained <= 0.0F) {
            return 0.0F;
        }
        if (!simulate) {
            chunk.get().setFlux(chunk.get().getFlux() - drained);
            data.markChanged();
        }
        return drained;
    }

    public static void tickLevel(ServerLevel level) {
        if (level.getGameTime() % UPDATE_INTERVAL_TICKS != 0L) {
            return;
        }

        TCAuraSavedData data = TCAuraSavedData.get(level);
        if (data.isEmpty() || !data.hasLoadedChunks()) {
            return;
        }

        int moonPhase = (int)Math.floorMod(level.getDayTime() / 24000L, 8L);
        float phaseVis = PHASE_TABLE[moonPhase];
        float phaseMax = 1.0F + MAX_TABLE[moonPhase];
        float phaseFlux = 0.25F - phaseVis;
        boolean changed = false;

        for (AuraChunk chunk : new ArrayList<>(data.loadedChunks())) {
            changed |= processAuraChunk(level, data, chunk, phaseVis, phaseMax, phaseFlux);
        }

        if (changed) {
            data.markChanged();
        }
    }

    private static boolean processAuraChunk(
            ServerLevel level,
            TCAuraSavedData data,
            AuraChunk chunk,
            float phaseVis,
            float phaseMax,
            float phaseFlux
    ) {
        ArrayList<DirectionOffset> directions = new ArrayList<>(HORIZONTAL_DIRECTIONS);
        Collections.shuffle(directions, new Random(level.getSeed() ^ level.getGameTime() ^ chunk.getKey()));

        float effectiveBase = chunk.getBase() * phaseMax;
        float currentVis = chunk.getVis();
        float currentFlux = chunk.getFlux();
        AuraChunk neighborVisChunk = null;
        AuraChunk neighborFluxChunk = null;
        float lowestVis = Float.MAX_VALUE;
        float lowestFlux = Float.MAX_VALUE;
        boolean dirty = false;

        for (DirectionOffset direction : directions) {
            Optional<AuraChunk> neighbor = data.getChunk(
                    chunk.getChunkX() + direction.x(),
                    chunk.getChunkZ() + direction.z()
            );
            if (neighbor.isEmpty()) {
                continue;
            }

            AuraChunk candidate = neighbor.get();
            if ((neighborVisChunk == null || lowestVis > candidate.getVis())
                    && candidate.getVis() + candidate.getFlux() < candidate.getBase() * phaseMax) {
                neighborVisChunk = candidate;
                lowestVis = candidate.getVis();
            }
            if (neighborFluxChunk == null || lowestFlux > candidate.getFlux()) {
                neighborFluxChunk = candidate;
                lowestFlux = candidate.getFlux();
            }
        }

        if (neighborVisChunk != null && currentVis > 0.0F && lowestVis < currentVis && lowestVis / currentVis < 0.75F) {
            float amount = Math.min(currentVis - lowestVis, 1.0F);
            currentVis -= amount;
            neighborVisChunk.setVis(lowestVis + amount);
            dirty = true;
        }

        if (neighborFluxChunk != null
                && currentFlux > Math.max(5.0F, chunk.getBase() / 10.0F)
                && lowestFlux < currentFlux / 1.75F) {
            float amount = Math.min(currentFlux - lowestFlux, 1.0F);
            currentFlux -= amount;
            neighborFluxChunk.setFlux(lowestFlux + amount);
            dirty = true;
        }

        if (currentVis + currentFlux < effectiveBase) {
            float amount = Math.min(effectiveBase - (currentVis + currentFlux), phaseVis);
            if (amount > 0.0F) {
                currentVis += amount;
                dirty = true;
            }
        } else if (currentVis > effectiveBase * 1.25F && phaseFlux > 0.0F && level.getRandom().nextFloat() < 0.1F) {
            currentFlux += phaseFlux;
            currentVis -= phaseFlux;
            dirty = true;
        } else if (currentVis <= effectiveBase * 0.1F
                && currentVis >= currentFlux
                && phaseFlux > 0.0F
                && level.getRandom().nextFloat() < 0.1F) {
            currentFlux += phaseFlux;
            dirty = true;
        }

        if (dirty) {
            chunk.setVis(currentVis);
            chunk.setFlux(currentFlux);
        }

        return dirty;
    }

    private static float sampleBiomeAuraModifier(ServerLevel level, int chunkX, int chunkZ) {
        BlockPos samplePos = new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8);
        return TCAuraBiomeModifiers.getBiomeAuraModifier(TCAuraBiomeModifiers.getUncachedBiome(level, samplePos), samplePos);
    }

    private static Random seededChunkRandom(long worldSeed, int chunkX, int chunkZ) {
        Random random = new Random(worldSeed);
        long xSeed = random.nextLong() >> 3;
        long zSeed = random.nextLong() >> 3;
        random.setSeed(xSeed * chunkX + zSeed * chunkZ ^ worldSeed);
        return random;
    }

    private record DirectionOffset(int x, int z) {
    }

    public record AuraStats(int savedChunks, int loadedChunks) {
    }
}

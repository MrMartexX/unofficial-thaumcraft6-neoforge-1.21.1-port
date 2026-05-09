package thaumcraft.common.world.aura;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import thaumcraft.Thaumcraft;

final class TCAuraSavedData extends SavedData {
    private static final String DATA_ID = Thaumcraft.MODID + "_aura";
    private static final SavedData.Factory<TCAuraSavedData> FACTORY = new SavedData.Factory<>(
            TCAuraSavedData::new,
            TCAuraSavedData::load
    );

    private final Map<Long, AuraChunk> chunks = new HashMap<>();
    private final Set<Long> loadedChunkKeys = new HashSet<>();

    static TCAuraSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_ID);
    }

    private static TCAuraSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        TCAuraSavedData data = new TCAuraSavedData();
        ListTag list = tag.getList("chunks", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int chunkX = entry.getInt("x");
            int chunkZ = entry.getInt("z");
            AuraChunk chunk = new AuraChunk(
                    chunkX,
                    chunkZ,
                    entry.getInt("base"),
                    entry.getFloat("vis"),
                    entry.getFloat("flux")
            );
            data.chunks.put(chunk.getKey(), chunk);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (AuraChunk chunk : chunks.values()) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", chunk.getChunkX());
            entry.putInt("z", chunk.getChunkZ());
            entry.putInt("base", chunk.getBase());
            entry.putFloat("vis", chunk.getVis());
            entry.putFloat("flux", chunk.getFlux());
            list.add(entry);
        }
        tag.put("chunks", list);
        return tag;
    }

    boolean isEmpty() {
        return chunks.isEmpty();
    }

    int savedChunkCount() {
        return chunks.size();
    }

    int loadedChunkCount() {
        return loadedChunkKeys.size();
    }

    boolean hasLoadedChunks() {
        return !loadedChunkKeys.isEmpty();
    }

    Collection<AuraChunk> loadedChunks() {
        return loadedChunkKeys.stream()
                .map(chunks::get)
                .filter(chunk -> chunk != null)
                .toList();
    }

    Optional<AuraChunk> getChunk(int chunkX, int chunkZ) {
        return Optional.ofNullable(chunks.get(ChunkPos.asLong(chunkX, chunkZ)));
    }

    AuraChunk setChunk(int chunkX, int chunkZ, int base, float vis, float flux) {
        AuraChunk chunk = new AuraChunk(chunkX, chunkZ, base, vis, flux);
        chunks.put(chunk.getKey(), chunk);
        setDirty();
        return chunk;
    }

    void markLoaded(int chunkX, int chunkZ) {
        loadedChunkKeys.add(ChunkPos.asLong(chunkX, chunkZ));
    }

    void markUnloaded(int chunkX, int chunkZ) {
        loadedChunkKeys.remove(ChunkPos.asLong(chunkX, chunkZ));
    }

    void markChanged() {
        setDirty();
    }
}

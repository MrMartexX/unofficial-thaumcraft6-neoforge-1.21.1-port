package thaumcraft.common.golems.seals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import thaumcraft.api.golems.seals.SealPos;

public final class TCSealSavedData extends SavedData {
    private static final Factory<TCSealSavedData> FACTORY = new Factory<>(TCSealSavedData::new, TCSealSavedData::load);
    final Map<SealPos, SealEntity> seals = new ConcurrentHashMap<>();

    public static TCSealSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, "thaumcraft_seals");
    }

    public static TCSealSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        TCSealSavedData data = new TCSealSavedData();
        ListTag entries = tag.getList("seals", 10);
        for (int i = 0; i < entries.size(); i++) {
            SealEntity seal = new SealEntity();
            seal.readNBT(entries.getCompound(i), registries);
            if (seal.getSeal() != null && seal.getSealPos() != null) {
                data.seals.put(seal.getSealPos(), seal);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();
        for (SealEntity seal : seals.values()) entries.add(seal.writeNBT(registries));
        tag.putInt("version", 1);
        tag.put("seals", entries);
        return tag;
    }
}

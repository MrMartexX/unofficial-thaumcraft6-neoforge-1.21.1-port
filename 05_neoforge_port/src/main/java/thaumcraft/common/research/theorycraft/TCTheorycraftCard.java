package thaumcraft.common.research.theorycraft;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class TCTheorycraftCard {
    private long seed;

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("seed", seed);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        seed = tag.getLong("seed");
    }

    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        return true;
    }

    public boolean isAidOnly() {
        return false;
    }

    public String getResearchCategory() {
        return null;
    }

    public List<ItemStack> getRequiredItems() {
        return List.of();
    }

    public List<Boolean> getRequiredItemsConsumed() {
        return List.of();
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = Math.abs(seed);
    }

    public abstract int getInspirationCost();

    public abstract Component getLocalizedName();

    public abstract Component getLocalizedText();

    public abstract boolean activate(ServerPlayer player, TCResearchTableData data);
}

package thaumcraft.common.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardReject extends TCTheorycraftCard {
    private String category;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("cat", category == null ? "" : category);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        category = tag.getString("cat");
    }

    @Override
    public int getInspirationCost() {
        return 0;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.reject.name", CardStudy.categoryName(category));
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.reject.text", CardStudy.categoryName(category));
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        ArrayList<String> categories = new ArrayList<>();
        for (String key : data.categoryTotals.keySet()) {
            if (!data.categoriesBlocked.contains(key)) {
                categories.add(key);
            }
        }
        if (categories.isEmpty()) {
            return false;
        }
        category = categories.get(new Random(getSeed()).nextInt(categories.size()));
        return category != null;
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (category == null) {
            return false;
        }
        data.addTotal("BASICS", 5);
        data.categoriesBlocked.add(category);
        return true;
    }
}

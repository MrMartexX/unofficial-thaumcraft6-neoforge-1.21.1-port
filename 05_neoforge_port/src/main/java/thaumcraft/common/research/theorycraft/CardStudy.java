package thaumcraft.common.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardStudy extends TCTheorycraftCard {
    private String category = "BASICS";

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("cat", category);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        category = tag.getString("cat");
    }

    @Override
    public String getResearchCategory() {
        return category;
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        ArrayList<String> categories = data.getAvailableCategories(player);
        if (categories.isEmpty()) {
            return false;
        }
        category = categories.get(new Random(getSeed()).nextInt(categories.size()));
        return category != null;
    }

    @Override
    public boolean isAidOnly() {
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.study.name", categoryName(category));
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.study.text", categoryName(category));
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(category, randomBetween(player, 15, 25));
        return true;
    }

    static Component categoryName(String category) {
        return Component.translatable("tc.research_category." + category);
    }

    static int randomBetween(ServerPlayer player, int min, int max) {
        return min + player.getRandom().nextInt(max - min + 1);
    }
}

package thaumcraft.common.research.theorycraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardNotation extends TCTheorycraftCard {
    private String category1;
    private String category2;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("cat1", category1 == null ? "" : category1);
        tag.putString("cat2", category2 == null ? "" : category2);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        category1 = tag.getString("cat1");
        category2 = tag.getString("cat2");
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
        return Component.translatable("card.notation.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.notation.text", CardStudy.categoryName(category1), CardStudy.categoryName(category2));
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        if (data.categoryTotals.size() < 2) {
            return false;
        }

        int lowValue = Integer.MAX_VALUE;
        String lowKey = "";
        int highValue = 0;
        String highKey = "";
        for (String category : data.categoryTotals.keySet()) {
            int value = data.getTotal(category);
            if (value < lowValue) {
                lowValue = value;
                lowKey = category;
            }
            if (value > highValue) {
                highValue = value;
                highKey = category;
            }
        }

        if (highKey.equals(lowKey) || lowValue <= 0) {
            return false;
        }
        category1 = lowKey;
        category2 = highKey;
        return true;
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (category1 == null || category2 == null) {
            return false;
        }
        int lowValue = data.getTotal(category1);
        data.addTotal(category1, -lowValue);
        data.addTotal(category2, lowValue / 2 + CardStudy.randomBetween(player, 0, lowValue / 2));
        return true;
    }
}

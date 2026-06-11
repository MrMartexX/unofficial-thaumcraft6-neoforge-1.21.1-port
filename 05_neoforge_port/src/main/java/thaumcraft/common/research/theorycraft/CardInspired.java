package thaumcraft.common.research.theorycraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardInspired extends TCTheorycraftCard {
    private String category;
    private int amount;

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putString("cat", category == null ? "" : category);
        tag.putInt("amt", amount);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        category = tag.getString("cat");
        amount = tag.getInt("amt");
    }

    @Override
    public String getResearchCategory() {
        return category;
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        if (data.categoryTotals.isEmpty()) {
            return false;
        }

        int highValue = 0;
        String highKey = "";
        for (String currentCategory : data.categoryTotals.keySet()) {
            int value = data.getTotal(currentCategory);
            if (value > highValue) {
                highValue = value;
                highKey = currentCategory;
            }
        }
        category = highKey;
        amount = 10 + highValue / 2;
        return true;
    }

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.inspired.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.inspired.text", amount, CardStudy.categoryName(category));
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        data.addTotal(category, amount);
        return true;
    }
}

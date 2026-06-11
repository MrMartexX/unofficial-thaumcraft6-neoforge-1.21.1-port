package thaumcraft.common.research.theorycraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCResearchManager;

final class CardAnalyze extends TCTheorycraftCard {
    private String category = null;

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
    public String getResearchCategory() {
        return category;
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        return false;
    }

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.analyze.name", CardStudy.categoryName(category));
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.analyze.text", CardStudy.categoryName(category), CardStudy.categoryName("BASICS"));
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (category == null || TCResearchManager.getKnowledgePoints(player, TCKnowledgeType.OBSERVATION, category) < 1) {
            return false;
        }
        data.addTotal("BASICS", 5);
        TCResearchManager.consumeKnowledgeRaw(player, TCKnowledgeType.OBSERVATION, category, TCKnowledgeType.OBSERVATION.pointsToRaw(1));
        data.addTotal(category, CardStudy.randomBetween(player, 25, 50));
        return true;
    }
}

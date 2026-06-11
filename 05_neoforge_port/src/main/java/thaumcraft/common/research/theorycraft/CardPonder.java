package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardPonder extends TCTheorycraftCard {
    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.ponder.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.ponder.text");
    }

    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        return data.categoriesBlocked.size() < data.categoryTotals.size();
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        int amount = 25;
        int tries = 0;
        while (amount > 0 && tries < 1000) {
            tries++;
            for (String category : data.categoryTotals.keySet()) {
                if (data.categoriesBlocked.contains(category)) {
                    if (data.categoryTotals.size() <= 1) {
                        return false;
                    }
                    continue;
                }
                data.addTotal(category, 1);
                amount--;
                if (amount <= 0) {
                    break;
                }
            }
        }
        data.addTotal("BASICS", 5);
        data.bonusDraws++;
        return amount != 20;
    }
}

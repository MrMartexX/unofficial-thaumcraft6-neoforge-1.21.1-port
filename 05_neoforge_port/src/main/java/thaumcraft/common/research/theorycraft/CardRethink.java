package thaumcraft.common.research.theorycraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class CardRethink extends TCTheorycraftCard {
    @Override
    public boolean initialize(ServerPlayer player, TCResearchTableData data) {
        int amount = 0;
        for (String category : data.categoryTotals.keySet()) {
            amount += data.getTotal(category);
        }
        return amount >= 10;
    }

    @Override
    public int getInspirationCost() {
        return -1;
    }

    @Override
    public Component getLocalizedName() {
        return Component.translatable("card.rethink.name");
    }

    @Override
    public Component getLocalizedText() {
        return Component.translatable("card.rethink.text");
    }

    @Override
    public boolean activate(ServerPlayer player, TCResearchTableData data) {
        if (!initialize(player, data)) {
            return false;
        }

        int amount = 0;
        for (String category : data.categoryTotals.keySet()) {
            amount += data.getTotal(category);
        }
        amount = Math.min(amount, 10);

        int tries = 0;
        while (amount > 0 && tries < 1000) {
            tries++;
            for (String category : data.categoryTotals.keySet()) {
                data.addTotal(category, -1);
                amount--;
                if (amount <= 0 || !data.hasTotal(category)) {
                    break;
                }
            }
        }

        data.bonusDraws++;
        data.addTotal("BASICS", CardStudy.randomBetween(player, 1, 10));
        return true;
    }
}
